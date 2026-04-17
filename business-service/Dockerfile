# Dockerfile pour business-service
# Construction multi-étapes pour réduire la taille de l'image

# Étape 1 : Construction avec Maven
FROM maven:3.9.6-eclipse-temurin-21 AS construction
WORKDIR /app
COPY pom.xml .
# Télécharger les dépendances (mise en cache Docker)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Étape 2 : Image d'exécution légère
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=construction /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
