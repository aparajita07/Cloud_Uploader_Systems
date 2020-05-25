package eu.arrowhead.client.skeleton.provider.Entity;

import eu.arrowhead.client.skeleton.provider.JSONReader;
import eu.arrowhead.client.skeleton.provider.OPC_UA.OPCUAConnection;
import eu.arrowhead.client.skeleton.provider.OPC_UA.OPCUAService;
import org.jose4j.json.internal.json_simple.parser.ParseException;

import java.io.IOException;
import java.util.Date;

public class Device {
    private String id;
    private String Definition;
    private String Value;
    private Date timestamp;
    private long time;

    public Device() {
    }

    public Device(String id, OPCUAConnection connection) throws IOException, ParseException {
        this.id = id;
        JSONReader reader= new JSONReader(id);
        OPCUAService service= new OPCUAService();
        this.Definition = reader.getDefinition();
        this.Value = service.read(id,connection);
        this.timestamp= java.util.Calendar.getInstance().getTime();
        this.time= System.currentTimeMillis()/1000;
    }
    public Device(String id) throws IOException, ParseException {
        this.id = id;
        JSONReader reader= new JSONReader(id);
        OPCUAService service= new OPCUAService();
        this.Definition = reader.getDefinition();
        this.Value = "false";
        //this.Value = service.read(id,connection);
        this.timestamp= java.util.Calendar.getInstance().getTime();
        this.time= System.currentTimeMillis()/1000;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefinition() {
        return Definition;
    }

    public void setDefinition(String definition) {
        Definition = definition;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
    public Date getDate() {
        return timestamp;
    }

    public void setDate(Date timestamp) {
        this.timestamp = timestamp;
    }
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
