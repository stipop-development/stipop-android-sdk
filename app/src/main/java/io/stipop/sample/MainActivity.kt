package io.stipop.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.StipopDelegate
import io.stipop.extend.StipopImageView
import io.stipop.models.SPPackage
import io.stipop.models.SPSticker
import io.stipop.sample.adapter.ChatAdapter
import io.stipop.sample.models.ChatItem

// IMPORTANT :: The received sticker can be handled through StipopDelegate Interface.
class MainActivity : AppCompatActivity(), StipopDelegate {

    // IMPORTANT :: The downloaded sticker is saved according to the user ID.
    private val testUserId = "put-user-id-here"
    private val testProfileUrl = "change-user-profile-image-url-here"
    private val testUserName = "change-user-name-anything"

    private val chatInputEditText: AppCompatEditText by lazy { findViewById(R.id.chatInputEditText) }
    private val chatRecyclerview: RecyclerView by lazy { findViewById(R.id.chatRecyclerView) }
    private val stipopPickerImageView: StipopImageView by lazy { findViewById(R.id.stickerPickerImageView) }
    private val stipopSearchImageView: StipopImageView by lazy { findViewById(R.id.stickerSearchImageView) }
    private val chatsAdapter: ChatAdapter by lazy { ChatAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // IMPORTANT :: This must be called to use the Stipop SDK.
        Stipop.connect(this, stipopPickerImageView, testUserId, "en", "US", this)

        chatRecyclerview.apply {
            chatsAdapter.setHasStableIds(true)
            adapter = chatsAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        stipopPickerImageView.setOnClickListener {
            Stipop.showKeyboard()
        }

        stipopSearchImageView.setOnClickListener {
            Stipop.showSearch()
        }

        chatInputEditText.setOnEditorActionListener { _, _, _ ->
            sendMessage(chatInputEditText.text?.toString() ?: "")
            true
        }
    }

    private fun sendMessage(chatMessage: String) {
        if (chatMessage.isNotEmpty()) {
            val item = ChatItem(
                nickname = testUserName,
                message = chatMessage,
                profileUrl = testProfileUrl
            )
            chatsAdapter.run {
                addChatItem(item)
                notifyItemInserted(itemCount - 1)
                chatRecyclerview.scrollToPosition(itemCount - 1)
            }
            chatInputEditText.setText("")
        }
    }

    private fun sendSticker(stickerImg: String?) {
        val item = ChatItem(
            nickname = testUserName,
            profileUrl = testProfileUrl,
            stickerUrl = stickerImg.toString()
        )
        chatsAdapter.run {
            addChatItem(item)
            notifyItemInserted(itemCount - 1)
            chatRecyclerview.scrollToPosition(itemCount - 1)
        }
    }

    override fun onStickerSelected(sticker: SPSticker): Boolean {
        sendSticker(sticker.stickerImg)
        return true
    }

    override fun canDownload(spPackage: SPPackage): Boolean {
        return true
    }
}