package com.alepe.rest_demo.services;

import com.alepe.rest_demo.Person;
import com.intellisrc.web.Service;
import spark.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides the service to retrieve the list of Person objects
 * @since 2020/01/15.
 */
public class PersonListService extends Service {
    static final int maxRecodsAllowed = 1000;

    public PersonListService(String path) {
        setPath(path);
        setAction((ActionRequest) (Request request) -> {
            int offset = Integer.parseInt(request.params("offset"));
            int qty = Integer.parseInt(request.params("qty"));
            // Prevent negative numbers
            if(offset < 0) {
                offset = 0;
            }
            // limit the amount of records to download at once
            if(qty <= 0 || qty > maxRecodsAllowed) {
                qty = maxRecodsAllowed;
            }
            List<Map<String,Object>> response = new ArrayList<>();
            for(Person person : Person.getAll(offset, qty)) {
                response.add(person.toMap());
            }
            return response;
        });
    }
}
