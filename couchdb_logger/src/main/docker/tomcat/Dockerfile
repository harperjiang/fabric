FROM tomcat:9.0.58
COPY ../../../../target/couchdb_logger-1.0.0.war /var/lib/tomcat9/webapps/ROOT.war
RUN ["rm","-rf","/var/lib/tomcat9/webapps/ROOT"]
