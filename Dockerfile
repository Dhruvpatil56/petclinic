# Stage 1: Install JDK
FROM openjdk:11-jdk-slim AS build

# Stage 2: Install Tomcat
FROM build AS tomcat
ENV CATALINA_HOME /opt/tomcat
ENV PATH $CATALINA_HOME/bin:$PATH
ENV TOMCAT_VERSION 9.0.76

# Install curl and download Tomcat separately to debug
RUN apt-get update && apt-get install -y curl

# Download Tomcat
RUN curl -O https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.65/bin/apache-tomcat-9.0.65.tar.gz

# Install Tomcat
RUN mkdir -p $CATALINA_HOME \
    && tar -xvf apache-tomcat-9.0.65.tar.gz -C $CATALINA_HOME --strip-components=1 \
    && rm apache-tomcat-9.0.65.tar.gz

# Clean up
RUN apt-get remove -y curl && apt-get autoremove -y && apt-get clean

# Stage 3: Final image with Tomcat and deployed WAR
FROM tomcat AS final

# Copy the WAR file from your build context to the Tomcat webapps directory
COPY petclinic.war $CATALINA_HOME/webapps/

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]

