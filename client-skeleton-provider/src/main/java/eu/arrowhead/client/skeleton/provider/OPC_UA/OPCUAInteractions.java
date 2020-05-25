package eu.arrowhead.client.skeleton.provider.OPC_UA;


import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

/**
 * This class contains different ways of interacting with OPC-UA. Note that the clients
 * supplied to the functions must already be connected (e.g. created through the OPCUAConnection class)
 * @author Niklas Karvonen
 */


public class OPCUAInteractions {

    public static Vector<String> browseNode(OpcUaClient client, NodeId browseRoot) {
        //String returnString = "";
        Vector<String> returnNodes = new Vector<String>();
        try {
            List<Node> nodes = client.getAddressSpace().browse(browseRoot).get();
            for(Node node:nodes) {
                returnNodes.add("ns=" + node.getNodeId().get().getNamespaceIndex() + ",identifier=" + node.getNodeId().get().getIdentifier() + ",displayName=" + node.getDisplayName().get().getText() + ",nodeClass=" + node.getNodeClass().get());
            }
        } catch (Exception e) {
            System.out.println("Browsing nodeId=" + browseRoot + " failed: " + e.getMessage());
        }
        return returnNodes;
    }


    public static String readNode(OpcUaClient client, NodeId nodeId) {
        String val = "";
        String returnString="";
        try {
            VariableNode node = client.getAddressSpace().createVariableNode(nodeId);
            DataValue value = node.readValue().get();

            CompletableFuture<DataValue> test = client.readValue(0.0, TimestampsToReturn.Both, nodeId);
            DataValue data = test.get();
            System.out.println("nodeId Object: " + nodeId.toString());
            System.out.println("DataValue Object: " + data.getValue());
            val = data.getValue().toString();
            returnString = val.replace("Variant{value=","").replace("}", "");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }
        return returnString;
    }


    public static CompletableFuture<StatusCode> writeNode2(
            final OpcUaClient client,
            final NodeId nodeId,
            final Object value) {

        return client.writeValue(nodeId, new DataValue(new Variant(value)));
    }

    public static String writeNode(OpcUaClient client, NodeId nodeId, boolean value) {

        // FIXME There should be a way to programmatically get the type from Eclipse Milo and write the variable directly using that type. As far as I can see, however, Milo only supports writing Variants which requires the conversion of a value into an object before it can be written.
        String returnString = "";
        returnString += value;
        try {
            VariableNode node = client.getAddressSpace().createVariableNode(nodeId);
            Object val = new Object();
            Object identifier = node.getDataType().get().getIdentifier();
            UInteger id = UInteger.valueOf(0);

            if(identifier instanceof UInteger) {
                id = (UInteger) identifier;
            }
            System.out.println("value passed as: "+value);
            System.out.println("nodeid passed as: "+nodeId.toString());
            DataValue data = new DataValue(new Variant(value),StatusCode.GOOD, null);
            StatusCode status = client.writeValue(nodeId, data).get();
            System.out.println("Wrote DataValue: " + data + " status: " + status);
            returnString = status.toString();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.toString());
        }
        return returnString;
    }

}
