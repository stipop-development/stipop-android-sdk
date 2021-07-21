## Stipop Android SDK Demo Quick Start Guide

Get demo running in 5 minutes.
With the demo you can test 2 view types for the sticker integration:
  1. Search View: Instant usage where users search for stickers and send right away.
  2. Keyboard(Picker) View: Users can download packs from store and send them from the sticker keyboard.
<br/>

## Quick Start :rocket:  

1. Download code and add Stipop.json file
  - Sign up on <a href="https://dashboard.stipop.io/" target="_blank">Stipop Dashboard</a> and download **Stipop.json** for free.
  - Open code on Android Studio, then create 'assets' folder under **app > src > main**.
  - Move Stipop.json into the assets folder you created. In the Stipop.json file there is your personal API key and customization values.
  - For more guide on this, please see <a href="https://docs.stipop.io/en/sdk/android/get-started/quick-start/" target="_blank">Stipop Docs</a>.
<br/>

2. 코드를 실행해 디폴트로 설정되어 있는 Search View를 테스트해보세요.
  - 이모티콘의 언어 등은 사용자 lang 정보에 맞게 최적화 됩니다.
  - 사용자 lang 정보 수정은 아래 4번의 언어 정보 전환 방법을 참고해주세요.
<br/>

3. Search View를 테스트 해본 뒤 Keyboard View로 전환해 테스트해보세요.
  - Search View는 사용자가 이모티콘을 검색해 바로 사용할 수 있는 뷰입니다.
  - Keyboard View는 카카오톡 이모티콘 처럼 이모티콘을 다운로드해 키보드에서 전송하는 방식입니다.
  - 뷰를 전환하는 방법은 다음과 같습니다.
     - **app > src > main > java.io.stipop.stipopsample > MainActivity**로 이동
     - stipopIV.setOnClickListener의 다음 두 UI 중 하나를 선택 (디폴트로 키보드는 주석처리 되어있습니다)
          Stipop.showSearch()      //검색 UI창
          Stipop.showKeyboard()    //키보드 UI창
<br/>

4. 언어정보 전환 방법 (영어 > 한글)
  - **app > src > main > java.io.stipop.stipopsample > MainActivity**로 이동
  - Stipop.connect(this, stipopIV, "9937", "en", "US", this)에서 lang을 **ko**, country를 **KR** 로 수정
<br/>



데모 코드 관련하여 궁금한 사항은 tech-support@stipop.io로 문의주세요.
