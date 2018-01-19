#!/bin/bash

export VERSION="0.1.0-SNAPSHOT"

# Check if dependencies are present
if ! [ -x "$(which java)" ]; then
    echo "Java not installed"
    exit
fi

if ! [ -x "$(which psql)" ]; then
    echo "psql not installed"
    exit
else
    psql -d leaflike --command='\q' 2>/dev/null
    if [ $? == 2 ]; then
	echo "Could not connect to the leaflike database"
	exit
    fi
fi

# Clean out old jar file
lein clean

# Build the uberjar
lein uberjar

# Run postgres migrations
java -jar target/uberjar/leaflike-$VERSION-standalone.jar migrate

# Start server
java -jar target/uberjar/leaflike-$VERSION-standalone.jar
