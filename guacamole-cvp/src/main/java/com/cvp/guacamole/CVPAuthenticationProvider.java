package com.cvp.guacamole;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import net.sourceforge.guacamole.GuacamoleException;
import net.sourceforge.guacamole.net.auth.AuthenticationProvider;
import net.sourceforge.guacamole.net.auth.Credentials;
import net.sourceforge.guacamole.properties.GuacamoleProperties;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;

public class CVPAuthenticationProvider implements AuthenticationProvider {
	 
	private Logger logger = LoggerFactory.getLogger(CVPAuthenticationProvider.class);
	 
	@Override
	public Map<String, GuacamoleConfiguration> getAuthorizedConfigurations(
			Credentials credentials) throws GuacamoleException {
		
		if ((credentials == null)||(credentials.getUsername() == null)||(credentials.getPassword() == null)) {
			return null;
		}
		
		// load properties
		Properties properties = new Properties();

		try {
			InputStream stream = GuacamoleProperties.class.getResourceAsStream("/cvp.properties");
			if (stream == null) throw new IOException("Resource /cvp.properties not found.");

			// Load properties, always close stream
			try { properties.load(stream); }
			finally { stream.close(); }

		}
		catch (IOException e) {
			throw new GuacamoleException("Error reading cvp.properties", e);
		}

		String authEndpointAddress = properties.getProperty("auth-endpoint-address");
		if ((authEndpointAddress == null)||(authEndpointAddress.isEmpty() == true)) {
			throw new GuacamoleException("Error reading auth-endpoint-address parameter", null);
		}
		
		List<Object> providers = new ArrayList<Object>();
		providers.add( new JacksonJaxbJsonProvider() );
	   
		WebClient client = WebClient.create(authEndpointAddress, providers);
		client = client
				.accept("application/json")
				.type("application/json");
	   
		AuthRequest authRequest = new AuthRequest();
		authRequest.setUsername(credentials.getUsername());
		authRequest.setPassword(credentials.getPassword());
		
		AuthResponse authResponse = null;
		try {
			authResponse = client.post(authRequest, AuthResponse.class);
		}
		catch (ServerWebApplicationException e) {
			logger.info("Status "+e.getStatus());
			return null;
		}
		
		if ((authResponse == null)||(authResponse.getConnections() == null)||(authResponse.getConnections().size() == 0)) {
			return null;
		}
		
		logger.info("Username:" + authResponse.getUsername());
		//logger.info("AAA2:" + authResponse.getConnections());
		//logger.info("AAA3:" + authResponse.getConnections().size());
		
		Map<String, GuacamoleConfiguration> configs = new HashMap<String, GuacamoleConfiguration>();
		for (Connection con : authResponse.getConnections()) {
			GuacamoleConfiguration config = new GuacamoleConfiguration();
			config.setProtocol(con.getProtocol());
			config.setParameter("hostname", con.getHost());
			config.setParameter("port", ""+con.getPort());
			config.setParameter("password", con.getPassword());
			
			configs.put(con.getName(), config);
			
			logger.info("Name:" + con.getName());
			logger.info("Host:" + con.getHost());
			logger.info("Protocol:" + con.getProtocol());
			logger.info("Port:" + con.getPort());
			//logger.info("Password:" + con.getPassword());
		}

		return configs;
	}
	
	public static void main( String[] args ) {
		Credentials credentials = new Credentials();
		credentials.setUsername("jora@nebulent.com");
		credentials.setPassword("$2a$10$2lXDMzkkAM9PgIJnBbiAXOKFbORXTbVY8MeOFppNd83pncWKJsDZy");
		//credentials.setPassword("$2a$10$2lXDMzkkAM9PgI");
		
		CVPAuthenticationProvider prov = new CVPAuthenticationProvider();
		try {
			Map<String, GuacamoleConfiguration> map = prov.getAuthorizedConfigurations(credentials);
			System.out.println(map);
		} catch (GuacamoleException e) {
			e.printStackTrace();
		}
		
	}

}
