![Github_stipop_SDK](https://user-images.githubusercontent.com/42525347/145160731-acbe1005-48f5-4c9e-93b7-8ce2c7d6dcb8.png)

<h1>Stipop UI SDK for Android</h1>

[![](https://jitpack.io/v/stipop-development/stipop-android-sdk.svg)](https://jitpack.io/#stipop-development/stipop-android-sdk)
<a href="https://android-arsenal.com/api?level=16"><img alt="AndroidMinApi" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a></br>
Stipop SDK provides over 150,000 .png and .gif stickers that can be easily integrated into mobile app chats, comment sections, live streams, video calls, and other features. Bring fun to your mobile app with stickers loved by millions of users worldwide.

Requirements
-------------------
- Kotlin
- Android + (API level 16) or higher
- Java 7 or higher
- Support androidx only
- Gradle 3.4.0 or higher

Getting Started
--------
1. Sign up to <a href="https://dashboard.stipop.io/" target="_blank">Stipop Dashboard</a>
2. Create your application to get API Key.
3. Download **'Stipop.json'** file.

Try Demo
--------
1. Clone this repository.<br/>
2. Move **Stipop.json** into the assets folder you created. 
3. Build and run 'sample' on your device.<br/><br/>

<p align="center">
      
![Android-demo-screenshot (1)](https://user-images.githubusercontent.com/42525347/139039328-e02059dc-11fd-416f-9135-1d124ef782b7.png)
      
</p>

Including in your project
--------
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

1. Move **Stipop.json** into the assets folder you created. 
2. Make or update your application class. (This operation initializes the SDK from 'Stipop.json' file)
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stipop.configure(this)
    }
}
```
3. Update your 'AndroidManifest.xml' to specify application class.<br>
   Check your application include 'INTERNET' permission to perform network operations.<br>
   Please put 'tools:replace="android:theme" to avoid conflict theme file in the application setting area.

```xml
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:name=".{YourApplicationClass}"
                 ...
        tools:replace="android:theme">
```
4. Then implement 'StipopDelegate' interface and Call 'Stipop.connect' method wherever you want to use it. (like Activity or Fragment)
```kotlin
class YourActivity : Activity(), StipopDelegate {

    override fun onCreate() {
        super.onCreate()
        Stipop.connect(activity = this, userId = "userId", delegate = this)
    }
    
    
    override fun onStickerSelected(sticker: SPSticker): Boolean {
      // Sticker will be received here.
      // sendSticker(sticker.stickerImg)
      return true
    }
    
   
    override fun onStickerPackRequested(spPackage: SPPackage): Boolean {
        // IMPORTANT
        // true -> the sticker package can be downloaded
        // false -> the sticker package can't be downloaded
        return true
    }


    override fun onStickerDoubleTapped(sticker: SPSticker): Boolean {
        // listener for double tap on the sticker
        return true
    }
}
```

5. Choose one of the two supported UI components.
      a. Stipop.showKeyboard() : Sticker Picker View
      b. Stipop.showSearch() : Sticker Search View
   
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
        // Stipop.showSearch() // Use Sticker SDK in dialog with keyword search
      }
    }
    
    override fun onStickerSelected(sticker: SPSticker): Boolean {
      // Sticker will be received here.
      return true
    }

    override fun onStickerPackRequested(spPackage: SPPackage): Boolean {
        // IMPORTANT
        // true -> the sticker package can be downloaded
        // false -> the sticker package can't be downloaded
        return true
    }

    override fun onStickerDoubleTapped(sticker: SPSticker): Boolean {
       // listener for double tap on the sticker
        return true
    }

}
```
<br/>

## Contact us

- For more information, visit [Stipop Documentation][1].
- Email us at tech-support@stipop.io if you need our help.

[1]: https://docs.stipop.io/en/sdk/android/get-started/quick-start
