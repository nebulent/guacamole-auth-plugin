package net.sourceforge.guacamole.net.cvp;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import redis.clients.jedis.Jedis;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context.xml"})
public class RedisConnectorTest {

    @Autowired
    protected RedisConnector connector;

    protected Resource sampleResponse1 = new ClassPathResource("sample-response1.js");

//    @Test
    public void jsonConverterTest(){
	try{
	    String response = FileCopyUtils.copyToString(new FileReader(sampleResponse1.getFile()));
	    System.out.println(response);
	    AuthResponse auth = connector.convert(response);  
	    System.out.println("Test passed ok");
	}catch(IOException ioe){
	    ioe.printStackTrace();
	}
    }
    
    @Test
    public void connectorTest() {
	Jedis jedis = null;
	try {
	    jedis = connector.getServer().getResource();
	    System.out.println("PING: " + jedis.ping());
	    jedis.set("testkey", FileCopyUtils.copyToString(new FileReader(sampleResponse1.getFile())));	    
	    Map<String, GuacamoleConfiguration> configs = connector.findConfigurations("testkey");
	    Assert.notNull(configs, "Invalid configs map. Has to be not null");
	    Assert.isTrue(configs.size() == 2, "Invalid configs count. Has to be 2");
	    System.out.println("Test passed ok");
	} catch (Exception e) {
	    e.printStackTrace();
	}finally{
	    connector.getServer().returnResource(jedis);
	}
    }
}
