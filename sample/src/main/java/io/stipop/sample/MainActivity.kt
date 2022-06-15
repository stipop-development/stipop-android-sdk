package io.stipop.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.stipop.Stipop
import io.stipop.StipopDelegate
import io.stipop.custom.StipopImageView
import io.stipop.models.SPPackage
import io.stipop.models.SPSticker
import io.stipop.sample.adapter.ChatAdapter
import io.stipop.sample.models.ChatItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * IMPORTANT
 * @see GlobalApplication
 * @see assets/Stipop.json
 *
 *
 * Before looking at this code, make sure that Stipop.json file is in the asset folder
 * and that Stipop.configure() method is called in  your application class.
 *
 * Please make sure, that the themes-replace snippet is enabled in AndroidManifest.xml
 *
 * The received sticker can be handled through StipopDelegate Interface.
 */

class MainActivity : AppCompatActivity(), StipopDelegate, ChatAdapter.GuideDelegate {

    @SuppressLint("SimpleDateFormat")
    private lateinit var userID: String

    private val TAG: String = "MainActivity"

    // This Code below is used to configure the sample app, so you can ignore it.
    private val toolBar: Toolbar by lazy { findViewById(R.id.toolBar) }
    private val profileImageView: AppCompatImageView by lazy { findViewById(R.id.profileImageView) }
    private val nameTextView: AppCompatTextView by lazy { findViewById(R.id.nameTextView) }
    private val statusTextView: AppCompatTextView by lazy { findViewById(R.id.statusTextView) }
    private val chatInputEditText: AppCompatEditText by lazy { findViewById(R.id.chatInputEditText) }
    private val chatRecyclerview: RecyclerView by lazy { findViewById(R.id.chatRecyclerView) }
    private val stipopPickerImageView: StipopImageView by lazy { findViewById(R.id.stickerPickerImageView) }
    private val sendImageView: AppCompatImageView by lazy { findViewById(R.id.sendImageView) }
    private val chatsAdapter: ChatAdapter by lazy { ChatAdapter(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userIDSetup()

        // IMPORTANT :: This method must be called to use STIPOP SDK in the activity.
        Stipop.connect(this, userID, this, stipopPickerImageView, taskCallBack = {
            Log.d(
                this.javaClass.name,
                "If you need additional settings, please call it in callback scope."
            )
        })

        stipopPickerImageView.setOnClickListener {
            Stipop.showKeyboard()
        }

        initSampleUi()
    }
    private fun userIDSetup(){
        val bundle = intent.extras

         userID = bundle!!.getString("user_id", "someone_user_id")

    }

    /**
     * onStickerPackRequested
     * @param sticker: the SPSticker item on clicked to be Downloaded
     * @return true : if the sticker item was clicked
     *         false: if the sticker is not enabled to perform select operation
     *
     */
    override fun onStickerSingleTapped(sticker: SPSticker): Boolean {
        Toast.makeText(applicationContext, "Clicked on the Sticker", Toast.LENGTH_SHORT).show()
        return true
    }

    override fun onStickerDoubleTapped(sticker: SPSticker): Boolean {
        sendSticker(sticker)
        return true
    }

    /**
     * onStickerPackRequested
     * @param spPackage: the package to be Downloaded
     * @return true : if the package can be
     *         false: if the package can't be requested to download
     *
     *         The downloaded sticker package is saved according to the user ID.
     */
    override fun onStickerPackRequested(spPackage: SPPackage): Boolean {
        Log.d(TAG, spPackage.toString());
        return true
    }

    /**
     * initSampleUi
     * @sample configure the sample app
     */
    @SuppressLint("SetTextI18n")
    private fun initSampleUi() {

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        when(userID){
            "someone_user_id" -> nameTextView.text = "Common user"
            else -> nameTextView.text = "Random user"
        }

        Glide.with(this).load(R.drawable.img_profile).apply(RequestOptions().circleCrop()).into(profileImageView)

        chatRecyclerview.apply {
            chatsAdapter.setHasStableIds(true)
            adapter = chatsAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    chatRecyclerview.postDelayed({
                        chatRecyclerview.smoothScrollToPosition(chatsAdapter.itemCount - 1)
                    }, 100)
                }
            }
        }

        chatInputEditText.setOnClickListener {
            Stipop.hideKeyboard()
        }

        chatInputEditText.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                toggleSendButton(false)
            } else {
                toggleSendButton(true)
            }
        }
        sendImageView.setOnClickListener {
            sendMessage()
        }
        chatInputEditText.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun toggleSendButton(isActivate: Boolean) {
        when (isActivate) {
            true -> sendImageView.setColorFilter(ContextCompat.getColor(this, R.color.primary))
            false -> sendImageView.setColorFilter(ContextCompat.getColor(this, R.color.deactivate))
        }
    }

    private fun sendMessage(message: String? = null) {
        val chatMessage = message ?: chatInputEditText.text?.toString() ?: ""
        if (chatMessage.isNotEmpty()) {
            val item = ChatItem(
                message = chatMessage
            )
            chatsAdapter.run {
                addChatItem(item)
                notifyItemInserted(itemCount - 1)
                chatRecyclerview.scrollToPosition(itemCount - 1)
            }
            chatInputEditText.setText("")
        }
    }

    private fun sendSticker(spSticker: SPSticker?) {
        val item = ChatItem(
            spSticker = spSticker
        )
        chatsAdapter.run {
            addChatItem(item)
            notifyItemInserted(itemCount - 1)
            chatRecyclerview.smoothScrollToPosition(itemCount - 1)
        }
    }

    override fun onSentStickerClick(packageId: Int?) {
        packageId?.let {
            Stipop.showStickerPackage(supportFragmentManager, packageId)
        }
    }

    override fun onStickerSearchViewClick() {
        sendMessage("Let me try Sticker Search View! \uD83D\uDD0D")
        Stipop.showSearch()
    }

    override fun onStickerPickerViewClick() {
        sendMessage("Let me try Sticker Keyboard Picker View! \uD83D\uDE00")
        Stipop.showKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.contactUs -> {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://developers.stipop.io/contact-us")
                ).run { startActivity(this) }
            }
            R.id.goToGithub -> {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/stipop-development/stipop-android-sdk")
                ).run { startActivity(this) }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}