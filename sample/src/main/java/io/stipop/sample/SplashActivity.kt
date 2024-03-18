package io.stipop.sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val newUserLoginView: View by lazy { findViewById(R.id.new_user_login_view) }
    private val commonUserLoginView: View by lazy { findViewById(R.id.common_user_login_view) }
    private val goToDocsTextView: View by lazy { findViewById(R.id.goToDocsTextView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        newUserLoginView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            val bundle = Bundle()

            val randomUUID = UUID.randomUUID()
            bundle.putString("user_id", randomUUID.toString())
            intent.putExtras(bundle)

            startActivity(intent)
        }
        commonUserLoginView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            val bundle = Bundle()
            bundle.putString("user_id", "-1")
            intent.putExtras(bundle)

            startActivity(intent)
        }
        goToDocsTextView.setOnClickListener {
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://docs.stipop.io/en/sdk/android/get-started/before-you-begin")
            ).run { startActivity(this) }
        }
    }
}