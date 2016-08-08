package cloud.context.client.decoder;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Decoder that verifies signature with an x509 public key
 *
 * @author william.acosta
 * @since 0.0.1
 */

public class X509JwtDecoder implements JwtDecoder {

    private static Logger log = LoggerFactory.getLogger(X509JwtDecoder.class);

    private X509Certificate certificate;

    @Value("${jwt.expected.issuer}")
    private String expectIssuer;

    @Value("${jwt.expected.audience}")
    private String expectAudience;

    public X509JwtDecoder(X509Certificate cert) {
        this.certificate = cert;
    }

    @Override
    public Map<String, ?> decode(String jwtData) throws InvalidJwtException {
        PublicKey x509PublicKey = certificate.getPublicKey();
        JwtConsumer jwtConsumer;
        jwtConsumer = new JwtConsumerBuilder()
                .setExpectedIssuer(expectIssuer)
                .setExpectedAudience(expectAudience)
                .setVerificationKey(x509PublicKey)
                .build();
        return jwtConsumer.processToClaims(jwtData).getClaimsMap();
    }
}
