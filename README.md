This is a project, utilizing Spring MVC.

Technologies used:
- Spring Boot 3 on Java 17
- JUnit 5 & Mockito for unit testing (though no mocks were needed)

How to run:
1. Install JDK 17 (I used java 17.0.11-tem, installing via sdkman, but I guess any version should work)
2. Run `mvn clean install` inside project
3. Run the application either by running `ApplicationMain` via the IDE, or running `mvn spring-boot:run` via the command line
4. Access the app at localhost:8080

Have fun!

Upload csv files of format:
`employee id, project id, start date, end date`
