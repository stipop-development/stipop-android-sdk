package io.stipop.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class SplashActivity : AppCompatActivity() {

    private val startButtonView: View by lazy { findViewById(R.id.startSampleButtonView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startButtonView.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}