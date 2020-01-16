package com.alepe.rest_demo.person.actions;

import com.alepe.rest_demo.person.Person;
import com.intellisrc.core.Log;
import com.intellisrc.web.Service;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides the service to search Person objects
 * @since 2020/01/15.
 */
public class PersonSearchService extends Service {
    public PersonSearchService(String path) {
        setPath(path);
        setAction((ActionRequestResponse) (Request request, Response response) -> {
            String name = request.params("keyword");
            List<Map<String, Object>> list = new ArrayList<>();
            if(! name.isEmpty()) {
                List<Person> found = Person.searchByName(name);
                if(found.isEmpty()) {
                    response.status(204);
                    Log.w("Search [%s] didn't match any record", name);
                } else {
                    for (Person person : found) {
                        list.add(person.toMap());
                    }
                }
            }
            return list;
        });
    }
}
