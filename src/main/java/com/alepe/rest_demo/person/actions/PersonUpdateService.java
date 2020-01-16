package com.alepe.rest_demo.person.actions;

import com.intellisrc.web.Service;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Provides the service to update Person objects
 * @since 2020/01/15.
 */
public class PersonUpdateService extends Service {
    public PersonUpdateService(String path) {
        setMethod(Service.Method.PUT);
        setPath(path);
        setAction((ActionRequestResponse) (Request request, Response response) -> {

            return (Map<String, Boolean>) Map.of("ok", true);
        });
    }
}
