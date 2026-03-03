# API Routes - falae.dev-api

Documentacao das rotas da API organizadas por controller.

---

## AuthController (`/auth`)

### POST `/auth/author/login`
**Descricao:** Autentica um autor no sistema.

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "message": "string",
  "role": "string",
  "email": "string",
  "userName": "string",
  "profileImageUrl": "string",
  "name": "string",
  "token": "string",
  "emailVerified": "boolean"
}
```

---

### POST `/auth/admin/login`
**Descricao:** Autentica um administrador no sistema.

**Request Body:**
```json
{
  "email": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "message": "string",
  "role": "string",
  "email": "string",
  "userName": null,
  "profileImageUrl": null,
  "name": "string",
  "token": "string",
  "emailVerified": true
}
```

---

### POST `/auth/google`
**Descricao:** Autentica ou cadastra um autor via Google. Se o email ja existir com login Google, faz login. Se nao existir, cria uma nova conta.

**Request Body:**
```json
{
  "credential": "string (Google ID Token)"
}
```

**Response:**
```json
{
  "message": "string",
  "role": "string",
  "email": "string",
  "userName": "string",
  "profileImageUrl": "string",
  "name": "string",
  "token": "string",
  "emailVerified": true
}
```

**Erros:**
- `401 Unauthorized` - Token do Google invalido ou email nao verificado
- `422 Unprocessable Entity` - Email ja registrado com login por senha

> **Nota:** Contas Google sempre tem `emailVerified: true` pois o Google ja verifica o email.

---

### GET `/auth/validate`
**Descricao:** Valida se o token JWT do usuario ainda e valido.

**Response:**
- `200 OK` - Token valido
- `401 Unauthorized` - Token invalido ou ausente

---

### POST `/auth/logout`
**Descricao:** Realiza o logout do usuario, removendo o cookie de autenticacao.

**Response:** `"Logout successful"`

---

### GET `/auth/verify-email`
**Descricao:** Verifica o email do autor atraves do token enviado por email. Retorna o userName e um token de login para o frontend redirecionar o usuario logado para seu perfil.

**Query Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| token | string | Token JWT de verificacao enviado por email |

**Response:**
```json
{
  "message": "Email verified successfully",
  "userName": "string",
  "token": "string"
}
```

**Erros:**
- `400 Bad Request` - Token invalido ou expirado
- `422 Unprocessable Entity` - Email ja verificado

> **Nota:** O token de login tambem e setado como cookie HTTP-only.

---

### POST `/auth/resend-verification`
**Descricao:** Reenvia o email de verificacao para o autor.

**Query Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| email | string | Email do autor |

**Response:** `"Verification email sent successfully"`

**Erros:**
- `404 Not Found` - Email nao encontrado
- `422 Unprocessable Entity` - Email ja verificado ou conta Google (verificacao automatica)

---

## AuthorController (`/api/authors`)

### POST `/api/authors/create`
**Descricao:** Cadastra um novo autor.

**Request Body:**
```json
{
  "email": "string",
  "password": "string",
  "name": "string",
  "userName": "string"
}
```

**Response:**
```json
{
  "message": "Author created successfully. Please check your email to verify your account.",
  "id": "UUID",
  "email": "string",
  "name": "string"
}
```

> **Nota:** Ao criar uma conta, um email de verificacao e enviado automaticamente. O autor deve verificar o email para desbloquear todas as funcionalidades (quando `emailVerificationRequired` estiver ativado no ForumConfig).

---

### PATCH `/api/authors/basic-profile-update`
**Descricao:** Atualiza informacoes basicas do perfil do autor autenticado.

**Request Body:**
```json
{
  "name": "string",
  "gitHub": "string",
  "bio": "string"
}
```

**Response:**
```json
{
  "message": "string",
  "name": "string",
  "gitHub": "string",
  "bio": "string"
}
```

---

### PATCH `/api/authors/profile`
**Descricao:** Atualiza informacoes do perfil do autor autenticado. Todos os campos sao opcionais. Email e userName devem ser unicos.

**Request Body:**
```json
{
  "email": "string | null",
  "password": "string | null",
  "name": "string | null",
  "userName": "string | null",
  "gitHub": "string | null",
  "profileImageUrl": "string | null",
  "bio": "string | null"
}
```

**Validacoes:**
| Campo | Regra |
|-------|-------|
| email | Deve ser um email valido |
| password | Minimo 6 caracteres |
| userName | 3-30 caracteres, apenas letras minusculas, numeros e hifens |
| gitHub | URL valida do GitHub (ex: https://github.com/usuario) |
| bio | Maximo 500 caracteres |

**Response:**
```json
{
  "message": "string",
  "email": "string",
  "name": "string",
  "userName": "string",
  "gitHub": "string",
  "profileImageUrl": "string",
  "bio": "string"
}
```

**Erros:**
- `409 Conflict` - Email ou userName ja existe para outro autor

---

### POST `/api/authors/profile-image`
**Descricao:** Envia a foto de perfil do autor autenticado. Requer autenticacao de autor. Se ja existir uma imagem de perfil no bucket R2, ela sera deletada automaticamente.

**Content-Type:** `multipart/form-data`

**Parametros:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| file | MultipartFile | Imagem de perfil (JPG/JPEG, PNG, GIF, WEBP) |

**Restricoes:**
- Tamanho maximo: 600 KB
- Formatos aceitos: JPG/JPEG, PNG, GIF, WEBP

**Response:** `string` (URL da imagem de perfil)

**Erros:**
- `400 Bad Request` - Arquivo excede 600 KB ou formato invalido
- `401 Unauthorized` - Usuario nao autenticado

---

### GET `/api/authors/{userName}`
**Descricao:** Busca o perfil publico de um autor pelo userName.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| userName | string | Username do autor |

**Response:**
```json
{
  "id": "UUID",
  "name": "string",
  "userName": "string",
  "gitHub": "string",
  "profileImageUrl": "string",
  "bio": "string",
  "bugCoins": "int",
  "title": "string",
  "articleCount": "long",
  "topicCount": "long",
  "commentCount": "long",
  "emailVerified": "boolean"
}
```

---

### GET `/api/authors/{userName}/articles`
**Descricao:** Lista artigos de um autor paginados.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| userName | string | Username do autor |

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | AuthorContentSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES, SAVES, COMMENTS) |

**Response:** `ArticlePageResponse` (mesmo formato de `/article`)

---

### GET `/api/authors/{userName}/topics`
**Descricao:** Lista topicos de um autor paginados.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| userName | string | Username do autor |

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | AuthorContentSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES, COMMENTS) |

**Response:** `TopicPageResponse` (mesmo formato de `/api/topic`)

---

### GET `/api/authors/{userName}/comments`
**Descricao:** Lista comentarios de um autor paginados.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| userName | string | Username do autor |

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | AuthorContentSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES) |

**Response:** `CommentPageResponse` (mesmo formato de `/api/comment`)

---

## ArticleController (`/article`)

### POST `/article/saveNew`
**Descricao:** Cadastra um novo artigo. Requer autenticacao de autor.

**Request Body:**
```json
{
  "title": "string",
  "description": "string",
  "tags": ["string"],
  "originalPost": "string"
}
```

**Response:**
```json
{
  "message": "string",
  "id": "UUID",
  "title": "string",
  "slug": "string"
}
```

> **Nota:** Ao criar um artigo, o autor recebe bugCoins automaticamente conforme configurado em `ForumConfig.coinsPerArticle`.

---

### PUT `/article/edit`
**Descricao:** Edita um artigo existente. Requer autenticacao de autor (dono do artigo). Todos os campos sao opcionais exceto articleId.

**Request Body:**
```json
{
  "articleId": "UUID",
  "title": "string | null",
  "description": "string | null",
  "tags": ["string"] | null,
  "originalPost": "string | null",
  "deletedImagePaths": ["string"] | null
}
```

**Validacoes:**
| Campo | Regra |
|-------|-------|
| articleId | Obrigatorio |
| title | Maximo 255 caracteres |
| description | Maximo 500 caracteres |
| tags | Cada tag: maximo 50 caracteres, apenas letras e numeros |
| originalPost | Deve ser uma URL valida |
| deletedImagePaths | Lista de caminhos de imagens a serem removidas |

**Response:** `200 OK` (corpo vazio)

---

### POST `/article/saveArticleContent`
**Descricao:** Envia o arquivo com o conteudo do artigo (extensao .md ou .html). Requer autenticacao de autor.

**Content-Type:** `multipart/form-data`

**Parametros:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| file | MultipartFile | Arquivo .md ou .html |
| articleId | UUID | ID do artigo |

**Response:** `string` (URL do conteudo)

---

### POST `/article/saveArticleCover`
**Descricao:** Envia a capa do artigo (extensao JPG/JPEG, PNG, GIF, WEBP). Requer autenticacao de autor.

**Content-Type:** `multipart/form-data`

**Parametros:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| file | MultipartFile | Imagem de capa |
| articleId | UUID | ID do artigo |

**Response:** `string` (URL da capa)

---

### POST `/article/saveArticleImage`
**Descricao:** Envia uma imagem para o artigo (extensao JPG/JPEG, PNG, GIF, WEBP). Requer autenticacao de autor.

**Content-Type:** `multipart/form-data`

**Parametros:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| file | MultipartFile | Imagem |
| articleId | UUID | ID do artigo |

**Response:** `string` (URL da imagem)

---

### GET `/article`
**Descricao:** Lista artigos paginados.

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | FeedSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES) |

**Response:**
```json
{
  "articles": [
    {
      "id": "UUID",
      "authorId": "UUID",
      "authorName": "string",
      "authorUserName": "string",
      "authorProfileImage": "string | null",
      "creationDate": "LocalDateTime",
      "isMarkdown": "boolean",
      "title": "string",
      "slug": "string",
      "coverImage": "string",
      "originalPost": "string",
      "tags": ["string"],
      "description": "string",
      "urlArticleContent": "string",
      "likesCount": "int",
      "dislikesCount": "int",
      "commentsCount": "int",
      "savesCount": "int",
      "isLiked": "boolean | null",
      "isDisliked": "boolean | null",
      "isSaved": "boolean | null"
    }
  ],
  "page": "int",
  "size": "int",
  "totalElements": "long",
  "totalPages": "int",
  "hasNext": "boolean"
}
```

---

### GET `/article/search`
**Descricao:** Busca artigos por titulo.

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| title | string | obrigatorio | Termo de busca no titulo |
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | AuthorContentSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES, SAVES, COMMENTS) |

**Response:** `ArticlePageResponse` (mesmo formato de `/article`)

---

### GET `/article/{articleId}`
**Descricao:** Busca um artigo por ID.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| articleId | UUID | ID do artigo |

**Response:**
```json
{
  "id": "UUID",
  "authorId": "UUID",
  "authorName": "string",
  "authorUserName": "string",
  "authorProfileImage": "string | null",
  "creationDate": "LocalDateTime",
  "isMarkdown": "boolean",
  "title": "string",
  "slug": "string",
  "coverImage": "string",
  "originalPost": "string",
  "tags": ["string"],
  "description": "string",
  "urlArticleContent": "string",
  "likesCount": "int",
  "dislikesCount": "int",
  "commentsCount": "int",
  "savesCount": "int",
  "isLiked": "boolean | null",
  "isDisliked": "boolean | null",
  "isSaved": "boolean | null"
}
```

---

### GET `/article/{userName}/{slug}`
**Descricao:** Busca um artigo pelo userName do autor e slug.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| userName | string | Username do autor |
| slug | string | Slug do artigo (URL-friendly) |

**Response:**
```json
{
  "id": "UUID",
  "authorId": "UUID",
  "authorName": "string",
  "authorUserName": "string",
  "authorProfileImage": "string | null",
  "creationDate": "LocalDateTime",
  "isMarkdown": "boolean",
  "title": "string",
  "slug": "string",
  "coverImage": "string",
  "originalPost": "string",
  "tags": ["string"],
  "description": "string",
  "urlArticleContent": "string",
  "likesCount": "int",
  "dislikesCount": "int",
  "commentsCount": "int",
  "savesCount": "int",
  "isLiked": "boolean | null",
  "isDisliked": "boolean | null",
  "isSaved": "boolean | null"
}
```

---

### DELETE `/article/delete/{articleId}`
**Descricao:** Deleta um artigo. Requer autenticacao de autor (dono do artigo).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| articleId | UUID | ID do artigo |

**Response:** `204 No Content`

---

### POST `/article/{articleId}/like`
**Descricao:** Curtir/descurtir um artigo. Requer autenticacao de autor.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| articleId | UUID | ID do artigo |

**Response:**
```json
{
  "isActive": "boolean",
  "message": "string"
}
```

---

### POST `/article/{articleId}/dislike`
**Descricao:** Dar/remover dislike em um artigo. Requer autenticacao de autor.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| articleId | UUID | ID do artigo |

**Response:**
```json
{
  "isActive": "boolean",
  "message": "string"
}
```

> **Nota:** Like e dislike sao mutuamente exclusivos. Ao dar dislike em algo que ja tem like, o like e removido automaticamente.

---

### POST `/article/{articleId}/save`
**Descricao:** Salvar/remover dos salvos um artigo. Requer autenticacao de autor.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| articleId | UUID | ID do artigo |

**Response:**
```json
{
  "isActive": "boolean",
  "message": "string"
}
```

---

## TopicController (`/api/topic`)

### POST `/api/topic/saveNew`
**Descricao:** Cadastra um novo topico. Requer autenticacao de autor.

**Request Body:**
```json
{
  "title": "string",
  "topicContent": "string",
  "tags": ["string"] | null
}
```

**Response:**
```json
{
  "id": "UUID",
  "title": "string",
  "slug": "string"
}
```

> **Nota:** Ao criar um topico, o autor recebe bugCoins automaticamente conforme configurado em `ForumConfig.coinsPerTopic`.

---

### PUT `/api/topic/edit`
**Descricao:** Edita um topico existente. Requer autenticacao de autor (dono do topico). Todos os campos sao opcionais exceto topicId.

**Request Body:**
```json
{
  "topicId": "UUID",
  "title": "string | null",
  "topicContent": "string | null",
  "tags": ["string"] | null
}
```

**Validacoes:**
| Campo | Regra |
|-------|-------|
| topicId | Obrigatorio |
| title | Maximo 255 caracteres |
| topicContent | Maximo 10000 caracteres |

**Response:** `200 OK` (corpo vazio)

---

### GET `/api/topic`
**Descricao:** Lista topicos paginados.

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | FeedSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES) |

**Response:**
```json
{
  "topics": [
    {
      "id": "UUID",
      "authorId": "UUID",
      "authorName": "string",
      "authorUserName": "string",
      "authorProfileImage": "string | null",
      "creationDate": "LocalDateTime",
      "title": "string",
      "slug": "string",
      "topicContent": "string",
      "tags": ["string"],
      "likesCount": "int",
      "dislikesCount": "int",
      "commentsCount": "int",
      "isLiked": "boolean | null",
      "isDisliked": "boolean | null"
    }
  ],
  "page": "int",
  "size": "int",
  "totalElements": "long",
  "totalPages": "int",
  "hasNext": "boolean"
}
```

---

### GET `/api/topic/search`
**Descricao:** Busca topicos por titulo.

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| title | string | obrigatorio | Termo de busca no titulo |
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | AuthorContentSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES, COMMENTS) |

> **Nota:** A opcao SAVES nao e suportada para topicos.

**Response:** `TopicPageResponse` (mesmo formato de `/api/topic`)

---

### GET `/api/topic/{topicId}`
**Descricao:** Busca um topico por ID.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| topicId | UUID | ID do topico |

**Response:**
```json
{
  "id": "UUID",
  "authorId": "UUID",
  "authorName": "string",
  "authorUserName": "string",
  "authorProfileImage": "string | null",
  "creationDate": "LocalDateTime",
  "title": "string",
  "slug": "string",
  "topicContent": "string",
  "tags": ["string"],
  "likesCount": "int",
  "dislikesCount": "int",
  "commentsCount": "int",
  "isLiked": "boolean | null",
  "isDisliked": "boolean | null"
}
```

---

### GET `/api/topic/{userName}/{slug}`
**Descricao:** Busca um topico pelo userName do autor e slug.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| userName | string | Username do autor |
| slug | string | Slug do topico (URL-friendly) |

**Response:**
```json
{
  "id": "UUID",
  "authorId": "UUID",
  "authorName": "string",
  "authorUserName": "string",
  "authorProfileImage": "string | null",
  "creationDate": "LocalDateTime",
  "title": "string",
  "slug": "string",
  "topicContent": "string",
  "tags": ["string"],
  "likesCount": "int",
  "dislikesCount": "int",
  "commentsCount": "int",
  "isLiked": "boolean | null",
  "isDisliked": "boolean | null"
}
```

---

### DELETE `/api/topic/delete/{topicId}`
**Descricao:** Deleta um topico. Requer autenticacao de autor (dono do topico).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| topicId | UUID | ID do topico |

**Response:** `204 No Content`

---

### POST `/api/topic/{topicId}/like`
**Descricao:** Curtir/descurtir um topico. Requer autenticacao de autor.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| topicId | UUID | ID do topico |

**Response:**
```json
{
  "isActive": "boolean",
  "message": "string"
}
```

---

### POST `/api/topic/{topicId}/dislike`
**Descricao:** Dar/remover dislike em um topico. Requer autenticacao de autor.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| topicId | UUID | ID do topico |

**Response:**
```json
{
  "isActive": "boolean",
  "message": "string"
}
```

> **Nota:** Like e dislike sao mutuamente exclusivos. Ao dar dislike em algo que ja tem like, o like e removido automaticamente.

---

## CommentController (`/api/comment`)

### POST `/api/comment/save`
**Descricao:** Cadastra um novo comentario. Requer autenticacao de autor.

**Request Body:**
```json
{
  "message": "string",
  "articleId": "UUID | null",
  "topicId": "UUID | null",
  "parentId": "UUID | null",
  "tags": ["string"] | null
}
```

> **Nota:** Deve-se fornecer `articleId` OU `topicId` para comentarios raiz. `parentId` e opcional para respostas.
>
> **Nota:** Ao criar um comentario, o autor recebe bugCoins automaticamente conforme configurado em `ForumConfig.coinsPerComment`.

**Response:**
```json
{
  "message": "string",
  "id": "UUID",
  "content": "string"
}
```

---

### GET `/api/comment`
**Descricao:** Lista todos os comentarios paginados.

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | FeedSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES) |

**Response:**
```json
{
  "comments": [
    {
      "id": "UUID",
      "content": "string",
      "authorName": "string",
      "authorUserName": "string",
      "authorProfileImage": "string | null",
      "authorId": "UUID",
      "createdAt": "LocalDateTime",
      "likes": "int",
      "dislikes": "int",
      "depth": "int",
      "replyCount": "long",
      "parentId": "UUID | null",
      "articleId": "UUID | null",
      "topicId": "UUID | null",
      "deleted": "boolean",
      "parentAuthorUserName": "string | null",
      "parentTitle": "string | null",
      "parentSlug": "string | null",
      "isLiked": "boolean | null",
      "isDisliked": "boolean | null",
      "isOwner": "boolean | null",
      "tags": ["string"] | null
    }
  ],
  "page": "int",
  "size": "int",
  "totalElements": "long",
  "totalPages": "int",
  "hasNext": "boolean"
}
```

> **Nota:** Os campos `parentAuthorUserName`, `parentTitle` e `parentSlug` indicam o autor, titulo e slug do artigo/topico ao qual o comentario pertence. Use `parentSlug` junto com `parentAuthorUserName` para construir URLs SEO-friendly (ex: `/{parentAuthorUserName}/{parentSlug}`).
>
> **Nota:** Os campos `isLiked`, `isDisliked` e `isOwner` sao preenchidos apenas quando o usuario esta autenticado.
>
> **Nota:** Quando `deleted: true`, os campos `content`, `authorName`, `authorUserName`, `authorProfileImage`, `authorId`, `likes` e `dislikes` retornam `null` ou `0`. O comentario permanece na arvore para manter a estrutura de respostas, permitindo que o front exiba como "[comentario removido]".

---

### GET `/api/comment/article/{articleId}`
**Descricao:** Busca comentarios raiz de um artigo (paginado).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| articleId | UUID | ID do artigo |

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |

**Response:** `CommentPageResponse` (mesmo formato acima)

---

### GET `/api/comment/topic/{topicId}`
**Descricao:** Busca comentarios raiz de um topico (paginado).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| topicId | UUID | ID do topico |

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |

**Response:** `CommentPageResponse` (mesmo formato acima)

---

### GET `/api/comment/{commentId}/replies`
**Descricao:** Busca respostas diretas de um comentario.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| commentId | UUID | ID do comentario |

**Response:**
```json
[
  {
    "id": "UUID",
    "content": "string",
    "authorName": "string",
    "authorUserName": "string",
    "authorProfileImage": "string | null",
    "authorId": "UUID",
    "createdAt": "LocalDateTime",
    "likes": "int",
    "dislikes": "int",
    "depth": "int",
    "replyCount": "long",
    "parentId": "UUID",
    "articleId": "UUID | null",
    "topicId": "UUID | null",
    "deleted": "boolean",
    "parentAuthorUserName": "string | null",
    "parentTitle": "string | null",
    "parentSlug": "string | null",
    "isLiked": "boolean | null",
    "isDisliked": "boolean | null",
    "isOwner": "boolean | null",
    "tags": ["string"] | null
  }
]
```

---

### GET `/api/comment/{commentId}`
**Descricao:** Busca um comentario por ID.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| commentId | UUID | ID do comentario |

**Response:** `CommentResponse` (mesmo formato acima)

---

### DELETE `/api/comment/delete/{commentId}`
**Descricao:** Deleta um comentario. Requer autenticacao de autor (dono do comentario).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| commentId | UUID | ID do comentario |

**Response:** `204 No Content`

> **Nota:** Se o comentario possui respostas, ele sera marcado como deletado (soft delete) e seu conteudo sera removido, mas permanecera visivel como "[comentario removido]". Se nao possui respostas, sera removido completamente (hard delete).

---

### POST `/api/comment/{commentId}/like`
**Descricao:** Curtir/descurtir um comentario. Requer autenticacao de autor.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| commentId | UUID | ID do comentario |

**Response:**
```json
{
  "isActive": "boolean",
  "message": "string"
}
```

---

### POST `/api/comment/{commentId}/dislike`
**Descricao:** Dar/remover dislike em um comentario. Requer autenticacao de autor.

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| commentId | UUID | ID do comentario |

**Response:**
```json
{
  "isActive": "boolean",
  "message": "string"
}
```

> **Nota:** Like e dislike sao mutuamente exclusivos. Ao dar dislike em algo que ja tem like, o like e removido automaticamente.

---

## AdminController (`/api/admin`)

> Todas as rotas requerem autenticacao de administrador.

### DELETE `/api/admin/article/{articleId}`
**Descricao:** Deleta um artigo (admin).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| articleId | UUID | ID do artigo |

**Response:** `204 No Content`

---

### DELETE `/api/admin/author/{authorId}`
**Descricao:** Deleta um autor (admin).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| authorId | UUID | ID do autor |

**Response:** `204 No Content`

---

### DELETE `/api/admin/comment/{commentId}`
**Descricao:** Deleta um comentario (admin).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| commentId | UUID | ID do comentario |

**Response:** `204 No Content`

---

### DELETE `/api/admin/topic/{topicId}`
**Descricao:** Deleta um topico (admin).

**Path Parameters:**

| Nome | Tipo | Descricao |
|------|------|-----------|
| topicId | UUID | ID do topico |

**Response:** `204 No Content`

---

## FeedController (`/api/feed`)

### GET `/api/feed`
**Descricao:** Lista feed unificado (artigos, topicos e comentarios).

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |
| sort | FeedSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES) |
| type | FeedItemType | null | Filtro por tipo (ARTICLE, TOPIC, COMMENT) |

**Response:**
```json
{
  "items": [
    {
      "id": "UUID",
      "type": "ARTICLE | TOPIC | COMMENT",
      "title": "string",
      "slug": "string | null",
      "content": "string",
      "authorId": "UUID",
      "authorName": "string",
      "authorUserName": "string",
      "authorProfileImage": "string | null",
      "createdAt": "LocalDateTime",
      "likesCount": "int",
      "dislikesCount": "int",
      "commentsCount": "int",
      "savesCount": "int",
      "coverImage": "string | null",
      "tags": ["string"] | null,
      "parentType": "ARTICLE | TOPIC | null",
      "parentAuthorUserName": "string | null",
      "parentTitle": "string | null",
      "parentSlug": "string | null",
      "isLiked": "boolean | null",
      "isDisliked": "boolean | null",
      "isSaved": "boolean | null",
      "isOwner": "boolean | null"
    }
  ],
  "page": "int",
  "size": "int",
  "totalElements": "long",
  "totalPages": "int",
  "hasNext": "boolean"
}
```

> **Nota:** O campo `slug` e preenchido para itens do tipo ARTICLE e TOPIC. Para COMMENT, o slug e null.
>
> **Nota:** Os campos `parentType`, `parentAuthorUserName`, `parentTitle` e `parentSlug` sao preenchidos apenas para itens do tipo COMMENT, indicando o artigo ou topico ao qual o comentario pertence. Use `parentSlug` para construir URLs SEO-friendly.
>
> **Nota:** Os campos `isLiked`, `isDisliked`, `isSaved` e `isOwner` sao preenchidos apenas quando o usuario esta autenticado.

---

## SearchController (`/api/search`)

### GET `/api/search/tags`
**Descricao:** Busca artigos, topicos e comentarios que contenham pelo menos uma das tags especificadas.

**Query Parameters:**

| Nome | Tipo | Default | Descricao |
|------|------|---------|-----------|
| tags | List<string> | obrigatorio | Tags para buscar (retorna conteudos que contenham pelo menos uma) |
| type | SearchContentType | ALL | Filtro por tipo (ALL, ARTICLE, TOPIC, COMMENT) |
| sort | FeedSortType | RECENT | Ordenacao (RECENT, OLDEST, LIKES) |
| page | int | 0 | Numero da pagina |
| size | int | 20 | Tamanho da pagina |

**Response:**
```json
{
  "items": [
    {
      "id": "UUID",
      "type": "ARTICLE | TOPIC | COMMENT",
      "title": "string | null",
      "slug": "string | null",
      "content": "string",
      "authorId": "UUID",
      "authorName": "string",
      "authorUserName": "string",
      "createdAt": "LocalDateTime",
      "likesCount": "int",
      "isLiked": "boolean",
      "coverImage": "string | null",
      "tags": ["string"],
      "parentType": "ARTICLE | TOPIC | null",
      "parentAuthorUserName": "string | null",
      "parentSlug": "string | null"
    }
  ],
  "page": "int",
  "size": "int",
  "totalElements": "long",
  "totalPages": "int",
  "hasNext": "boolean"
}
```

> **Nota:** O campo `slug` e preenchido para itens do tipo ARTICLE e TOPIC. Para COMMENT, o slug e null.
>
> **Nota:** Os campos `parentType`, `parentAuthorUserName` e `parentSlug` sao preenchidos apenas para itens do tipo COMMENT, indicando o artigo ou topico ao qual o comentario pertence. Use `parentSlug` junto com `parentAuthorUserName` para construir URLs SEO-friendly (ex: `/{parentAuthorUserName}/{parentSlug}`).

---

## ForumController (`/api/forum`)

### GET `/api/forum/stats`
**Descricao:** Retorna estatisticas do forum (com cache).

**Response:**
```json
{
  "totalArticles": "long",
  "totalTopics": "long",
  "totalComments": "long"
}
```

---

## TestController (`/api`)

> Endpoints de teste para verificar autenticacao.

### GET `/api/admin/dashboard`
**Descricao:** Dashboard do admin (requer autenticacao de admin).

**Response:** `"Welcome to Admin Dashboard, {username}!"`

---

### GET `/api/author/profile`
**Descricao:** Perfil do autor (requer autenticacao de autor).

**Response:** `"Welcome to Author Profile, {username}!"`

---

### GET `/api/public`
**Descricao:** Endpoint publico (sem autenticacao).

**Response:** `"This is a public endpoint!"`

---

## Tipos Auxiliares

### FeedSortType (enum)
```
RECENT   - Ordenar por mais recentes
OLDEST   - Ordenar por mais antigos
LIKES    - Ordenar por mais curtidos
```

### FeedItemType (enum)
```
ARTICLE  - Artigos
TOPIC    - Topicos
COMMENT  - Comentarios
```

### AuthorContentSortType (enum)
```
RECENT   - Ordenar por mais recentes
OLDEST   - Ordenar por mais antigos
LIKES    - Ordenar por mais curtidos
SAVES    - Ordenar por mais salvos (apenas para artigos)
COMMENTS - Ordenar por mais comentados
```

### SearchContentType (enum)
```
ALL      - Todos os tipos de conteudo
ARTICLE  - Apenas artigos
TOPIC    - Apenas topicos
COMMENT  - Apenas comentarios
```

---

## Sistema de BugCoins

O sistema de bugCoins recompensa autores por suas contribuicoes no forum. As configuracoes sao gerenciadas pela entidade `ForumConfig`.

### Configuracoes de Recompensas

| Propriedade | Descricao |
|-------------|-----------|
| `coinsPerArticle` | Quantidade de bugCoins recebidos ao criar um artigo |
| `coinsPerTopic` | Quantidade de bugCoins recebidos ao criar um topico |
| `coinsPerComment` | Quantidade de bugCoins recebidos ao criar um comentario |
| `amountCoinsStart` | Quantidade inicial de bugCoins para novos usuarios |
| `coinsFirstArticle` | Bonus de bugCoins pelo primeiro artigo (futuro) |
| `coinsFirstTopic` | Bonus de bugCoins pelo primeiro topico (futuro) |
| `coinsFirstComment` | Bonus de bugCoins pelo primeiro comentario (futuro) |

### Feature Flags

| Propriedade | Descricao |
|-------------|-----------|
| `articleCreationUnlocked` | Permite criacao de artigos |
| `topicCreationUnlocked` | Permite criacao de topicos |
| `commentUnlocked` | Permite criacao de comentarios |
| `emailVerificationRequired` | Exige verificacao de email para criar artigos, topicos e comentarios |
| `storeUnlocked` | Habilita a loja (futuro) |
| `userTitleUnlocked` | Permite titulos personalizados (futuro) |

> **Nota:** O saldo de bugCoins de um autor pode ser visualizado no endpoint `GET /api/authors/{userName}` no campo `bugCoins`.

---

## Sistema de Verificacao de Email

O sistema de verificacao de email garante que os autores confirmem a propriedade do email cadastrado.

### Fluxo de Verificacao

1. **Cadastro tradicional** (`POST /api/authors/create`)
   - Email de verificacao e enviado automaticamente
   - `emailVerified = false` ate confirmacao

2. **Cadastro via Google** (`POST /auth/google`)
   - `emailVerified = true` automaticamente (Google ja verifica)

3. **Usuario clica no link do email**
   - Link redireciona para `{frontend-url}/verify-email?token=xxx`
   - Frontend chama `GET /auth/verify-email?token=xxx`

4. **Resposta da verificacao**
   - Retorna `userName` e `token` de login
   - Frontend pode redirecionar logado para o perfil

### Comportamento com `emailVerificationRequired`

Quando `emailVerificationRequired = true` no ForumConfig:

| Acao | Email Verificado | Resultado |
|------|------------------|-----------|
| Criar artigo | Sim | Permitido |
| Criar artigo | Nao | Erro 422: "You must verify your email before creating articles" |
| Criar topico | Sim | Permitido |
| Criar topico | Nao | Erro 422: "You must verify your email before creating topics" |
| Criar comentario | Sim | Permitido |
| Criar comentario | Nao | Erro 422: "You must verify your email before commenting" |

> **Nota:** Quando `emailVerificationRequired = false`, a verificacao de email nao e obrigatoria para nenhuma acao.
