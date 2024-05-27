FROM eclipse-temurin:21-alpine AS jre

# custom java runtime ig?
RUN $JAVA_HOME/bin/jlink \
  --add-modules java.base,java.sql \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress zip-2 \
  --output /javaruntime  

FROM alpine:latest AS build

# system deps for clojure linux install script
RUN apk add curl openjdk21 git bash libsodium

# clojure install (since clojure in alpine repos is annoying)
RUN curl -sSL https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh | sh

WORKDIR /rember

# install java deps
COPY deps.edn .
RUN clojure -X:deps prep

COPY build.clj .
COPY src/ src/
RUN clojure -T:build uberjar

FROM alpine:latest

ARG version

# runtime dep I think?
RUN apk add libsodium

# copy over java runtime
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:$PATH"
COPY --from=jre /javaruntime $JAVA_HOME

WORKDIR /srv/rember
COPY --from=build /rember/target/rember-$version-standalone.jar rember.jar

CMD ["java", "-jar", "rember.jar"]
