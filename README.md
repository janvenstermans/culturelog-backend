# CULTURELOG REST backend

REST bakend for the CultureLog application.

## What is CultureLog?

Keep track of your cultural experiences: save info of your theater, book, film, ... experiences by date.

After logging them, the experiences can be filter by date, location, medium, ....

## Technical info

This rest backend project is a Maven project using Spring boot and persisting to a Postgres database.

### Prerequisits

#### database

Application expects a postgres db running.
Database parameters are specified in application.properties.

default: local server on port 5432, database culturelog, user culturelog with password culturelog.

### Running the application

Application can be run as a spring boot application in a IDE.

If the project is build via "mvm clean install", the jar can be run with its embedded tomcat server via command "java -jar culturelog-rest.jar".

In both cases, the start REST url is http://localhost:8080/api . 
The url can be configured by changing parameters "server.port" or "server.contextPath" in application.properties file 
or add them as startup parameters ("java -jar culturelog-rest.jar --server.port=8081").

### Security

Login/password security is used.

## model documentation

### experience

This is the central object of the api. It is the (cultural) experience that has some kind of date attachted to t.
More in detail, this object contains info about:

* *type* : WHAT: obligatory: kind of experience: film, theater, a sport manifestation, ...
* *moment* : WHEN: obligatory: contains some kind of date/time info. This can be a single date, a date+time, a period or a mix of all those things.
* *location* : WHERE: optional: information about the (geographical) location of an experience: a specific cinema, a venue, ...

## REST api documentation

Default api url startpoint is host/api/...

### User endpoint

create user: POST /users/register , body Json object UserCreateDto

(TODO) update user (not username or password): PUT /users/update , body Json object UserDto

(TODO) change user password: PUT /users/changePassword , body Json object UserCreateDto

### Locations endpoints

create a location for logged in user: POST /locations , body Json object LocationDto

get all locations of logged in user: GET /locations

get one location of logged in user: GET /locations/{locationId}

update a location of logged in user: GET /locations/{locationId} , body Json object LocationDto

### (Experience endpoints: made unavailable)

create experience for logged in user: POST /experiences , body Json object Experience

get all experiences of logged in user: GET /experiences

get one experience of logged in user: GET /experiences/{experienceId}

update an experience of logged in user: GET /experiences/{experienceId} , body Json object Experience