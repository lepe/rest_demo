package com.alepe.rest_demo.person.actions;

import com.alepe.rest_demo.person.Person;
import com.intellisrc.core.Log;
import com.intellisrc.web.JSON;
import com.intellisrc.web.Service;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the service to create Person objects
 * @since 2020/01/15.
 */
public class PersonCreateService extends Service {
    public PersonCreateService(String path) {
        // Create Person service:
        setMethod(Service.Method.POST);
        setPath(path);
        setAction((ActionRequestResponse) (Request request, Response response) -> {
            int id = 0;
            boolean ok = false;
            String err = "";
            String body = request.body().trim();
            if(! body.isEmpty()) {
                // Import json and convert it into HashMap with string keys:
                var json = JSON.decode(body).toMap();
                if(!json.isEmpty()) {
                    HashMap<String, Object> map = new HashMap<>();
                    for (var key : json.keySet()) {
                        map.put(key.toString(), json.get(key));
                    }
                    map.put("id", 0); //Prevent it from being set.

                    // Import map into Person and clone it:
                    Person personMap;
                    try {
                        personMap = Person.fromMap(map);
                        id = Person.clone(personMap).getId();
                        ok = true;
                    } catch (Person.IllegalPersonException exception) {
                        response.status(400);
                        err = "Error while creating object.";
                    }
                } else {
                    response.status(400);
                    err = "Data was empty or invalid. Please check the request.";
                }
            } else {
                response.status(400);
                err = "Body was empty";
            }
            // Log in case of error
            if(! err.isEmpty()) {
                Log.w("err");
            }
            return (Map<String, ?>) Map.of("ok", ok, "id", id, "err", err);
        });
    }
}
