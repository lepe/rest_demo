package com.alepe.rest_demo.person.actions;

import com.alepe.rest_demo.types.Color;
import com.alepe.rest_demo.person.Person;
import com.intellisrc.core.Log;
import com.intellisrc.web.Service;
import spark.Request;
import spark.Response;

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
            boolean ok = false;
            String name = request.queryParams("name");
            String last = request.queryParams("last");
            short age = 0;
            try {
                age = Short.parseShort(request.queryParams("age"));
            } catch(NumberFormatException exception) {
                Log.w("Age value was incorrect.");
            }
            String color = request.queryParams("color");
            Color favColor = Color.fromString(color);
            String[] hobby = request.queryParams("hobby").split(",");
            try {
                new Person(name, last, age, favColor, hobby);
                ok = true;
            } catch (Person.IllegalPersonException ignored) {}
            return (Map<String, Boolean>) Map.of("ok", ok);
        });

    }
}
