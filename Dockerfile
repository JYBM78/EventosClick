#
# Etapa 1: Construcción del JAR
#
FROM gradle:8.7-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

#
# Etapa 2: Imagen final para ejecutar el JAR
#
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Render asigna el puerto como variable de entorno
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
