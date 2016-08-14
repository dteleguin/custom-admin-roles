package hello;

import org.keycloak.Config;
import org.keycloak.models.AdminRoles;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.provider.ProviderEvent;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import static hello.HelloAdminAuth.ROLE_VIEW_HELLO;
import static hello.HelloAdminAuth.ROLE_MANAGE_HELLO;
import java.util.List;
import org.keycloak.models.utils.KeycloakModelUtils;

/**
 * @author Dimitri Teleguin <mitya@cargosoft.ru>
 */
public class HelloResourceProviderFactory implements RealmResourceProviderFactory {

    private static final String ID = "hello";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void close() {
    }

    @Override
    public HelloResourceProvider create(KeycloakSession session) {
        return new HelloResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

        KeycloakModelUtils.runJobInTransaction(factory, (KeycloakSession session) -> {
            ClientModel client;
            List<RealmModel> realms = session.realms().getRealms();
            RealmManager manager = new RealmManager(session);
            for (RealmModel realm : realms) {
                client = realm.getMasterAdminClient();
                if (client.getRole(ROLE_VIEW_HELLO) == null && client.getRole(ROLE_MANAGE_HELLO) == null)
                    addMasterAdminRoles(manager, realm);
                if (!realm.getName().equals(Config.getAdminRealm())) {
                    client = realm.getClientByClientId(manager.getRealmAdminClientId(realm));
                    if (client.getRole(ROLE_VIEW_HELLO) == null && client.getRole(ROLE_MANAGE_HELLO) == null)
                        addRealmAdminRoles(manager, realm);
                }
            }
        });

        factory.register((ProviderEvent event) -> {
            if (event instanceof RealmModel.RealmPostCreateEvent) {
                RealmModel.RealmPostCreateEvent postCreate = (RealmModel.RealmPostCreateEvent) event;
                RealmModel realm = postCreate.getCreatedRealm();
                RealmManager manager = new RealmManager(postCreate.getKeycloakSession());
                addMasterAdminRoles(manager, realm);
                if (!realm.getName().equals(Config.getAdminRealm()))
                    addRealmAdminRoles(manager, realm);
            }
        });

    }

    private void addMasterAdminRoles(RealmManager manager, RealmModel realm) {

        RealmModel master = manager.getRealmByName(Config.getAdminRealm());
        RoleModel admin = master.getRole(AdminRoles.ADMIN);
        ClientModel client = realm.getMasterAdminClient();

        addRoles(client, admin);

    }

    private void addRealmAdminRoles(RealmManager manager, RealmModel realm) {

        ClientModel client = realm.getClientByClientId(manager.getRealmAdminClientId(realm));
        RoleModel admin = client.getRole(AdminRoles.REALM_ADMIN);

        addRoles(client, admin);

    }

    private void addRoles(ClientModel client, RoleModel parent) {

        String[] names = new String[] { ROLE_VIEW_HELLO, ROLE_MANAGE_HELLO };

        for (String name : names) {
            RoleModel role = client.addRole(name);
            role.setDescription("${role_" + name + "}");
            parent.addCompositeRole(role);
        }

    }

}
