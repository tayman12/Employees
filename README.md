# Running
- If you want to run the full environment, please use the following gradle command: ./gradlew buildFullEnvironment
- If you want to run from the IDE, please use the following gradle command: ./gradlew buildSupportingEnvironment then you can run main class from IDE 

# Postman Collection
Postman collection of endpoints implemented in this repo is in Employees.postman_collection.json file 

# Original Task
PeopleFlow (www.pplflw.com) is a global HR platform enabling companies to hire & onboard their employees internationally, at the push of a button. It is our mission to create opportunities for anyone to work from anywhere. As work is becoming even more global and remote, there has never been a bigger chance to build a truly global HR-tech company.


As a part of our backend engineering team, you will be responsible for building our core platform including an  employees managment system.

The employees on this system are assigned to different states, Initially when an employee is added it will be assigned "ADDED" state automatically .


The other states (State machine) for A given Employee are:
- ADDED
- IN-CHECK
- APPROVED
- ACTIVE

Our backend stack is:
- Java 11
- Spring Framework
- Kafka


**First Part:**


Your task is to build  Restful API doing the following:
- An Endpoint to support adding an employee with very basic employee details including (name, contract information, age, you can decide.) With initial state "ADDED" which incidates that the employee isn't active yet.

- Another endpoint to change the state of a given employee to "In-CHECK" or any of the states defined above in the state machine

Please provide a solution with the  above features with the following consideration.

- Being simply executable with the least effort Ideally using Docker and docker-compose or any smailiar approach.
- For state machine could be as simple as of using ENUM or by using https://projects.spring.io/spring-statemachine/
- Please provide testing for your solution.
- Providing an API Contract e.g. OPENAPI spec. is a big plus




**Second Part (Optional but a plus):**

Being concerned about developing high quality, resilient software, giving the fact, that you will be participating, mentoring other engineers in the coding review process.


- Suggest what will be your silver bullet, concerns while you're reviewing this part of the software that you need to make sure is being there.
- What the production-readiness criteria that you consider for this solution





**Third Part (Optional but a plus):**
Another Team in the company is building another service, This service will be used to provide some statistics of the employees, this could be used to list the number of employees per country, other types of statistics which is very vague at the moment.


- Please think of a solution without any further implementation that could be able to integrate on top of your service, including the integration pattern will be used, the database storage etc.

A high-level architecture diagram is sufficient to present this.




