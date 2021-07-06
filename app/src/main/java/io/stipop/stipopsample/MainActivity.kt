package io.stipop.stipopsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.stipop.Stipop
import io.stipop.StipopDelegate
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker

class MainActivity : AppCompatActivity(), StipopDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stipopIV = findViewById<StipopImageView>(R.id.stipopIV)

        Stipop.connect(this, stipopIV, "1234", "en", "US", this)

        stipopIV.setOnClickListener {
             Stipop.showSearch()
//            Stipop.showKeyboard()
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