package com.alepe.rest_demo.person

import com.alepe.rest_demo.Main
import com.alepe.rest_demo.types.Color
import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Shared
import spock.lang.Unroll

/**
 * This is an integration test for PersonService (and the general API system)
 * @since 2020/01/17.
 */
class PersonServiceTest extends Specification {
    @Shared
    int version = PersonService.version
    @Shared
    Main main = new Main()
    @Shared
    def client = new RESTClient( "http://localhost:" + main.port)
    
    /**
     * Launch service if its not running
     */
    def launchService() {
        // If the port is available it means it is not running. Launch it.
        if(NetworkInterface.isPortAvailable(main.port)) {
            main.onStart()
            while (!main.running) {
                sleep(1000) //Wait until server is up
            }
        }
    }
    
    def setup() {
        launchService()
    }
    
    /*
     *  [GET] Tests for /people/
     */
    
    @SuppressWarnings("GrUnresolvedAccess")
    def 'should return 200 code and must have content when used to retrieve a list of people' () {
        when: 'request the list'
            def response = client.get( path : "/api/v${version}/people/${offset}/${limit}" )
        
        then: 'server returns 200 code (ok)'
            assert response.status == 200 : 'response code should be 200 when getting one person'
            assert response.responseData.size() > 0 : 'response must have an object'
            assert response.contentType == 'application/json' : 'response should be in json format'
        where: 'All allowed cases:'
            offset  | limit
            5       | 10
            0       | 5
    }
    
    @SuppressWarnings("GrUnresolvedAccess")
    def 'should return 200 code and must have content when searching' () {
        when: 'search in the list'
            def response = client.get( path : "/api/v${version}/people/a" )
        
        then: 'server returns 200 code (ok)'
            assert response.status == 200 : 'response code should be 200 when getting one person'
            assert response.responseData.size() > 0 : 'response must have an object'
            assert response.contentType == 'application/json' : 'response should be in json format'
    }
    
    @SuppressWarnings("GrUnresolvedAccess")
    def "should return 204 code when the request returns empty" () {
        when: 'retrieve non-existing person'
            def response = client.get(path: "/api/v${version}/people/300/10")
        
        then: 'server returns 204 code (empty)'
            assert response.status == 204 : 'response code should be 204 when there is no data'
            assert ! response.responseData : 'response must be empty'
    }
    
    @SuppressWarnings("GrUnresolvedAccess")
    def "should return 204 code when the search returns empty" () {
        when: 'search for a non-existing person'
            def response = client.get(path: "/api/v${version}/people/batman")
        
        then: 'server returns 204 code (empty)'
            assert response.status == 204 : 'response code should be 204 when there is no data'
            assert ! response.responseData : 'response must be empty'
    }
    
    @SuppressWarnings("GrUnresolvedAccess")
    @Unroll //Report each case separately
    def "should return 400 code when list request is invalid" () {
        when: 'requesting invalid offset and/or limit'
            client.get( path : "/api/v${version}/people/${offset}/${limit}" )
        
        then: 'server returns 400 code (Bad Request)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 400: 'response code should be 400 when request is invalid'
        
        where: 'All failure cases:'
            offset  | limit
            "A"     | "B"
            "A"     | 100
            1       | 0
            -1      | 10
            10      | -1
            9999999999999999999999999999 | 99999999999999999999999999
    }
    
    /*
     * [GET] Tests for /person/
     */
    
    @SuppressWarnings("GrUnresolvedAccess")
    def 'should return 200 code and must have content when used to retrieve existing person' () {
        when: 'retrieve first person'
            def response = client.get( path : "/api/v${version}/person/1" )
        
        then: 'server returns 200 code (ok)'
            assert response.status == 200 : 'response code should be 200 when getting one person'
            assert response.responseData.size() > 0 : 'response must have an object'
            assert response.contentType == 'application/json' : 'response should be in json format'
            assert response.data.first : 'First name should be in the response'
            assert response.data.last  : 'Last name should be in the response'
            assert response.data.age   : 'Age should be in the response'
            assert response.data.color : 'Color should be in the response'
            assert response.data.hobby : 'Hobby list should be in the response'
    }
    
