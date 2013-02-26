package net.sourceforge.guacamole.net.cvp;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnector {

    protected JedisPool server;
    protected String serverUrl = "localhost";
    protected int serverPort = 6379;
    protected String serverPassword = null;
    protected int serverMaxActive = 20; 
    protected int serverMaxIdle = 5;
    protected int serverMaxWait = 1000;
    protected boolean serverTestOnBorrow = false;
    
    protected static String PROP_KEY_HOSTNAME = "hostname";
    protected static String PROP_KEY_PORT = "port";
    protected static String PROP_KEY_PASSWORD = "password";
    protected ObjectMapper objectMapper = new ObjectMapper();
    protected Logger logger = LoggerFactory.getLogger(RedisConnector.class);

    public void init() {
	JedisPoolConfig config = new JedisPoolConfig();
	config.setMaxActive(serverMaxActive);
	config.setMaxIdle(serverMaxIdle);
	config.setMaxWait(serverMaxWait);
	config.setTestOnBorrow(serverTestOnBorrow);
	server = new JedisPool(config, serverUrl, serverPort, 1000, serverPassword);
//	System.out.println("REDIS started ok: " + serverUrl + ":" + serverPort);
    }

    public void destroy() {
	server.destroy();
    }

    public Map<String, GuacamoleConfiguration> findConfigurations(String key) {
	Jedis jedis = server.getResource();
	String value = null;
	try {
	    value = jedis.get(key);
	    jedis.del(key);
	} finally {
	    server.returnResource(jedis);
	}
	logger.debug(key + " -> " + value);
	AuthResponse authResponse = convert(value);
	if (authResponse != null) {
	    Map<String, GuacamoleConfiguration> configs = new HashMap<String, GuacamoleConfiguration>();
	    for (Connection con : authResponse.getConnections()) {
		GuacamoleConfiguration config = new GuacamoleConfiguration();
		config.setProtocol(con.getProtocol());
		config.setParameter(PROP_KEY_HOSTNAME, con.getHost());
		config.setParameter(PROP_KEY_PORT, String.valueOf(con.getPort()));
		config.setParameter(PROP_KEY_PASSWORD, con.getPassword());

		configs.put(con.getName(), config);

		logger.info("Name:" + con.getName());
		logger.info("Host:" + con.getHost());
		logger.info("Protocol:" + con.getProtocol());
		logger.info("Port:" + con.getPort());
		//logger.info("Password:" + con.getPassword());
	    }
	    return configs;
	}
	return null;
    }

    public AuthResponse convert(String value) {
	if (value != null) {
	    try {
		AuthResponse rv = objectMapper.readValue(value, AuthResponse.class);
		return rv;
	    } catch (IOException ioe) {
		logger.error("Error in deserializing json", ioe);
	    }
	}
	return null;
    }

    public JedisPool getServer() {
	return server;
    }

    public void setServerUrl(String serverUrl) {
	this.serverUrl = serverUrl;
    }

    public void setServerPort(int serverPort) {
	this.serverPort = serverPort;
    }

    public void setServerPassword(String serverPassword) {
	this.serverPassword = serverPassword;
    }

    public void setServerMaxActive(int serverMaxActive) {
	this.serverMaxActive = serverMaxActive;
    }

    public void setServerMaxIdle(int serverMaxIdle) {
	this.serverMaxIdle = serverMaxIdle;
    }

    public void setServerMaxWait(int serverMaxWait) {
	this.serverMaxWait = serverMaxWait;
    }

    public void setServerTestOnBorrow(boolean serverTestOnBorrow) {
	this.serverTestOnBorrow = serverTestOnBorrow;
    }
    
}
