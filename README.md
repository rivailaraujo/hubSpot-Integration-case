# Projeto HubSpot OAuth Debug

Este projeto é uma aplicação Spring para integração com a API do HubSpot, usando OAuth. A aplicação também integra com WebSockets via SockJS e Stomp.

## Requisitos

Antes de rodar a aplicação, você precisará das seguintes ferramentas:

- **Java 17+**: A aplicação usa o Spring Framework, que requer o Java 17 ou superior.
- **ASDF**: Gerenciador de versões de ferramentas para gerenciar versões de Java, Node.js, e outras dependências.
- **Localtunnel**: Usado para expor sua aplicação local para a web, útil durante o desenvolvimento.

Obs: O ASDF simplificará a instalação dos requesitos.

## 1. Rodando local


### 1.1 Instalando o ASDF

Se você ainda não tiver o ASDF instalado, siga as instruções abaixo:

- [Instruções de instalação do ASDF](https://asdf-vm.com/#/core-manage-asdf-vm)

### 1.2 Instalando as versões de ferramentas necessárias

Com o ASDF instalado, use os seguintes comandos para instalar as versões de Java e Node.js que a aplicação precisa:

```bash

# Instalando asdf plugin Java
asdf plugin-add java

# Instalando asdf plugin node
asdf plugin-add nodejs

# Rodando dentro da pasta do projeto
asdf install
```
### 1.3 Instalar localtunnel
```bash

npm install -g localtunnel
```

### 1.4. Rode a aplicação
```bash

./gradlew bootRun
```

### 1.5 Rode o localtunnel
```bash

# Em outro terminal rode:
lt -p 8080 -s meetimecaseteste
```

### 1.6. Acesso
Acesse aplicação a partir de https://meetimecaseteste.loca.lt


## 2. Rodando pelo docker
```bash

sudo docker-compose build
sudo docker-compose up

#ou
sudo docker-compose up --build
```
