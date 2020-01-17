package com.alepe.rest_demo.person;

import com.alepe.rest_demo.types.Color;
import com.intellisrc.core.Config;
import com.intellisrc.core.Log;
import com.intellisrc.core.SysInfo;
import com.intellisrc.db.DB;
import com.intellisrc.db.Data;
import com.intellisrc.db.Database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Representation of a Person
 * @since 2020/01/15.
 * @author A.Lepe
 */
public class Person {
    private static String table = "person";

    protected int id;
    protected String firstName;
    protected String lastName;
    protected int age;
    protected Color favouriteColor;
    protected String[] hobby = new String[0];

    /**
     * Exception thrown when we can not create a Person object
     */
    static public class IllegalPersonException extends Exception {}

    /**
     * Create a new Person
     * @param firstName : First Name (e.g. John)
     * @param lastName  : Last Name  (e.g. Doe)
     * @param age       : Age        (e.g. 50)
     * @param favouriteColor : Color object
     * @param hobby     : String array with a list of hobbies
     * @throws IllegalPersonException : Unable to create Person in database
     */
    public Person(String firstName, String lastName, int age, Color favouriteColor, String[] hobby) throws IllegalPersonException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.favouriteColor = favouriteColor;
        this.hobby = hobby;

        DB db = Database.connect();
        Map<String, Object> row = new HashMap<>();
        row.put("first_name", firstName);
        row.put("last_name", lastName);
        row.put("age", age);
        row.put("favourite_colour", favouriteColor.toString());
        row.put("hobby", String.join(",", hobby));
        boolean inserted = db.table(table).insert(row);
        id = db.getLastID();
        db.close();

