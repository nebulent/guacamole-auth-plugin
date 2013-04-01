package net.sourceforge.guacamole.net.cvp;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnector {

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected AuthResponse convert(String value) {
        if (value != null) {
            try {
                return objectMapper.readValue(value, AuthResponse.class);
            } catch (IOException ioe) {
                logger.error("Error in deserializing json", ioe);
            }
        }
        return null;
    }
}
