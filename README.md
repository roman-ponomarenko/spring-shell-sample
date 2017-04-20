[maven]: http://maven.apache.org/
[java 8]: http://www.oracle.com/technetwork/java/javase/downloads/index.html

## Preconditions
 - [Java 8][java 8] and [Maven][maven] are required.

To run test you should perform following steps:

1) Clone the repository

2) Go to the repository

2) Before running the application you have to package it first.

 - 2.1 To package application run command below (assuming you are in the project root catalog):

```bash
$ mvn clean package
```

 - 2.2 Go to in signal-distinguisher/target folder
 - 2.3 Run the application by using the command below:

```bash
$ java -jar signal-distinguisher-1.0-SNAPSHOT.jar
```


