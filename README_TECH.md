# Documentação Técnica

Relatando em mais detalhes as escolhas da solução.

## 1. Bibliotecas externas usadas

### 1.1 Lombock
Para deixar o código mais clean e facilitar os getter/setters/builds.

### 1.2 Guava
Criação do cache para guardar os tokens. facilitando o uso do refresh token.

### 1.3 Web/Websocket/Thymeleaf
Para a criação da view onde é possivel testar a aplicação.

### 1.4 SockJS
Se mostrou mais estável com o ngrok

## 2. Estrutura de pastas
```
hubspotintegration/
│── config/              # Configurações da aplicação
│   ├── HubSpotConfig            # Configuração do HubSpot API
│   ├── WebSocketConfig          # Configuração de WebSocket (se aplicável)
│
│── controller/          # Controladores (endpoints REST)
│   ├── DebugController           # Controlador para debug view
│   ├── HubSpotController         # Controlador principal para integração com HubSpot
│   ├── HubSpotWebhookController  # Controlador para receber webhooks do HubSpot
│
│── exception/           # Manipulação de exceções
│   ├── GlobalExceptionHandler    # Handler global para exceções
│   ├── UserExistsException       # Exceção personalizada para usuário já existente
│
│── model/               # Modelos de dados (DTOs)
│   ├── Contact                     # Representação de um contato
│   ├── HubSpotContactRequest       # DTO para requisição de criação de contato
│   ├── HubSpotContactResponse      # DTO para resposta de contato
│   ├── HubSpotTokenRequest         # DTO para requisição de token
│   ├── HubSpotTokenResponse        # DTO para resposta de token
│
│── service/             # Camada de serviços (lógica de negócios)
│   ├── HubSpotService           # Serviço principal para integração com HubSpot
│   ├── HubSpotWebHookService    # Serviço para tratamento de webhooks
│   ├── TokenService             # Serviço para gerenciamento de tokens
│
│── HubSpotIntegrationApplication  # Classe principal da aplicação Spring Boot
```

## 3. Sobre o tratamento Exceptions
Criado um GlobalExceptionHandler usado pra tratar basicamente as duas exceptions esperadas do case:
AuthenticationException e UserExistsException.
Redirecionando para o endpoint de autenticação ou tratando a mensagem para a view.

## 4. Sobre a validação do Webhook Hubspot
[Segundo as instruções da API Webhook](https://developers.hubspot.com/docs/guides/apps/authentication/validating-requests)
o "x-hubspot-signature" é usado no HubSpotWebHookService->calculateSignature para garantir que as solicitações estejam realmente vindo do HubSpot

## 5. Sobre os tokens
Os tokens estão sendo salvos com base no HttpSession. Assim é possivel abrir a aplicação em outro navegador caso queira testar com outra conta HubSpot

## 6. Sobre Rate limit na criação de contatos
Pelo tempo acabei implementando um simples ExponentialBackoff fazendo tentativas e dobrando o tempo de espera entre elas, tentativas essas definidas em 4 o que pode levar a um periodo total de 15s de tentativa da request, que comtempla o reset do rate limit da API de 110 reqs/10seg

## 7. Sobre Melhorias futuras
- Melhorar solução descrita no tópico 6, talvez usando Token.
- Vincular as conexões do websocket ao session também, semelhante ao que é feito com token.
