package cloud.context.client;

import org.jose4j.jwt.consumer.InvalidJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * {@link org.springframework.web.filter.GenericFilterBean Generic Filter Bean}
 * for applying the client context JWT extraction, verification and Client Context initiation
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

public class ContextTokenFilter extends GenericFilterBean {
    public static final String CONTEXT_TOKEN_HEADER_KEY = "X-Context-Token";
    private static Logger log = LoggerFactory.getLogger(ContextTokenFilter.class);
    private ClientContextHandler handler;

    public ContextTokenFilter(ClientContextHandler handler) {
        this.handler = handler;
    }

    public void doFilter(final ServletRequest request,
                         final ServletResponse response, final FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        log.debug("request URL for filter : {} ", httpRequest.getRequestURL());
        try {
            String token = httpRequest.getHeader(CONTEXT_TOKEN_HEADER_KEY);
            log.debug("Read Context Token from header: {}", token);
            handler.handle(token);
            filterChain.doFilter(request, response);
        } catch (InvalidJwtException e) {
            log.error("Error verifying JWT: {}", e);
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new SecurityException("JWT claim failed!", e);
        } catch (Exception e) {
            log.error("Unknown error", e);
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new SecurityException("Unknown error", e);
        } finally {
            /*
             * Must clean up the Client Context
             */
            handler.removeContext();
        }
    }
}