#!/bin/bash

cd ../../..

mvn clean package
cp target/couchdb_logger-1.0.0.war src/main/compose/logger.war
cd src/main/compose
docker-compose build
docker-compose up -d

