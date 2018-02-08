#!/bin/bash

VERSION="0.1.0-SNAPSHOT"
HOST="139.59.65.123"
JAR_NAME="leaflike-$VERSION-standalone.jar"

SCRIPT="systemctl --user restart leaflike.service"

# Clean out old jar file
lein clean

# Build the uberjar
lein uberjar

echo "Copying jar to server"
scp target/uberjar/$JAR_NAME leaflike@$HOST:leaflike/


#echo "Running migrations"
ssh -t leaflike@$HOST "${SCRIPT}"
