package com.alepe.rest_demo.services;

import com.alepe.rest_demo.Person;
import com.intellisrc.core.Log;
import com.intellisrc.web.Service;
import com.intellisrc.web.Service.ActionRequest;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Provides the service to destroy Person objects
 * @since 2020/01/15.
 */
public class PersonDestroyService extends Service {
    public PersonDestroyService(String path) {
        setMethod(Service.Method.DELETE);
        setPath(path);
        setAction((ActionRequestResponse) (Request request, Response response) -> {
            boolean ok = false;
            int id;
            try {
                id = Integer.parseInt(request.queryParams("id"));
                if(id > 0) {
                    Person person = new Person(id);
                    person.delete();
                } else {
                    Log.w("ID was zero.");
                }
            } catch(NumberFormatException | Person.IllegalPersonException exception) {
                Log.w("Person was not found.");
            }
            return (Map<String, Boolean>) Map.of("ok", ok);
        });
    }
}
