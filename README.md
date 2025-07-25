<a name="top"></a>
  
# 외국인 맞춤 쇼핑지원 서비스, Peek&Pick

> **Peek&Pick(픽앤픽)**은 바코드 스캔을 통해 상품 정보를 조회하고, 태그 기반 추천, 리뷰 요약 및 번역 기능을 제공하여 외국인 사용자의 한국 쇼핑을 돕는 서비스로
훑어보고(Peek), 고른다(Pick)는 의미를 담고 있습니다.

- 구분: 팀 프로젝트
- 기간: 2025년 5월 8일 ~ 2025년 7월 11일

<details>
  <summary>Table of Contents</summary>
  
  1. [프로젝트 팀원](#프로젝트-팀원)
  2. [프로젝트 개요](#프로젝트-개요)
  3. [사용 기술](#사용-기술)
  4. [주요 기능](#주요-기능)
  5. [미리보기](#미리보기)

</details>
<br>

메인사진 뭐넣지
<br><br>

## 팀원
<table>
	<tr>
    <td><img width="201" height="328" alt="소연" src="https://github.com/user-attachments/assets/ad5b7397-ad1c-493e-acbe-89a9c4b4a9a9" /></td>
    <td><img width="170" height="357" alt="동훈" src="https://github.com/user-attachments/assets/74d2dd3f-10cf-4841-a792-c24b7466a725" /></td>
    <td><img width="201" height="328" alt="강민" src="https://github.com/user-attachments/assets/d1a9dc18-801e-483f-8964-8ab2ccdb82e3" /></td>
    <td><img width="201" height="328" alt="근화" src="https://github.com/user-attachments/assets/176d88fb-a379-42ae-bb4b-cc3f37ed8731" /></td>
    <td><img width="180" height="362" alt="image" src="https://github.com/user-attachments/assets/a335d597-1fae-4c8e-9a94-cb9beee3f36f" /></td>
	</tr>
  <tr>
    <th><a href="https://github.com/KimSoYeonnn">김소연</a></th>
    <th><a href="">강동훈</a> </th>
    <th><a href="">배강민</a> </th>
    <th><a href="">이근화</a> </th>
    <th><a href="">이은진</a> </th>
  </tr>
  <th> 팀장,BE,FE </th>
  <th> BE,FE </th>
  <th> BE,FE </th>
  <th> BE,FE </th>
  <th> BE,FE </th>
</table>

<br>

## 📌 프로젝트 개요

최근 한국 편의점은 ‘미니 관광 코스’로 주목받으며 외국인 관광객 사이에서 큰 인기를 끌고 있습니다.  
SNS에서는 한국 제품을 소개하는 콘텐츠가 화제가 되고 있지만, 외국인 방문자들은 실제로 제품을 구매할 때 언어 장벽과 정보 부족으로 불편을 겪고 있습니다.

기존의 바코드 스캔 앱(예: **ScanLife**, **Payke**)은 국내 사용자 리뷰가 부족하고, 커뮤니티 기반 Q&A는 응답 시간이 길어 실시간으로 정보를 얻기 어렵습니다.

이에 팀 **‘삑 그리고 다음’**은 바코드 스캔만으로 상품 정보와 사용자 리뷰를 실시간으로 확인하고, AI 기반 맞춤 추천 기능까지 제공하는  
**외국인 대상 쇼핑 지원 서비스, Peek&Pick(픽앤픽)**을 제안합니다.

<br>

## 🔍 주요 기능

### ✅ 사용자 기능

- **바코드 스캔 상품 조회**  
  → 제품명, 원산지, 성분, 가격 등 상세 정보를 실시간 제공

- **리뷰 요약 및 번역**  
  → AI가 리뷰를 긍정/부정으로 요약하고, 사용자 언어로 번역 제공

- **상품 추천 및 검색**
  - 사용자 선호 태그 기반 AI 추천
  - 리뷰 평점/좋아요 기반 랭킹 제공
  - Elasticsearch 기반 정밀 검색

- **위치 기반 편의점 안내**  
  → 현재 위치를 기반으로 근처 편의점을 지도에 표시

- **AI 챗봇 추천 상담**  
  → 챗봇과 대화하며 개인 맞춤 상품 추천

- **리뷰 작성 알림**  
  → 스캔 후 리뷰 미작성 시 푸시 알림 전송

<br>

### ✅ 관리자 기능

- **상품 등록 및 바코드 관리**  
  → 상품 추가, 정보 수정, 이미지 업로드, 바코드 관리

- **리뷰 데이터 관리**  
  → 사용자 리뷰 모니터링 및 신고 리뷰 삭제/숨김 처리

- **사용자 관리**  
  → 사용자 활동 내역 확인, 부적절한 사용자 제재 가능

- **문의 및 요청사항 확인**  
  → 사용자 문의, 상품 등록 요청 등 응대 및 관리

- **통계 대시보드 제공**  
  → 가입자 수, 리뷰 수, 국적 분포 등 통계를 그래프로 시각화

<br>

> 👀 *“Peek(훑어보고), Pick(고른다)” — 외국인도 손쉽게 한국 제품을 탐색하고 선택할 수 있도록!*  


## 🧰 기술 스택 (Tech Stack)

### 🚀 Frontend
- HTML, CSS, TailwindCSS, Bootstrap
- React.js, React Router v7, React Query
- ZXing API (바코드 스캔)
- Google Maps API
- Firebase API
- PWA (Progressive Web App)

### 🔧 Backend
- Spring Boot, Spring Security
- Spring Data JPA, Spring AI
- OpenAI API, DeepL API, Google Natural Language API
- 공공데이터포털 API
- Elasticsearch
- Google OAuth API

### 🗄️ Database
- PostgreSQL

### ☁️ Deployment
- AWS
- Apache Tomcat

<br>

## 🖥️ 개발 환경 (Software & Tools)

- **개발 언어**: Java 17, TypeScript  
- **IDE**: IntelliJ, VS Code, DBeaver  
- **형상 관리**: Git, GitHub  
- **협업 도구**: Notion, Google Drive

<br>

## 📽 프로젝트 시연
링크 넣기

<br>

## 🖇ERD
<img width="2122" height="1063" alt="ERD" src="https://github.com/user-attachments/assets/919645c9-d723-48db-9c8a-7bbe6de348ac" />

<br>

## 🏗️ 서비스 아키텍쳐 (Service Architecture)
<img width="2964" height="1444" alt="서비스아키텍처" src="https://github.com/user-attachments/assets/90670615-1094-4449-bf1c-552b7f446c1e" />

<br>

## 📂 프로젝트 자료 (Documents)
- 🗣️ [발표 자료 (PDF)](https://github.com/user-attachments/files/21423062/3._Peek.Pick_.pdf.pdf)
- 🧾 [화면 설계서 (PDF)](https://github.com/user-attachments/files/21423082/3._._.pdf)
- 📄 [요구사항 정의서 (PDF)](https://github.com/user-attachments/files/21423094/3._._.pdf)
- 📋 [API명세서(PDF)](https://github.com/user-attachments/files/21423089/3._._API.pdf)
- 📂 [메뉴트리(PDF)](https://github.com/user-attachments/files/21423088/3._._.pdf)

<br>
<h5 align="right"><a href="#top">⬆️TOP</a></h5>
