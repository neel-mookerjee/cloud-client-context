package cloud.context.client.config;

import cloud.context.client.ClientContextBuilder;
import cloud.context.client.ClientContextExtendedBuilder;
import cloud.context.client.ClientContextHandler;
import cloud.context.client.decoder.JwtDecoder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration}
 * for the Context Builder components
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

@Configuration
@AutoConfigureAfter(JwkConfiguration.class)
public class ClientContextConfiguration {

    @Bean
    @ConditionalOnProperty(value = "jwt.signature.verify.ignoreNullToken", havingValue = "false")
    public ClientContextBuilder clientContextBuilder(JwtDecoder decoder) {
        return new ClientContextBuilder(decoder);
    }

    @Bean
    @ConditionalOnProperty(value = "jwt.signature.verify.ignoreNullToken", havingValue = "true", matchIfMissing = true)
    public ClientContextBuilder clientContextExtendedBuilder(JwtDecoder decoder) {
        return new ClientContextExtendedBuilder(decoder);
    }

    @Bean
    public ClientContextHandler clientContextHandler(ClientContextBuilder builder) {
        return new ClientContextHandler(builder);
    }
}