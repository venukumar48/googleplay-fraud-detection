# Google Play Search Fraud Rank and Malware Detection System

An enterprise-level AI-powered cybersecurity dashboard designed to audit Play Store mobile applications, detect rating/rank manipulations, analyze review sentiment anomalies, and scan for malicious permission behaviors.

---

## 🚀 Quick Start (Dockerized Deployment)

The entire system is orchestrate-ready. If you have Docker installed, you can launch all components (PostgreSQL Database, ML Microservice, Spring Boot REST Backend, and Next.js Frontend) in one command:

```bash
docker-compose up --build
```

- **Next.js Frontend UI**: `http://localhost:3000`
- **Spring Boot REST API**: `http://localhost:8080`
- **Python ML Microservice**: `http://localhost:8000`
- **PostgreSQL Database**: `localhost:5432`

---

## 🛠️ Tech Stack & Architecture

- **Frontend**: Next.js (App Router), TypeScript, Tailwind CSS (v4), Recharts, Axios, React Query.
- **Backend API**: Java 21, Spring Boot, Spring Security + JWT, H2 Database (with PostgreSQL profile), OpenPDF for PDF audit exporting.
- **ML Microservice**: Python 3.14+, FastAPI, Uvicorn, NumPy, Pydantic.
- **DevOps**: Docker, Docker Compose, GitHub Actions.

---

## 💻 Manual Local Startup

If you do not want to use Docker, follow these instructions to launch services individually:

### 1. Python ML Microservice
```bash
cd ml-service
python -m venv venv
.\venv\Scripts\activate
pip install -r requirements.txt
python -m uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### 2. Spring Boot REST Backend
By default, the backend runs in a `dev` profile using an in-memory H2 database, so no database installations are required to test the REST APIs!
To start the backend, run:
```bash
cd backend
mvn spring-boot:run
```
*(To use PostgreSQL, activate the `prod` profile in application.yml and run your local PostgreSQL server).*

### 3. Next.js Frontend UI
```bash
cd frontend
npm install
npm run dev
```

---

## 🔑 Authentication Credentials
To access the dashboard, sign in with the default operator passcode:
- **Username**: `admin`
- **Password**: `password`

---

## 📊 Testing Data Ingestion
We have bundled a sample play store dataset for testing. 
1. Log in to the dashboard portal.
2. Go to the **Upload Dataset** page.
3. Upload the `play_store_test_dataset.csv` located at the root of this project.
4. Execute the pipeline scan to run the anomaly and malware heuristics!
