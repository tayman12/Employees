FROM adoptopenjdk/openjdk11:jre-11.0.3_7-alpine
COPY build/libs/employees-0.0.1-SNAPSHOT.jar /employees-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "employees-0.0.1-SNAPSHOT.jar"]