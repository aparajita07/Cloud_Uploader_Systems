package eu.arrowhead.client.skeleton.consumer;

import eu.arrowhead.common.dto.shared.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO.Builder;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.SSLProperties;
//import eu.arrowhead.client.skeleton.provider.JSONReader;

import java.util.*;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE}) //TODO: add custom packages if any
public class CloudUploader implements ApplicationRunner {

	//=================================================================================================
	// members

	@Autowired
	private ArrowheadService arrowheadService;

	@Autowired
	protected SSLProperties sslProperties;
	//=================================================================================================
	// methods

	//------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(CloudUploader.class, args);
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void run(final ApplicationArguments args) throws Exception {
		    monitorService("monitorable");

		while(true) {
			readservice("sensorvalue");
			readservice("actuatorvalue");
		}
	}

	public void readservice(String ServiceDef){
		Long baseTime= System.currentTimeMillis()/1000;
		String SenMLbase= "  {\n" +
				"    \"bn\": \""+ServiceDef+"\",\n" +
				"    \"bs\": 0,\n" +
				"    \"bt\": "+baseTime+",\n" +
				"    \"bu\": \"\",\n" +
				"    \"bv\": 0,\n" +
				"    \"bver\": 0\n" +
				"  },\n";

		List<OrchestrationResultDTO> response = orchestrate(ServiceDef);
		for(int i=0;i<response.size();i++){
			OrchestrationResultDTO result=response.get(i);
			Map<String, String> meta = result.getMetadata();
			final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
			final String address = result.getProvider().getAddress();
			final int port = result.getProvider().getPort();
			final String serviceUri = result.getServiceUri();
			final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
			String token = null;
			String inventoryId= meta.get("param-inventoryId");
			String systemId= meta.get("param-SystemId");
			if (result.getAuthorizationTokens() != null) {
				token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
			}
			final Object payload = null; //Can be null if not specified in the description of the service.

			if(serviceUri.equalsIgnoreCase("/factory/monitor")){
				System.out.println("GET " + address + serviceUri);
				final String consumedReadService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload);
				System.out.println("Service response: " + consumedReadService);
			}
			else if (serviceUri.contains("/sensor") || serviceUri.contains("/actuator")) {
				String description= meta.get("param-description");
				//System.out.println("GET " + address + serviceUri);
				final String consumedReadService1 = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload, "description", description);
				System.out.println("Service response: " + consumedReadService1);
				//-------------CREATING INDIVISUAL SENML PAYLOAD FOR LOGGING DATA INTO DATAMANGER-----------------------//
				SenMLbase=SenMLbase+getPayload(consumedReadService1);
			}
			else{
				System.out.println("GET " + address + serviceUri+"/"+"inventoryId");
				final String consumedReadService1 = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri+"/"+"inventoryId", interfaceName, token, payload, "inventoryid", inventoryId);
				System.out.println("Service response: " + consumedReadService1);

