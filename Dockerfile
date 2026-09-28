# --- Etapa 1: Compilación ---
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Descargar las dependencias (capa de caché)
RUN ./mvnw dependency:go-offline

# Copiar el código fuente y compilar el JAR
COPY src ./src
RUN ./mvnw clean package -DskipTests

# --- Etapa 2: Imagen de ejecución ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
