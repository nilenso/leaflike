#!/bin/bash

export VERSION="0.1.0-SNAPSHOT"
export HOST="139.59.65.123"
export JAR_NAME="leaflike-$VERSION-standalone.jar"

SCRIPT="systemctl --user restart leaflike.service"

cp resources/config/config.edn.dev resources/config/config.edn

# Clean out old jar file
lein clean

# Build the uberjar
lein uberjar

echo "Copying jar to server"
scp -v target/uberjar/$JAR_NAME leaflike@$HOST:leaflike/


#echo "Running migrations"
ssh -t -v leaflike@$HOST "${SCRIPT}"
