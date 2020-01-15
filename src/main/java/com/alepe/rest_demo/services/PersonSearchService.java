package com.alepe.rest_demo.services;

import com.alepe.rest_demo.Person;
import com.intellisrc.web.Service;
import spark.Request;

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
        setAction((ActionRequest) (Request request) -> {
            String name = request.params("keyword");
            List<Map<String, Object>> list = new ArrayList<>();
            if(! name.isEmpty()) {
                for(Person person : Person.searchByName(name)) {
                    list.add(person.toMap());
                }
            }
            return list;
        });
    }
}
