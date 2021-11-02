![Android-demo image](https://user-images.githubusercontent.com/42525347/139039262-2fc7a0d2-d000-4848-b7be-eee2beede9f8.png)
<h1>Stipop Android UI SDK</h1>

[![](https://jitpack.io/v/stipop-development/stipop-android-sdk.svg)](https://jitpack.io/#stipop-development/stipop-android-sdk)
<a href="https://android-arsenal.com/api?level=16"><img alt="AndroidMinApi" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a></br>
Stipop SDK powers over 150,000 stickers(+animated) that can be integrated to chat, camera, video call, and profile interfaces. Get access to world's no.1 sticker platform and boost user engagement.

Stipop Android UI SDK offers you a super easy way to implement sticker service right into your app.

Requirements
-------------------
- Kotlin
- Android + (API level 16) or higher
- Java 7 or higher
- Support androidx only
- Gradle 3.4.0 or higher

Getting Started
--------
1. Sign up on <a href="https://dashboard.stipop.io/" target="_blank">Stipop Dashboard</a>
2. Create your application to get API Key.
3. Download **'Stipop.json'** from the dashboard and move it into 'assets' folder in android project.
4. After that, you can build demo app or your own project.

Try Demo
--------

If you want to know what is 'Stipop Sticker SDK', try building a demo app first.</br></br>

1. Clone or download this repository.<br/>
2. Move Stipop.json into the assets folder you created. 
      - If you do not have this file, see 'Getting Started' first.
      - In the Stipop.json file there is your personal API key ans so on.
      - For more guide on this, please see <a href="https://docs.stipop.io/en/sdk/android/customize/overview/" target="_blank">Android SDK Customize</a>.
3. Build and run 'sample' app on your device.<br/><br/>

<p align="center">
      
![Android-demo-screenshot (1)](https://user-images.githubusercontent.com/42525347/139039328-e02059dc-11fd-416f-9135-1d124ef782b7.png)
      
</p>

Including in your project
--------
For detailed instructions and requirements, see [Android SDK docs page][1].

Use Gradle:

```gradle
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
  // like 0.2.2. Please check latest release!
  implementation 'com.github.stipop-development:stipop-android-sdk:{latest_version}' 
}
```
<br/>

How do I use Stipop SDK?
-------------------
1. Move Stipop.json into the assets folder you created. 
      - If you do not have this file, see 'Getting Started' first.
      - In the Stipop.json file there is your personal API key ans so on.
      - For more guide on this, please see <a href="https://docs.stipop.io/en/sdk/android/customize/overview/" target="_blank">Android SDK Customize</a>.
2. Make or update your application class. (This operation initializes the SDK from 'Stipop.json' file)
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stipop.configure(this)
    }
}
```
3. Update your 'AndroidManifest.xml' to specify application class.
```xml
    <application
        android:name=".{YourApplicationClass}"
                 ...
        tools:replace="android:theme">
}
```
4. Then implement 'StipopDelegate' interface and Call 'Stipop.connect' method wherever you want to use it. (like Activity or Fragment)
```kotlin
class YourActivity : Activity(), StipopDelegate {

    override fun onCreate() {
        super.onCreate()
        Stipop.connect(activity = this, userId = "userId", delegate = this)
    }
    ...
    override fun onStickerSelected(sticker: SPSticker): Boolean {
      // Sticker will be received here.
      // sendSticker(sticker.stickerImg)
      return true
    }
}
```
5. Two types of UI components are supported.
      - Stipop.showKeyboard() : Sticker Picker View
      - Stipop.showSearch() : Sticker Search View
```kotlin
class YourActivity : Activity(), StipopDelegate {

    override fun onCreate() {
        super.onCreate()
        Stipop.connect(activity = this, userId = "userId", delegate = this)
        setListener()
    }
    
    fun setListener() {
      button.onClick {
        Stipop.showKeyboard() // Use Sticker SDK in keyboard
        // Stipop.showSearch() // Use Sticker SDK in dialog
      }
    }
    
    override fun onStickerSelected(sticker: SPSticker): Boolean {
      // Sticker will be received here.
      return true
    }
}
```
<br/>

## Contact us

- For more information, visit [Stipop Docs][1] to see our official document.
- If you have any trouble or question, email us at tech-support@stipop.io . We'll gladly support you :)

[1]: https://docs.stipop.io/en/sdk/android/get-started/quick-start
