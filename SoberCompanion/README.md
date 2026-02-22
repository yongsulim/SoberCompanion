# 금주 동반자 (SoberCompanion)

> 금주를 결심한 당신 곁에 항상 함께하는 앱

---

## 앱 개요

**금주 동반자**는 금주를 유지하는 사람들을 위한 Android 앱입니다.
단순한 카운터가 아닌, 흔들리는 순간에도 곁에서 응원해주는 동반자를 목표로 합니다.

- 매일 상태(성공 / 흔들림 / 음주)를 기록하고
- 기분과 욕구 수준을 추적하며
- 마일스톤 달성으로 성취감을 느끼고
- 힘든 순간에는 위로 메시지로 버텨냅니다

**모든 데이터는 기기 내에만 저장됩니다.**
외부 서버 전송 없이, 완전한 프라이버시를 보장합니다.

---

## 주요 기능

### 홈 화면 (대시보드)
- 금주 시작일부터 현재까지의 연속 일수 + 시간 표시
- 오늘 상태 기록: **성공** / **흔들림** / **음주**
- 흔들림 기록 시 3시간 타이머 + 위로 메시지 카드 자동 표시
- 오늘의 한마디(격언) 표시
- 현재 연속 일수에 따른 맞춤 응원 배지

### 일일 기록
- 기분 슬라이더 (1~5, 이모지 표시)
- 욕구 수준 슬라이더 (1~5)
- 음주 여부 토글
- 메모/일기 자유 입력
- 음주 기록 시 자동으로 스트릭 리셋

### 통계
- 총 금주 일수, 최장 연속 기록, 평균 기분/욕구
- 최근 7일 기분 & 욕구 추이 그래프 (Canvas 기반)
- 최근 기록 목록

### 마일스톤
- 1 · 3 · 7 · 14 · 30 · 60 · 90 · 180 · 365일 목표
- 달성 시 자동 기록, 남은 일수 및 진행률 표시

### 설정
- 이름 및 금주 목표 이유 편집
- 매일 저녁 8시 리마인더 알림 토글
- 앱 버전 정보

### 위로 메시지 시스템
- 흔들림 기록 후 3시간이 지나면 위로 메시지 카드 표시
- 흔들림 횟수에 따라 고정 메시지(1~3회) / 순환 메시지(4회+) 자동 선택
- WorkManager 기반 백그라운드 스케줄링

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| **언어** | Kotlin 1.9.20 |
| **UI** | Jetpack Compose + Material 3 |
| **아키텍처** | MVVM (ViewModel + StateFlow) |
| **네비게이션** | Navigation Compose 2.7.5 |
| **로컬 DB** | Room 2.6.1 (SQLite ORM) |
| **경량 저장소** | DataStore Preferences 1.0.0 |
| **비동기** | Kotlin Coroutines 1.7.3 |
| **백그라운드 작업** | WorkManager 2.9.0 |
| **빌드** | Gradle 8.2.0 + KSP 1.9.20 |
| **Min SDK** | API 26 (Android 8.0) |
| **Target SDK** | API 34 (Android 14) |

---

## 프로젝트 구조

```
app/src/main/java/com/sobercompanion/
│
├── MainActivity.kt              # 앱 진입점, Edge-to-Edge 설정
├── SoberCompanionApp.kt         # Application 클래스, DB 초기화, 알림 채널 생성
│
├── data/                        # 데이터 레이어
│   ├── RecordStatus.kt          # 상태 열거형: SUCCESS / SHAKY / FAIL
│   ├── AppDataStore.kt          # DataStore 키-값 저장소 (실시간 세션 데이터)
│   ├── SoberRepository.kt       # 비즈니스 로직 + DataStore Flow 조합
│   │
│   ├── datastore/
│   │   └── UserPreferences.kt   # 온보딩 완료 여부, 사용자 이름 등
│   │
│   └── local/                   # Room 데이터베이스
│       ├── SoberDatabase.kt     # DB 싱글턴, 초기 데이터 삽입
│       ├── Converters.kt        # LocalDate/LocalDateTime ↔ String 변환
│       ├── dao/
│       │   └── SobrietyDao.kt   # CRUD 쿼리 인터페이스
│       └── entity/
│           └── SobrietyRecord.kt # DB 엔티티: SobrietyRecord, DailyLog, Milestone, MotivationalQuote
│
├── ui/                          # UI 레이어 (Jetpack Compose)
│   ├── Navigation.kt            # 화면 라우트 정의, 온보딩 완료 여부로 시작 화면 결정
│   ├── MainScreen.kt
│   ├── components/
│   │   └── ComfortMessageCard.kt # 위로 메시지 카드 컴포넌트
│   ├── screens/
│   │   ├── HomeScreen.kt        # 메인 대시보드
│   │   ├── OnboardingScreen.kt  # 최초 설정 화면
│   │   ├── DailyLogScreen.kt    # 일일 기분/욕구 기록
│   │   ├── StatisticsScreen.kt  # 통계 및 차트
│   │   ├── MilestonesScreen.kt  # 마일스톤 달성 현황
│   │   └── SettingsScreen.kt    # 사용자 설정
│   └── theme/
│       ├── Color.kt             # 색상 팔레트 (Sage Green, Warm Amber, Dusty Rose)
│       ├── Theme.kt             # MaterialTheme 설정
│       └── Type.kt              # 타이포그래피
│
├── viewmodel/
│   └── MainViewModel.kt         # UI 상태 관리, 흔들림 타이머, 자정 리셋 스케줄링
│
├── workers/
│   └── ComfortMessageWorker.kt  # WorkManager로 3시간 후 위로 메시지 플래그 설정
│
└── util/
    ├── ComfortMessageProvider.kt # 흔들림 횟수에 따른 위로 메시지 선택 로직
    └── ReminderWorker.kt         # 매일 저녁 리마인더 알림
```

