package hello;

import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.keycloak.Config;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.RealmManager;

/**
 * @author <a href="mailto:mitya@cargosoft.ru">Dimitri Teleguin</a>
 */
public class HelloResource {

    @Context
    private KeycloakSession session;

    @Context
    private HttpHeaders headers;
    
    private final AppAuthManager authManager;
    private HelloAdminAuth auth;

    public HelloResource() {
        this.authManager = new AppAuthManager();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HelloResponse hello() {

        auth.checkViewHello();

        return new HelloResponse("Hello " + auth.getUser().getUsername() + "@" + auth.getRealm().getName() + " [" + auth.getClient().getClientId() + "]");

    }

    void setupAuth() {

        String tokenString = authManager.extractAuthorizationHeaderToken(headers);

        if (tokenString == null) {
            throw new NotAuthorizedException("Bearer");
        }

        AccessToken token;

        try {
            JWSInput input = new JWSInput(tokenString);
            token = input.readJsonContent(AccessToken.class);
        } catch (JWSInputException e) {
            throw new NotAuthorizedException("Bearer token format error");
        }

        String realmName = token.getIssuer().substring(token.getIssuer().lastIndexOf('/') + 1);
        RealmManager realmManager = new RealmManager(session);
        RealmModel realm = realmManager.getRealmByName(realmName);

        if (realm == null) {
            throw new NotAuthorizedException("Unknown realm in token");
        }

        AuthenticationManager.AuthResult authResult = authManager.authenticateBearerToken(session, realm);
        if (authResult == null) {
            throw new NotAuthorizedException("Bearer");
        }

        ClientModel client =
            realm.getName().equals(Config.getAdminRealm()) ?
            session.getContext().getRealm().getMasterAdminClient() :
            realm.getClientByClientId(realmManager.getRealmAdminClientId(realm));

        UserModel user = authResult.getUser();

        auth = new HelloAdminAuth(realm, token, user, client);

    }

    public static class HelloResponse {

        private String hello;

        public HelloResponse(String hello) {
            this.hello = hello;
        }

        public String getHello() {
            return hello;
        }

        public void setHello(String hello) {
            this.hello = hello;
        }

    }

}
