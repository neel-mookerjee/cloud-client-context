package cloud.context.client.decoder;

import java.util.Map;

/**
 * Contract for JWT decoder
 *
 * @author william.acosta
 * @since 0.0.1
 */

public interface JwtDecoder {

    /**
     * Decodes the base64 encoded string and produces a context map
     * <p>Can verify signature of the token with issuer and audience information using a public key</p>
     *
     * @param jwtData JWT
     * @return Map @{@link Map} with context information
     * @throws Exception @{@link Exception}
     */
    Map<String, ?> decode(String jwtData) throws Exception;
}
