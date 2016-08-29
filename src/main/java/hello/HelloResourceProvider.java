package hello;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

/**
 * @author <a href="mailto:mitya@cargosoft.ru">Dimitri Teleguin</a>
 */
public class HelloResourceProvider implements RealmResourceProvider {

    public HelloResourceProvider(KeycloakSession session) {
    }

    @Override
    public void close() {
    }

    @Override
    public Object getResource() {

        HelloResource hello = new HelloResource();
        ResteasyProviderFactory.getInstance().injectProperties(hello);
        hello.setupAuth();
        return hello;

    }

}
