package com.alepe.rest_demo.services;

import com.intellisrc.web.Service;
import spark.Request;

import java.util.Map;

/**
 * Provides the service to update Person objects
 * @since 2020/01/15.
 */
public class PersonUpdateService extends Service {
    public PersonUpdateService(String path) {
        setMethod(Service.Method.PUT);
        setPath(path);
        setAction((Service.ActionRequest) (Request request) -> {

            return (Map<String, Boolean>) Map.of("ok", true);
        });
    }
}
