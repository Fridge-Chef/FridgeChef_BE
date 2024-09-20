`## 프로젝트명 :  fridge chef 

> 프로젝트 소개: 요리 재료를 검색하여 만들 수 있는 레시피를 알려주는 서비스 제공합니다.

---
### 팀명 : fridge chef

### 제작 기간 : 2024.08.22 ~ 2024.(개발중)

### 기능

- 유저 oauth2 jwt local server
- 레시피
- 냉장고
- 커뮤니티
- 관리자

---

### 구성

---

### 기술 스택

---

### 배포

OCP
운영 서버 : application  [무료]
개발 서버 : docker + jenkins + CI/CD [무료]
이미지 서버 : nginx [무료]
DNS : Cloudflare [무료]
도메인 : 가비아 [4천원]

---
### 프로젝트 구조 

도메인 패키징 


---
### DB
운영서버 : oracle 19c
로컬,개발 : h2 mod=oracle

---
### 도메인

- user ㄴ
- category ㄴ
- board ㄴ
- comment ㄴ
- business ㄴ
- ingredient
- fridge
- recipe

---

### 서비스

---

### API

---

### 테스트

서비스 , api 테스트 필수

---

### 기여도

---

### 아키텍쳐

ocp, s3 , ci/cd, jenkins,

spring batch,

이벤트 프로세싱, ci/cd 아키첵처

웹 전반적인 아키첵처

웹 서비스


---

---

---

---

소개
---

프로젝트 소개: 요리 재료를 검색하여 만들 수 있는 레시피를 알려주는 서비스 제공합니다.

전체적인 구조 이미지
---

이미지

## 프로젝트 목표
---

- 레시피 구현 목표
- 단순 기능구현뿐만 아니라 실사용자 300명 까지 구하는것이 목표입니다.
- 객체지향적 코드 80% 까지 구현하는 것이 목표입니다.
- 문서화,단위 테스트는 매우 높은 우선순위로 두어 작성했고 다른 로컬에서도 테스트가 가능한 프로젝트로 만들었습니다. ->jasypt 제외한다면

## 레시피 openapi -> db -> search 까지 과정 구조도

## 프로젝트 주요 관심사
---

### 공통사항

- 지속적인 성능 개선
- 나쁜 소트 리팩토링

### 코드 컨벤션

- 구글 코드 스타일 준수

## 브랜치 전략

운영 전 까지는 sub branch -> main pr 방식
운영 시작부터는 git flow 방식

## 배포/테스트/자동화 전략

- CI jenkins , main pr시 작동
- 원격서버 .jar 전달 및 내부 서비스에서 재시작

## 사용기술 및 환경
---
Spring boot, gradle , jpa, querydsl , jenkins, java 17 ,

Oracle cloud platform, Oracle cloud object storage, Oracle cloud db,

cloudflare dns, 가비아 도메인

## 화면 설게
---

고객 프로토타입

## 프로젝트 DB & ERD

이미지

## 테스트

전체 성공 이미지

## API 테스트

- API 문서 link