### 데이터 저장 이중 구조

| 저장소 | 역할 | 데이터 |
|--------|------|--------|
| **DataStore** | 실시간 세션 | 시작일, 스트릭, 오늘 상태, 흔들림 타임스탬프 |
| **Room DB** | 이력 & 분석 | 금주 기록, 일일 로그, 마일스톤, 명언 |

---

## 빌드 및 실행 방법

### 요구사항
- Android Studio Hedgehog (2023.1.1) 이상
- JDK 17
- Android SDK 34

### 빌드 단계

```bash
# 1. 저장소 클론
git clone <repo-url>
cd SoberCompanion

# 2. Android Studio에서 프로젝트 열기
#    File > Open > SoberCompanion 폴더 선택

# 3. Gradle Sync
#    Android Studio 상단 "Sync Now" 클릭 또는
./gradlew build

# 4. 디버그 APK 빌드
./gradlew assembleDebug

# 5. 에뮬레이터 또는 실기기에서 실행
./gradlew installDebug
```

### 최초 실행 흐름
1. 앱 시작 → 온보딩 화면 (이름, 금주 목표 이유 입력)
2. 완료 후 홈 화면으로 이동, 금주 카운터 시작
3. 매일 홈 화면에서 오늘의 상태 기록

---

## 데이터베이스 스키마

### sobriety_records (금주 기록)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 자동 증가 |
| startDate | LocalDateTime | 금주 시작 시각 |
| endDate | LocalDateTime? | 금주 종료 시각 (null = 진행 중) |
| isActive | Boolean | 현재 활성 기록 여부 |
| reason | String | 금주 목표 이유 |
| note | String | 메모 |

### daily_logs (일일 기록)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 자동 증가 |
| date | LocalDate | 기록 날짜 |
| mood | Int | 기분 (1~5) |
| cravingLevel | Int | 욕구 수준 (1~5) |
| didDrink | Boolean | 음주 여부 |
| drinkAmount | Int | 음주량 (표준잔) |
| note | String | 메모 |

### milestones (마일스톤)
| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | Long (PK) | 자동 증가 |
| title | String | 마일스톤 이름 |
| targetDays | Int | 목표 일수 |
| isAchieved | Boolean | 달성 여부 |
| achievedAt | LocalDateTime? | 달성 시각 |

---

## 향후 확장 가능성

### 서버 연동
- **클라우드 백업**: Firebase / AWS S3 연동으로 기기 교체 시에도 데이터 유지
- **익명 집계 통계**: 사용자 동의 하에 금주 트렌드 익명 집계 및 커뮤니티 통계 제공
- **계정 시스템**: 소셜 로그인(Google, Kakao) 연동으로 멀티 디바이스 동기화

### 고급 통계
- 월별/연도별 히트맵 (GitHub 잔디 형식)
- 기분 & 욕구 상관관계 분석
- 금주로 절약된 비용 계산기 (단위 음주 비용 설정)
- 건강 지표 개선 추정치 (간 회복, 수면 개선 등)

### 커뮤니티 기능
- 익명 피어 서포트 채팅방
- 금주 챌린지 그룹 참여
- 익명 성공 사례 공유

### AI 기능
- 기록 패턴 기반 위험 일자 예측 (욕구 급등 시점 알림)
- 개인화된 위로 메시지 생성 (Claude API 연동)
- 음주 트리거 패턴 분석 및 대처 전략 제안

### UX 개선
- 위젯 지원 (홈 화면에 금주 일수 표시)
- Wear OS 연동 (스마트워치 빠른 기록)
- 생체 인증 잠금 (지문/얼굴 인식)
- 다국어 지원 (영어, 일본어, 중국어)

---

## 개인정보 보호

- 모든 데이터는 **사용자 기기 내에만 저장**됩니다
- 외부 서버로 어떠한 데이터도 전송되지 않습니다
- 인터넷 권한을 사용하지 않습니다

---

## 버전 히스토리

| 버전 | 설명 |
|------|------|
| v0.0.6 | 최신 빌드 |
| v0.0.5 | 통계 화면 개선 |
| v0.0.4 | 마일스톤 기능 추가 |
| v0.0.3 | 위로 메시지 시스템 |
| v0.0.2 | 일일 기록 기능 |

---

*금주는 혼자 하는 게 아닙니다. 금주 동반자가 함께합니다.*
