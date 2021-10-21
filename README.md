[![](https://jitpack.io/v/stipop-development/stipop-android-sdk.svg)](https://jitpack.io/#stipop-development/stipop-android-sdk)

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

2. Run code and test 'Search View' (default) on device.
  - The language of stickers is optimized for user's lang info.
  - You can change user lang info in #4 below.
<br/>

3. Switch to 'Keyboard View' and test on device.
  - To switch the view from Search to Keyboard:
     - Go to **app > src > main > java.io.stipop.stipopsample > MainActivity**
     - From the **stipopIV.setOnClickListener**, comment the Search() part and activate Keyboard() part.
          Stipop.showSearch()      //Search UI
          Stipop.showKeyboard()    //Keyboard UI
<br/>

4. How to change user lang
  - Go to **app > src > main > java.io.stipop.stipopsample > MainActivity**
  - Change **lang** and **country** from **Stipop.connect(this, stipopIV, "9937", "en", "US", this)**. 
  - The lang code should be a two letter language code like en or es (Spanish), and country should be the same but capitalized.
<br/>


For any questions regarding the demo, please email us at tech-support@stipop.io. Thank you.
