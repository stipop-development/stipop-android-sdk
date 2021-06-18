package io.stipop.stipopsample

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.stipop.Stipop

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val packageDetailTV = findViewById(R.id.packageDetailTV) as TextView


        val stipopIV = findViewById(R.id.stipopIV) as ImageView

        Stipop.connect(this, stipopIV, 123, "en", "US")

        stipopIV.setOnClickListener {
            Stipop.show()
        }

        packageDetailTV.setOnClickListener {
            Stipop.detail(1234)
        }


    }
}