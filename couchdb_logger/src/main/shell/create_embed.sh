#!/bin/bash

cd ../../..

mvn clean package
cp target/couchdb_logger-1.0.0-jar-with-dependencies.jar src/main/compose/embed/logger.jar
cd src/main/compose/embed/
docker-compose build
docker-compose up -d

