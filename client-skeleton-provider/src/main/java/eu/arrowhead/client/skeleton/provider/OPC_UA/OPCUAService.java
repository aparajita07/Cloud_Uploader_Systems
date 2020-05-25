package eu.arrowhead.client.skeleton.provider.OPC_UA;

import eu.arrowhead.client.skeleton.provider.JSONReader;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class OPCUAService {

    // members
    @Value("${opc.ua.connection_address}")
    private String opcuaServerAddress;

    @Value("${opc.ua.root_node_namespace}")
    private int rootNodeNamespaceIndex;

    @Value("${opc.ua.root_node_identifier}")
    private String rootNodeIdentifier;

    // Methods


    public OPCUAService() {
    }

    public String read(String ComponentId, OPCUAConnection connection) throws IOException, ParseException {
        String returnval="";
        JSONReader reader= new JSONReader(ComponentId);
        String definition= reader.getDefinition();
        String nodeIdentifier= "\"Machine Status\""+".\""+definition+"\"";
        NodeId nodeId = new NodeId(3, nodeIdentifier);
        //opcuaServerAddress = opcuaServerAddress.replaceAll("opc.tcp://", "");
        //OPCUAConnection connection1 = new OPCUAConnection("192.168.1.1:4840");
        try {
            returnval = OPCUAInteractions.readNode(connection.getConnectedClient(), nodeId);
            connection.dispose();
            return returnval;
        } catch (Exception ex) {
            connection.dispose();
            return "There was an error reading the OPC-UA node.";
        }
    }

    public String write(String ComponentId, String value) throws IOException, ParseException {
        String status="";

        boolean bolValue= false;
        if(value.equalsIgnoreCase ("true"))
            bolValue=true;
        else bolValue=false;
        JSONReader reader= new JSONReader(ComponentId);
        String definition= reader.getDefinition();
        String nodeIdentifier= "\""+definition+"\"";
        NodeId nodeId = new NodeId(3, nodeIdentifier);
        //opcuaServerAddress = opcuaServerAddress.replaceAll("opc.tcp://", "");
        OPCUAConnection connection = new OPCUAConnection("192.168.1.1:4840");
        try {
            status = OPCUAInteractions.writeNode(connection.getConnectedClient(), nodeId, bolValue);
            connection.dispose();
            return status;
        } catch (Exception ex) {
            connection.dispose();
            return "There was an error reading the OPC-UA node.";
        }
    }
}
