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

# Build stage
FROM maven:3.8.4-openjdk-11-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# RUN mvn package
RUN mvn compile -DoutputDirectory=/app/target
RUN ls -a
RUN cd target && ls -a

# Run stage
FROM vertx/vertx4

ENV VERTICLE_NAME=exDock.MainVerticle
ENV VERTICLE_FILE=ex-dock-0.0.1-SNAPSHOT.jar
ENV VERTICLE_HOME=/usr/verticles

EXPOSE 8888

# Copy your verticle from the build stage to the container
COPY --from=build /app/target/$VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
