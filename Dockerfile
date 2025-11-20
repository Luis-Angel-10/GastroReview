
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app


COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Hacer ejecutable mvnw
RUN chmod +x mvnw

# Descargar dependencias
RUN ./mvnw dependency:go-offline

# Copiar el código fuente
COPY src src

# Compilar la app
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
