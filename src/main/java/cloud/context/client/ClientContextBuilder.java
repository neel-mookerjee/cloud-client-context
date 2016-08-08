package cloud.context.client;

import cloud.context.client.decoder.JwtDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Builder to initiate ClientContext
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

public class ClientContextBuilder {
    private static Logger log = LoggerFactory.getLogger(ClientContextBuilder.class);

    protected final JwtDecoder decoder;
    private String token;

    public ClientContextBuilder(JwtDecoder decoder) {
        this.decoder = decoder;
    }

    public ClientContextBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    void build() throws Exception {
        log.debug("executing custom client context filter");
        Map<String, String> contextMap = handleNullToken(token);
        for (ContextKeysEnum key : ClientContext.Keys.values()) {
            log.debug("initiate client context {}[{}] - {}", key, key.getKey(), contextMap.get(key.getKey()));
            ClientContext.addTrace(key, contextMap.get(key.getKey()));
        }
        log.debug("executed custom client context filter");
    }

    protected Map<String, String> handleNullToken(String token) throws Exception {
        return (Map<String, String>) decoder.decode(token);
    }
}

