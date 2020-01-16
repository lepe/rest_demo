package com.alepe.rest_demo.services;

import com.alepe.rest_demo.Person;
import com.intellisrc.core.Log;
import com.intellisrc.web.Service;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides the service to retrieve Person objects
 * @since 2020/01/15.
 */
public class PersonGetService extends Service {
    public PersonGetService(String path) {
        setPath(path);
         setAction((ActionRequestResponse) (Request request, Response response) -> {
            Map<String, Object> result = new HashMap<>();
            int id = 0;
            try {
                id = Integer.parseInt(request.params("id"));
                Log.i("Person %d requested by %s", id, request.ip());
                Person person = new Person(id);
                result = person.toMap();
            } catch (Person.IllegalPersonException e) {
                response.status(204);
                Log.w("Person with id: %d not found", id);
            } catch (NumberFormatException e) {
                response.status(400);
                Log.w("Person id was mistaken.");
            }
            return result;
        });
    }
}
