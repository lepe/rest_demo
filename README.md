# REST API Demo

This is a REST API which stores, updates, retrieves and deletes Person entities.

API URL: https://rest-demo.alepe.com/api/v1/ 

Demo Site: https://rest-demo.alepe.com/ <br>
   (password: admin) 
   
JavaDoc:  https://rest-demo.alepe.com/doc/ 
   
## API usage:

| Function       | Visibility | Method   | URI                               | Response                          |
|----------------|------------|----------|-----------------------------------|-----------------------------------|
| API Status     | Public     | GET      | /api/v1/                          | Object : status                   |
| List people    | Public     | GET      | /api/v1/people/$offset/$limit     | Array of objects : people list    |
| Search people  | Public     | GET      | /api/v1/people/$keyword           | Array of objects : people list    |
| Get person     | Public     | GET      | /api/v1/person/$id                | Object : person                   |
| Authentication | Public     | POST     | /api/v1/auth/login                |                                   |
| Logout         | Public     | GET      | /api/v1/auth/logout               |                                   |
| Create person  | Private    | POST     | /api/v1/person                    | Object : new id and status        |
| Update person  | Private    | PUT      | /api/v1/person/$id                | Object : status                   |
| Remove person  | Private    | DELETE   | /api/v1/person/$id                | Object : status                   |

### Examples:

#### Public 

