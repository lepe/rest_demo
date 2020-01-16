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

    /**
     * This test will verify that Person can update age
     * @return
     */
    def "should update Person's age or fail if incorrect"() {
        setup:
            Person.initDB()
            // Create the Person object:
            Color colorObj = Color.fromString("red")
            String[] hobby = ["reading"]
            Person person = new Person("Ben", "Walkman", 22, colorObj, hobby)
        when:
            assert person.updateAge(change) == correct
        then:
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
        setup:
            Person.initDB()
            // Create the Person object:
            Color colorObj = Color.fromString("white")
            String[] hobby = ["reading"]
            Person person = new Person("Ben", "Walkman", 22, colorObj, hobby)
        when:
            assert person.updateName(first, last) == correct
        then:
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
        setup:
            Person.initDB()
            // Create the Person object:
            Color colorObj = Color.fromString("blue")
            assert colorObj == Color.BLUE
            String[] hobby = ["reading","writing"]
            Person person = new Person("Sarah", "Johnson", 44, colorObj, hobby)
        when:
            Color newColor = Color.fromString(color)
        then:
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
        setup:
            Person.initDB()
            List<Person> list = []
            // Create the Person object:
            def people = [
                    [
                            first: "Larry",
                            last: "Bird",
                            age: 50,
                            color: Color.GREEN,
                            hobby: ["basketball","sports","reading"]
                    ],
                    [
                            first: "Michael",
                            last: "Jordan",
                            age: 40,
                            color: Color.RED,
                            hobby: ["basketball","sports","golf","baseball"]
                    ],
                    [
                            first: "Magic",
                            last: "Johnson",
                            age: 45,
                            color: Color.YELLOW,
                            hobby: ["basketball","sports","talking"]
                    ]
            ]
            people.each {
                list << new Person(
                        it.first.toString(),
                        it.last.toString(),
                        it.age as int,
                        it.color as Color,
                        it.hobby as String[]
                )
            }
        expect:
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
        setup:
            String[] hobbies = ["shopping","jogging","yoga","travelling"]
            Person person = new Person("Elizabeth", "Hurley", 30, Color.RED, hobbies)
            assert person.hobby.size() == hobbies.size()
        when:
            String[] newHobbies = ["games","swimming"]
            assert person.updHobbies(newHobbies)
        then:
            assert person.hobby == newHobbies   : "Hobby was not updated in object"
            assert new Person(person.id).hobby.toList().containsAll(newHobbies) : "Hobby was not updated in database"
            assert person.hobby.size() == newHobbies.size()
        when:
            newHobbies = []
            assert person.updHobbies(newHobbies)
        then:
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
        setup:
            String[] hobbies = ["dancing","singing"]
            Person person = new Person("Katty", "Perry", 25, Color.PINK, hobbies)
        when:
            def map = person.toMap()
        then:
            map.with {
                assert id       == person.id
                assert first    == person.firstName
                assert last     == person.lastName
                assert age      == person.age
                assert color    == person.favouriteColor.toString()
                assert person.hobby.toList().containsAll(hobbies)
            }
        cleanup:
            assert person.delete()
    }

    /**
     * This test will prove that People cloning is possible
     */
    def "Clone Person"() {
        setup:
            String[] hobbies = ["boxing","singing"]
            Person person = new Person("Ed", "Sheeran", 15, Color.ORANGE, hobbies)
        when:
            Person clone = Person.clone(person)
        then:
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
        when:
            Person person = Person.fromMap(
                id      : 200,
                first   : "Stephanie",
                last    : "Lee",
                age     : 50,
                color   : Color.BLACK,
                hobby   : ["martial arts","teaching"]
            )
        then:
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
