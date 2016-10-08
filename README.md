# CULTURELOG REST backend

REST bakend for the CultureLog application.

## What is CultureLog?

Keep track of your cultural experiences: save info of your theater, book, film, ... experiences by date.

After logging them, the experiences can be filter by date, location, medium, ....

## Technical info

This rest backend project is a Maven project using Spring boot and persisting to a Mongo database.

### Prerequisits

Application expects a mongod service running, i.e. "mongo" command is available and connects to database.

Mongod can be run by command "mongod –-dbpath <dbPath>"

### Running the application

Application can be run as a spring boot application in a IDE.

If the project is build via "mvm clean install", the jar can be run with its embedded tomcat server via command "java -jar culturelog-rest.jar".

In both cases, the start REST url is http://localhost:8080/api . 
The url can be configured by changing parameters "server.port" or "server.contextPath" in application.properties file 
or add them as startup parameters ("java -jar culturelog-rest.jar --server.port=8081").

The application will connect to or create a mongo database with name "culturelog". 
This name can be configured by the property "spring.data.mongodb.database" in application.properties file.

### Security

Login/password security is used.

## api documentation

currently, user and experience endpoints exist

### User endpoint

create user: POST /user/register , body Json object UserCreateDto

(TODO) update user (not username or password): PUT /user/update , body Json object UserDto

(TODO) change user password: PUT /user/changePassword , body Json object UserCreateDto

### Experience endpoints

create experience for logged in user: POST /experiences , body Json object Experience

get all experiencea of logged in user: GET /experiences

get one experience of logged in user: GET /experiences/{experienceId}

update an experience of logged in user: GET /experiences/{experienceId} , body Json object Experience