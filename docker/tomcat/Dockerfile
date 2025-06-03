FROM tomcat:7-jdk8

COPY wait-for-mysql.sh /usr/local/bin/wait-for-mysql.sh
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war
RUN chmod +x /usr/local/bin/wait-for-mysql.sh

COPY Dump20250601.sql.gz /usr/local/bin/Dump20250601.sql.gz

USER root
RUN apt-get update && apt-get install -y default-mysql-client gzip && rm -rf /var/lib/apt/lists/*
CMD ["wait-for-mysql.sh", "db", "3306", "--", "catalina.sh", "run"]