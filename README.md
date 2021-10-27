<h1 align="center">Stipop Sticker Android SDK</h1>
<p align="center">
:balloon: Power communication in your app with 150,000 stickers. :balloon:</br>

![Android-demo image](https://user-images.githubusercontent.com/42525347/139039262-2fc7a0d2-d000-4848-b7be-eee2beede9f8.png)

</p>
</br>

## Introducing SDK with Demo
[![](https://jitpack.io/v/stipop-development/stipop-android-sdk.svg)](https://jitpack.io/#stipop-development/stipop-android-sdk)
<a href="https://android-arsenal.com/api?level=16"><img alt="AndroidMinApi" src="https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat"/></a></br>
Add the sticker picker and search feature to let your users select the stickers they want to send in your app. The built-in design of the store provides easy access to the entire library of stickers grouped in packs with user-friendly navigation.

**1. Fully-featured design guide** <br/>
Exceptionally designed UI and assets with light and dark modes.<br/><br/>
**2. The world’s largest sticker library** <br/>
Over 150,000 GIF and PNG stickers, timely updated with trending content.<br/><br/>
**3. Full spectrum of features** <br/>
Trend reports, easy sticker search and access to favorites, feed of recently added stickers, and more.<br/><br/>


Demo
--------
<p align="center">
      
![Android-demo-screenshot (1)](https://user-images.githubusercontent.com/42525347/139039328-e02059dc-11fd-416f-9135-1d124ef782b7.png)
      
</p>
If you want to know what is 'Stipop Sticker SDK', try building a demo app first.</br></br>

1. Clone or download this repository.<br/>
2. Sign up on <a href="https://dashboard.stipop.io/" target="_blank">Stipop Dashboard</a> and download **'Stipop.json'** for free.<br/>
3. Open code on Android Studio, then create 'assets' resource folder under **'app > src > main'**.<br/>
4. Move Stipop.json into the assets folder you created. In the Stipop.json file there is your personal API key ans so on.
      - For more guide on this, please see <a href="https://docs.stipop.io/en/sdk/android/customize/overview/" target="_blank">Android SDK Customize</a>.
5. Build and run 'sample' app on your device.<br/><br/>

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
1. Sign up on <a href="https://dashboard.stipop.io/" target="_blank">Stipop Dashboard</a> and create a application.
2. Download **'Stipop.json'** and move it into 'assets'.
3. Make or update your application class. (This operation initializes the SDK from Stipop.json.)
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stipop.configure(this)
    }
}
```
4. Update your AndroidManifest.xml
```xml
    <application
        android:name=".{YourApplicationClass}"
                 ...
        tools:replace="android:theme">
}
```
5. Then implement 'StipopDelegate' interface and Call 'Stipop.connect' method wherever you want to use it.
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
6. Two types of explorers are provided.
```kotlin
class YourActivity : Activity(), StipopDelegate {
    ...
    fun setListener() {
      button.onClick {
        Stipop.showKeyboard() // Use Sticker SDK in keyboard
        // Stipop.showSearch() // Use Sticker SDK in dialog
      }
    }
}
```
<br/>

Requirements
-------------------
- Kotlin
- Android + (API level 16) or higher
- Java 7 or higher
- Support androidx only
- Gradle 3.4.0 or higher


For any questions regarding the demo, please email us at tech-support@stipop.io. Thank you.

<br/>
<br/>

[1]: https://docs.stipop.io/en/sdk/android/get-started/quick-start
