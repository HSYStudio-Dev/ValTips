# 📱 ValTips

**ValTips**는 발로란트 초보 유저들을 위해 요원 정보와 맵 공략을 제공하는 Android 애플리케이션입니다.  
Riot Games의 공식 API 및 비공식 Valorant API를 활용하며, 최신 Android 기술 스택으로 개발 중입니다.

---

## 🎯 프로젝트 목표

### **Why (왜?)**
- 발로란트 초보자들은 맵 위치 명칭, 요원 스킬 사용법 등을 익히는 데 어려움이 있음.
- 공략 자료가 흩어져 있고 접근성이 낮아, 빠른 학습이 어려움.

### **What (무엇을?)**
- 초보자도 직관적으로 사용할 수 있는 앱을 통해 **요원 정보, 맵 위치, 스킬 라인업**을 쉽게 확인.
- Riot API 및 비공식 Valorant API를 활용해 **실시간 데이터**와 **최신 게임 정보를 반영**.

### **How (어떻게?)**
- Jetpack Compose 기반의 UI/UX 제공.
- Room + DataStore를 통한 **오프라인 캐싱 및 사용자 상태 관리**.
- Coil3 기반의 **이미지 캐싱/프리패치**로 효율적인 UI 제공.
- MVVM + Clean Architecture로 **유지보수성과 확장성 확보**.

---

## 🛠️ 사용 기술 (Tech Stack)

### **언어 & 아키텍처**
- Kotlin
- Jetpack Compose (UI)
- MVVM + Clean Architecture

### **DI / Async**
- Hilt (의존성 주입)
- Kotlin Coroutines + Flow

### **데이터**
- Room (로컬 DB 캐싱, 24시간 유효성 검사)
- DataStore (온보딩 완료 여부 및 설정 저장)
- Retrofit + OkHttp (네트워크 통신)
- Kotlin Serialization (JSON 직렬화/역직렬화)

### **이미지**
- Coil 3.0 (이미지 로딩/캐싱)
- 전역 ImageLoader 모듈 (메모리/디스크 캐싱, OkHttp 연동)
- PreFetch 기능 (이미지 사전 로드)

### **테스트 및 코드 품질**
- **Ktlint**
  - Pre-commit 훅 적용 → 코드 스타일 검사
- **Detekt**
  - Pre-push 훅 적용 → 함수 복잡도, 코드 스멜 검출 등 **정적 분석**으로 코드 품질 관리

---

## 📂 프로젝트 구조

    com.hsystudio.valtips
    .
    ├── data
    │   ├── local # Room DB (Entity, Dao)
    │   ├── remote # Retrofit API 정의
    │   └── repository # Repository 구현체
    │
    ├── di # Hilt 모듈 (Network, ImageLoader 등)
    │
    ├── domain
    │   ├── model # Domain Model
    │   └── repository # Repository 인터페이스
    │
    ├── feature
    │   ├── login # Splash, Onboarding, Login 포함
    │       └── model, ui, viewmodel
    │
    ├── navigation # NavGraph, Route 정의

    ├── ui
    │   ├── component # 공용 UI 컴포넌트
    │   └── theme # 색상, 타이포그래피
    │
    └── util # 헬퍼 함수

---

## 📊 진행도

- [x] **Splash + Onboarding + Login UI**
- [ ] Stats
- [ ] Agents
- [ ] Maps
- [ ] LineUps
- [ ] User

---

## 📸 스크린샷

| Splash | Onboarding(임시) | Login |
|--------|-----------------|-------|
| <img src="docs/screenshots/splash.png" width="250"/> | <img src="docs/screenshots/onboarding.png" width="250"/> | <img src="docs/screenshots/login.png" width="250"/> |

---

## 📌 참고
- 본 앱은 **Riot Games 또는 Valorant 공식 앱이 아니며**, Riot Games와 무관한 개인 프로젝트입니다.
- Riot Games의 **개발자 정책 및 저작권 가이드라인**을 준수합니다.
