package io.stipop.stipopsample

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import io.stipop.Stipop
import io.stipop.StipopDelegate
import io.stipop.activity.Keyboard
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker

class MainActivity : AppCompatActivity(), StipopDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stipopIV = findViewById<StipopImageView>(R.id.stipopIV)

        Stipop.connect(this, stipopIV, "1234", "en", "US", this)


        val keyboardView = Keyboard(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, keyboardView)
            .commitAllowingStateLoss()

        stipopIV.setOnClickListener {
            // Stipop.showSearch()
            Stipop.showKeyboard()
        }
    }

    override fun onStickerSelected(sticker: SPSticker) {
        print(sticker)
    }

    override fun canDownload(spPackage: SPPackage): Boolean {
        print(spPackage)

        return true
    }
}