    @SuppressWarnings("GrUnresolvedAccess")
    def "should return 204 code when person doesn't exists" () {
        when: 'retrieve non-existing person'
            def response = client.get(path: "/api/v${version}/person/300")
        
        then: 'server returns 204 code (empty)'
            assert response.status == 204 : 'response code should be 204 when there is no data'
            assert ! response.responseData : 'response must be empty'
    }
    
    @SuppressWarnings("GrUnresolvedAccess")
    def "should return 400 code when get person request is invalid" () {
        when: 'requesting invalid id'
            client.get( path : "/api/v${version}/person/AAA" )
        
        then: 'server returns 400 code (Bad Request)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 400: 'response code should be 400 when request is invalid'
            
    }
    
    /*
     * [GET] Tests for `/login`
     */
    
    @SuppressWarnings("GrUnresolvedAccess")
    def 'should return 200 code when used valid credentials' () {
        when: 'login with invalid credentials'
            client.headers['Authorization'] = "Basic ${"$USERNAME:$PASSWORD".bytes.encodeBase64()}"
            def response = client.get(path: "/api/v${version}/auth")
        
        then: 'server returns 200 code (ok)'
            assert response.status == 200 : 'response code should be 200 when tried to authenticate with valid credentials'
    }
    
    def 'should return 401 (unauthorized) code when used invalid credentials' () {
        when: 'login with invalid credentials'
            client.headers['Authorization'] = "Basic ${"$USERNAME:$INVALID_PASSWORD".bytes.encodeBase64()}"
            client.get( path : '/basic-auth/user/pass' )
        
        then: 'server returns 401 code (unauthorized)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 401: 'response code should be 401 when you use wrong credentials'
    }
    
    /*
     * [POST] Tests for `/person`
     */
    @SuppressWarnings("GrUnresolvedAccess")
    def 'It should be able to create a Person and it must return 200 as status' () {
        when: 'Add new record'
            client.headers.Token = ""
            def response = client.post(
                    path: "/api/v${version}/person",
                    body : [
                        "first_name"        : "Sarah",
                        "last_name"         : "Robinson",
                        "age"               : 54,
                        "favourite_colour"  : "blue",
                        "hobby"             : ["chess"]
                    ],
                    requestContentType : ContentType.JSON
            )
        
        then: 'server returns 200 code (ok) and response should be as expected'
            assert response.status == 200 : 'response code should be 200 when adding a person'
            assert response.responseData.size() > 0 : 'response must have an object'
            assert response.data.id > 0 : 'It must return the new ID'
            assert response.data.ok == true : 'It must return OK = true'
        
        when: "Be sure it was created... now remove it"
            assert new Person(response.data.id).delete()
        
        then: "No exception"
            notThrown(Person.IllegalPersonException)
    }

    @SuppressWarnings("GrUnresolvedAccess")
    @Unroll
    def 'Create a Person should fail with invalid data' () {
        when: "Prepare create information"
            int id = 1
            client.headers.Token = ""
            def response = client.put(
                    path: "/api/v${version}/person/${id}",
                    body: [
                            "first_name"      : first,
                            "last_name"       : last,
                            "age"             : age,
                            "favourite_colour": color,
                            "hobby"           : hobby
                    ],
                    requestContentType: ContentType.JSON
            )

        then: 'server returns 400 code (Bad Request)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 400: 'response code should be 400 if input is invalid'

        where: 'Invalid inputs'
            first | last     | age | color    | hobby
            ""    | "Sponge" | 22  | "yellow" | ["swimming"]              //Invalid First Name
            "Bob" | ""       | 5   | "yellow" | ["swimming"]              //Invalid Last Name
            "Bob" | "Sponge" | 0   | "yellow" | ["swimming"]              //Invalid Age
            "Bob" | "Sponge" | -1  | "yellow" | ["swimming"]              //Invalid Age
            "Bob" | "Sponge" | "A" | "yellow" | ["swimming"]              //Invalid Age
            "Bob" | "Sponge" | 5   | 999999   | ["swimming"]              //Invalid Color
            "Bob" | "Sponge" | 5   | "lime"   | ["swimming"]              //Invalid Color
            "Bob" | "Sponge" | 5   | "yellow" | ""                        //Invalid Hobby
            "Bob" | "Sponge" | 5   | "yellow" | "swimming"                //Invalid Hobby type
            "Bob" | "Sponge" | 5   | "yellow" | ["swimming": "nothing"]    //Invalid Hobby type
    }
    /*
     * [PUT] Tests for `/person/id`
     */
    @SuppressWarnings("GrUnresolvedAccess")
    def 'Fields should be updated accordingly' () {
        when: "Prepare update information"
            int id = 1
            client.headers.Token = ""
            def response = client.put(
                path : "/api/v${version}/person/${id}",
                body : [
                    "first_name"        : "Alfred",
                    "last_name"         : "Wilson",
                    "age"               : 78,
                    "favourite_colour"  : "red",
                    "hobby"             : ["meditation"]
                ],
                requestContentType : ContentType.JSON
            )
        
        then: 'server returns 200 code (ok) and response should be as expected'
            assert response.status == 200 : 'response code should be 200 when updating a person'
            assert response.responseData.size() > 0 : 'response must have an object'
            assert response.data.ok == true : 'It must return OK = true'
    }

