package com.alepe.rest_demo.person;

import com.alepe.rest_demo.person.actions.*;
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
 * /api/v1/people/1/20
 *
 * Get Person with ID = 100:
 * /api/v1/person/100
 *
 * Find a Person by name:
 * /api/v1/people/john
 *
 * Add a Person: (POST)
 * /api/v1/person
 *
 * Update a Person: (PUT)
 * /api/v1/person/100
 *
 * Remove a Person: (DELETE)
 * /api/v1/person/100
 *
 * @since 2020/01/15.
 */
public class PersonAPI implements ServiciableMultiple {
    public static final int version = 1;

    @Override
    public String getPath() {
        return String.format("/api/v%d/", version);
    }

    @Override
    public List<Service> getServices() {
        List<Service> services = new ArrayList<>();
        // List Person service:
        services.add(new PersonListService("people/:offset/:qty"));
        // Get Person service:
        services.add(new PersonGetService("person/:id"));
        // Search Person service:
        services.add(new PersonSearchService("people/:keyword"));
        // Create Person service:
        services.add(new PersonCreateService("person"));
        // Update or Replace Person service:
        services.add(new PersonUpdateService("person/:id"));
        // Delete Person service:
        services.add(new PersonDestroyService("person/:id"));
        return services;
    }
}
