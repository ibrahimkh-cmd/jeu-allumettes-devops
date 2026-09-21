# ------------------------------------------------------------
# Étape 1 : Build (JDK complet pour compiler le code Java)
# ------------------------------------------------------------
FROM eclipse-temurin:23-jdk-alpine AS builder

WORKDIR /build

COPY src/ ./src/

# Compilation du code applicatif uniquement (sans les tests JUnit)
RUN mkdir -p out && javac -d out $(find src -name "*.java" ! -name "Test*")

# ------------------------------------------------------------
# Étape 2 : Runtime (JRE minimale, pas d'outils de compilation)
# ------------------------------------------------------------
FROM eclipse-temurin:23-jre-alpine

WORKDIR /app

COPY --from=builder /build/out/ ./

ENTRYPOINT ["java", "allumettes.Jouer"]

CMD ["Ibrahim@humain", "Ordinateur@naif"]