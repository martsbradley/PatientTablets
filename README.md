TODO

Optimistic database checking.

Updates must be in the admin role.


#Run Integration tests.
mvn failsafe:integration-test
mvn failsafe:integration-test -Dit.test=martinbradley.hospital.persistence.repository.PatientDBRepoTestIT
mvn integration-test -Dtest=martinbradley.hospital.persistence.repository.DatabaseTestIT





#Setting Up the Environment to allow.
# Running React from express and proxing to the non http wildfly.
# Running the code without express but in wildfly over https.


To do this the pom build will change.
The java will be jared up in one project.

That jar will be consumed by one of two other web projects.

One that presents as http.
Other that presents https


pom.xml
parent/pom.xl
http/pom.xml
https/pom.xml
code/pom.xml





