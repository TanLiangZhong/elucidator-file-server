FROM openjdk:11.0.9.1-jre
MAINTAINER LiangZhong.Tan<liangzhong.tan@outlook.com>
VOLUME /tmp
ADD ./build/libs/fs-elucidator-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java -server $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar"]