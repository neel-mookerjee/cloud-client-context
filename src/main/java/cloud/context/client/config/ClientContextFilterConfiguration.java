package cloud.context.client.config;

import cloud.context.client.ClientContextHandler;
import cloud.context.client.ContextTokenFilter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto-configuration}
 * for the token filter and registration
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

@Configuration
@AutoConfigureAfter(ClientContextConfiguration.class)
public class ClientContextFilterConfiguration {
    @Bean
    public ContextTokenFilter getContextTokenFilter(ClientContextHandler handler) {
        return new ContextTokenFilter(handler);
    }

    @Bean
    public FilterRegistrationBean registerContextTokenFilter(ContextTokenFilter contextTokenFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(contextTokenFilter);
        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return registration;
    }
}