package cloud.context.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log Context util that maintains the keys for parameters that needs to be logged as part of MDC
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

public class ClientContextLogUtil {
    static final String EMPTY_TRACE_DEFAULT_LOG_VALUE = "NONE";
    static Logger logger = LoggerFactory.getLogger(ClientContextLogUtil.class);

    enum Keys implements ContextLogKeysEnum {
        /*
        SESSION_ID(ClientContext.Keys.SESSION_ID.getKey()),
        AGENT_ID(ClientContext.Keys.AGENT_ID.getKey()),
        CFT(ClientContext.Keys.CFT.getKey()),
        ACQ_CHANNEL(ClientContext.Keys.ACQ_CHANNEL.getKey()),
        FINGER_PRINT(ClientContext.Keys.FINGER_PRINT.getKey()),
        SOURCE_IP(ClientContext.Keys.SOURCE_IP.getKey());
        */
        SESSION_ID(ClientContext.Keys.SESSION_ID.getKey(), "SessionId"),
        AGENT_ID(ClientContext.Keys.AGENT_ID.getKey(), "AgentId");
        String key;
        String displayKey;

        Keys(String key, String displayKey) {
            this.key = key;
            this.displayKey = displayKey;
        }

        public String getKey() {
            return this.key;
        }

        public String getDisplayKey() {
            return displayKey;
        }
    }

}