    @SuppressWarnings("GrUnresolvedAccess")
    @Unroll
    def 'Update a Person should fail with invalid data' () {
        when: "Prepare update information"
            int id = 1
            client.headers.Token = ""
            def response = client.put(
                    path : "/api/v${version}/person/${id}",
                    body : [
                            "first_name"        : first,
                            "last_name"         : last,
                            "age"               : age,
                            "favourite_colour"  : color,
                            "hobby"             : hobby
                    ],
                    requestContentType : ContentType.JSON
            )

        then: 'server returns 400 code (Bad Request)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 400 : 'response code should be 400 if input is invalid'

        where: 'Invalid inputs'
            first   | last      | age | color   | hobby
            ""      | "Sponge"  | 22  | "yellow"| ["swimming"]              //Invalid First Name
            "Bob"   | ""        | 5   | "yellow"| ["swimming"]              //Invalid Last Name
            "Bob"   | "Sponge"  | 0   | "yellow"| ["swimming"]              //Invalid Age
            "Bob"   | "Sponge"  | -1  | "yellow"| ["swimming"]              //Invalid Age
            "Bob"   | "Sponge"  | "A" | "yellow"| ["swimming"]              //Invalid Age
            "Bob"   | "Sponge"  | 5   | 999999  | ["swimming"]              //Invalid Color
            "Bob"   | "Sponge"  | 5   | "lime"  | ["swimming"]              //Invalid Color
            "Bob"   | "Sponge"  | 5   | "yellow"| ""                        //Invalid Hobby
            "Bob"   | "Sponge"  | 5   | "yellow"| "swimming"                //Invalid Hobby type
            "Bob"   | "Sponge"  | 5   | "yellow"| ["swimming":"nothing"]    //Invalid Hobby type

    }

    /*
     * [DELETE] Tests for `/person/id`
     */
    @SuppressWarnings("GrUnresolvedAccess")
    def 'Delete a Person' () {
        setup: "Create a Person"
            Person.initDB()
            Color colorObj = Color.fromString("red")
            String[] hobby = ["reading"]
            Person person = new Person("Paula", "Siemens", 22, colorObj, hobby)
            assert person.id
        
        when: "Delete person"
            client.headers.Token = ""
            def response = client.delete(
                    path : "/api/v${version}/person/${person.id}",
                    requestContentType : ContentType.JSON
            )
    
        then: 'server returns 200 code (ok) and response should be as expected'
            assert response.status == 200 : 'response code should be 200 when removing a person'
            assert response.responseData.size() > 0 : 'response must have an object'
            assert response.data.ok == true : 'It must return OK = true'
        
        when: "Verify that it was removed"
            //noinspection GroovyResultOfObjectAllocationIgnored
            new Person(person.id)
        then:
            thrown Person.IllegalPersonException
    }

    @SuppressWarnings("GrUnresolvedAccess")
    def 'Delete a Person should fail with invalid id' () {
        when: "Delete person with invalid input"
            def response = client.delete(
                    path : "/api/v${version}/person/${id}",
                    requestContentType : ContentType.JSON
            )

        then: 'server returns 400 code (Bad Request)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 400 : 'response code should be 400 if input is invalid'

        where: 'Invalid inputs'
            id      | _
            0       | 0
            -1      | 0
            99999   | 0
            "A"     | 0
    }

}
