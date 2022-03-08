FROM couchdb:latest
COPY local.ini /opt/couchdb/etc/local.d/
COPY create-database.sh /
COPY hao-entrypoint.sh /
#ENTRYPOINT ["tini","--","/hao-entrypoint.sh"]
