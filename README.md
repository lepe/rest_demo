# REST API Demo

This is a REST API which stores, updates, retrieves and deletes Person entities.

Person data example:

//TODO: check if we are handling "person" as key.
```json
{
    "person": [
      { 
        "first_name": "John",
        "last_name": "Keynes",
        "age": "29",
        "favourite_colour": "red",
        "hobby": ["shopping", "football"]
      },
      { 
        "first_name": "Sarah",
        "last_name": "Robinson",
        "age": "54",
        "favourite_colour": "blue",
        "hobby": ["chess"]
      }
    ]
}
```

## API usage:

| Function       | Visibility | Method   | URI                               | Response                          |
|----------------|------------|----------|-----------------------------------|-----------------------------------|
| List people    | Public     | GET      | /api/v1/people/$offset/$limit     | Array of objects : people list    |
| Search people  | Public     | GET      | /api/v1/people/$keyword           | Array of objects : people list    |
| Get person     | Public     | GET      | /api/v1/person/$id                | Object : person                   |
| Authentication | Public     | POST     | /api/v1/auth                      | Object : status                   |
| Create person  | Private    | POST     | /api/v1/person                    | Object : new id and status        |
| Update person  | Private    | PUT      | /api/v1/person/$id                | Object : status                   |
| Remove person  | Private    | DELETE   | /api/v1/person/$id                | Object : status                   |

### Examples:

#### Public 

To get a list of the first 10 people:
* `/api/v1/people/1/10`

To get a list of 20 people from the 11th position:
* `/api/v1/people/11/20`

Note: It can retrieve a maximum of 100 records at a time (hardcoded limit).

To search people by name: (partial match allowed)
* `/api/v1/people/jen`
* `/api/v1/people/mike`

To get a single person:
* `/api/v1/person/123`

To authorize:
* `/api/v1/auth`

Content example:
```json
{
    "user": "admin",
    "pass": "admin"
}
```

#### Private (Authorization required)

To create a new person:
* `/api/v1/person`

Content example:
```json
{ 
    "first_name": "Sarah",
    "last_name": "Robinson",
    "age": "54",
    "favourite_colour": "blue",
    "hobby": ["chess"]
}
```

To update a person:
* `/api/v1/person/50`

Content example:
```json
{ 
    "favourite_colour": "red",
    "hobby": ["meditation"]
}
```

To delete a person:
* `/api/v1/person/10`

#### Responses

The API will respond with a JSON object, for example:
```json
{
    "ok" : true
}
```
In case no data was found, it will respond with HTTP status 204.

In case of mistaken input, the server will respond with HTTP status 400.

## Technology used:
* Java 11
	* [Spark Framework](http://sparkjava.com/)
	* [GSON](https://github.com/google/gson)
	* [JLine](https://jline.github.io/)
* Groovy 2.5.6
    * [Intellisrc library](https://gitlab.com/intellisrc/common)    (developed by me)
	* [Spock Unit Test Framework](http://spockframework.org/)
* Gradle
* [M2D2](https://github.com/lepe/m2d2)  (JS framework developed by me)
* SQLite

## Developing environment:

### Requirements:
* IntelliJ
* Java 11 JDK
* Git
* Groovy
* Gradle
* rsync

### Development environment:
* Clone this repository: `git clone https://.....`
* Install SDK:
* Install Groovy and Gradle:

### How to build:
* Execute: `./compile` in project directory
The script will create a jar file inside build/libs/ directory.
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

## Production environment:

### Requirements:
* Java 11 JRE
* Modern Browser (IE and Edge may not be supported)

  Note: Tested in:
    - Linux with Chrome, Chromium, Vivaldi, Firefox and Opera.
    - Android with default browser (Chrome for Mobile)

### How to run:
* Execute: `./run`

  This script will launch the service and will listen on port 7777.
You can also execute: `./run stop` to stop the service or
`./run restart` to restart it.

### Maintenance:
* Logs are printed into `<application dir>/logs/` directory. 
To see the logs you can use `lnav` (recommended), `tail -f` or `less -r`.

* The database can be explored using the service console:
`./run console`

  It will launch an interactive console. The available commands can be displayed
typing: `help`. To display all records, for example, type: `list`.

## Limitations:

* The API doesn't support multiple user levels.
* For simplicity, the API is using session based authorization, for a public API, using API Keys or OAuth is recommended.
* UI may not display correctly in Windows and iOS (untested).

## Credits:

* Profile images from: [This Person Doesn't Exists](https://www.thispersondoesnotexist.com/)
* 'Poppins' font from: [Google Fonts](https://fonts.google.com/specimen/Poppins)