        if(inserted) {
            Log.i("NEW Person: [%s %s] with [%d]", firstName, lastName, id);
        } else {
            throw new IllegalPersonException();
        }
    }

    /**
     * Retrieves a Person knowing its ID
     * @param id : Person's ID
     * @throws IllegalPersonException : Unable to find Person in database
     */
    public Person(int id) throws IllegalPersonException {
        this.id = id;
        if(id <= 0) {
            Log.e("Trying to get a Person with invalid ID: %d", id);
            throw new IllegalPersonException();
        }

        DB db = Database.connect();
        Data row = db.table(table).key("id").get(id);
        db.close();

        if(row == null || row.isEmpty()) {
            Log.w("Person with id: %d doesn't exists", id);
            throw new IllegalPersonException();
        }

        HashMap<Object, Object> personMap = new HashMap<Object, Object>(row.toMap());
        this.firstName      = personMap.get("first_name").toString();
        this.lastName       = personMap.get("last_name").toString();
        this.age            = (Integer) personMap.get("age");
        this.favouriteColor = Color.fromString(personMap.get("favourite_colour").toString());
        String hobbies      = personMap.get("hobby").toString();
        if(!hobbies.isEmpty()) {
            this.hobby      = hobbies.split(",");
        }
    }

    /**
     * Empty constructor which is used internally to create Person objects
     * without connecting to the database.
     */
    private Person() {}

    /**
     * Get the ID
     * @return Person's id
     */
    public int getId() {
        return id;
    }
    /**
     * Updates a Person's name
     * @param newFirstName : first name
     * @param newLastName : last name
     * @return true if succeeds
     */
    public boolean updateName(String newFirstName, String newLastName) {
        boolean ok = false;
        if(isValid() &&! newFirstName.isEmpty() &&! newLastName.isEmpty()) {
            DB db = Database.connect();
            ok = db.table(table).key("id").update(Map.of("first_name", newFirstName, "last_name", newLastName), id);
            if(ok) {
                firstName = newFirstName;
                lastName = newLastName;
            }
            db.close();
            Log.i("Person with id: %d updated name to: %s %s", id, newFirstName, newLastName);
        }
        return ok;
    }

    /**
     * Update a Person's age
     * @param newAge : age
     * @return true if succeeds
     */
    public boolean updateAge(int newAge) {
        boolean ok = false;
        if(isValid() && newAge > 0) {
            DB db = Database.connect();
            ok = db.table(table).key("id").update(Map.of("age", newAge), id);
            if(ok) {
                age = newAge;
            }
            db.close();
            Log.i("Person with id: %d updated his/her age to: %d", id, newAge);
        }
        return ok;
    }

    /**
     * Update a Person's favourite color.
     * @param color : Color object
     * @return true if succeeds
     */
    public boolean updateColor(Color color) {
        boolean ok = false;
        if(isValid()) {
            DB db = Database.connect();
            ok = db.table(table).key("id").update(Map.of("favourite_colour", color.toString()), id);
            db.close();
            if(ok) {
                favouriteColor = color;
            }
            Log.i("Person with id: %d changed favourite color to: %s", id, color.toString());
        }
        return ok;
    }

    /**
     * Update hobby to a Person
     * @param hobbies : String array of new hobbies
     * @return true if succeeds
     */
    public boolean updHobbies(String[] hobbies) {
        boolean ok = false;
        if(isValid()) {
            DB db = Database.connect();
            String newHobbiesStr = String.join(",", hobbies);
            ok = db.table(table).key("id").update(Map.of("hobby", newHobbiesStr), id);
            db.close();
            if(ok) {
                hobby = hobbies;
            }
            Log.i("Person with id: %d added: %s as hobby", id, hobby);
        }
        return ok;
    }

    /**
     * Delete a person
     * @return true if succeeds
     */
    public boolean delete() {
        boolean ok = false;
        if(isValid()) {
            DB db = Database.connect();
            ok = db.table(table).key("id").delete(id);
            db.close();
            Log.i("Person with id: %d was deleted.", id);
            id = 0; //clear this Person
        }
        return ok;
    }

    /**
     * Validate a Person
     * @return true if a Person is valid
     */
    public boolean isValid() {
        boolean valid = id > 0 && !firstName.isEmpty();
        if(!valid) {
            Log.w("Person was not valid");
        }
        return valid;
    }

    /**
     * Export Person as Map object
     * @return a Map with Person data
     */
    public Map<String, Object> toMap() {
        Map<String, Object> personMap = new HashMap<>();
        personMap.put("id"      , id);
        personMap.put("first_name"   , firstName);
        personMap.put("last_name"    , lastName);
        personMap.put("age"     , age);
        personMap.put("favourite_colour"   , favouriteColor.toString());
        personMap.put("hobby"   , hobby);
        return personMap;
    }

    ////////////////////////// STATIC METHODS ////////////////////////////////

    /**
     * Clone a Person : I hope this is not illegal :)
     * This method will copy all fields from an existing Person and
     * create a new one based on it.
     *
     * @param person : Person object to copy from
     * @return Person (with new ID)
     * @throws IllegalPersonException : Unable to clone Person : if it was illegal after all.
     */
    static public Person clone(Person person) throws IllegalPersonException {
        Person clone = new Person(person.firstName, person.lastName, person.age, person.favouriteColor, person.hobby);
        Log.i("Clone process succeed. New id is: %d", clone.id);
        return clone;
    }
    /**
     * Creates a Person from a Map Object
     * @param map : Data to import
     * @return a Person object
     * @throws IllegalPersonException : Unable to import person from Map
     */
    static public Person fromMap(Map<?,?> map) throws IllegalPersonException {
        Person person = new Person();
        HashMap<?, ?> personMap = new HashMap<>(map);
        try {
            person.id = Integer.parseInt(personMap.get("id").toString());
            person.firstName = personMap.get("first_name").toString();
            person.lastName = personMap.get("last_name").toString();
            person.age = Integer.parseInt(personMap.get("age").toString().replaceAll("\\..*",""));
            person.favouriteColor = Color.fromString(personMap.get("favourite_colour").toString());
            String hobbies = personMap.get("hobby").toString();
            if (!hobbies.isEmpty()) {
                person.hobby = hobbies.split(",");
            }
        } catch (Exception e) {
            Log.e("Unable to import Person from Map", e);
            throw new IllegalPersonException();
        }
        return person;
    }

    /**
     * Generates a list of Person objects from a Data object (database)
     * @param rows : Database object containing the records
     * @return a list of Person objects
     */
    static private List<Person> fromData(Data rows) {
        List<Person> list = new ArrayList<>();
        if(rows != null) {
            var data = rows.toListMap();
            for (var datum : data) {
                try {
                    list.add(Person.fromMap(datum));
                } catch (IllegalPersonException ignored) {}
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Search all people by name
     * @param name : Partial or full string to match
     * @return a list of Person objects found
     */
    static public List<Person> searchByName(String name) {
        DB db = Database.connect();
        List<Person> result = new ArrayList<>();
        String search = String.join("", name.toLowerCase().replaceAll("[^a-z]", ""));
        if(!search.isEmpty()) {
            Log.i("Searching for: %s", search);
            search = "%" + search + "%";
            Data rows = db.table(table).where("lower(`first_name`) LIKE ? or lower(`last_name`) LIKE ?", search, search).get();
            result = fromData(rows);
            db.close();
        }
        return result;
    }

    /**
     * Get all records as Person
     * @param offset : retrieve starting at this count
     * @param qty  : how many records to get
     * @return list of Person objects in database
     */
    static public List<Person> getAll(int offset, int qty) {
        DB db = Database.connect();
        Data rows = db.table(table).limit(qty, offset).get();
        db.close();
        return fromData(rows);
    }

    /**
     * It will create the database if the database doesn't exists.
     * NOTE: You may remove the database file to create it again.
     */
    static public void initDB() {
        File dbFile = SysInfo.getFile(Config.get("db.name", "rest") + ".db");
        if(!dbFile.exists()) {
            DB db = Database.connect();
            File createSQL = SysInfo.getFile("create.sql");
            if (createSQL.exists()) {
                try {
                    String query = Files.readString(createSQL.toPath());
                    if(!query.isEmpty()) {
                        for(String command : query.replaceAll("\n","").split(";")) {
                            if(!command.trim().isEmpty()) {
                                db.set(command);
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.w("Unable to open file: %s", createSQL.getAbsoluteFile());
                }
            }
            db.close();
        }
    }
}
