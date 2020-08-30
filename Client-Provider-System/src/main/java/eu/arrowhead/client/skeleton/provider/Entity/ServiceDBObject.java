package eu.arrowhead.client.skeleton.provider.Entity;

import eu.arrowhead.client.skeleton.provider.DBQuery;

import java.sql.SQLException;

public class ServiceDBObject {

    private String serviceId;
    private String ServiceDefinition;
    private String SystemID;

    public ServiceDBObject(String serviceDefinition) throws SQLException {
        DBQuery db= new DBQuery();
        this.serviceId = db.getServiceId(serviceDefinition);
        this.SystemID = db.getSystemId(serviceDefinition);
        this.ServiceDefinition = serviceDefinition;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceDefinition() {
        return ServiceDefinition;
    }

    public void setServiceDefinition(String serviceDefinition) {
        ServiceDefinition = serviceDefinition;
    }

    public String getSystemID() {
        return SystemID;
    }

    public void setSystemID(String systemID) {
        SystemID = systemID;
    }
}
