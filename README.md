# Sistema de Encurtador de URLs com AWS

Este projeto implementa um sistema simples e funcional para encurtar URLs utilizando a infraestrutura da AWS. Com ele, é possível gerar links curtos que redirecionam para URLs originais, além de definir prazos de expiração. Tudo é gerenciado de forma serverless.

## Funcionalidades

O sistema é composto por duas funções Lambda:

### 1. Geração de URLs
Responsável por criar links curtos e salvá-los no Amazon S3, armazenando:
- A URL original
- O tempo de expiração
- Um identificador único (UUID)

### 2. Redirecionamento
Valida o código da URL curta, verifica o prazo de expiração e redireciona o usuário para a URL original.

## Pré-requisitos

## Tecnologias Utilizadas

- **AWS Lambda**: Para executar as funções de geração e redirecionamento de URLs.
- **Amazon S3**: Para armazenar os dados em formato JSON.
- **API Gateway**: Para expor endpoints HTTP.

## Configuração

### 1. Criar Funções Lambda

- **GenerateUrlShortener**: Implementa a lógica de criação e armazenamento das URLs.


- **RedirectUrlShortenerLambda**: Gerencia o redirecionamento com validação de códigos.

### 2. Configurar Permissões

- Permita que as funções Lambda leiam e gravem no bucket S3.

### 3. Configurar o API Gateway

- **Rota POST**: `/create` conectada à função `GenerateUrlShortener`.
- **Rota GET**: `/{urlCode}` conectada à função `RedirectUrlShortenerLambda`.
- Depois de implementado o API Gateway, podemos excluir os acessos diretos às funções Lambda, garantindo que todas as requisições passem pelo API Gateway.

## Diagrama da aplicação
![Diagrama da aplicação](diagrama-url.png)
