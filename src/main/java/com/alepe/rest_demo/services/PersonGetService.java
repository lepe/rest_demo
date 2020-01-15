package com.alepe.rest_demo.services;

import com.alepe.rest_demo.Person;
import com.intellisrc.core.Log;
import com.intellisrc.web.Service;
import spark.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the service to retrieve Person objects
 * @since 2020/01/15.
 */
public class PersonGetService extends Service {
    public PersonGetService(String path) {
        setPath(path);
        setAction((ActionRequest) (Request request) -> {
            int id = Integer.parseInt(request.params("id"));
            Map<String, Object> response = new HashMap<>();
            try {
                Person person = new Person(id);
                response = person.toMap();
            } catch (Person.IllegalPersonException e) {
                Log.w("Person with id: %d not found", id);
            }
            return response;
        });
    }
}
