# Lost & Found

A mobile platform for reporting lost and found items on a university campus, built as a **Spring Boot REST API** with a **native Android client** (Kotlin + Jetpack Compose).

Users publish an item as *lost* or *found* with a photo, category and location, browse what others have posted, and talk to each other through an in-app chat until the item is returned and the post is marked as resolved.

> University project (IADE – Universidade Europeia). The final report, database scripts and presentation are in [`docs/`](docs).

---

## Features

- **Accounts & auth**: register and log in, stateless JWT authentication, BCrypt password hashing.
- **Item posts**: create, edit, delete and resolve posts with title, description, category, coordinates, place name and date.
- **Image upload**: photos are uploaded through the API (multipart) and stored on **Cloudinary**; the database only keeps the URL.
- **Browse & filter**: list all items, or filter by status (`LOST` / `FOUND`) and by category; "my items" view.
- **Ownership rules**: only the author of a post can edit, resolve or delete it.
- **Chat**: one conversation per item between the two users, with an inbox sorted by last message, last-message preview and unread counter.
- **Profile**: view, update and delete your own account.

## Architecture

```
┌──────────────────────────┐        HTTPS / JSON + JWT        ┌──────────────────────────────┐
│ Android app              │ ───────────────────────────────▶ │ Spring Boot API              │
│ Kotlin · Jetpack Compose │                                  │ Controller → Service → Repo  │
│ MVVM · Retrofit · Coil   │ ◀─────────────────────────────── │ Spring Security (JWT filter) │
└──────────────────────────┘                                  └──────┬───────────────┬───────┘
                                                                     │               │
                                                              PostgreSQL        Cloudinary
                                                           (users, items,     (item images)
                                                           chat, categories)
```

**Backend** (`backend-app/`) follows a layered structure:

| Layer | Package | Responsibility |
|---|---|---|
| Controllers | `controller` | HTTP endpoints, request validation, response codes |
| DTOs | `dto` | Request/response contracts; entities are never exposed directly |
| Services | `service` | Business rules (ownership checks, conversation lookup, unread counts) |
| Repositories | `repository` | Spring Data JPA queries |
| Security | `security` | `SecurityFilterChain`, JWT generation/validation, `UserDetailsService` |
| Config | `config` | Cloudinary client |

**Android app** (`frontend-app/`) uses MVVM: Compose screens → ViewModels → repositories → a Retrofit `ApiService`. An OkHttp interceptor attaches the JWT stored by `TokenManager` to every request.

### Data model

`User` 1—N `Item` N—1 `Category` · `Item` 1—N `Conversation` N—N `User` · `Conversation` 1—N `Message`

The SQL scripts used for the course delivery (schema, seed data and example queries) are in [`docs/database`](docs/database).

## API

All routes are under `/api`. `GET` on items and categories is public; everything else requires `Authorization: Bearer <token>`.

| Method | Path | Description |
|---|---|---|
| `POST` | `/auth/register` | Create an account |
| `POST` | `/auth/login` | Log in and receive a JWT |
| `GET` | `/items` | List all items |
| `GET` | `/items/{id}` | Item details |
| `GET` | `/items/status/{status}` | Filter by `LOST` or `FOUND` |
| `GET` | `/items/category/{categoryId}` | Filter by category |
| `GET` | `/items/me` | Items posted by the current user |
| `POST` | `/items` | Create an item (multipart: data + image) |
| `PUT` | `/items/{id}` | Update an item (owner only) |
| `PATCH` | `/items/{id}/resolve` | Mark as resolved (owner only) |
| `DELETE` | `/items/{id}` | Delete an item (owner only) |
| `GET` / `POST` / `DELETE` | `/categories` | List / create / delete categories |
| `POST` / `DELETE` | `/images/upload`, `/images` | Upload or remove an image |
| `GET` / `POST` | `/chat/conversations` | Inbox / start a conversation about an item |
| `GET` / `POST` | `/chat/conversations/{id}/messages` | Read / send messages |
| `GET` / `PUT` / `DELETE` | `/users/me` | Profile |

## Tech stack

| | |
|---|---|
| Backend | Java 17, Spring Boot 3.2, Spring Web, Spring Data JPA (Hibernate), Spring Security, Bean Validation, Actuator |
| Auth | JWT (jjwt), BCrypt |
| Database | PostgreSQL |
| Storage | Cloudinary |
| Android | Kotlin, Jetpack Compose, Material 3, Navigation Compose, ViewModel, Retrofit, OkHttp, Coil |
| Build | Maven, Gradle (Kotlin DSL) |

## Running locally

### 1. Backend

Requirements: Java 17+, Docker (for PostgreSQL) and a free Cloudinary account.

```bash
cd backend-app
cp .env.example .env               # fill in the values
export $(grep -v '^#' .env | xargs) # load them into the shell

docker compose up -d               # PostgreSQL on localhost:5432
./mvnw spring-boot:run             # API on http://localhost:8080
```

| Variable | Description |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection |
| `JWT_SECRET` | Base64 key for signing tokens (`openssl rand -base64 64`) |
| `JWT_EXPIRATION_MS` | Token lifetime (default 24h) |
| `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` | Image storage |

No credentials live in the repository: `application.properties` only reads environment variables.

### 2. Android app

Open `frontend-app/` in Android Studio and run it on an emulator. The app points to `http://10.0.2.2:8080/`, which is the host machine's `localhost` as seen from the Android emulator. To use a physical device, change `BASE_URL` in `data/remote/RetrofitClient.kt`.

## Project structure

```
lost-and-found-app/
├── backend-app/        Spring Boot API
├── frontend-app/       Android app (Kotlin, Jetpack Compose)
└── docs/
    ├── database/       SQL: schema, seed data, queries
    ├── presentation/   Slides and poster
    └── relatorio-final.pdf
```

## Possible next steps

- Database migrations with Flyway instead of `ddl-auto`
- Pagination on item listings
- Integration tests with Testcontainers
- Map view of items using the stored coordinates
