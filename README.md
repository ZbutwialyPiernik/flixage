# Flixage
Music service project using Spring Boot, heavily inspired by Spotify. This is my first try to create microservice architecture. 
Project contains 8 modules, frontend done in Flutter, docker-compose for ready local development, database schema.

 1. **core** - domain exceptions, common configurations for every sub project. 
 2. **core-authentication** - contains classes that are needed to provide authentication to microservice
 3. **core-resource** - core resource logic shared between **service-resource** and **service-admin**
 4. **service-discovery** - Service discovery microservice with [Eureka](https://github.com/mechero/spring-boot-eureka)
 5. **service-admin** - Admin panel microservice with [Vaadin](https://vaadin.com/) as frontend, uses normal cookies as session. For now whole front-end is done with Vaadin server-side components, without any custom styling.
 6. **service-gateway** - Gateway microservice using  [Spring Cloud Gateway](https://github.com/spring-cloud/spring-cloud-gateway) allows adding new artists, albums, tracks. 
 7. **service-resource** - Resource microservice, serves content thought REST API to [Flutter](https://flutter.dev/) frontend, uses JWT tokens as stateless Authentication. For media files storage project uses [Spring Content](https://paulcwarren.github.io/spring-content/).
 8. **service-authentication** - Authentication microservice with JWT tokens, allows registering new users. 

## Todo list
- Custom gradle task with docker
- Fix docker compose
- Tests, tests, tests and more tests... 
- Connect admin panel microservice to eureka
- Importing artist data from external services like last.fm API
- Use Hashcorp Vault to externalize configuration
- Register/Change password endpoints