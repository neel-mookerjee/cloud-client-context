package cloud.context.client;

import cloud.context.client.decoder.NoDecoder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */
public class ClientContextFilterUnitTest {
    private static final String TEST_SESSION_ID = "session";
    private static final String TEST_AGENT_ID = "arghanil";
    private static final String TEST_FINGER_PRINT = "bfp";
    private static final String TEST_ACQ_CHANNEL = "channel";
    private static final String TEST_SOURCE_IP = "address";
    private static final String TEST_CFT = "app";
    private static final String TEST_TOKEN = "eyJraWQiOiJhcGlLZXkiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0b2tlbi1zZXJ2aWNlIiwiYXVkIjoiYXBpLXNlcnZpY2VzIiwianRpIjoiRVNHV204QnlQWlpJT2hQUjIwMDNHUSIsImlhdCI6MTQ2MzA4OTgzMiwic3ViIjoiYXJnaGFuaWwiLCJhZ2VudElkIjoiYXJnaGFuaWwiLCJzZXNzaW9uSWQiOiJzZXNzaW9uIiwiYWNxdWlzaXRpb25DaGFubmVsIjoiY2hhbm5lbCIsImNmdCI6ImFwcCIsImNyZWF0ZURhdGUiOiIyMDE2LTA1LTEyVDIxOjUwOjMyLjk3OFoiLCJmaW5nZXJwcmludCI6ImJmcCIsInNvdXJjZUlwIjoiYWRkcmVzcyJ9.KqaxYBi6yt2u4wA-GOE_8BN48OimYMwn5sN7RATlQp2AUoFMhOkB9VOH7CDOxV2tyVrATU1OCfSIenGx5VvCBptdMPzfToLtFUk7ITdwAqeI4IeerxGVFaw26wESYTufUr_PuXRxU15S3AbGpCLWh4Wr1BSmgTxuoR1ku9wNxzRuM8ZrpFigbiHgAfn9eZgoy4LjMIWWzE0Gq4DnywlfZBCVi2jBqTCFRlJxccLWDKJxnUKQ1epEniLizDWxbwdfpIGCqYyiXBOKa0ozSLl6eOLBN87Vii9Qy4Feots0AifbcIHEmFf-2p_eE1VwGnPoE39_wiax4ad5L4B2Iye2iQ";

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @Before
    public void setUp() throws Exception {
        request = new MockHttpServletRequest();
        request.setMethod(WebContentGenerator.METHOD_GET);
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain(new MockServlet());
    }

    @Test
    public void contextTokenFilterTest1() throws Exception {
        ContextTokenFilter filter = new ContextTokenFilter(new ClientContextHandler(new ClientContextBuilder(new NoDecoder())));

        // check empty context
        assertNull(ClientContext.getTrace(ClientContext.Keys.AGENT_ID));
        assertNull(ClientContext.getTrace(ClientContext.Keys.ACQ_CHANNEL));
        assertNull(ClientContext.getTrace(ClientContext.Keys.CFT));
        assertNull(ClientContext.getTrace(ClientContext.Keys.FINGER_PRINT));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SOURCE_IP));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SESSION_ID));

        // check MDC - no trace
        assertNull(MDC.get(ClientContextLogUtil.Keys.AGENT_ID.getDisplayKey()));
        assertNull(MDC.get(ClientContextLogUtil.Keys.SESSION_ID.getDisplayKey()));

        // add token to header
        request.addHeader(ContextTokenFilter.CONTEXT_TOKEN_HEADER_KEY, TEST_TOKEN);

        filter.doFilter(request, response, filterChain);

        // check context empty after filter
        assertNull(ClientContext.getTrace(ClientContext.Keys.AGENT_ID));
        assertNull(ClientContext.getTrace(ClientContext.Keys.ACQ_CHANNEL));
        assertNull(ClientContext.getTrace(ClientContext.Keys.CFT));
        assertNull(ClientContext.getTrace(ClientContext.Keys.FINGER_PRINT));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SOURCE_IP));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SESSION_ID));

        // check MDC - no trace
        assertNull(MDC.get(ClientContextLogUtil.Keys.AGENT_ID.getDisplayKey()));
        assertNull(MDC.get(ClientContextLogUtil.Keys.SESSION_ID.getDisplayKey()));
    }

    private static class MockServlet extends HttpServlet {
        private static Logger log = LoggerFactory.getLogger(MockServlet.class);

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            // verify ClientContext
            assertEquals(TEST_AGENT_ID, ClientContext.getTrace(ClientContext.Keys.AGENT_ID));
            assertEquals(TEST_ACQ_CHANNEL, ClientContext.getTrace(ClientContext.Keys.ACQ_CHANNEL));
            assertEquals(TEST_CFT, ClientContext.getTrace(ClientContext.Keys.CFT));
            assertEquals(TEST_FINGER_PRINT, ClientContext.getTrace(ClientContext.Keys.FINGER_PRINT));
            assertEquals(TEST_SOURCE_IP, ClientContext.getTrace(ClientContext.Keys.SOURCE_IP));
            assertEquals(TEST_SESSION_ID, ClientContext.getTrace(ClientContext.Keys.SESSION_ID));

            // verify MDC
            assertEquals(TEST_AGENT_ID, MDC.get(ClientContextLogUtil.Keys.AGENT_ID.getDisplayKey()));
            assertEquals(TEST_SESSION_ID, MDC.get(ClientContextLogUtil.Keys.SESSION_ID.getDisplayKey()));
        }
    }
}
