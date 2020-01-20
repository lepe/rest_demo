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
class PersonAPITest extends Specification {
    @Shared
    int version = PersonAPI.version
    @Shared
    Main main = new Main()
    @Shared
    def client = new RESTClient( "http://localhost:" + main.port)
    /**
     * Login into system
     * This is used to be able to execute restricted services.
     * @return cookies
     */
    @SuppressWarnings("GrUnresolvedAccess")
    List<String> login() {
        def response = client.post(
                path: "/api/v${version}/auth/login",
                body: [
                        user : "admin",
                        pass : "admin"
                ],
                requestContentType : ContentType.JSON
        )
        List<String> cookies = []
        response.getHeaders('Set-Cookie').each {
            //Log.s("Set cookie: %s", it.value.toString())
            cookies.add(it.value.toString().split(';')[0])
        }
        return cookies
    }
    /**
     * Logout from system
     * @return
     */
    def logout() {
        client.get(path : "/api/v${version}/auth/logout")
    }

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
            assert response.data.first_name       : 'First name should be in the response'
            assert response.data.last_name        : 'Last name should be in the response'
            assert response.data.age              : 'Age should be in the response'
            assert response.data.favourite_colour : 'Color should be in the response'
            assert response.data.hobby            : 'Hobby list should be in the response'
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
            def response = client.post(
                    path: "/api/v${version}/auth/login",
                    body: [
                        user : "admin",
                        pass : "admin"
                    ],
                    requestContentType : ContentType.JSON
            )
        
        then: 'server returns 200 code (ok)'
            assert response.status == 200 : 'response code should be 200 when tried to authenticate with valid credentials'

        when: 'logout'
            response = client.get(path: "/api/v${version}/auth/logout")

