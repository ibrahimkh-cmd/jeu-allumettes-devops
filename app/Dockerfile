# ------------------------------------------------------------
# Étape 1 : Build + tests (Maven)
# ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

# Dépendances d'abord : couche mise en cache tant que pom.xml ne change pas
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Code source, puis compilation + tests JUnit (le build échoue si un test échoue)
COPY src ./src
RUN mvn -B package

# ------------------------------------------------------------
# Étape 2 : Runtime minimal (JRE seule, utilisateur non-root)
# ------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /build/target/jeu-allumettes.jar app.jar
USER app

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["Ibrahim@humain", "Ordinateur@naif"]