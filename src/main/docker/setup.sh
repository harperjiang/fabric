#!/bin/bash

# clean up
docker stop couchdb proxy
docker rm couchdb proxy

# network
docker network create --driver bridge fabric-net

# Setup couchdb
cd couchdb
COUCHDB_IMG_ID=$(docker build -q .)
COUCHDB_CONTAINER_ID=$(docker run -d --name couchdb --network fabric-net ${COUCHDB_IMG_ID})

# Setup tomcat
cd ../tomcat
# TODO Copy WAR files
TOMCAT_IMG_ID=$(docker build -q .)
TOMCAT_CONTAINER_ID=$(docker run -d --name proxy --network fabric-net ${TOMCAT_IMG_ID})

# Create Databases
docker exec couchdb curl -X PUT -u fabric:fabric localhost:5984/demo
docker exec couchdb curl -X PUT -u fabric:fabric localhost:5984/fabric