FROM gradle:8.14-jdk21

WORKDIR /home/gradle/niffler-ng-10
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./niffler-e-2-e-tests ./niffler-e-2-e-tests
COPY ./niffler-grpc-common ./niffler-grpc-common
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD gradle test -Dtest.env=docker -Drepository=jpa -Duser.timezone=Europe/Moscow