        then : 'server returns 200 code (ok)'
            assert response.status == 200 : 'response code should be 200 when logging out.'

    }
    
    def 'should return 401 (unauthorized) code when used invalid credentials' () {
        when: 'login with invalid credentials'
            def response = client.post(
                    path: "/api/v${version}/auth/login",
                    body: [
                            user : user,
                            pass : pass
                    ],
                    requestContentType : ContentType.JSON
            )

        then: 'server returns 401 code (unauthorized)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 401: 'response code should be 401 when you use wrong credentials'

        where:
            user      | pass
            "admin"   | "incorrect_pass"
            "nouser"  | "admin"
            ""        | ""
            "admin"   | ""
            ""        | "admin"
    }

    def 'Trying to access restricted resources without logging in should return 403 as status'() {
        when: 'Try to add a record'
            client.post(
                    path: "/api/v${version}/person",
                    body : [
                            "first_name"        : "Terry",
                            "last_name"         : "Blower",
                            "age"               : 10,
                            "favourite_colour"  : "blue",
                            "hobby"             : "none"
                    ],
                    requestContentType : ContentType.JSON
            )

        then: 'Expect 403 code'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 403: 'response must be: forbidden'

        when: 'Try to update a record'
            client.put(
                    path : "/api/v${version}/person/1",
                    body : [
                            "first_name"        : "Pete",
                            "last_name"         : "Jackson",
                            "age"               : 20,
                            "favourite_colour"  : "black",
                            "hobby"             : ["none"]
                    ],
                    requestContentType : ContentType.JSON
            )

        then: 'Expect 403 code'
            e = thrown(HttpResponseException)
            assert e.response.status == 403: 'response must be: forbidden'

        when: 'Try to delete a record'
            client.delete(
                    path : "/api/v${version}/person/1"
            )

        then: 'Expect 403 code'
            e = thrown(HttpResponseException)
            assert e.response.status == 403: 'response must be: forbidden'

    }
    
    /*
     * [POST] Tests for `/person`
     */
    @SuppressWarnings("GrUnresolvedAccess")
    @Unroll
    def 'It should be able to create a Person and it must return 200 as status' () {
        setup: 'Login'
            List<String> cookies = login()

        when: 'Add new record'
            def response = client.post(
                    path: "/api/v${version}/person",
                    headers : [
                        "Cookie"  : cookies.join(";")
                    ],
                    body : [
                        "first_name"        : first,
                        "last_name"         : last,
                        "age"               : age,
                        "favourite_colour"  : color,
                        "hobby"             : hobby
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

        cleanup: 'Logout'
            logout()

        where: 'Valid inputs'
            first    | last     | age | color    | hobby
            "Mega"   | "Stone"  | 29  | "yellow" | ["dancing"]
            "Teresa" | "Wang"   | 8   | "lime"   | ["reading"] // Invalid color will translate into "none"
            "Britney"| "Spears" | 1   | ""       | ["singing"] // Empty color should be converted into "none"
            "Mel"    | "Martin" | 5   | "orange" | "swimming"  // Hobby can also be specified as String
            "Isla"   | "Fisher" | 35  | "red"    | []          // Empty hobby should be fine
    }

    @SuppressWarnings("GrUnresolvedAccess")
    @Unroll
    def 'Create a Person should fail with invalid data' () {
        setup: 'Login'
            List<String> cookies = login()

        when: "Prepare create information"
            int id = 1
            def response = client.post(
                    path: "/api/v${version}/person",
                    headers : [
                            "Cookie"  : cookies.join(";")
                    ],
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

        cleanup: 'Logout'
            logout()

        where: 'Invalid inputs'
            first | last     | age | color    | hobby
            ""    | "Martin" | 22  | "orange" | ["swimming"]              //Invalid First Name
            "Mel" | ""       | 5   | "orange" | ["swimming"]              //Invalid Last Name
            "Mel" | "Martin" | 0   | "orange" | ["swimming"]              //Invalid Age
            "Mel" | "Martin" | -1  | "orange" | ["swimming"]              //Invalid Age
            "Mel" | "Martin" | "A" | "orange" | ["swimming"]              //Invalid Age
            "Mel" | "Martin" | 5   | "orange" | ["swimming": "nothing"]   //Invalid Hobby type (Map)
    }
    /*
     * [PUT] Tests for `/person/id`
     */
    @SuppressWarnings("GrUnresolvedAccess")
    @Unroll
    def 'Update should be updated accordingly with valid data' () {
        setup: 'Login'
            List<String> cookies = login()

        when: "Prepare update information"
            int id = 1
            def response = client.put(
                path : "/api/v${version}/person/${id}",
                headers : [
                        "Cookie"  : cookies.join(";")
                ],
                body : [
                    "first_name"        : first,
                    "last_name"         : last,
                    "age"               : age,
                    "favourite_colour"  : color,
                    "hobby"             : hobby
                ],
                requestContentType : ContentType.JSON
            )
        
        then: 'server returns 200 code (ok) and response should be as expected'
            assert response.status == 200 : 'response code should be 200 when updating a person'
            assert response.responseData.size() > 0 : 'response must have an object'
            assert response.data.ok == true : 'It must return OK = true'

        cleanup: 'Logout'
            logout()

        where: 'Valid inputs'
            first    | last     | age | color    | hobby
            "Albert" | "Wilson" | 78  | "black"  | ["meditation"]
            "Mike"   | "Fowler" | 18  | "navy"   | ["sports"]  // Invalid color will translate into "none"
            "Kerry"  | "Haynes" | 100 | ""       | ["running"] // Empty color should be converted into "none"
            "Stacy"  | "Vega"   | 88  | "orange" | "cycling"   // Hobby can also be specified as String
            "Diane"  | "Moore"  | 45  | "pink"   | []          // Empty hobby should be fine
    }

    @SuppressWarnings("GrUnresolvedAccess")
    @Unroll
    def 'Update a Person should fail with invalid data' () {
        setup: 'Login'
            List<String> cookies = login()

        when: "Prepare update information"
            int id = 1
            def response = client.put(
                    path : "/api/v${version}/person/${id}",
                    headers : [
                            "Cookie"  : cookies.join(";")
                    ],
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

        cleanup: 'Logout'
            logout()

        where: 'Invalid inputs'
            first   | last      | age | color   | hobby
            ""      | "Sponge"  | 22  | "yellow"| ["swimming"]              //Invalid First Name
            "Bob"   | ""        | 5   | "yellow"| ["swimming"]              //Invalid Last Name
            "Bob"   | "Sponge"  | 0   | "yellow"| ["swimming"]              //Invalid Age
            "Bob"   | "Sponge"  | -1  | "yellow"| ["swimming"]              //Invalid Age
            "Bob"   | "Sponge"  | "A" | "yellow"| ["swimming"]              //Invalid Age
            "Bob"   | "Sponge"  | 5   | "yellow"| ["swimming":"nothing"]    //Invalid Hobby type (Map)
    }

    /*
     * [DELETE] Tests for `/person/id`
     */
    @SuppressWarnings("GrUnresolvedAccess")
    def 'Delete a Person' () {
        setup: "Create a Person"
            login()
            Person.initDB()
            Color colorObj = Color.fromString("red")
            String[] hobby = ["reading"]
            Person person = new Person("Paula", "Siemens", 22, colorObj, hobby)
            assert person.id
        
        when: "Delete person"
            def response = client.delete(
                    path : "/api/v${version}/person/${person.id}"
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

        cleanup: 'Logout'
            logout()
    }

    @SuppressWarnings("GrUnresolvedAccess")
    def 'Delete a Person should fail with invalid id' () {
        setup: 'Login'
            List<String> cookies = login()

        when: "Delete person with invalid input"
            def response = client.delete(
                    path : "/api/v${version}/person/${id}",
                    headers : [
                            "Cookie"  : cookies.join(";")
                    ]
            )

        then: 'server returns 400 code (Bad Request)'
            HttpResponseException e = thrown(HttpResponseException)
            assert e.response.status == 400 : 'response code should be 400 if input is invalid'

        cleanup: 'Logout'
            logout()

        where: 'Invalid inputs'
            id      | _
            0       | 0
            -1      | 0
            99999   | 0
            "A"     | 0
    }

}
