package eu.arrowhead.client.skeleton.provider.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.arrowhead.client.skeleton.provider.Entity.Device;
import eu.arrowhead.client.skeleton.provider.Entity.ServiceDBObject;
import eu.arrowhead.client.skeleton.provider.OPC_UA.OPCUAConnection;
import eu.arrowhead.client.skeleton.provider.OPC_UA.OPCUAService;
import org.jose4j.json.internal.json_simple.parser.ParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeviceService {


    public DeviceService() throws IOException, ParseException {

    }

    public static List<Device> getAllSensors(OPCUAConnection connection) throws IOException, ParseException {
        ArrayList<Device> devices= new ArrayList();
        for(int i=1;i<=9;i++) {
            devices.add(new Device("I"+i, connection));
        }
        return  devices;
    }
    public static List<Device> getAllSensors() throws IOException, ParseException {
        ArrayList<Device> devices= new ArrayList();
        for(int i=1;i<=9;i++) {
            devices.add(new Device("I"+i));
        }
        return  devices;
    }

    public static List<Device> getAllActuators(OPCUAConnection connection) throws IOException, ParseException {
        //return new ArrayList<Device>(actuators.values());

        ArrayList<Device> devices= new ArrayList();
        for(int i=1;i<=10;i++) {
            devices.add(new Device("Q"+i, connection));
        }
        return  devices;
    }
    public static List<Device> getAllActuators() throws IOException, ParseException {
        //return new ArrayList<Device>(actuators.values());

        ArrayList<Device> devices= new ArrayList();
        for(int i=1;i<=10;i++) {
            devices.add(new Device("Q"+i));
        }
        return  devices;
    }

    public static Device getSensor(String id, OPCUAConnection connection) throws IOException, ParseException {
        //return sensors.get(id);
        return new Device(id, connection);
    }
    public static Device getSensor(String id) throws IOException, ParseException {
        //return sensors.get(id);
        return new Device(id);
    }
    public static Device getActuator(String id, OPCUAConnection connection) throws IOException, ParseException {
        //return actuators.get(id);
        return new Device(id, connection);
    }
    public static Device getActuator(String id) throws IOException, ParseException {
        //return actuators.get(id);
        return new Device(id);
    }

    public static Device UpdateActuator(String id, String val, OPCUAConnection connection) throws IOException, ParseException {
        if(!((id.contains("Q")))){
            return null;
        }
        OPCUAService service= new OPCUAService();
        String status=service.write(id,val);
        return new Device(id, connection);
    }
    public static Device UpdateActuator(String id, String val) throws IOException, ParseException {
        if(!((id.contains("Q")))){
            return null;
        }
        OPCUAService service= new OPCUAService();
        String status=service.write(id,val);
        return new Device(id);
    }

    public static List<ServiceDBObject> getAllServices() throws SQLException {
        ArrayList<ServiceDBObject> services= new ArrayList();
        services.add(new ServiceDBObject("sensorvalue"));
        services.add(new ServiceDBObject("actuatorvalue"));

        return services;
    }

    public static ServiceDBObject getService(String serviceDefinition) throws SQLException {
        return new ServiceDBObject(serviceDefinition);
    }

}
