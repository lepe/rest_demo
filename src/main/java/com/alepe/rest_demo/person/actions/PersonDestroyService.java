package com.alepe.rest_demo.person.actions;

import com.alepe.rest_demo.auth.AuthService;
import com.alepe.rest_demo.person.Person;
import com.intellisrc.core.Log;
import com.intellisrc.web.Service;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Provides the service to destroy Person objects
 * Access: Private
 * @since 2020/01/15.
 */
public class PersonDestroyService extends Service {
    public PersonDestroyService(String path) {
        setMethod(Service.Method.DELETE);
        setPath(path);
        setAllow(AuthService.allowAdmin()); //Only authorized users can perform this action
        setAction((ActionRequestResponse) (Request request, Response response) -> {
            boolean ok = false;
            String err = "";
            int id;
            try {
                id = Integer.parseInt(request.params("id"));
                Person person = new Person(id);
                ok = person.delete();
            } catch(NumberFormatException | Person.IllegalPersonException exception) {
                response.status(400);
                err = "Person was not found";
                Log.w(err);
            }
            return (Map<String, ?>) Map.of("ok", ok, "err", err);
        });
    }
}
