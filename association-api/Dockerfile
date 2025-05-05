FROM tomcat:10.1.40-jdk17

#ARG ENVIRONMENT
#RUN echo ${ENVIRONMENT}

# Add War file
ADD target/vehicle-association-service.war /tmp/vehicle-association-service.war

# Add conf directory
ADD config/dev/ /usr/local/tomcat/conf/vehicle-association-service/

# Add context file
ADD config/dev/context.xml /usr/local/tomcat/conf/Catalina/localhost/vehicle-association-service.xml

#Add server.xml
ADD config/dev/server.xml /usr/local/tomcat/conf/server.xml

ADD src/main/resources/kafka.client.keystore.jks /kafka/ssl/kafka.client.keystore.jks
ADD src/main/resources/kafka.client.truststore.jks /kafka/ssl/kafka.client.truststore.jks

RUN rm -rf /usr/local/tomcat/webapps/* && \
    mv /tmp/vehicle-association-service.war /usr/local/tomcat/webapps
RUN addgroup --system appgroup 
RUN adduser --system appuser --ingroup appgroup
RUN chown -R appuser:appgroup /usr/local/tomcat/
USER appuser
ENTRYPOINT /bin/sh /usr/local/tomcat/bin/catalina.sh run
 

