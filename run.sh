#!/bin/bash

# Change directory to 'target'
cd target || { echo "Directory 'target' not found!"; exit 1; }

# Run the Java client application
java -jar ws-client-1.0-SNAPSHOT-jar-with-dependencies.jar
