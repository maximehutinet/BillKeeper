# Bill Keeper

Bill Keeper is a medical bill management tool that integrates a web-app 💻 and an [iOS app](https://github.com/maximehutinet/BillKeeper-iOS) 📱 allowing users to scan, organize, and track medical bills and insurance claims.

![Sneakpeek](/sneakpeek.gif)

## Main features

* 📷 Scanning bills and sending them automatically to the server ([iOS app](https://github.com/maximehutinet/BillKeeper-iOS))
* 📄 Adding documents to bills via the web-app or [iOS app](https://github.com/maximehutinet/BillKeeper-iOS)
* 📂 Merging documents
* 🔎 Keeping track of bills and their status
* 💬 Collaborating on bills via comments
* 👪 Creating a family and adding members so they can have access to the same bills and submissions
* 🛡️ Creating insurance submission claim, tracking their status, updating the reimbursed amounts
* 📈 Getting stats about the numbers of bills to file, amounts to pay and amounts waiting to be reimbursed

## Getting started

To get a local copy up and running, please follow these simple steps.

### Prerequisites

Make sure you have the following installed before getting started:

- **Java** 21+
- **Node.js** 20+ and **npm** 10+
- **Docker** & **Docker Compose**

### Setup

#### 1. Clone the repository and go to the project folder

```bash
git clone https://github.com/maximehutinet/BillKeeper.git
cd BillKeeper
```

#### 2. Install frontend dependencies

```bash
npm --prefix frontend/ install
```

#### 3. Start services with Docker Compose

This starts the database, Keycloak, and Smtp4Dev.

```bash
docker-compose up -d
```

#### 4. Run the frontend and backend

You'll need two bash sessions to run these services:

**Backend:**
```bash
./backend/mvnw spring-boot:run -f backend/pom.xml -Dspring-boot.run.profiles=dev
```

**Frontend:**
```bash
npm --prefix frontend/ run start
```

#### 5. Open the app

Navigate to http://localhost:4200 and log in with the default test user:

| Field    | Value  |
|----------|--------|
| Username | `test` |
| Password | `test` |

#### Summary of the different services running

| Service            | URL                    | Description                        |
|--------------------|------------------------|------------------------------------|
| Bill Keeper Front  | http://localhost:4200  | Angular web app                    |
| Bill Keeper Server | http://localhost:8080  | Spring Boot REST API               |
| Bill Keeper DB     | `localhost:10490`      | PostgreSQL — Server database       |
| Keycloak           | http://localhost:10491 | Auth server & Admin console        |
| Keycloak DB        | `localhost:10492`      | PostgreSQL — Keycloak database     |
| smtp4dev UI        | http://localhost:10493 | UI with all outgoing emails in dev |
| smtp4dev SMTP      | `localhost:10494`      | SMTP endpoint for the backend      |

## Auth

Bill Keeper uses [Keycloak](https://www.keycloak.org/) for authentication and authorization. The Angular frontend redirects users to Keycloak's login page; the Spring backend validates the resulting JWT on every protected request.

The admin console is available at `http://localhost:10491`.

| Field    | Value   |
|----------|---------|
| Username | `admin` |
| Password | `admin` |

A default user has been imported so you can log into the app.

## Emails

In development, all outgoing emails are caught by **smtp4dev**. Nothing is ever sent to a real inbox.

To view emails sent by the app, open the **smtp4dev UI** at `http://localhost:10493`.

## OCR

Bill Keeper supports OCR to extract information from uploaded bills using [Tesseract OCR](https://github.com/tesseract-ocr/tesseract). It is disable by default in dev mode. 

To enable it locally:

1. Install Tesseract

```bash
sudo apt install tesseract-ocr
```

2. Update the `backend/src/main/resources/application-dev.properties` file. 

Note that the `settings.tesseract-data-directory` points to a directory containing [trained language data](https://github.com/tesseract-ocr/tessdata). `settings.tesseract-language` must also be specified for it to work.

```properties
settings.ocr-enabled=true
settings.tesseract-language=<language>
settings.tesseract-data-directory=<path-to-trained-tesseract-data>
```

## Testing

### Backend tests

```bash
./backend/mvnw test -f backend/pom.xml
```