				System.out.println("GET " + address + serviceUri+"/"+"systemId");
				final String consumedReadService2 = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri+"/"+"systemId", interfaceName, token, payload, "systemId", systemId);
				System.out.println("Service response: " + consumedReadService2);
			}

		}

		String DMPayload= "[\n" +SenMLbase+"]";
		DMPayload= DMPayload.replace("  },\n" + "]", "  }\n" + "]");
		DMService(ServiceDef, DMPayload);
		System.out.println(DMPayload);
	}
	//-----------------------SENDING SENSOR AND ACTUATOR DATA TO DATA MANAGER----------------------------------//
	public void DMService(String ServiceDef, String payload){

		String DMServiceURI= "datamanager/historian/cloud_uploader/"+ServiceDef;
		final String consumedHistorianService = arrowheadService.consumeServiceHTTP(String.class, HttpMethod.PUT, "127.0.0.1", 8461, DMServiceURI, "HTTPS-JSON-SECURE", null, payload);
		System.out.println("Service response from historian: " + consumedHistorianService);
	}
	public String getPayload(String consumedService){

		Boolean vBool= false;
		String timeVal=(consumedService.substring(consumedService.lastIndexOf("\"time\":")+7,consumedService.indexOf(",\"value\"")));
		Long time= Long.parseLong(timeVal);
		String nVal="";
		if(consumedService.substring(7,11).contains("I"))
			nVal="\"Sensor_"+consumedService.substring(7,11).replace(",","");
		else if(consumedService.substring(7,11).contains("Q"))
			nVal="\"Actuator_"+consumedService.substring(7,11).replace(",","");
		String vbVal=consumedService.substring(consumedService.lastIndexOf("\"value\":")+8,consumedService.indexOf(",\"definition\""));
		if(vbVal.equalsIgnoreCase("\"true\""))
			vBool=true;
		String n= "  {\n" +
				"    \"n\": "+nVal+",\n" +
				"    \"s\": 0,\n" +
				"    \"t\": "+0+",\n" +
				"    \"u\": \"\",\n" +
				"    \"ut\": 0,\n" +
				"    \"v\": 0,\n" +
				"    \"vb\": "+vBool+",\n" +
				"    \"vd\": \"\",\n" +
				"    \"vs\": \"\"\n" +
				"  },\n";
		return n;
	}
	public void monitorService(String ServiceDef){

		List<OrchestrationResultDTO> response = orchestrate(ServiceDef);
		for(int i=0;i<response.size();i++){
			OrchestrationResultDTO result=response.get(i);
			Map<String, String> meta = result.getMetadata();
			final HttpMethod httpMethod = HttpMethod.GET;//Http method should be specified in the description of the service.
			final String address = result.getProvider().getAddress();
			final int port = result.getProvider().getPort();
			final String serviceUri = result.getServiceUri();
			final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
			String token = null;
			String inventoryId= meta.get("param-inventoryId");
			String systemId= meta.get("param-SystemId");
			if (result.getAuthorizationTokens() != null) {
				token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
			}
			final Object payload = null; //Can be null if not specified in the description of the service.

			if(serviceUri.equalsIgnoreCase("/factory/monitor")){
				System.out.println("GET " + address + serviceUri);
				final String consumedReadService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri, interfaceName, token, payload);
				System.out.println("Service response: " + consumedReadService);
			}

			else{
				System.out.println("GET " + address + serviceUri+"/"+"inventoryId");
				final String consumedReadService1 = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri+"/"+"inventoryId", interfaceName, token, payload, "inventoryid", inventoryId);
				System.out.println("Service response: " + consumedReadService1);

				System.out.println("GET " + address + serviceUri+"/"+"systemId");
				final String consumedReadService2 = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri+"/"+"systemId", interfaceName, token, payload, "systemId", systemId);
				System.out.println("Service response: " + consumedReadService2);
			}
		}
	}
	public void writeservice(){
		String actuatorId="";
		Scanner in3= new Scanner(System.in);
		System.out.println("Enter the Actuator ID to write new value: Q1-Q10");
		actuatorId= in3.next();

		String actuatorvalue="";
		Scanner in4= new Scanner(System.in);
		System.out.println("Enter the Actuator value to be passed");
		actuatorvalue= in4.next();

		List<OrchestrationResultDTO> response = orchestrate("actuatorvalue");
		for(int i=0;i<response.size();i++) {
			OrchestrationResultDTO result = response.get(i);
			Map<String, String> meta = result.getMetadata();
			final HttpMethod httpMethod = HttpMethod.PUT;//Http method should be specified in the description of the service.
			final String address = result.getProvider().getAddress();
			final int port = result.getProvider().getPort();
			final String serviceUri = result.getServiceUri();
			final String interfaceName = result.getInterfaces().get(0).getInterfaceName(); //Simplest way of choosing an interface.
			String token = null;

			if (result.getAuthorizationTokens() != null) {
				token = result.getAuthorizationTokens().get(interfaceName); //Can be null when the security type of the provider is 'CERTIFICATE' or nothing.
			}
			final Object payload = null; //Can be null if not specified in the description of the service.
			System.out.println("PUT " + address + ":" + port + serviceUri + "/" + actuatorId + "/" + actuatorvalue);
			final String consumedReadService = arrowheadService.consumeServiceHTTP(String.class, httpMethod, address, port, serviceUri + "/" + actuatorId + "/" + actuatorvalue, interfaceName, token, payload);
			System.out.println("Service response: " + consumedReadService);
		}
	}

	/*--------------------Orchestration using ServiceDefinition-------------------------*/
	public List<OrchestrationResultDTO> orchestrate(String serviceDefinition) {
		final Builder orchestrationFormBuilder = arrowheadService.getOrchestrationFormBuilder();

		final ServiceQueryFormDTO requestedService = new ServiceQueryFormDTO();
		requestedService.setServiceDefinitionRequirement(serviceDefinition);

		orchestrationFormBuilder.requestedService(requestedService)
				.flag(Flag.MATCHMAKING, false) //When this flag is false or not specified, then the orchestration response cloud contain more proper provider. Otherwise only one will be chosen if there is any proper.
				.flag(Flag.OVERRIDE_STORE, true) //When this flag is false or not specified, then a Store Orchestration will be proceeded. Otherwise a Dynamic Orchestration will be proceeded.
				.flag(Flag.TRIGGER_INTER_CLOUD, false); //When this flag is false or not specified, then orchestration will not look for providers in the neighbor clouds, when there is no proper provider in the local cloud. Otherwise it will.

		final OrchestrationFormRequestDTO orchestrationRequest = orchestrationFormBuilder.build();

		OrchestrationResponseDTO response = null;
		try {
			response = arrowheadService.proceedOrchestration(orchestrationRequest);
		} catch(final ArrowheadException ex) {
			//Handle the unsuccessful request as you wish!
		}

		if(response ==null||response.getResponse().isEmpty()) {
			//If no proper providers found during the orchestration process, then the response list will be empty. Handle the case as you wish!
			System.out.println("FATAL ERROR: Orchestration response came back empty. Make sure the Service you try to consume is in the Service Registry and that the Consumer has the privileges to consume this Service (e.g. check intra_cloud_authorization and intra_cloud_interface_connection).");
			System.exit(1);
		}

		final List<OrchestrationResultDTO> result = response.getResponse(); //Simplest way of choosing a provider.
		return result;
	}
	private void printOut(final Object object) {
		System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
	}
	private String getInterface() {
		return sslProperties.isSslEnabled() ? "HTTPS-SECURE-JSON" : "HTTP-INSECURE-JSON";
	}
}
