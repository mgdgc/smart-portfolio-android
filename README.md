<div>
<img src="/screenshots/00.png" width="128px"/>
<img src="/screenshots/fg_animation.gif" width="128px"/>
</div>

# SmartPortfolio

여러 멤버의 개발 이력과 프로젝트를 관리하고 확인할 수 있는 포트폴리오 애플리케이션

## 인트로 화면

<div>
<img src="/screenshots/01.png" width="180px"/>
<img src="/screenshots/01b.png" width="180px"/>
</div>

- 앱의 이름과 다음화면으로 넘어갈 수 있는 'Start' 버튼을 제공한다.
- 로그인 인증 방식을 제공하며, Biometric 로그인을 지원한다.

## 스플래시 화면

<div>
<img src="/screenshots/02.png" width="180px"/>
<img src="/screenshots/03.gif" width="180px"/>
<div>

- 인트로 화면 다음 메인화면 진입전 애니메이션을 보여주는 화면
- 1\~3초 이내 애니메이션(프로그래스)표기 후 메인화면으로 전환 (Thread 활용)
  - 1)프로그래스 바 표현 (로딩화면을 대체함)
  - 2)애니메이션 라이브러리는 Lottie 사용
  - 3)매번 진입시마다 랜덤하게, 다른 애니메이션을 보여줘야한다. (3~5개)
  
## 메인 화면

<div>
<img src="/screenshots/04.png" width="180px"/>
<img src="/screenshots/05.png" width="180px"/>
<img src="/screenshots/06.png" width="180px"/>
<div>

- 팀원들의 프로필이 간략한 소개 및 포트폴리오 상세 이동기능 제공 
- 우측상단 햄버거 메뉴 제공, 팀원들 포트폴리오 상세 이동기능 제공
  - 햄버거 메뉴는 메인화면, 포트폴리오 상세화면에서 제공된다. 
  - 로그아웃 버튼을 제공하여, 클릭시 인트로화면으로 이동한다. 
  - 새로운 사용자 추가/수정/삭제 기능 제공
- PageController 사용하여 좌우 스와이프로 사용자 포트폴리오 전환을 제공

## 사용자별 포트폴리오 상세화면

<div>
<img src="/screenshots/07.png" width="180px"/>
<img src="/screenshots/08.png" width="180px"/>
<img src="/screenshots/09.png" width="180px"/>
<div>

- 팀원들의 포트폴리오 정보는 사전에 입력되어 있는 상태로 진행
  - 사전에 입력된 사용자정보는 추가/수정/삭제는 불가능하다.
  - sqlite에 데이터 활용하며, 사용자의 포트폴리오 정보를 관리한다.
  - 팀원들의 포트폴리오 UI는 아래 보기형식중 겹치지 않게 표현한다.
  
## 포트폴리오 수정 화면

<div>
<img src="/screenshots/10.png" width="180px"/>
<img src="/screenshots/11.png" width="180px"/>
<div>

- 타이틀 영역에는 해당 사용자의 프로필이미지, 햄버거 메뉴를 제공한다. 
  * 프로필이미지 선택시 수정 기능을 제공한다.
  * 사진선택화면을 Custom 사진선택화면으로 제공한다.
- 상세화면의 내용을 추가/수정/삭제 제공한다.

  
## 내부/외부 웹브라우저
  
<div>
<img src="/screenshots/12.png" width="180px"/>
<img src="/screenshots/13.png" width="180px"/>
<img src="/screenshots/14.png" width="180px"/>
<div>

- 내부 웹브라우저에서 최초이동한 URL을 외부 웹브라우저로 이동하는 기능을 제공한다.
- (내부) WebView, (외부) Chrome Custom Tab
  

## 포트폴리오 전송(공유)
<div>
<img src="/screenshots/15.png" width="180px"/>
<img src="/screenshots/16.png" width="180px"/>
<div>
  
- 블루투스로 두 기기를 페어링하면 포트폴리오를 공유할 수 있다.
  
  
