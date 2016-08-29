package hello;

import javax.ws.rs.NotAuthorizedException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resources.admin.AdminAuth;

/**
 * @author <a href="mailto:mitya@cargosoft.ru">Dimitri Teleguin</a>
 */
public class HelloAdminAuth extends AdminAuth {

    public static final String ROLE_VIEW_HELLO = "view-hello";
    public static final String ROLE_MANAGE_HELLO = "manage-hello";

    public HelloAdminAuth(RealmModel realm, AccessToken token, UserModel user, ClientModel client) {
        super(realm, token, user, client);
    }

    void checkViewHello() {
        if (!hasAppRole(getClient(), ROLE_VIEW_HELLO))
            throw new NotAuthorizedException(ROLE_VIEW_HELLO);
    }

    void checkManageHello() {
        if (!hasAppRole(getClient(), ROLE_MANAGE_HELLO))
            throw new NotAuthorizedException(ROLE_MANAGE_HELLO);
    }

}
