# 🚨 AI-Powered Root Cause Analysis (RCA) System

Spring Boot 서비스의 장애 로그를 실시간으로 감지하고, **Gemini AI**를 통해 원인을 분석하여 **Slack**으로 전문적인 리포트를 전송하는 자동화 관제 시스템입니다.

---

## 🏗️ 시스템 아키텍처

```mermaid
graph TD
    User((사용자)) -->|API 호출| SB[Spring Boot App]
    SB -->|장애 발생 시 Context 전송| n8n[n8n Workflow]
    n8n -->|RCA 요청| AI [Gemini 2.5 Flash]
    AI -->|분석 결과 반환| n8n
    n8n -->|Slack Block Kit 리포트| Slack[Slack Channel]
```

### 🛰️ 핵심 기술 스택
- **Backend**: Java 17, Spring Boot 3.2
- **Automation**: n8n (Self-hosted)
- **AI**: Google Gemini AI
- **Notification**: Slack (Block Kit)
- **Infrastructure**: Docker, Docker Compose

---

## 🚀 시작 가이드 (Quick Start)

### 1. 전제 조건 (Prerequisites)
- [Docker & Docker Compose](https://www.docker.com/products/docker-desktop/) 설치
- [n8n](https://n8n.io/) 워크플로우 구성 권한
- Google Gemini API Key 발급 ([Google AI Studio](https://aistudio.google.com/))
- Slack App 생성 및 Bot Token (`chat:write` 권한 필요)

### 2. 환경 설정 (Environment Setup)
프로젝트 루트 디렉토리에 `.env` 파일을 생성하거나 `docker-compose.yml`의 환경 변수를 수정합니다.

```env
# n8n Webhook URL (n8n 워크플로우를 Active로 전환 후 획득한 주소)
N8N_WEBHOOK_URL=http://n8n:5678/webhook/log-analysis
```

### 3. 서비스 실행
```bash
# 전체 서비스 빌드 및 실행
docker compose up -d --build
```

---

## ⚙️ n8n 워크플로우 구성 방법

1. **Webhook Node**: 
   - HTTP Method: `POST`
   - Path: `log-analysis`
2. **AI Node (Basic LLM Chain)**:
   - Model: Gemini 2.5 Flash
   - Prompt: 수신된 에러 로그와 `endpoint`, `http_method` 정보를 바탕으로 RCA 리포트 작성 요청
3. **Slack Node**:
   - Message Type: `Blocks` (Expression 모드)
   - Payload: 수신된 AI 텍스트와 원본 로그 데이터를 결합한 Block Kit JSON 적용

---

## ✅ 장애 시뮬레이션 및 테스트
서비스가 실행 중일 때 브라우저에서 아래 엔드포인트를 호출하여 AI 분석 성능을 테스트할 수 있습니다.

```bash
curl http://localhost:8080/simulate-error
```

**출력 결과 (Slack Notification):**
- **서비스명**: `ai-rca-sample-project`
- **발생 위치**: `[GET] /simulate-error`
- **분석 내용**: 장애 요약, 예상 원인, 권장 조치 사항 포함

---

## 🏆 프로젝트 성과
- **실시간 장애 대응**: Webhook 기반으로 장애 발생 즉시 AI 분석 가이드 제공
- **정확한 상황 인지**: 단순 로그가 아닌 HTTP 컨텍스트를 포함한 지능형 분석
- **가독성 최적화**: 슬랙 Block Kit을 활용한 프리미엄 리포트 UI 구현
