## Extend vert.x image
#FROM vertx/vertx4
#
## Set the name of the verticle to deploy
#ENV VERTICLE_NAME=/exDock/MainVerticle.kt
#
## Set the location of the verticles
#ENV VERTICLE_HOME=/usr/verticles
#
#EXPOSE 8080
#
## Copy your verticle to the container
#COPY $VERTICLE_NAME $VERTICLE_HOME/
#
## Launch the verticle
#WORKDIR $VERTICLE_HOME
#ENTRYPOINT ["sh", "-c"]
#CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

# Extend vert.x image
FROM vertx/vertx4

#                                                       (1)
ENV VERTICLE_NAME=exDock.MainVerticle
ENV VERTICLE_FILE=target/exDock-main-verticle-1.0-SNAPSHOT.jar

# Set the location of the verticles
ENV VERTICLE_HOME=/usr/verticles

EXPOSE 8080

# Copy your verticle to the container                   (2)
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
