1. 뷰 전환방법
- app > src > main > java.io.stipop.stipopsamle > MainActivity 이동
- stipopIV.setOnClickListener의 
  Stipop.showSearch() 검색 UI창
  Stipop.showKeyboard() 키보드 UI창
  둘중에 원하는 뷰를 선택

2. 언어정보 전환 방법
- app > src > main > java.io.stipop.stipopsamle > MainActivity 이동
Stipop.connect(this, stipopIV, "9937", "ko", "KR", this)의 내용 변경
