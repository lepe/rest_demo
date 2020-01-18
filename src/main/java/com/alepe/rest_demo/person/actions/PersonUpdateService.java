package com.alepe.rest_demo.person.actions;

import com.alepe.rest_demo.person.Person;
import com.alepe.rest_demo.types.Color;
import com.intellisrc.core.Log;
import com.intellisrc.web.JSON;
import com.intellisrc.web.Service;
import spark.Request;
import spark.Response;

import java.util.HashMap;
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
            boolean ok = false;
            String err = "";
            int id;
            try {
                id = Integer.parseInt(request.params("id"));
                String body = request.body().trim();
                if(! body.isEmpty()) {
                    Person person = new Person(id);
                    // Import json and convert it into HashMap with string keys:
                    HashMap<String, Object> clean = Person.cleanInputMap(JSON.decode(body).toMap());
                    if(!clean.isEmpty()) {
                        Log.i("Update Person requested by: %s", request.ip());
                        //Then update if all is fine
                        for (var key : clean.keySet()) {
                            switch (key) {
                                case "last_name": break; //Do nothing. It will be covered by "first".
                                case "first_name" :
                                    ok = person.updateName(clean.get("first_name").toString(), clean.get("last_name").toString());
                                    break;
                                case "age" :
                                    ok = person.updateAge((Integer) clean.get("age"));
                                    break;
                                case "favourite_colour":
                                    ok = person.updateColor((Color) clean.get("favourite_colour"));
                                    break;
                                case "hobby":
                                    ok = person.updHobbies((String[]) clean.get("hobby"));
                                    break;
                                default:
                                    Log.w("Unidentified key: %s", key);
                            }
                        }

                    } else {
                        response.status(400);
                        err = "data was empty or invalid. Please check the request.";
                    }
                } else {
                    response.status(400);
                    err = "Body was empty";
                }
            } catch (NumberFormatException | Person.IllegalPersonException ignored) {
                response.status(400);
                err = "Invalid ID passed to update";
            }
            // Log in case of error
            if(! err.isEmpty()) {
                Log.w("err");
            }
            return (Map<String, ?>) Map.of("ok", ok, "err", err);
        });
    }
}