To get a list of the first 10 people:
* [GET] [/api/v1/people/1/10](https://rest-demo.alepe.com/api/v1/people/1/10)

To get a list of 20 people from the 11th position:
* [GET] [/api/v1/people/11/20](https://rest-demo.alepe.com/api/v1/people/11/20)

**Note**: It can retrieve a maximum of 100 records at a time (hardcoded limit).

To search for people by name: (partial match allowed)
* [GET] [/api/v1/people/jen](https://rest-demo.alepe.com/api/v1/people/jen)
* [GET] [/api/v1/people/san](https://rest-demo.alepe.com/api/v1/people/san)

Response example for `people` request:
```json
[
    { 
        "id"              : 12,
        "first_name"      : "John",
        "last_name"       : "Keynes",
        "age"             : "29",
        "favourite_colour": "red",
        "hobby"           : ["shopping", "football"]
    },
    { 
        "id"              : 13,
        "first_name"      : "Sarah",
        "last_name"       : "Robinson",
        "age"             : "54",
        "favourite_colour": "blue",
        "hobby"           : ["chess"]
    }
]
```

To get a single person:
* [GET] [/api/v1/person/12](https://rest-demo.alepe.com/api/v1/person/12)

Response example:
```json
{ 
    "id"              : 12,
    "first_name"      : "John",
    "last_name"       : "Keynes",
    "age"             : "29",
    "favourite_colour": "red",
    "hobby"           : ["shopping", "football"]
}
```

To authorize:
* [POST] `/api/v1/auth/login`

Request example:
```json
{
    "user": "admin",
    "pass": "admin"
}
```

To logout:
* [GET] `/api/v1/auth/logout`

#### Private (Authorization required)

To create a new person:
* [POST] `/api/v1/person`

Request example:
```json
{ 
    "first_name": "Sarah",
    "last_name": "Robinson",
    "age": "54",
    "favourite_colour": "blue",
    "hobby": ["chess"]
}
```
Response example (in which "id" is the inserted id):
```json
{
    "ok" : true,
    "id" : 9
}
```

To update a person:
* [PUT] `/api/v1/person/5`

Request example:
```json
{ 
    "favourite_colour": "red",
    "hobby": ["meditation"]
}
```

To delete a person:
* [DELETE] `/api/v1/person/10`

Response example for create and update:
```json
{
    "ok" : true
}
```
In case no data were found, it will respond with HTTP status 204. <br>
In case of mistaken input, the server will respond with HTTP status 400.

## Technology used:
* Java `11` (API code)
	* [Spark Framework](http://sparkjava.com/) (Web Server)
	* [GSON](https://github.com/google/gson) (JSON encoder/decoder)
* Groovy `2.5.6` (Unit and Integration Tests)
    * [Intellisrc library](https://gitlab.com/intellisrc/common) (Utilities and wrappers) : Developed by me
	* [Spock Unit Test Framework](http://spockframework.org/) (Unit Testing)
	* [Groovy HTTPBuilder.RESTClient](https://github.com/jgritman/httpbuilder/wiki/RESTClient) (Integration Testing)
* Gradle `5.4.1` (Build Tool)
* Javascript (User Interface)
    * [M2D2](https://github.com/lepe/m2d2) (UI Framework) : Developed by me
    * [Notie](https://github.com/jaredreich/notie) (Notifications)
* SQLite (Database)

## Setting up the developing environment:

### Requirements:
* IntelliJ
* Java 11 JDK
* Git
* Groovy
* Gradle
* rsync

### Development environment:
* Clone this repository: 
  ```
  git clone https://github.com/lepe/rest_demo
  ```
* Install SDKMAN:  
  ```
  curl -s "https://get.sdkman.io" | bash
  source "$HOME/.sdkman/bin/sdkman-init.sh";
  ```
* Install Groovy and Gradle:
  ```
  sdk install groovy 2.5.6
  sdk install gradle 5.4.1
  ```
* Import the project in IntelliJ
  
### How to build:
* Execute: `gradle compile` in the project directory.

  The script will create a jar file inside `build/libs/` directory.

### How to run tests:
* Execute: `gradle test`

  It will display:
  ```
  <===========--> 88% EXECUTING [39s]
  > :test > 50 tests completed
  > :test > Executing test com.alepe.rest_demo.person.PersonAPITest
  ```
  Finally:
  ```
  BUILD SUCCESSFUL in 42s
  3 actionable tasks: 3 executed
  ```

### How to deploy:
* Execute: `./deploy.sh user@host:path/to/ [port]`

  This script will synchronize with the target host and copy the
required files to execute the services.

## Setting up the production environment:

### Requirements:

Server:

* Linux
* Java 11 JRE

Client:

* Modern Browser (IE is not supported and Edge may not work)

  **Note**: Tested in:
    - Chrome, Chromium, Vivaldi, Firefox and Opera on Linux.
    - Android default browser (Chrome for Mobile)

### How to run:
* Execute: `./run`

  This script will launch the service and will listen on port 7777.
You can also execute: `./run stop` to stop the service or
`./run restart` to restart it.

### Maintenance:
* Logs are printed into `<application dir>/logs/` directory. 
To view the logs you can use `lnav` (recommended), `tail -f` or `less -r`.

## Limitations:

### API :
* It doesn't support multiple user levels (only `guest` and `admin`).
* For simplicity, the API is using session based authentication, for a public API, using API Keys or OAuth is recommended.
* It doesn't provide a service to verify if user is logged in or not.
* Names are limited to `FirstName` + `space` + `LastName`. No middle names or single name allowed.
* Unicode encoded names (names with characters outside the English alphabet) are not supported to simplify validation.
* By design, it supports only a limited number of Colors.

### User Interface:
* The UI may not be displayed correctly in Windows and iOS (untested).
* The UI is very simple and can be enhanced in many ways which are outside the scope of this demonstration 
(e.g. people search, table sorting, pagination, auto-scrolling, picture upload, picture edition, password change, etc).
* Profile photos are assigned using person's id, they can not be uploaded or modified.
* The UI comes with up to 100 different profile photos (above 100, the images will be recycled).
* It doesn't support pagination, so a maximum of 100 people can be displayed on the screen. Ideally it should load and 
display items as needed (aka RecyclerView).
* For simplicity, only password is requested (user is fixed as 'admin').

## Security
* Authentication is done through user/password verification.
* Passwords are stored in database using BCrypt hash algorithm.
* Authorization is achieved using a session and comparing the original IP address to the one requesting the service.
* All user inputs are sanitazed and are parametrized to prevent SQL Injection and XSS attacks.
* No external -untrusted- resources/scripts are loaded or injected in any way (with the exception of a google font).
* API content-type is always "application/json".
* Only HTTPS is used (Using Let's Encrypt certification).
* Cookies are secured with "HttpOnly" and "Secure" flags.
* Using Content-Security-Policy header to protect against XSS attacks.

## Credits:

* Profile images from: [This Person Doesn't Exists](https://www.thispersondoesnotexist.com/)
* `Poppins` font from: [Google Fonts](https://fonts.google.com/specimen/Poppins)
