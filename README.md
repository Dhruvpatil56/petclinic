# 🐾 Spring PetClinic – CI/CD with Jenkins, Docker & MySQL

This project demonstrates a production-style CI/CD pipeline for the classic Spring PetClinic application using Jenkins, Docker, Docker Compose, SonarQube, and Trivy. The application is deployed as a containerized Java WAR running on Tomcat, backed by MySQL.

![Java](https://img.shields.io/badge/Java-8-orange)
![Spring](https://img.shields.io/badge/Spring-5.3.18-green)
![MySQL](https://img.shields.io/badge/MySQL-5.7-blue)
![Docker](https://img.shields.io/badge/Docker-Latest-2496ED)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D24939)

---

## 📌 Project Overview

- **Application:** Spring PetClinic (Java, Spring MVC, JDBC)
- **Build Tool:** Maven
- **Runtime:** Apache Tomcat 9
- **Database:** MySQL 5.7
- **CI/CD:** Jenkins (Declarative Pipeline)
- **Security & Quality:**
  - SonarQube (Code Quality Gate)
  - Trivy (Image Vulnerability Scan)
- **Containerization:** Docker & Docker Compose
- **Deployment Target:** Ubuntu Linux server (EC2 compatible)

---

## 🏗️ Architecture (High Level)

```
Developer
   ↓
GitHub Repository
   ↓
Jenkins Pipeline
   ├── Maven Build
   ├── SonarQube Analysis
   ├── Quality Gate
   ├── Docker Image Build
   ├── Trivy Image Scan
   └── Docker Compose Deployment
            ├── PetClinic (Tomcat)
            └── MySQL Database
```

---

## 📂 Repository Structure

```
.
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile        # Managed via GitHub UI
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       └── resources/
│           ├── jdbc.properties
│           ├── spring/
│           │   └── data-access.properties
│           └── db/mysql/
│               ├── schema.sql
│               └── data.sql
└── README.md
```

---

## ⚙️ Prerequisites

Ensure the following are installed on the Jenkins server / deployment host:

- Java 8
- Maven 3.8+
- Docker
- Docker Compose v2
- Jenkins
- SonarQube (configured in Jenkins)
- Trivy
- Git

---

## 🔐 Jenkins Credentials Required

Create the following credentials in **Jenkins → Manage Jenkins → Credentials → System → Global credentials**

Add as **Secret text** type:

| Credential ID | Description |
|---------------|-------------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password |
| `MYSQL_DATABASE` | Database name (e.g. `petclinic`) |
| `MYSQL_USER` | Database user |
| `MYSQL_PASSWORD` | Database password |
| `SONAR_TOKEN` | SonarQube authentication token |

---

## 🚀 CI/CD Pipeline Workflow

### 1️⃣ Source Code Checkout
Jenkins pulls code from the `main` branch of GitHub.

### 2️⃣ Maven Build
```bash
mvn clean package -P MySQL -DskipTests
```
- Builds the application
- Generates `petclinic.war`

### 3️⃣ SonarQube Code Analysis
- Static code analysis
- Enforces quality standards

### 4️⃣ Quality Gate
- Pipeline fails automatically if quality gate fails

### 5️⃣ Docker Image Build
```bash
docker build -t petclinic:latest .
```
- WAR is deployed inside Tomcat container

### 6️⃣ Trivy Image Scan
- Scans Docker image for HIGH & CRITICAL vulnerabilities
- Generates security report

### 7️⃣ Docker Compose Deployment
```bash
docker compose down || true
docker compose up -d --build
```

**Services started:**
- `petclinic-app` → Spring PetClinic (Tomcat)
- `petclinic-mysql` → MySQL Database

MySQL initializes automatically using:
- `schema.sql`
- `data.sql`

---

## 🌐 Application Access

After successful deployment:

```
http://<server-ip>:8090
```

Example:
```
http://100.28.xxx.xxx:8090
```

---

## 🛠️ Jenkins Setup

### Step 1: Configure Tools

**Navigate to:** Manage Jenkins → Tools

Configure:
- **JDK:** Name: `JDK-8`, Path: `/usr/lib/jvm/java-8-openjdk-amd64`
- **Maven:** Name: `Maven-3.8`, Path: `/usr/share/maven`
- **SonarQube Scanner:** Name: `sonar-scanner`, Install from Maven Central

### Step 2: Install Required Plugins

Go to **Manage Jenkins → Plugins → Available Plugins**

Install:
- Pipeline
- Git
- Docker Pipeline
- SonarQube Scanner
- JaCoCo

### Step 3: Configure SonarQube Server

**Navigate to:** Manage Jenkins → System → SonarQube servers

Add SonarQube:
- Name: `sonar`
- Server URL: `http://localhost:9000` (or your SonarQube server)
- Server authentication token: Add from credentials

### Step 4: Create Pipeline Job

1. **New Item** → Enter name: `petclinic-cicd` → **Pipeline** → OK
2. **Pipeline** section:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `https://github.com/Dhruvpatil56/petclinic.git`
   - Branch: `*/main`
   - Script Path: `Jenkinsfile`
3. **Save**
4. Click **Build Now**

---

## 🧠 Key Design Decisions

- **MySQL profile** forced for predictable DB behavior
- **Service name** (`mysql`) used instead of `localhost` for container networking
- **Secrets** handled via Jenkins credentials, not hardcoded
- **Docker Compose** used for local & CI parity
- **Clean separation** of build, scan, and deploy stages

---

## 🔄 Local Development

To run locally without Jenkins:

```bash
# Clone repository
git clone https://github.com/Dhruvpatil56/petclinic.git
cd petclinic

# Create .env file
cat > .env << 'EOF'
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=petclinic
MYSQL_USER=petclinic
MYSQL_PASSWORD=petclinic
EOF

# Start services
docker compose up -d --build

# Access application
http://localhost:8090
```

---

## 🐳 Docker Commands

### Build Image
```bash
mvn clean package -P MySQL -DskipTests
docker build -t petclinic:latest .
```

### Run with Docker Compose
```bash
# Start services
docker compose up -d

# View logs
docker compose logs -f

# Check status
docker compose ps

# Stop services
docker compose down
```

---

## 📝 License

This project is based on the Spring PetClinic sample application.

---

## 👤 Author

**Dhruv Patil**  
GitHub: [@Dhruvpatil56](https://github.com/Dhruvpatil56)

---

⭐ Star this repository if you find it helpful!
