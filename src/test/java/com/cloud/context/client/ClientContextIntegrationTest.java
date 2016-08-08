package cloud.context.client;

import cloud.context.client.config.ClientContextConfiguration;
import cloud.context.client.config.ClientContextFilterConfiguration;
import cloud.context.client.config.JwkConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author arghanil.mukhopadhya
 * @since 1.0.0.RC3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ClientContextIntegrationTest.Config.class,
        JwkConfiguration.class, ClientContextConfiguration.class,
        ClientContextFilterConfiguration.class})
@WebIntegrationTest(randomPort = true)
public class ClientContextIntegrationTest {
    private static final String TEST_SESSION_ID = "session";
    private static final String TEST_AGENT_ID = "arghanil";
    private static final String TEST_FINGER_PRINT = "bfp";
    private static final String TEST_ACQ_CHANNEL = "channel";
    private static final String TEST_SOURCE_IP = "address";
    private static final String TEST_CFT = "app";
    private static final String TEST_TOKEN = "eyJraWQiOiJhcGlLZXkiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0b2tlbi1zZXJ2aWNlIiwiYXVkIjoiYXBpLXNlcnZpY2VzIiwianRpIjoiRVNHV204QnlQWlpJT2hQUjIwMDNHUSIsImlhdCI6MTQ2MzA4OTgzMiwic3ViIjoiYXJnaGFuaWwiLCJhZ2VudElkIjoiYXJnaGFuaWwiLCJzZXNzaW9uSWQiOiJzZXNzaW9uIiwiYWNxdWlzaXRpb25DaGFubmVsIjoiY2hhbm5lbCIsImNmdCI6ImFwcCIsImNyZWF0ZURhdGUiOiIyMDE2LTA1LTEyVDIxOjUwOjMyLjk3OFoiLCJmaW5nZXJwcmludCI6ImJmcCIsInNvdXJjZUlwIjoiYWRkcmVzcyJ9.KqaxYBi6yt2u4wA-GOE_8BN48OimYMwn5sN7RATlQp2AUoFMhOkB9VOH7CDOxV2tyVrATU1OCfSIenGx5VvCBptdMPzfToLtFUk7ITdwAqeI4IeerxGVFaw26wESYTufUr_PuXRxU15S3AbGpCLWh4Wr1BSmgTxuoR1ku9wNxzRuM8ZrpFigbiHgAfn9eZgoy4LjMIWWzE0Gq4DnywlfZBCVi2jBqTCFRlJxccLWDKJxnUKQ1epEniLizDWxbwdfpIGCqYyiXBOKa0ozSLl6eOLBN87Vii9Qy4Feots0AifbcIHEmFf-2p_eE1VwGnPoE39_wiax4ad5L4B2Iye2iQ";
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    Config config;
    @Autowired
    TestRestController testRestController;

    @Before
    public void setup() {
        ClientContext.clearTraces();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void traceContextTest1() {

        // check empty ClientContext
        assertNull(ClientContext.getTrace(ClientContext.Keys.AGENT_ID));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SESSION_ID));
        assertNull(ClientContext.getTrace(ClientContext.Keys.FINGER_PRINT));
        assertNull(ClientContext.getTrace(ClientContext.Keys.ACQ_CHANNEL));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SOURCE_IP));
        assertNull(ClientContext.getTrace(ClientContext.Keys.CFT));

        // check MDC
        assertNull(MDC.get(ClientContextLogUtil.Keys.AGENT_ID.getDisplayKey()));
        assertNull(MDC.get(ClientContextLogUtil.Keys.SESSION_ID.getDisplayKey()));

        RequestEntity<?> requestEntity = RequestEntity
                .get(URI.create("http://localhost:" + this.config.port + "/service1"))
                .header(ContextTokenFilter.CONTEXT_TOKEN_HEADER_KEY, TEST_TOKEN)
                .build();

        ResponseEntity<String> resp = new RestTemplate().exchange(requestEntity,
                String.class);

        assertEquals(resp.getBody(), "[from service 1][from service 2]");

        // check empty ClientContext
        assertNull(ClientContext.getTrace(ClientContext.Keys.AGENT_ID));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SESSION_ID));
        assertNull(ClientContext.getTrace(ClientContext.Keys.FINGER_PRINT));
        assertNull(ClientContext.getTrace(ClientContext.Keys.ACQ_CHANNEL));
        assertNull(ClientContext.getTrace(ClientContext.Keys.SOURCE_IP));
        assertNull(ClientContext.getTrace(ClientContext.Keys.CFT));

        // check empty MDC
        assertNull(MDC.get(ClientContextLogUtil.Keys.AGENT_ID.getDisplayKey()));
        assertNull(MDC.get(ClientContextLogUtil.Keys.SESSION_ID.getDisplayKey()));
    }

    @Configuration
    @EnableAutoConfiguration
    static class Config
            implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
        int port;

        @Override
        public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
            this.port = event.getEmbeddedServletContainer().getPort();
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        TestRestController customRestController(Config config, RestTemplate restTemplate) {
            return new TestRestController(config, restTemplate);
        }
    }

    @RestController
    static class TestRestController {
        Config config;
        RestTemplate restTemplate;

        TestRestController(Config config, RestTemplate restTemplate) {
            this.config = config;
            this.restTemplate = restTemplate;
        }

        @RequestMapping("/service1")
        public String service1() {
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

            RequestEntity<?> requestEntity = RequestEntity
                    .get(URI.create("http://localhost:" + this.config.port + "/service2")).build();
            ResponseEntity<String> resp = restTemplate.exchange(requestEntity,
                    String.class);
            return "[from service 1]" + resp.getBody();
        }

        @RequestMapping("/service2")
        public String service2() {
            // verify ClientContext
            assertNull(ClientContext.getTrace(ClientContext.Keys.AGENT_ID));
            assertNull(ClientContext.getTrace(ClientContext.Keys.ACQ_CHANNEL));
            assertNull(ClientContext.getTrace(ClientContext.Keys.CFT));
            assertNull(ClientContext.getTrace(ClientContext.Keys.FINGER_PRINT));
            assertNull(ClientContext.getTrace(ClientContext.Keys.SOURCE_IP));
            assertNull(ClientContext.getTrace(ClientContext.Keys.SESSION_ID));

            // verify MDC
            assertEquals(ClientContextLogUtil.EMPTY_TRACE_DEFAULT_LOG_VALUE, MDC.get(ClientContextLogUtil.Keys.AGENT_ID.getDisplayKey()));
            assertEquals(ClientContextLogUtil.EMPTY_TRACE_DEFAULT_LOG_VALUE, MDC.get(ClientContextLogUtil.Keys.SESSION_ID.getDisplayKey()));
            return "[from service 2]";
        }
    }
}