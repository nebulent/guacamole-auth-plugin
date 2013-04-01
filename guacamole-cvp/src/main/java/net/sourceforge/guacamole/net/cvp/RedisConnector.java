package net.sourceforge.guacamole.net.cvp;

import java.util.HashMap;
import java.util.Map;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnector extends AbstractConnector implements Connector {

    protected JedisPool server;
    protected String serverUrl = "localhost";
    protected int serverPort = 6379;
    protected String serverPassword = null;
    protected int serverMaxActive = 20; 
    protected int serverMaxIdle = 5;
    protected int serverMaxWait = 1000;
    protected boolean serverTestOnBorrow = false;
    
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

    @Override
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
