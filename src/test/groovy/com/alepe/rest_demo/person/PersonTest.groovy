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
    def setupSpec() {
        Database.init()
        File dbFile = SysInfo.getFile(Config.get("db.name", "rest") + ".db")
        if(dbFile.exists()) {
            dbFile.delete()
        }
        Person.initDB()
        assert dbFile.exists()
        assert ! dbFile.empty
        assert ! Person.getAll(0, 1).empty
    }
    /**
     * Cleanup for all tests
     * @return
     */
    def cleanupSpec() {
        Database.quit()
    
        File dbFile = SysInfo.getFile(Config.get("db.name", "rest") + ".db")
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
        when: "Create the Person object:"
            Color colorObj = Color.fromString(color)
            String[] hobbyList = hobby.split(",")
            Person person = new Person(first, last, age, colorObj, hobbyList)
            int id = person.id
        
        then: "Check if person is valid"
            assert person.valid : "Person was not valid"
            assert id : "ID must not be zero"
            assert hobbyList.size() > 0 : "Hobby list was empty"
            // It should not raise and Exception:
            notThrown(Person.IllegalPersonException)
        
        when: "Verify that person was inserted"
            Person readPerson = new Person(id)
        
        then: "Exception must not be thrown and person must match"
            notThrown(Person.IllegalPersonException)
            assert readPerson.firstName == person.firstName : "Not the same person"
        
        when: "Delete Person"
            // Delete should return true
            assert person.delete() : "Person was not deleted"
        
        then: "The person should not be valid anymore"
            assert ! person.valid : "After delete, it should be valid"
        
        when: "If we try to get it again, it should raise an exception"
            //noinspection GroovyResultOfObjectAllocationIgnored
            new Person(id)
        
        then: "Exception should be thrown"
            thrown(Person.IllegalPersonException)
        
        where:
            first   | last      | age | color   | hobby
            "Jenny" | "Mandela" | 22  | "black" | "reading,movies"
            "Bob"   | "Sponge"  | 5   | "yellow"| "swimming"
    }

    /**
     * This test will verify that Person can update age
     * @return
     */
    def "should update Person's age or fail if incorrect"() {
        setup: "Create the Person object:"
            Color colorObj = Color.fromString("red")
            String[] hobby = ["reading"]
            Person person = new Person("Ben", "Walkman", 22, colorObj, hobby)
        
        when: "Verify that update returns true/false accordingly"
            assert person.updateAge(change) == correct
        
        then: "Only if its correct, verify that values match"
            if(correct) {
                assert person.age == change: "Age was not updated in object"
                assert new Person(person.id).age == change : "Age was no updated in db"
            }
        
        cleanup:
            assert person.delete()
        
        where:
            change | correct
            33     | true
            0      | false
            -100   | false
    }

    /**
     * This test will test changing person name
     * @return
     */
    def "should update Person's name or fail if incorrect"() {
        setup: "Create the Person object:"
            Color colorObj = Color.fromString("white")
            String[] hobby = ["reading"]
            Person person = new Person("Ben", "Walkman", 22, colorObj, hobby)
        
        when: "Verify that update returns true/false accordingly"
            assert person.updateName(first, last) == correct
        
        then: "Only if its correct, verify that values match"
            if(correct) {
                assert person.firstName == first: "First name was not updated in object"
                assert person.lastName == last : "Last name was not updated in object"
                new Person(person.id).with {
                    assert firstName == first : "First name was not updated in db"
                    assert lastName == last : "Last name was not updated in db"
                }
            }
        
        cleanup:
            assert person.delete()
        
        where:
            first       | last      | correct
            "Peter"     | "Watson"  | true
            ""          | "Redson"  | false
            "Brittany"  | ""        | false
    }

    /**
     * Tests if favourite color is updated correctly
     * @return
     */
    def "should update Person's favourite color"() {
        setup: "Create the Person object:"
            Color colorObj = Color.fromString("blue")
            assert colorObj == Color.BLUE
            String[] hobby = ["reading","writing"]
            Person person = new Person("Sarah", "Johnson", 44, colorObj, hobby)
        
        when: "New color is correctly assigned"
            Color newColor = Color.fromString(color)
        
        then: "Verify that favourite color was updated"
            assert expected == newColor
            assert person.updateColor(newColor)
            assert person.favouriteColor == newColor : "Color was not updated in object"
            assert new Person(person.id).favouriteColor == newColor : "Color was not updated in db"
        
        cleanup:
            assert person.delete()
        
        where:
            color     | expected
            "green "  | Color.GREEN
            "none"    | Color.NONE
            ""        | Color.NONE  //must be "none"
            "invalid" | Color.NONE
    }

    /**
     * This Test is to verify that search method is working as expected
     * @return
     */
    def "Search must return correct Person objects"() {
        setup: "Create the list in which we are going to search"
            List<Person> list = []
            def people = [
                    [
                            first_name  : "Larry",
                            last_name   : "Bird",
                            age         : 50,
                            favourite_colour    : Color.GREEN,
                            hobby       : ["basketball","sports","reading"]
                    ],
                    [
                            first_name  : "Michael",
                            last_name   : "Jordan",
                            age         : 40,
                            favourite_colour    : Color.RED,
                            hobby       : ["basketball","sports","golf","baseball"]
                    ],
                    [
                            first_name  : "Magic",
                            last_name   : "Johnson",
                            age         : 45,
                            favourite_colour: Color.YELLOW,
                            hobby       : ["basketball","sports","talking"]
                    ]
            ]
        
        when: "Insert all the objects"
            people.each {
                list << new Person(
                        it.first_name.toString(),
                        it.last_name.toString(),
                        it.age as int,
                        it.favourite_colour as Color,
                        it.hobby as String[]
                )
            }
        
        then: "Verify that search is working as expected"
            assert Person.searchByName("michael").size() > 0
            assert Person.searchByName("jordan").size() > 0
            assert Person.searchByName("magic").size() > 0
            assert Person.searchByName("bird").size() > 0
            assert Person.searchByName("zzzz").empty
        
        cleanup :
            if(!list.empty) {
                list.each { it.delete() }
            }
    }
    /**
     * This test will add and remove hobbies
     */
    def "Update Hobbies"() {
        setup: "Create the Person object"
            String[] hobbies = ["shopping","jogging","yoga","travelling"]
            Person person = new Person("Elizabeth", "Hurley", 30, Color.RED, hobbies)
            assert person.hobby.size() == hobbies.size()
        
        when: "Update hobbies"
            String[] newHobbies = ["games","swimming"]
            assert person.updHobbies(newHobbies)
        
        then: "Be sure that hobbies were updated"
            assert person.hobby == newHobbies   : "Hobby was not updated in object"
            assert new Person(person.id).hobby.toList().containsAll(newHobbies) : "Hobby was not updated in database"
            assert person.hobby.size() == newHobbies.size()
        
        when: "Clear hobbies"
            newHobbies = []
            assert person.updHobbies(newHobbies)
        
        then: "Be sure that we don't have any hobby"
            assert person.hobby.length == 0 : "[Empty] Hobby was not updated in object"
            assert new Person(person.id).hobby.length == 0 : "[Empty] Hobby was not updated in database"
        
        cleanup:
            person.delete()
    }

    /**
     * This test verify that exporting a Person object
     * will return the expected Map
     */
    def "Person exported as Map"() {
        setup: "Create the Person object"
            String[] hobbies = ["dancing","singing"]
            Person person = new Person("Katty", "Perry", 25, Color.PINK, hobbies)
        
        when: "We convert it into Map"
            def map = person.toMap()
        
        then: "Be sure that all information match in the new Map"
            map.with {
                assert id               == person.id
                assert first_name       == person.firstName
                assert last_name        == person.lastName
                assert age              == person.age
                assert favourite_colour == person.favouriteColor.toString()
                assert person.hobby.toList().containsAll(hobbies)
            }
        
        cleanup:
            assert person.delete()
    }

    /**
     * This test will prove that People cloning is possible
     */
    def "Clone Person"() {
        setup: "Create the Person object"
            String[] hobbies = ["boxing","singing"]
            Person person = new Person("Ed", "Sheeran", 15, Color.ORANGE, hobbies)
        
        when: "Clone the person"
            Person clone = Person.clone(person)
        
        then: "All information should match between the clone and the original except for the ID"
            notThrown(Person.IllegalPersonException)
            assert clone.id
            person.with {
                assert id != clone.id
                assert firstName == clone.firstName
                assert lastName == clone.lastName
                assert age == clone.age
                assert favouriteColor == clone.favouriteColor
                assert hobby == clone.hobby
            }
        
        cleanup:
            assert person.delete()
            assert clone.delete()
    }

    /**
     * This test verify that a Person can be created from Map
     * However this method won't create it on database.
     */
    def "Person from Map"() {
        when: "Create a Person object from Map"
            Person person = Person.fromMap(
                id                  : 200,
                first_name          : "Stephanie",
                last_name           : "Lee",
                age                 : 50,
                favourite_colour    : Color.BLACK,
                hobby               : ["martial arts","teaching"]
            )
        
        then: "Verify that the Person object was created successfully"
            notThrown(Person.IllegalPersonException)
            person.with {
                assert id
                assert firstName
                assert lastName
                assert age
                assert favouriteColor
                assert hobby
            }
    }
}
