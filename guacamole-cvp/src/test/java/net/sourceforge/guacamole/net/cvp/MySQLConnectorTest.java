package net.sourceforge.guacamole.net.cvp;

import java.util.Map;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context.xml"})
public class MySQLConnectorTest {

    @Autowired
    protected Connector connector;

    @Test
    public void connectorTest() {
        try {
            Map<String, GuacamoleConfiguration> configs = connector.findConfigurations("jopa");
            Assert.notNull(configs, "Invalid configs map. Has to be not null");
            Assert.isTrue(configs.size() == 2, "Invalid configs count. Has to be 2");
            System.out.println("Test passed ok");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
