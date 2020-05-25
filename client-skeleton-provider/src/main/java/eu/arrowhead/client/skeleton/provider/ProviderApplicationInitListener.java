package eu.arrowhead.client.skeleton.provider;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.util.*;
import java.io.FileReader;
import java.io.FileNotFoundException;

import eu.arrowhead.client.skeleton.provider.Entity.ServiceDBObject;
import eu.arrowhead.client.skeleton.provider.Service.DeviceService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceSecurityType;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.model.nodes.objects.AuditAddNodesEventNode;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import eu.arrowhead.client.library.ArrowheadService;
import eu.arrowhead.client.library.config.ApplicationInitListener;
import eu.arrowhead.client.library.util.ClientCommonConstants;
import eu.arrowhead.client.skeleton.provider.security.ProviderSecurityConfig;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.core.CoreSystem;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.client.skeleton.provider.OPC_UA.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import javax.annotation.PreDestroy;
import javax.websocket.OnClose;

@Component
public class ProviderApplicationInitListener extends ApplicationInitListener {
	
	//=================================================================================================
	// members
	
	@Autowired
	private ArrowheadService arrowheadService;
	
	@Autowired
	private ProviderSecurityConfig providerSecurityConfig;
	
	@Value(ClientCommonConstants.$TOKEN_SECURITY_FILTER_ENABLED_WD)
	private boolean tokenSecurityFilterEnabled;
	
	@Value(CommonConstants.$SERVER_SSL_ENABLED_WD)
	private boolean sslEnabled;

	@Value(ClientCommonConstants.$CLIENT_SYSTEM_NAME)
	private String mySystemName;

	@Value(ClientCommonConstants.$CLIENT_SERVER_ADDRESS_WD)
	private String mySystemAddress;

	@Value(ClientCommonConstants.$CLIENT_SERVER_PORT_WD)
	private int mySystemPort;

	/*@Value("${opc.ua.connection_address}")
	private String opcuaServerAddress;

	@Value("${opc.ua.root_node_namespace}")
	private int rootNodeNamespaceIndex;

	@Value("${opc.ua.root_node_identifier}")
	private String rootNodeIdentifier;*/


