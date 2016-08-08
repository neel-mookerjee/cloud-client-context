package cloud.context.client;

/**
 * Handler to create and remove (clear) Client Context
 *
 * @author arghanil.mukhopadhya
 * @since 0.0.1
 */

public class ClientContextHandler {
    private ClientContextBuilder builder;

    public ClientContextHandler(ClientContextBuilder builder) {
        this.builder = builder;
    }

    private void createContext(String token) throws Exception {
        builder.setToken(token).build();
    }

    void removeContext() {
        ClientContext.clearTraces();
    }

    void handle(String token) throws Exception {
        createContext(token);
    }
}
