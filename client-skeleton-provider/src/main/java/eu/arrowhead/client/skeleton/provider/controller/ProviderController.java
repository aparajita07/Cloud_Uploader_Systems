package eu.arrowhead.client.skeleton.provider.controller;
import eu.arrowhead.client.skeleton.provider.Entity.Device;
import eu.arrowhead.client.skeleton.provider.Entity.ServiceDBObject;
import eu.arrowhead.client.skeleton.provider.JSONReader;
import eu.arrowhead.client.skeleton.provider.Service.DeviceService;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import eu.arrowhead.client.skeleton.provider.OPC_UA.*;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/factory")
public class ProviderController {
	//=================================================================================================
	// members
	@Value("${opc.ua.connection_address}")
	private String opcuaServerAddress;

	@Value("${opc.ua.root_node_namespace}")
	private int rootNodeNamespaceIndex;

	@Value("${opc.ua.root_node_identifier}")
	private String rootNodeIdentifier;

	public ProviderController() throws IOException, ParseException {
	}

	@RequestMapping(path = "/monitor")
	@ResponseBody
	public String echoService() {
		return "Provider System Up and running!!";
	}

	@GetMapping(path = {"/I1/monitor/inventoryId","/I2/monitor/inventoryId","/I3/monitor/inventoryId","/I4/monitor/inventoryId","/I5/monitor/inventoryId",
			"/I6/monitor/inventoryId","/I7/monitor/inventoryId","/I8/monitor/inventoryId","/I9/monitor/inventoryId","/Q1/monitor/inventoryId",
			"/Q2/monitor/inventoryId","/Q3/monitor/inventoryId","/Q4/monitor/inventoryId","/Q5/monitor/inventoryId","/Q6/monitor/inventoryId",
			"/Q7/monitor/inventoryId","/Q8/monitor/inventoryId","/Q9/monitor/inventoryId","/Q10/monitor/inventoryId"})
	@ResponseBody
	public String monitorInventoryId(@RequestParam (name="inventoryid") String inv) throws IOException, ParseException {
		return inv;
	}
	@GetMapping(path = {"/I1/monitor/systemId","/I2/monitor/systemId","/I3/monitor/systemId","/I4/monitor/systemId","/I5/monitor/systemId",
			"/I6/monitor/systemId","/I7/monitor/systemId","/I8/monitor/systemId","/I9/monitor/systemId","/Q1/monitor/systemId",
			"/Q2/monitor/systemId","/Q3/monitor/systemId","/Q4/monitor/systemId","/Q5/monitor/systemId","/Q6/monitor/systemId",
			"/Q7/monitor/systemId","/Q8/monitor/systemId","/Q9/monitor/systemId","/Q10/monitor/systemId"})
	@ResponseBody
	public String monitorSystemId(@RequestParam (name="systemId") String sysid) throws IOException, ParseException {
		return sysid;
	}
	@GetMapping(path = {"/I1/monitor/ping","/I2/monitor/ping","/I3/monitor/ping","/I4/monitor/ping","/I5/monitor/ping",
			"/I6/monitor/ping","/I7/monitor/ping","/I8/monitor/ping","/I9/monitor/ping","/Q1/monitor/ping",
			"/Q2/monitor/ping","/Q3/monitor/ping","/Q4/monitor/ping","/Q5/monitor/ping","/Q6/monitor/ping",
			"/Q7/monitor/ping","/Q8/monitor/ping","/Q9/monitor/ping","/Q10/monitor/ping"})
	@ResponseBody
	public String monitorPing() throws IOException, ParseException {
		return "Working correctly";
	}

	@GetMapping(path = {"/I1/sensor","/I2/sensor","/I3/sensor","/I4/sensor","/I5/sensor", "/I6/sensor","/I7/sensor","/I8/sensor","/I9/sensor","/Q1/actuator",
			"/Q2/actuator","/Q3/actuator","/Q4/actuator","/Q5/actuator","/Q6/actuator","/Q7/actuator","/Q8/actuator","/Q9/actuator","/Q10/actuator"})
	@ResponseBody
	public Device getValue(@RequestParam(name = "description") String des) throws IOException, ParseException {
		String id= des.substring(0,3).trim();
		OPCUAConnection connection = new OPCUAConnection("192.168.1.1:4840");
		return DeviceService.getSensor(id, connection);
		//return DeviceService.getSensor(id);
	}

	@GetMapping(path = "/{sensorId}/sensor")
	@ResponseBody
	public Device getsensor(@PathVariable(name = "sensorId") String id) throws IOException, ParseException {
		OPCUAConnection connection = new OPCUAConnection("192.168.1.1:4840");
		return DeviceService.getSensor(id, connection);
		//return DeviceService.getSensor(id);
	}

	@GetMapping(path = "/{actuatorId}/actuator")
	@ResponseBody
	public Device getActuator(@PathVariable(name = "actuatorId") String id) throws IOException, ParseException {
		OPCUAConnection connection = new OPCUAConnection("192.168.1.1:4840");
		return DeviceService.getActuator(id, connection);
		//return DeviceService.getActuator(id);
	}

	@PostMapping(path= "/exit")
	@ResponseBody
	public String sessionExit(){
		System.exit(0);
		return "System shutting down!!";
	}
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your provider related REST end points
	// FIXME Double-check that the token security prevents tampering with variables in the OPC-UA it is not supposed to access (I.e. only allows access to the variables in the Service Registry)
	//-------------------------------------------------------------------------------------------------

	@RequestMapping("*")
	@ResponseBody
	public String fallbackMethod(){
		return "fallback method";
	}
}
