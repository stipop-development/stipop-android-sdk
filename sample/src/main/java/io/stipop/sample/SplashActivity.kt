package io.stipop.sample

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class SplashActivity : AppCompatActivity() {

    private val startButtonView: View by lazy { findViewById(R.id.startSampleButtonView) }
    private val goToDocsTextView: View by lazy { findViewById(R.id.goToDocsTextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startButtonView.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        goToDocsTextView.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://docs.stipop.io/en/sdk/android/get-started/quick-start")
            ).run { startActivity(this) }
        }
    }
}