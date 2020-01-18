package com.alepe.rest_demo.auth;

import com.intellisrc.core.Log;
import com.intellisrc.crypt.hash.PasswordHash;
import com.intellisrc.db.DB;
import com.intellisrc.db.Database;
import com.intellisrc.web.JSON;
import com.intellisrc.web.Service.Allow;
import com.intellisrc.web.ServiciableAuth;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * This class is used to authenticate users into the service and
 * authorize other services.
 * It uses a session object for authorization.
 * <p>
 * NOTE: Using session for authorization may not be ideal for a public API
 * as there may be clients which don't support it. For this particular
 * project we can control the client, so this is the simplest solution.
 * <p>
 * Access: Public
 *
 * @since 1/18/20.
 */
public class AuthService implements ServiciableAuth {
    public static final int version = 1;
    private static final String authTable = "auth";

    enum Level {
        GUEST, ADMIN
    }

    /**
     * Evaluates if we should allow user
     * This is used by private services
     *
     * It will verify that the IP address and user level are correct
     *
     * @return Allow interface which is used to evaluate the request
     */
    static public Allow allowAdmin() {
        return (Request request) -> {
            if (request.session() != null) {
                try {
                    //Log.s("Got cookie: %s", request.headers("Cookie"));
                    return request.ip() == request.session().attribute("ip") &&
                            Level.valueOf(request.session().attribute("level").toString().toUpperCase()) == Level.ADMIN;
                } catch (Exception e) {
                    Log.e("Error during authorization", e);
                    return false;
                }
            } else {
                return false;
            }
        };
    }

    /**
     * Returns the service path
     *
     * @return path as string
     */
    @Override
    public String getPath() {
        return String.format("/api/v%d/auth", version);
    }

    @Override
    public String getLoginPath() {
        return "/login";
    }

    @Override
    public String getLogoutPath() {
        return "/logout";
    }

    /**
     * Provides the login logic
     * @param request HTTP request
     * @param response HTTP response
     * @return a map which becomes the session
     */
    @Override
    public Map<String, Object> onLogin(Request request, Response response) {
        Level level = Level.GUEST;
        var json = JSON.decode(request.body()).toMap();
        String user = json.get("user").toString();
        char[] pass = json.get("pass").toString().toCharArray();
        if (pass.length > 0) {
            DB db = Database.connect();
            String hash = db.table(authTable).field("pass").key("user").get(user).toString();
            db.close();
            if (!hash.isEmpty()) {
                PasswordHash ph = new PasswordHash();
                ph.setPassword(pass);
                boolean login = ph.verify(hash);
                if (login) {
                    level = Level.ADMIN;
                    Log.s("[%s] logged in as %s", request.ip(), user);
                } else {
                    Log.s("[%s] Provided password is incorrect. Hash: [%s]", request.ip(), ph.BCryptNoHeader());
                }
            } else {
                Log.s("[%s] User %s not found.", request.ip(), user);
            }
        } else {
            Log.s("[%s] Password was empty", request.ip(), user);
        }
        if(level == Level.GUEST) {
            response.status(401);
        }
        return Map.of("level", level, "ip", request.ip());
    }

    /**
     * Logout
     * @param request HTTP request
     * @param response HTTP response
     * @return boolean on success.
     */
    @Override
    public boolean onLogout(Request request, Response response) {
        boolean ok = false;
        if(request.session() != null) {
            request.session().invalidate();
            ok = true;
        } else {
            Log.s("[%s] Session was empty", request.ip());
        }
        Log.s("[%s] logged out", request.ip());
        return ok;
    }
}
