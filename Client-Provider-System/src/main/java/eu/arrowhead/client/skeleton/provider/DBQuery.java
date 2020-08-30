package eu.arrowhead.client.skeleton.provider;

import eu.arrowhead.client.skeleton.provider.Provider_Constants;

import java.sql.*;

public class DBQuery {

    public DBQuery() {
    }

    public Connection dbconnection() throws SQLException {

        DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        String url = Provider_Constants.jdbcUrl;
        Connection conn = DriverManager.getConnection(url,Provider_Constants.UserName,Provider_Constants.Passwd);
        return conn;
    }

    public String getServiceId(String ServiceDefinition) throws SQLException {

        String serviceId="";
        String sql="SELECT id FROM arrowhead.service_definition where service_definition='"+ServiceDefinition+"';";
        Statement stmt = dbconnection().createStatement();
        ResultSet rs;
        rs=stmt.executeQuery(sql);
        while ( rs.next() ) {
            serviceId = rs.getString(1);
            //System.out.println(serviceDefinition);
        }
        dbconnection().close();
        return serviceId;
    }
    public String getSystemId(String ServiceDefinition) throws SQLException {

        String systemId="";
        String sql="SELECT system_id,service_id FROM arrowhead.service_registry where service_id=(SELECT id FROM arrowhead.service_definition where service_definition='"+ServiceDefinition+"');";
        Statement stmt = dbconnection().createStatement();
        ResultSet rs;
        rs=stmt.executeQuery(sql);
        while ( rs.next() ) {
            systemId = rs.getString(1);
            //System.out.println(serviceDefinition);
        }
        dbconnection().close();
        return systemId;
    }

}
