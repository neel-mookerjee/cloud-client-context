package cloud.context.client;

import cloud.context.client.decoder.JwtDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Temporary - This extended Builder to initiate ClientContext ignores null token (creates an empty @{@link ClientContext})
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

public class ClientContextExtendedBuilder extends ClientContextBuilder {
    private static Logger log = LoggerFactory.getLogger(ClientContextExtendedBuilder.class);

    public ClientContextExtendedBuilder(JwtDecoder decoder) {
        super(decoder);
    }

    /**
     * Logs a warning message and creates an empty @{@link ClientContext}
     *
     * @param token JWT token @{@link String}
     * @return Map @{@link Map} with context info
     * @throws Exception @{@link Exception} if decoding fails
     */
    protected Map<String, String> handleNullToken(String token) throws Exception {
        if (StringUtils.isEmpty(token)) {
            log.warn("Context token not received; client context is null.");
            return new HashMap<>();
        }
        return (Map<String, String>) decoder.decode(token);
    }
}