	private final Logger logger = LogManager.getLogger(ProviderApplicationInitListener.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Override
	protected void customInit(final ContextRefreshedEvent event) {

		//Checking the availability of necessary core systems
		checkCoreSystemReachability(CoreSystem.SERVICE_REGISTRY);
		if (tokenSecurityFilterEnabled) {
			checkCoreSystemReachability(CoreSystem.AUTHORIZATION);			

			//Initialize Arrowhead Context
			arrowheadService.updateCoreServiceURIs(CoreSystem.AUTHORIZATION);			
		}

		setTokenSecurityFilter();
		
		//TODO: implement here any custom behavior on application start up
		//Register services into ServiceRegistry

		// "opc.tcp://" must be stripped off as Eclipse Milo will add this to the address regardless of whether it is there already
		/*opcuaServerAddress = opcuaServerAddress.replaceAll("opc.tcp://", "");
		System.out.println("OPC UA SERVER_ADDRESS:" + opcuaServerAddress);

		JSONParser parser = new JSONParser();

		try {

			//Read The JSON File SR_Entry
			Object obj = parser.parse(new FileReader("client-skeleton-provider/src/main/resources/SR_Entry.json"));
			JSONObject jsonObject =  (JSONObject) obj;
			JSONArray Services = (JSONArray) jsonObject.get("Services");
			Iterator<JSONObject> iterator = Services.iterator();

				while (iterator.hasNext()) {
					JSONObject iter=iterator.next();
					String serviceDef= iter.get("ServiceDef").toString();
					System.out.println(serviceDef);
					Map metadata= ((Map)iter.get("MetaData"));
					Iterator<Map.Entry> iter1=metadata.entrySet().iterator();

					// Register read and write services
					ServiceRegistryRequestDTO serviceRequest1 = createServiceRegistryRequest("read_" + serviceDef,  "/read/variable", HttpMethod.GET);
					ServiceRegistryRequestDTO serviceRequest2 = createServiceRegistryRequest("write_" + serviceDef,  "/write/variable", HttpMethod.POST);

						while (iter1.hasNext()){
						Map.Entry pair =iter1.next();
						String Key= pair.getKey().toString();
						String Value= pair.getValue().toString();
						serviceRequest1.getMetadata().put(Key, Value);
						serviceRequest2.getMetadata().put(Key, Value);
						System.out.println(Key+":"+Value);
					}
					arrowheadService.forceRegisterServiceToServiceRegistry(serviceRequest1);
					System.out.println("Registered read service for variable " + serviceDef + ".");

					if (serviceDef.contains("q1")||serviceDef.contains("q2")||serviceDef.contains("q3")||serviceDef.contains("q4")||serviceDef.contains("q5")||serviceDef.contains("q6")||serviceDef.contains("q7")||serviceDef.contains("q8")||serviceDef.contains("q9")||serviceDef.contains("q10"))
						arrowheadService.forceRegisterServiceToServiceRegistry(serviceRequest2);
					    System.out.println("Registered write service for variable " + serviceDef + ".");
				}



		} catch (ParseException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("ERROR: Could not register to ServiceRegistry.");
		}*/

		for(int i=1;i<=9;i++){
			ServiceRegistryRequestDTO serviceRequest1 = createServiceRegistryRequest("SensorValue","/factory/I"+i+"/sensor",HttpMethod.GET);
			ServiceRegistryRequestDTO serviceRequest2 = createServiceRegistryRequest("Monitorable","/factory/I"+i+"/monitor",HttpMethod.GET);
			try {
				JSONReader json= new JSONReader("I"+i);
				serviceRequest1.getMetadata().put("param-description",json.getDefinition());
				serviceRequest2.getMetadata().put("param-inventoryId", "invI"+i);
				serviceRequest2.getMetadata().put("param-SystemId", "SysI"+i);
				arrowheadService.forceRegisterServiceToServiceRegistry(serviceRequest1);
				arrowheadService.forceRegisterServiceToServiceRegistry(serviceRequest2);
				System.out.println("Registered SensorValue and Monitorable services for I"+i+".");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		for(int j=1;j<=10;j++){
			ServiceRegistryRequestDTO serviceRequest1 = createServiceRegistryRequest("ActuatorValue","/factory/Q"+j+"/actuator",HttpMethod.GET);
			ServiceRegistryRequestDTO serviceRequest2 = createServiceRegistryRequest("Monitorable","/factory/Q"+j+"/monitor",HttpMethod.GET);
			try {
				JSONReader json= new JSONReader("Q"+j);
				serviceRequest1.getMetadata().put("param-description", json.getDefinition());
				serviceRequest1.getMetadata().put("param-value", "value");
				serviceRequest2.getMetadata().put("param-inventoryId", "invQ"+j);
				serviceRequest2.getMetadata().put("param-SystemId", "SysQ"+j);
				arrowheadService.forceRegisterServiceToServiceRegistry(serviceRequest1);
				arrowheadService.forceRegisterServiceToServiceRegistry(serviceRequest2);
				System.out.println("Registered ActuatorValue and Monitorable service for Q"+j+".");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		ServiceRegistryRequestDTO serviceRequest = createServiceRegistryRequest("Monitorable","/factory/monitor",HttpMethod.GET);
		arrowheadService.forceRegisterServiceToServiceRegistry(serviceRequest);
		System.out.println("Registered Monitorable service for provider system");
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public void customDestroy() {
		//TODO: implement here any custom behavior on application shout down
		logger.info("Unregistering services!!");
		arrowheadService.unregisterServiceFromServiceRegistry("sensorvalue");
		arrowheadService.unregisterServiceFromServiceRegistry("actuatorvalue");
		arrowheadService.unregisterServiceFromServiceRegistry("monitorable");
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------

	//-------------------------------------------------------------------------------------------------
	private ServiceRegistryRequestDTO createServiceRegistryRequest(final String serviceDefinition, final String serviceUri, final HttpMethod httpMethod) {
		final ServiceRegistryRequestDTO serviceRegistryRequest = new ServiceRegistryRequestDTO();
		serviceRegistryRequest.setServiceDefinition(serviceDefinition);
		final SystemRequestDTO systemRequest = new SystemRequestDTO();
		systemRequest.setSystemName(mySystemName);
		systemRequest.setAddress(mySystemAddress);
		systemRequest.setPort(mySystemPort);

		if (tokenSecurityFilterEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.TOKEN);
			serviceRegistryRequest.setInterfaces(List.of("HTTPS-SECURE-JSON"));
		} else if (sslEnabled) {
			systemRequest.setAuthenticationInfo(Base64.getEncoder().encodeToString(arrowheadService.getMyPublicKey().getEncoded()));
			serviceRegistryRequest.setSecure(ServiceSecurityType.CERTIFICATE);
			serviceRegistryRequest.setInterfaces(List.of("HTTPS-SECURE-JSON"));
			serviceRegistryRequest.setSecure(ServiceSecurityType.NOT_SECURE);
			serviceRegistryRequest.setInterfaces(List.of("HTTP-INSECURE-JSON"));
		}
		serviceRegistryRequest.setProviderSystem(systemRequest);
		serviceRegistryRequest.setServiceUri(serviceUri);
		serviceRegistryRequest.setMetadata(new HashMap<>());
		serviceRegistryRequest.getMetadata().put("http-method", httpMethod.name());
		return serviceRegistryRequest;
	}



	private void setTokenSecurityFilter() {
		if(!tokenSecurityFilterEnabled) {
			logger.info("TokenSecurityFilter in not active");
		} else {
			final PublicKey authorizationPublicKey = arrowheadService.queryAuthorizationPublicKey();
			if (authorizationPublicKey == null) {
				throw new ArrowheadException("Authorization public key is null");
			}
			
			KeyStore keystore;
			try {
				keystore = KeyStore.getInstance(sslProperties.getKeyStoreType());
				keystore.load(sslProperties.getKeyStore().getInputStream(), sslProperties.getKeyStorePassword().toCharArray());
			} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
				throw new ArrowheadException(ex.getMessage());
			}			
			final PrivateKey providerPrivateKey = Utilities.getPrivateKey(keystore, sslProperties.getKeyPassword());

			providerSecurityConfig.getTokenSecurityFilter().setAuthorizationPublicKey(authorizationPublicKey);
			providerSecurityConfig.getTokenSecurityFilter().setMyPrivateKey(providerPrivateKey);
		}
	}
}
