package com.alepe.rest_demo;

import com.alepe.rest_demo.services.*;
import com.intellisrc.web.Service;
import com.intellisrc.web.ServiciableMultiple;

import java.util.ArrayList;
import java.util.List;

/**
 * Sets the API Route Controller for CRUD operations related to Person
 *
 * URI examples:
 *
 * Get Person List (offset: 1, 20 records):
 * /api/v1/person.lst/1/20/
 *
 * Get Person with ID = 100:
 * /api/v1/person.get/100/
 *
 * Find a Person by name:
 * /api/v1/person.fnd/john/
 *
 * Add a Person: (POST)
 * /api/v1/person.add
 *
 * Update a Person: (PUT)
 * /api/v1/person.upd/100/
 *
 * Remove a Person: (DELETE)
 * /api/v1/person.del/100/
 *
 * @since 2020/01/15.
 */
public class PersonService implements ServiciableMultiple {
    static final int version = 1;

    @Override
    public String getPath() {
        return String.format("/api/v%d/person", version);
    }

    @Override
    public List<Service> getServices() {
        List<Service> services = new ArrayList<>();
        // List Person service:
        services.add(new PersonListService(".lst/:offset/:qty"));
        // Get Person service:
        services.add(new PersonGetService(".get/:id"));
        // Search Person service:
        services.add(new PersonSearchService(".fnd/:keyword"));
        // Create Person service:
        services.add(new PersonCreateService(".add"));
        // Update or Replace Person service:
        services.add(new PersonUpdateService(".upd/:id"));
        // Delete Person service:
        services.add(new PersonDestroyService(".del/:id"));
        return services;
    }
}
