package com.alepe.rest_demo.person

import com.alepe.rest_demo.types.Color
import com.intellisrc.core.Config
import com.intellisrc.core.SysInfo
import com.intellisrc.db.Database
import spock.lang.Specification

/**
 * Unit Tests for Person
 * @since 1/16/20.
 */
class PersonTest extends Specification {
    /**
     * Initialization for all tests
     * @return
     */
    def setup() {
        Database.init()
    }
    /**
     * Cleanup for all tests
     * @return
     */
    def cleanup() {
        Database.quit()
    }
    /**
     * This test will verify that the DB can be created correctly
     */
    def "Must create database"() {
        setup :
            File dbFile = SysInfo.getFile(Config.get("db.name", "rest") + ".db")
            if(dbFile.exists()) {
                dbFile.delete()
            }
            Person.initDB()
        expect:
            assert dbFile.exists()
            assert ! dbFile.empty
            assert ! Person.getAll(0, 1).empty
        cleanup:
            if(dbFile.exists()) {
                dbFile.delete()
            }
    }
    /**
     * This test will verify that Person is being created in the database
     * Then it will delete it and verify that it was deleted correctly
     * @return
     */
    def "Must insert and delete Person"() {
        setup:
            Person.initDB()
        when:
            // Create the Person object:
            Color colorObj = Color.fromString(color)
            String[] hobbyList = hobby.split(",")
            Person person = new Person(first, last, age, colorObj, hobbyList)
            int id = person.id
        then:
            assert person.valid : "Person was not valid"
            assert id : "ID must not be zero"
            assert hobbyList.size() > 0 : "Hobby list was empty"
            // It should not raise and Exception:
            notThrown(Person.IllegalPersonException)
        when:
            Person readPerson = new Person(id)
        then:
            notThrown(Person.IllegalPersonException)
            assert readPerson.firstName == person.firstName : "Not the same person"
        when:
            // Delete should return true
            assert person.delete() : "Person was not deleted"
        then:
            // The person should not be valid anymore
            assert ! person.valid : "After delete, it should be valid"
        when:
            // If we try to get it again, it should raise an exception
            new Person(id)
        then:
            thrown(Person.IllegalPersonException)
        where:
            first   | last      | age | color   | hobby
            "Jenny" | "Mandela" | 22  | "black" | "reading,movies"
            "Bob"   | "Sponge"  | 5   | "yellow"| "swimming"
    }

    def "Update age should update Person's age"() {
        setup:
            Person.initDB()
        when:
            // Create the Person object:
            Color colorObj = Color.fromString(color)
            String[] hobbyList = hobby.split(",")
            Person person = new Person(first, last, age, colorObj, hobbyList)
        then:
            assert person.valid : "Person was not valid"
            // It should not raise and Exception:
            notThrown(Person.IllegalPersonException)
        when:
            person.updateAge(change)
        then:
            assert person.age == change : "Age was not updated in object"
            new Person(person.id).age == change
        cleanup:
            person.delete()
        where:
            first   | last      | age | change | color   | hobby
            "Jenny" | "Mandela" | 22  | 33     | "black" | "reading,movies"

    }
}
