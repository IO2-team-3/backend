########build stage########
FROM maven:3.8.7-eclipse-temurin-11-alpine as maven_build
WORKDIR /app

COPY pom.xml .
COPY ./src/main/resources/contracts/api.yml /app/src/main/resources/contracts/api.yml
# To resolve dependencies in a safe way (no re-download when the source code changes)
#RUN mvn clean package -Dmaven.main.skip -Dmaven.test.skip && rm -r target
RUN mvn dependency:go-offline -Dmaven.main.skip -Dmaven.test.skip -B
#RUN mvn -Dskip.plugin:org.springframework.boot:spring-boot-maven-plugin install
COPY src ./src
RUN mvn package -DskipTests
# To package the application
#RUN mvn clean package -Dmaven.test.skip

########run stage########
FROM openjdk:11-jre-slim
WORKDIR /app
EXPOSE 5000
ENV JAVA_OPTS ""

COPY --from=maven_build /app/target/*.jar ./

#run the app
CMD [ "bash", "-c", "java ${JAVA_OPTS} -jar *.jar -v"]