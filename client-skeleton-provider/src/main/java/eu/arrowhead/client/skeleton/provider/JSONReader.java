package eu.arrowhead.client.skeleton.provider;

import eu.arrowhead.client.skeleton.provider.OPC_UA.OPCUAConnection;
import eu.arrowhead.client.skeleton.provider.OPC_UA.OPCUAInteractions;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.*;

public class JSONReader {

    @Value("${opc.ua.connection_address}")
    public String opcuaServerAddress;

    @Value("${opc.ua.root_node_namespace}")
    public int rootNodeNamespaceIndex;

    @Value("${opc.ua.root_node_identifier}")
    public String rootNodeIdentifier;

    //Finding list of devices based on the input value
    JSONParser parser = new JSONParser();
    Object obj = parser.parse(new FileReader("client-skeleton-provider/src/main/resources/SR_Entry.json"));
    JSONObject jsonObject =  (JSONObject) obj;
    JSONArray Services = (JSONArray) jsonObject.get("Services");
    Iterator<JSONObject> iterator = Services.iterator();

    String [] Device=new String[29];
    String [] DeviceType=new String[29];
    String [] Location=new String[29];
    String [] Instance=new String[29];
    String [] ServiceDefinition=new String[29];
    String [] nodeIdentifier= new String[29];
    Vector<String> returnNodes = new Vector<String>();
    int i=0;
    private String Definition="";
    private String Value="";


   /* public JSONReader() throws IOException, ParseException {

        while(iterator.hasNext()){
            JSONObject iter = iterator.next();
            String serviceDef = iter.get("ServiceDef").toString();
            JSONObject mdata = (JSONObject) iter.get("MetaData");
            String device = mdata.get("Device").toString();
            String deviceType = mdata.get("DeviceType").toString();
            String location = mdata.get("Location").toString();
            String instance = mdata.get("Instance").toString();

            Device[i]= device; DeviceType[i]= deviceType; Location[i]= location; Instance[i]= instance;
            nodeIdentifier[i]= "\"Machine Status\".\""+serviceDef+"\"";
            ServiceDefinition[i]=serviceDef;
            returnNodes.add(serviceDef);
            i++;
        }
    }

    public String[] GetServiceDefinition(String deviceval, String deviceTypeval, String locationval, String instanceval){
        while(iterator.hasNext()){
            JSONObject iter = iterator.next();
            String serviceDef = iter.get("ServiceDef").toString();
            JSONObject mdata = (JSONObject) iter.get("MetaData");
            String device = mdata.get("Device").toString();
            String deviceType = mdata.get("DeviceType").toString();
            String location = mdata.get("Location").toString();
            String instance = mdata.get("Instance").toString();

            if((device.equalsIgnoreCase(deviceval)||deviceval.equalsIgnoreCase("All"))&& (deviceType.equalsIgnoreCase(deviceTypeval)|| deviceTypeval.equalsIgnoreCase("All"))
                    && (location.equalsIgnoreCase(locationval)|| locationval.equalsIgnoreCase("All"))&& (instance.equalsIgnoreCase(instanceval) || instanceval.equalsIgnoreCase("All"))){
                Device[i]= device; DeviceType[i]= deviceType; Location[i]= location; Instance[i]= instance;
                nodeIdentifier[i]= "\"Machine Status\".\""+serviceDef+"\"";
                ServiceDefinition[i]=serviceDef;
                i++;
            }
        }

      return ServiceDefinition;
    }*/
    public JSONReader(String component) throws IOException, ParseException {
        String Definition = "";

        while(iterator.hasNext()){
            JSONObject iter = iterator.next();
            String serviceDef = iter.get("ServiceDef").toString();

            if(serviceDef.contains(component+" ")){
                Definition= serviceDef;
                break;
            }
        }

        this.Definition=Definition;

    }


    public String getDefinition() {
        return Definition;
    }
}
