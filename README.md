##Stipop Android SDK 데모 사용 가이드

소개

해당 데모 이모티콘 검색 및 키보드 뷰를 바로 실행해볼 수 있는 코드입니다.
다음 가이드를 통해 데모 앱을 실행해보세요.

1. 코드 다운로드 및 json 파일 추가
  - 스티팝 대시보드 (dashboard.stipop.io) 혹은 이메일을 통해 받은 Stipop.json 파일을 app>src>main>assets에 추가해주세요.
  - Stipop.json 파일에는 API key, custom 설정 값들이 저장되어 있습니다.
  - 더 자세한 가이드는 https://docs.stipop.io/en/sdk/android/get-started/quick-start/ 를 확인해주세요.

2. 코드를 뒤 디폴트로 설정되어 있는 Search View를 테스트해보세요.
  - 이모티콘의 언어 등은 사용자 lang 정보에 맞게 최적화 됩니다.
  - 사용자 lang 정보 수정은 아래 4번의 언어 정보 전환 방법을 참고해주세요.

3. Search View를 테스트 해보신 뒤에는 뷰를 전환해 Keyboard View도 함께 테스트해보세요.
  - Search View는 사용자가 이모티콘을 검색해 바로 사용할 수 있는 뷰입니다.
  - Keyboard View는 카카오톡 이모티콘 처럼 이모티콘을 다운로드해 키보드에서 전송하는 방식입니다.
  - 뷰를 전환하는 방법은 다음과 같습니다.
     - app > src > main > java.io.stipop.stipopsamle > MainActivity 이동
     - stipopIV.setOnClickListener의 다음 두 UI 중 하나를 선택 (디폴트로 키보드는 주석처리 되어있습니다)
          Stipop.showSearch() 검색 UI창
          Stipop.showKeyboard() 키보드 UI창

4. 언어정보 전환 방법
  - app > src > main > java.io.stipop.stipopsamle > MainActivity 이동
  - Stipop.connect(this, stipopIV, "9937", "ko", "KR", this)에서 lang 코드를 en (영어), 등으로 수정


데모 코드 관련하여 궁금한 사항은 tech-support@stipop.io 여기로 문의주세요.
