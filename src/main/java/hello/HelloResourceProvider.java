package hello;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

/**
 * @author <a href="mailto:mitya@cargosoft.ru">Dimitri Teleguin</a>
 */
public class HelloResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    HelloResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void close() {
    }

    @Override
    public Object getResource() {
        return new HelloResource(session);
    }

}
