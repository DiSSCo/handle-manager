FROM openjdk:11.0.15
WORKDIR /app

#Dependencies 
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve

#Now for the application
COPY src ./src
CMD ["./mvnw", "spring-boot:run"]

