# falae.dev API

API REST para a plataforma falae.dev - um forum focado em desenvolvedores.

## Tech Stack

- **Java 25** + **Spring Boot 3.5.6**
- **PostgreSQL** com Flyway migrations
- **JWT** para autenticacao (+ Google OAuth)
- **AWS S3** para armazenamento de arquivos
- **Testcontainers** para testes de integracao

## Arquitetura

Arquitetura Hexagonal (Ports and Adapters) com 3 modulos Maven:

```
core (dominio) → application (use cases) → infrastructure (adapters)
```

| Modulo | Responsabilidade |
|--------|------------------|
| **core** | Entidades de dominio puras (Article, Author, Comment, Topic, Admin) |
| **application** | Use cases, ports (interfaces) e DTOs |
| **infrastructure** | Spring Boot, controllers, JPA, servicos externos |

## Executando

```bash
# Build
./mvnw clean install

# Build sem testes
./mvnw clean install -DskipTests

# Rodar a aplicacao
./mvnw spring-boot:run -pl infrastructure

# Rodar testes
./mvnw test
```

## Principais Funcionalidades

- **Autores**: Cadastro, login, perfil, foto de perfil
- **Artigos**: CRUD, upload de conteudo (md/html), capa e imagens
- **Topicos**: Discussoes no forum
- **Comentarios**: Aninhados, suporte a replies
- **Interacoes**: Like, dislike, save
- **BugCoins**: Sistema de gamificacao para recompensar contribuicoes
- **Admin**: Moderacao de conteudo

## API

Documentacao completa das rotas em [API_ROUTES.md](API_ROUTES.md).

### Endpoints Principais

| Controller | Base Path | Descricao |
|------------|-----------|-----------|
| Auth | `/auth` | Login, logout, Google OAuth, verificacao de email |
| Authors | `/api/authors` | Perfil e conteudo do autor |
| Articles | `/article` | CRUD de artigos |
| Topics | `/api/topic` | CRUD de topicos |
| Comments | `/api/comment` | CRUD de comentarios |
| Feed | `/api/feed` | Feed unificado |
| Search | `/api/search` | Busca por tags |
| Admin | `/api/admin` | Operacoes administrativas |

## Variaveis de Ambiente

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/falae
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JWT
JWT_SECRET=your-secret-key

# AWS S3 / R2
AWS_ACCESS_KEY_ID=...
AWS_SECRET_ACCESS_KEY=...
AWS_S3_BUCKET=...
AWS_S3_ENDPOINT=...

# Google OAuth
GOOGLE_CLIENT_ID=...

# Email (opcional)
RESEND_API_KEY=...
```

## Licenca

Projeto privado.
