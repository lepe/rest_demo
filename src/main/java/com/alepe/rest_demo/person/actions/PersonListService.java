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
 * Provides the service to retrieve the list of Person objects
 * @since 2020/01/15.
 */
public class PersonListService extends Service {
    static final int maxRecordsAllowed = 1000;

    public PersonListService(String path) {
        setPath(path);
        setAction((ActionRequestResponse) (Request request, Response response) -> {
            List<Map<String,Object>> result = new ArrayList<>();
            int offset, qty;
            try {
                offset = Integer.parseInt(request.params("offset"));
                qty = Integer.parseInt(request.params("qty"));
                // Prevent negative numbers
                if(offset >= 0 && qty > 0) {
                    // limit the amount of records to download at once
                    if (qty > maxRecordsAllowed) {
                        qty = maxRecordsAllowed;
                    }
                    Log.i("Requesting list: [%d,%d] from %s", offset, qty, request.ip());
                    List<Person> list = Person.getAll(offset, qty);
                    if (list.isEmpty()) {
                        response.status(204);
                        Log.w("Records were not found.");
                    } else {
                        for (Person person : list) {
                            result.add(person.toMap());
                        }
                    }
                } else {
                    response.status(400);
                    Log.w("Requested list parameters were mistaken");
                }
            } catch (NumberFormatException e) {
                response.status(400);
                Log.w("Requested list parameters were mistaken");
            }
            return result;
        });
    }
}
