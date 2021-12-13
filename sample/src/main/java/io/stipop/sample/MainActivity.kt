package io.stipop.sample

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
 * Before looking at this code, make sure that Stipop.json file is in the asset folder
 * and that Stipop.configure method is called in the your application class.
 *
 * The received sticker can be handled through StipopDelegate Interface.
 */
class MainActivity : AppCompatActivity(), StipopDelegate, ChatAdapter.GuideDelegate {

    // IMPORTANT :: The downloaded sticker is saved according to the user ID.
    @SuppressLint("SimpleDateFormat")
    private val testUserId = SimpleDateFormat("yyyyMMdd").format(Date())

    // This Code below is used to configure the sample app, so you can ignore it.
    private val toolBar: Toolbar by lazy { findViewById(R.id.toolBar) }
    private val profileImageView: AppCompatImageView by lazy { findViewById(R.id.profileImageView) }
    private val nameTextView: AppCompatTextView by lazy { findViewById(R.id.nameTextView) }
    private val statusTextView: AppCompatTextView by lazy { findViewById(R.id.statusTextView) }
    private val chatInputEditText: AppCompatEditText by lazy { findViewById(R.id.chatInputEditText) }
    private val chatRecyclerview: RecyclerView by lazy { findViewById(R.id.chatRecyclerView) }
    private val stipopPickerImageView: StipopImageView by lazy { findViewById(R.id.stickerPickerImageView) }
    private val stipopSearchImageView: StipopImageView by lazy { findViewById(R.id.stickerSearchImageView) }
    private val sendImageView: AppCompatImageView by lazy { findViewById(R.id.sendImageView) }
    private val chatsAdapter: ChatAdapter by lazy { ChatAdapter(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // IMPORTANT :: This method must be called to use STIPOP SDK in the activity.
        Stipop.connect(this, testUserId, this, stipopPickerImageView, taskCallBack = {
            Log.d(
                this.javaClass.name,
                "If you need additional settings, please call it in callback scope."
            )
        })

        stipopPickerImageView.setOnClickListener {
            Stipop.showKeyboard()
        }

        stipopSearchImageView.setOnClickListener {
            Stipop.showSearch()
        }
        initSampleUi()
    }

    override fun onStickerSelected(sticker: SPSticker): Boolean {
        sendSticker(sticker)
        return true
    }

    override fun onStickerPackRequested(spPackage: SPPackage): Boolean {
        return true
    }

    //
    // The code below is used to configure the sample app, so you can ignore it.
    //
    @SuppressLint("SetTextI18n")
    private fun initSampleUi() {

        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nameTextView.text = "Test User Id : $testUserId"
        statusTextView.text = "Language : ${Locale.getDefault().language} / Country : ${Locale.getDefault().country}"

        Glide.with(this).load(R.drawable.img_profile).apply(RequestOptions().circleCrop())
            .into(profileImageView)

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