# Etapa 1: Use a imagem base do OpenJDK
FROM openjdk:17-jdk-slim AS build

# Defina o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copiar os arquivos do projeto para o contêiner
COPY . .

# Rodar o build do Gradle para gerar o JAR
RUN ./gradlew build

# Etapa 2: Imagem para a aplicação final
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o JAR gerado pela etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Expor a porta
EXPOSE 8080

# Comando para rodar a aplicação Spring Boot
CMD ["java", "-jar", "app.jar"]
