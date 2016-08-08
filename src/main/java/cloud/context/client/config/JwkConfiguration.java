package cloud.context.client.config;

import cloud.context.client.decoder.JwtDecoder;
import cloud.context.client.decoder.NoDecoder;
import cloud.context.client.decoder.X509JwtDecoder;
import org.jose4j.keys.X509Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.security.cert.X509Certificate;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration}
 * for JWK decoders
 *
 * @author william.acosta
 * @since 0.0.1
 */

@Configuration
@ConditionalOnProperty(value = "cloud.client.context.enabled", matchIfMissing = true)
public class JwkConfiguration {
    private static Logger log = LoggerFactory.getLogger(JwkConfiguration.class);

    @Configuration
    @ConditionalOnProperty(value = "jwt.signature.verify.enabled", havingValue = "true")
    protected static class JwkDecoderConfig {

        @Value("${jwt.auth.service.x509.url}")
        private String authServiceX509Url;

        @Bean
        public X509Certificate getRemoteCertificate() throws Exception {
            RestTemplate template = new RestTemplate();
            log.info("Getting X.509 Cert from {}", authServiceX509Url);
            String certString = template.getForObject(authServiceX509Url, String.class);
            log.debug("Got X.509 Cert: {}", certString);
            X509Util x509Util = new X509Util();
            X509Certificate certificate = x509Util.fromBase64Der(certString);
            return certificate;
        }

        @Bean
        public JwtDecoder getJwtDecoder(X509Certificate certificate) {
            return new X509JwtDecoder(certificate);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "jwt.signature.verify.enabled", havingValue = "false", matchIfMissing = true)
    protected static class JwkNoDecoderConfig {
        @Bean
        public JwtDecoder getNoDecoder() {
            return new NoDecoder();
        }
    }


}
