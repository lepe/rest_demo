# REST API Demo

This is a REST API which stores, updates, retrieves and deletes Person entities.

## API usage:

| Function       | Visibility | Method   | URI                               | Response                          |
|----------------|------------|----------|-----------------------------------|-----------------------------------|
| List people    | Public     | GET      | /api/v1/people/$offset/$limit     | Array of objects : people list    |
| Search people  | Public     | GET      | /api/v1/people/$keyword           | Array of objects : people list    |
| Get person     | Public     | GET      | /api/v1/person/$id                | Object : person                   |
| Authentication | Public     | POST     | /api/v1/auth/login                | Object : status                   |
| Logout         | Public     | GET      | /api/v1/auth/logout               | Object : status                   |
| Create person  | Private    | POST     | /api/v1/person                    | Object : new id and status        |
| Update person  | Private    | PUT      | /api/v1/person/$id                | Object : status                   |
| Remove person  | Private    | DELETE   | /api/v1/person/$id                | Object : status                   |

### Examples:

#### Public 

To get a list of the first 10 people:
* [GET] `/api/v1/people/1/10`

To get a list of 20 people from the 11th position:
* [GET] `/api/v1/people/11/20`

**Note**: It can retrieve a maximum of 100 records at a time (hardcoded limit).

To search people by name: (partial match allowed)
* [GET] `/api/v1/people/jen`
* [GET] `/api/v1/people/mike`

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
* [GET] `/api/v1/person/12`

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
* Java `11`
	* [Spark Framework](http://sparkjava.com/)
	* [GSON](https://github.com/google/gson)
	* [JLine](https://jline.github.io/)
* Groovy `2.5.6`
    * [Intellisrc library](https://gitlab.com/intellisrc/common) (developed by me)
	* [Spock Unit Test Framework](http://spockframework.org/)
	* [Groovy HTTPBuilder.RESTClient](https://github.com/jgritman/httpbuilder/wiki/RESTClient)
* Gradle `5.4.1`
* Javascript
    * [M2D2](https://github.com/lepe/m2d2) (Framework developed by me)
    * [Notie](https://github.com/jaredreich/notie)
* SQLite

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
  
### How to build:
* Execute: `./compile` in project directory.

  The script will create a jar file inside `build/libs/` directory.
  Alternatively you can run: `gradle shadowJar`

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
  3 actionable tasks: 1 executed, 2 up-to-date
  ```

### How to deploy:
* Execute: `./deploy [ip|hostname]`

  This script will synchronize with the target host and copy the
required files to execute the services.

## Setting up the production environment:

### Requirements:
* Java 11 JRE
* Modern Browser (IE and Edge may not be supported)

  **Note**: Tested in:
    - Linux with Chrome, Chromium, Vivaldi, Firefox and Opera.
    - Android with default browser (Chrome for Mobile)

### How to run:
* Execute: `./run`

  This script will launch the service and will listen on port 7777.
You can also execute: `./run stop` to stop the service or
`./run restart` to restart it.

### Maintenance:
* Logs are printed into `<application dir>/logs/` directory. 
To view the logs you can use `lnav` (recommended), `tail -f` or `less -r`.

* The database can be explored using the service console:
`./run console`

  It will launch an interactive console. The available commands can be displayed
typing: `help`. To display all records, for example, type: `list`.

## API and UI Limitations:

* The API doesn't support multiple user levels (only `guest` and `admin`).
* For simplicity, the API is using session based authentication, for a public API, using API Keys or OAuth is recommended.
* UI may not display correctly in Windows and iOS (untested).
* The UI is very simple and can be enhanced in many ways which are outside the scope of this demonstration.
* Names are limited to `FirstName` + `space` + `LastName`. No middle names or single name allowed.
* Unicode encoded names (names with characters outside the English alphabet) are not supported for validation simplicity.
* Profile photos are assigned using person's id, they can not be uploaded or modified.
* The system comes with up to 100 different profile photos (above 100, the images will be recycled).
* The UI doesn't support pagination, so a maximum of 100 people can be displayed on screen. Ideally it should load and 
display items as needed (aka RecyclerView).
* The system supports a limited number of Colors (sorry no navy, ivory, honey, cherry, etc).

## Credits:

* Profile images from: [This Person Doesn't Exists](https://www.thispersondoesnotexist.com/)
* `Poppins` font from: [Google Fonts](https://fonts.google.com/specimen/Poppins)