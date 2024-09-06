# -- V1 --
## Extend vert.x image
#FROM vertx/vertx4
#
##                                                       (1)
#ENV VERTICLE_NAME=exDock.MainVerticle
#ENV VERTICLE_FILE=target/ex-dock-0.0.1-SNAPSHOT.jar
#
## Set the location of the verticles
#ENV VERTICLE_HOME=/usr/verticles
#
#EXPOSE 8888
#
## Copy your verticle to the container                   (2)
#COPY $VERTICLE_FILE $VERTICLE_HOME/
#
## Launch the verticle
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

# -- V2 --
## Build stage
#FROM maven:3.8.4-openjdk-11-slim AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
##RUN mvn package --fail-at-end
#RUN mvn package --fail-never
##RUN mvn compile -DoutputDirectory=/app/
#RUN ls -a
#RUN cd target && ls -a
#
## Run stage
#FROM vertx/vertx4
#
#ENV VERTICLE_NAME=exDock.MainVerticle
#ENV VERTICLE_FILE=ex-dock-0.0.1-SNAPSHOT.jar
#ENV VERTICLE_HOME=/usr/verticles
#
#EXPOSE 8888
#
## Copy your verticle from the build stage to the container
#COPY --from=build /app/target/$VERTICLE_FILE $VERTICLE_HOME/
#
## Launch the verticle
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

# -- V3 --
## Build stage
#FROM maven:3.8.4-openjdk-11-slim AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#
## Run Maven with verbose output and skip tests
#RUN mvn package -X -DskipTests
#
## List contents of the app directory
#RUN echo "Contents of /app:" && ls -la /app
#
## List contents of the target directory if it exists
#RUN if [ -d "target" ]; then \
#        echo "Contents of /app/target:" && ls -la /app/target; \
#    else \
#        echo "target directory does not exist"; \
#    fi
#
## Display Maven version and Java version
#RUN mvn --version
#RUN java -version
#
## Run stage
#FROM vertx/vertx4
#
##ENV VERTICLE_NAME=exDock.MainVerticle
#ENV VERTICLE_NAME=com.ex_dock.ex_dock.MainVerticle
#ENV VERTICLE_FILE=ex-dock-0.0.1-SNAPSHOT.jar
#ENV VERTICLE_HOME=/usr/verticles
#
#EXPOSE 8888
#
## Copy your verticle from the build stage to the container
#COPY --from=build /app/target/$VERTICLE_FILE $VERTICLE_HOME/
#
## Launch the verticle
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

# -- V4 --
# 1st Docker build stage: build the project with Maven
FROM maven:3.6.3-openjdk-11 as builder
WORKDIR /project
COPY . /project/
RUN mvn package -DskipTests -B

# 2nd Docker build stage: copy builder output and configure entry point
#FROM openjdk:21-jdk
FROM eclipse-temurin:21-jre
ENV APP_DIR /application
ENV APP_FILE ex-dock-0.0.1-SNAPSHOT.jar

EXPOSE 8888

WORKDIR $APP_DIR
COPY --from=builder /project/target/*-fat.jar $APP_DIR/$APP_FILE

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $APP_FILE"]
