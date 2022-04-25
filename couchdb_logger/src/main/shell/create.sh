#!/bin/bash

network_name=$1
counter=$2
port=$3

# Setup couchdb
cd couchdb
docker run -d --name realcouchdb${counter} --network ${network_name} -e COUCHDB_USER=admin -e COUCHDB_PASSWORD=adminpw couchdb:latest

# Setup tomcat
docker run -d --name couchdb${counter} --network ${network_name} -e COUCHDB_HOST=http://realcouchdb${counter}:5984 tomcat:9.0.53


sleep 10

# init couchdb
docker cp ../couchdb/create-database.sh realcouchdb${counter}:/
docker exec realcouchdb${counter} /bin/bash /create-database.sh

# init tomcat
docker cp ../../../target/couchdb_logger-1.0.0.war couchdb${0}:/var/lib/tomcat9/webapps/ROOT.war
docker restart couchdb${0}