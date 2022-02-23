FROM tomcat:9.0.58
COPY ../../../../target/CouchDBLogger-1.0-SNAPSHOT.war /var/lib/tomcat9/webapps/ROOT.war
RUN ["rm","-rf","/var/lib/tomcat9/webapps/ROOT"]
