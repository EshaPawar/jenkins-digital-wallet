FROM tomcat:11.0-jdk21-temurin

RUN rm -rf /usr/local/tomcat/webapps/*
COPY target/digital-wallet.war /usr/local/tomcat/webapps/digital-wallet.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
