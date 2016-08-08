package cloud.context.client;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for gathering and reporting Client Context information.
 * <ul>
 * <li><b>SESSION_ID</b> - Client session Id</li>
 * <li><b>AGENT_ID</b> - Agent Id of the logged in agent who's performing an operation or a transaction</li>
 * <li><b>CFT</b> - Client Facing Tool used by the agent for the operation</li>
 * <li><b>ACQ_CHANNEL</b> - Acquisition Channel</li>
 * <li><b>FINGER_PRINT</b> - Browser finger print</li>
 * <li><b>SOURCE_IP</b> - IP of the agent machine</li>
 * </ul>
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

public final class ClientContext {
    private static InheritableThreadLocal<Map<String, String>> TRACES =
            new InheritableThreadLocal<Map<String, String>>() {
                @Override
                protected Map<String, String> initialValue() {
                    return new HashMap<>();
                }
            };

    private ClientContext() {
    }

    /*
     * clear context and clean MDC
     */
    static void clearTraces() {
        TRACES.get().clear();
        TRACES.remove();
        for (ContextLogKeysEnum keyToLog : ClientContextLogUtil.Keys.values()) {
            ClientContextLogUtil.logger.debug("removing from MDC {}[{}] - {}", keyToLog, keyToLog.getDisplayKey(), MDC.get(keyToLog.getDisplayKey()));
            MDC.remove(keyToLog.getDisplayKey());
        }
    }


    /**
     * Adds values to @{@link ClientContext} against the keys bounded in @{@link Keys}
     * <p>Adds the attribute to MDC</p>
     *
     * @param key   a key from @{@link Keys}
     * @param value value fro trace against the key
     */
    static void addTrace(ContextKeysEnum key, String value) {
        TRACES.get().put(key.getKey(), value);
        for (ContextLogKeysEnum keyToLog : ClientContextLogUtil.Keys.values()) {
            if (keyToLog.getKey().equals(key.getKey())) {
                ClientContextLogUtil.logger.debug("adding to MDC {}[{}] - {}", key, keyToLog.getDisplayKey(), ClientContext.getTrace(key));
                MDC.put(keyToLog.getDisplayKey(), StringUtils.isEmpty(value) ? ClientContextLogUtil.EMPTY_TRACE_DEFAULT_LOG_VALUE : value);
            }
        }
        ClientContextLogUtil.logger.debug("checking MDC");

    }

    /**
     * Return info from @{@link ClientContext} against a key from @{@link Keys}
     *
     * @param key a key from @{@link Keys}
     * @return String @{@link String} that contains trace info against the key
     */
    public static String getTrace(ContextKeysEnum key) {
        return TRACES.get().get(key.getKey());
    }

    public enum Keys implements ContextKeysEnum {
        SESSION_ID("sessionId"),
        AGENT_ID("agentId"),
        CFT("cft"),
        ACQ_CHANNEL("acquisitionChannel"),
        FINGER_PRINT("fingerprint"),
        SOURCE_IP("sourceIp");

        String key;

        Keys(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

}
