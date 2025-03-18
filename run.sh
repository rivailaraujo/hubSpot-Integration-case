#!/bin/bash

# Rodar o Spring Boot em segundo plano
java -jar /app/app.jar &

# Rodar o LocalTunnel para expor a porta 8080
lt --port 8080 --subdomain meetimecaseteste --local-host localhost

# Esperar o processo de LocalTunnel rodar
wait
