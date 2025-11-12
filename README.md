"# Java - Projekt do nauki podstaw" 

## 📋 Spis Treści

- [Funkcjonalności](#funkcjonalności)
- [Technologie](#technologie)
- [Wymagania](#wymagania)
- [Instalacja i Uruchomienie](#instalacja-i-uruchomienie)
  - [Lokalne uruchomienie (profil DEV)](#lokalne-uruchomienie-profil-dev)
  - [Uruchomienie z Docker (profil PROD)](#uruchomienie-z-docker-profil-prod)
- [Konfiguracja](#konfiguracja)
- [API Endpoints](#api-endpoints)
- [Struktura Projektu](#struktura-projektu)
- [Bezpieczeństwo](#bezpieczeństwo)
- [Dokumentacja API](#dokumentacja-api)

## 🚀 Funkcjonalności

- ✅ **Rejestracja pacjentów** - tworzenie nowych kont użytkowników
- ✅ **Uwierzytelnianie JWT** - bezpieczne logowanie z tokenami JWT
- ✅ **Zarządzanie lekarzami** - przeglądanie dostępnych lekarzy
- ✅ **Rezerwacja wizyt** - system umożliwiający rezerwację terminów
- ✅ **Role użytkowników** - różne poziomy dostępu (PACJENT, LEKARZ, ADMIN)
- ✅ **RESTful API** - standardowe endpointy REST
- ✅ **Dokumentacja Swagger** - interaktywna dokumentacja API

## 🛠 Technologie

- **Java 17** - język programowania
- **Spring Boot 3.5.7** - framework aplikacyjny
- **Spring Security** - bezpieczeństwo i autoryzacja
- **JWT (JSON Web Tokens)** - uwierzytelnianie bezstanowe
- **Spring Data JPA** - warstwa dostępu do danych
- **Hibernate** - ORM (Object-Relational Mapping)
- **PostgreSQL 15** - baza danych produkcyjna
- **H2 Database** - baza danych deweloperska (w pamięci)
- **Docker & Docker Compose** - konteneryzacja aplikacji
- **Maven** - zarządzanie zależnościami
- **Lombok** - redukcja boilerplate code
- **SpringDoc OpenAPI** - dokumentacja API (Swagger)

## 📦 Wymagania

- **Java 17** lub nowszy
- **Maven 3.6+**
- **Docker Desktop** (dla uruchomienia z Docker)
- **PostgreSQL 15** (opcjonalnie, dla lokalnego uruchomienia bez Dockera)

## 🚀 Instalacja i Uruchomienie

### Lokalne uruchomienie (profil DEV)

1. **Sklonuj repozytorium:**
   ```bash
   git clone <repository-url>
   cd java-project
   ```

2. **Uruchom aplikację:**
   ```bash
   mvn spring-boot:run
   ```
   
   Aplikacja uruchomi się z profilem `dev`, który używa bazy H2 w pamięci.

3. **Aplikacja będzie dostępna pod adresem:**
   - API: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console` (tylko w profilu dev)

### Uruchomienie z Docker (profil PROD)

1. **Przygotuj konfigurację:**
   
   Przed uruchomieniem zaktualizuj hasła i klucze w pliku `docker-compose.yml`:
   - `POSTGRES_PASSWORD` - hasło do bazy danych
   - `SPRING_DATASOURCE_PASSWORD` - hasło do bazy danych (to samo co wyżej)
   - `SECURITY_JWT_SECRET_KEY` - sekretny klucz JWT (min. 256 bitów, Base64)

2. **Uruchom kontenery:**
   ```bash
   docker-compose up --build
   ```

3. **Aplikacja będzie dostępna pod adresem:**
   - API: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - PostgreSQL: `localhost:5432`

4. **Zatrzymanie kontenerów:**
   ```bash
   docker-compose down
   ```

   Aby usunąć również dane z bazy (wolumeny):
   ```bash
   docker-compose down -v
   ```

## ⚙️ Konfiguracja

### Profile Spring Boot

Aplikacja obsługuje dwa profile:

#### Profil DEV (`application-dev.properties`)
- Baza danych: H2 (w pamięci)
- H2 Console: włączona
- DDL: `update` (automatyczne tworzenie tabel)
- SQL logging: włączony

#### Profil PROD (`application-prod.properties`)
- Baza danych: PostgreSQL
- H2 Console: wyłączona
- DDL: `update` (dla środowiska testowego)
- SQL logging: wyłączony

### Zmienne środowiskowe (Docker)

W `docker-compose.yml` można skonfigurować:

- `SPRING_PROFILES_ACTIVE` - aktywny profil (domyślnie: `prod`)
- `SPRING_DATASOURCE_URL` - URL bazy danych
- `SPRING_DATASOURCE_USERNAME` - użytkownik bazy danych
- `SPRING_DATASOURCE_PASSWORD` - hasło bazy danych
- `SECURITY_JWT_SECRET_KEY` - klucz JWT (Base64)
- `SECURITY_JWT_EXPIRATION_TIME` - czas wygaśnięcia tokenu (ms)

### Generowanie klucza JWT

Aby wygenerować bezpieczny klucz JWT (Base64):

```bash
# Linux/Mac
openssl rand -base64 32

# Windows (PowerShell)
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))
```

## 📡 API Endpoints

### Autoryzacja

- `POST /api/auth/register` - Rejestracja nowego pacjenta
- `POST /api/auth/login` - Logowanie (zwraca token JWT)

### Lekarze

- `GET /api/doctors` - Lista dostępnych lekarzy

### Dokumentacja API

- `GET /swagger-ui/index.html` - Interaktywna dokumentacja Swagger UI (główna strona)
- `GET /swagger-ui.html` - Przekierowanie do Swagger UI
- `GET /v3/api-docs` - Dokumentacja OpenAPI w formacie JSON
- `GET /v3/api-docs.yaml` - Dokumentacja OpenAPI w formacie YAML

## 📁 Struktura Projektu

```
src/
├── main/
│   ├── java/com/example/medappoint/
│   │   ├── config/              # Konfiguracja (Security, JWT)
│   │   ├── controller/          # Kontrolery REST
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Obsługa wyjątków
│   │   ├── model/               # Encje JPA
│   │   │   └── enums/          # Enumeracje
│   │   ├── repository/          # Repozytoria Spring Data
│   │   └── service/            # Logika biznesowa
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
└── test/                        # Testy jednostkowe
```

## 🔒 Bezpieczeństwo

- **JWT Authentication** - bezstanowe uwierzytelnianie
- **Spring Security** - zabezpieczenie endpointów
- **BCrypt** - hashowanie haseł
- **CSRF Protection** - wyłączone dla API (bezstanowe)
- **Role-based Access Control** - kontrola dostępu oparta na rolach

### Role użytkowników

- `ROLE_PATIENT` - Pacjent
- `ROLE_DOCTOR` - Lekarz
- `ROLE_ADMIN` - Administrator

### Używanie tokenu JWT

Po zalogowaniu otrzymasz token JWT. Używaj go w nagłówku żądań:

```
Authorization: Bearer <your-jwt-token>
```

## 📚 Dokumentacja API

Po uruchomieniu aplikacji, interaktywna dokumentacja Swagger jest dostępna pod adresem:

**http://localhost:8080/swagger-ui/index.html**

Alternatywnie możesz użyć:
- **http://localhost:8080/swagger-ui.html** (przekierowanie)

Dokumentacja zawiera:
- Listę wszystkich endpointów
- Opisy parametrów
- Przykłady żądań i odpowiedzi
- Możliwość testowania API bezpośrednio z przeglądarki

## 🐳 Docker

### Obrazy Docker

- **medappoint-app** - aplikacja Spring Boot
- **medappoint-db** - baza danych PostgreSQL 15

### Wolumeny

- `postgres-data` - dane bazy PostgreSQL (persystentne)

### Porty

- `8080` - aplikacja Spring Boot
- `5432` - PostgreSQL

## 🧪 Testowanie

Uruchomienie testów:

```bash
mvn test
```

## 📝 Uwagi

- **Hasła i klucze**: Przed wdrożeniem na produkcję zmień wszystkie domyślne hasła i klucze JWT!
- **DDL Strategy**: W prawdziwej produkcji użyj narzędzi do migracji bazy danych (Flyway/Liquibase) zamiast `spring.jpa.hibernate.ddl-auto=update`
- **H2 Console**: Konsola H2 jest dostępna tylko w profilu `dev` ze względów bezpieczeństwa

## 🤝 Wsparcie

W razie problemów sprawdź:
1. Logi aplikacji w konsoli
2. Logi kontenerów Docker: `docker-compose logs`
3. Dokumentację Swagger: `http://localhost:8080/swagger-ui/index.html`

## 📄 Licencja

Ten projekt jest przykładową aplikacją demonstracyjną.

---

**Wersja:** 0.0.1-SNAPSHOT  
**Spring Boot:** 3.5.7  
**Java:** 17
