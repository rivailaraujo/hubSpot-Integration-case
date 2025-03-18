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

# Instalar Node.js e Localtunnel
RUN apt-get update && apt-get install -y curl && \
    curl -sL https://deb.nodesource.com/setup_16.x | bash && \
    apt-get install -y nodejs && \
    npm install -g localtunnel

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o JAR gerado pela etapa anterior
COPY --from=build /app/build/libs/*.jar app.jar

# Expor a porta
EXPOSE 8080

# Copiar o script run.sh
COPY run.sh /app/run.sh
RUN chmod +x /app/run.sh

# Comando para rodar o script
ENTRYPOINT ["/app/run.sh"]
