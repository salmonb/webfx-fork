package mongoose.auth.authz;

import naga.framework.router.auth.authz.RouteAuthorityParser;
import naga.framework.router.auth.authz.RouteAuthorizationParser;
import naga.platform.services.auth.spi.authz.AuthorizationRegistry;
import naga.platform.services.auth.spi.authz.LoadedAuthorizationsUser;

/**
 * @author Bruno Salmon
 */
public class MongooseUser extends LoadedAuthorizationsUser {

    private final Object userAccountId;

    public MongooseUser(Object userAccountId) {
        super(DEFAULT_AUTHORIZATION);
        this.userAccountId = userAccountId;
    }

    public Object getUserAccountId() {
        return userAccountId;
    }

    private static AuthorizationRegistry DEFAULT_AUTHORIZATION = new AuthorizationRegistry();
    static {
        DEFAULT_AUTHORIZATION.addAuthorityParser(new RouteAuthorityParser());
        DEFAULT_AUTHORIZATION.addAuthorizationParser(new RouteAuthorizationParser());
        DEFAULT_AUTHORIZATION.registerAuthorization("route: /monitor");
        DEFAULT_AUTHORIZATION.registerAuthorization("route: /tester");
        DEFAULT_AUTHORIZATION.registerAuthorization("route: /bookings/*");
    }
}