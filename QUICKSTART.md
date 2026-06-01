# 🚀 Quick Start Guide

## Prerequisites
- Java 21
- Node.js 20+
- Docker & Docker Compose
- Maven 3.9+
- PostgreSQL 16 (or use Docker)

## Setup Steps

### 1. Clone & Configure
```bash
git clone https://github.com/your-org/neuralforge
cd NeuralForge
cp .env.example .env
```

### 2. Edit .env file
Add your API keys:
- OPENAI_API_KEY
- PINECONE_API_KEY
- Database credentials

### 3. Start with Docker Compose
```bash
docker-compose up --build
```

### 4. Access the Platform
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- AI Engine: http://localhost:8090
- GraphQL Playground: http://localhost:8080/graphql
- Grafana: http://localhost:3001 (admin/neuralforge123)
- Prometheus: http://localhost:9090

## Manual Setup (Without Docker)

### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### AI Engine
```bash
cd ai-engine
mvn clean install
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## First Steps

1. Register a new account at http://localhost:3000
2. Login with your credentials
3. Try the Intent-to-Feature Engine:
   - "Create a user profile system with avatar upload"
   - "Add a notification system with email and SMS"
   - "Build a payment gateway with Stripe"

## Architecture Overview

```
┌─────────────────┐
│   React UI      │ ← User Interface (3D Neural Dashboard)
└────────┬────────┘
         │
┌────────▼────────┐
│  Spring Boot    │ ← REST API + GraphQL + WebSocket
│    Backend      │
└────────┬────────┘
         │
┌────────▼────────┐
│   AI Engine     │ ← LangChain4j + GPT-4o + Vector Memory
│  (Autonomous)   │
└────────┬────────┘
         │
┌────────▼────────┐
│  PostgreSQL     │ ← Persistent Storage
│  Redis + Kafka  │ ← Caching + Event Streaming
└─────────────────┘
```

## Key Features to Try

### 1. Intent-to-Feature Engine
Generate complete features from plain English descriptions.

### 2. Predictive Bug Oracle
Upload Java code and get bug predictions before runtime.

### 3. Neural Code Review
Get feedback from 3 AI agents (Architect, Security, Performance).

### 4. Self-Healing Runtime
AI monitors production and auto-patches errors.

### 5. Temporal Code Memory
Store architectural decisions in vector memory for future recall.

## Troubleshooting

### Port Already in Use
```bash
# Kill processes on ports
npx kill-port 3000 8080 8090 5432 6379 9092
```

### Database Connection Failed
```bash
# Reset database
docker-compose down -v
docker-compose up -d postgres
```

### AI Generation Timeout
Increase timeout in `backend/src/main/resources/application.yml`:
```yaml
neuralforge:
  ai:
    timeout: 180
```

## Production Deployment

### AWS EKS
```bash
cd infrastructure/terraform
terraform init
terraform apply

# Deploy to Kubernetes
kubectl apply -f ../k8s/
```

### Environment Variables (Production)
- Use AWS Secrets Manager
- Enable HTTPS/TLS
- Configure auto-scaling
- Set up CloudWatch monitoring

## Support

- Documentation: https://docs.neuralforge.ai
- Issues: https://github.com/your-org/neuralforge/issues
- Discord: https://discord.gg/neuralforge

---

Built with ❤️ by the NeuralForge Team
