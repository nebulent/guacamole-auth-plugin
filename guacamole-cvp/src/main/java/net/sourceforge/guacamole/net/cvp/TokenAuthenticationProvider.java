package net.sourceforge.guacamole.net.cvp;

import java.util.Map;
import net.sourceforge.guacamole.GuacamoleException;
import net.sourceforge.guacamole.net.auth.AuthenticationProvider;
import net.sourceforge.guacamole.net.auth.Credentials;
import net.sourceforge.guacamole.protocol.GuacamoleConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TokenAuthenticationProvider implements AuthenticationProvider {

    protected static Connector connector = new MySQLConnector();
    protected TokenParser parser = new TokenParser();
    protected static String PARAM_TOKEN = "username";
    
    protected final static ApplicationContext applicationContext;
    
    static {
        applicationContext = new ClassPathXmlApplicationContext(new String[]{"classpath:guacamole-cvp-context.xml"});
	connector = applicationContext.getBean("connector", MySQLConnector.class);
    }    

    @Override
    public Map<String, GuacamoleConfiguration> getAuthorizedConfigurations(Credentials credentials) throws GuacamoleException {
	if (credentials != null) {
	    String token = parser.parse(credentials.getRequest().getParameter(PARAM_TOKEN));
	    return connector.findConfigurations(token);
	}
	return null;
    }
    
}
