package cloud.context.client.decoder;

import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Decoder that skips all validations
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

public class NoDecoder implements JwtDecoder {
    private static Logger log = LoggerFactory.getLogger(NoDecoder.class);

    @Override
    public Map<String, ?> decode(String jwtData) throws Exception {
        log.warn("Signature not verified");
        JwtConsumer jwtConsumer;
        jwtConsumer = new JwtConsumerBuilder()
                .setSkipSignatureVerification()
                .setSkipAllValidators()
                .build();

        return jwtConsumer.processToClaims(jwtData).getClaimsMap();
    }
}
