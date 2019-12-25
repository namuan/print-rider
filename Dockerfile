FROM gradle:5.6-jdk11 as gradle
COPY --chown=gradle . /home/app
WORKDIR /home/app
RUN gradle assemble --no-daemon

FROM oracle/graalvm-ce:19.3.0-java11 as graalvm
COPY --from=gradle /home/app/build/libs/print-rider-*-all.jar /home/app/print-server.jar
COPY proxy-config.json /home/app/
WORKDIR /home/app
RUN gu install native-image
RUN native-image --no-server --static -cp /home/app/print-server.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/print-rider /app/print-rider
ENTRYPOINT ["/app/print-rider", "-Djava.library.path=/app"]
