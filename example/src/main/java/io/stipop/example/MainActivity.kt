package io.stipop.example

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.StipopDelegate
import io.stipop.extend.StipopImageView
import io.stipop.refactor.data.models.SPPackage
import io.stipop.refactor.data.models.SPSticker
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), StipopDelegate {

    private lateinit var chatText: EditText
    private lateinit var chat_recyclerview: RecyclerView

    var chatList = arrayListOf<ChatModel>()
    val mAdapter = ChatAdapter(this, chatList)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter.setHasStableIds(true)

        val stipopIV = findViewById<StipopImageView>(R.id.stipopIV)
        val stipopIV2 = findViewById<StipopImageView>(R.id.stipopIV2)

        chat_recyclerview = findViewById<RecyclerView>(R.id.chat_recyclerview);
        chat_recyclerview.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)

        chatText = findViewById(R.id.chatET)

        Stipop.connect(this, stipopIV, "0000", "en", "US", this)

//        val keyboardView = Keyboard(this)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, keyboardView)
//            .commitAllowingStateLoss()

        stipopIV.setOnClickListener {
            Stipop.showKeyboard()
        }

        stipopIV2.setOnClickListener {
            Stipop.showSearch()
        }

        chatText.setOnEditorActionListener { textView, action, event ->
            sendMessage()
            true
        }
    }

    private fun sendMessage() {

        if (chatText.text.toString().length !== 0) {

            val item = ChatModel("testName", chatText.text.toString(), "example", true, "")
            mAdapter.addItem(item)
            mAdapter.notifyItemInserted(mAdapter.itemCount - 1)
            chatText.setText("")
            chat_recyclerview.scrollToPosition(mAdapter.itemCount - 1)

        }
    }

    private fun sendSticker(stickerImg: String?) {

        val item = ChatModel("testName", "", "example", false, stickerImg.toString())

        mAdapter.addItem(item)
        mAdapter.notifyItemInserted(mAdapter.itemCount - 1)

        chat_recyclerview.scrollToPosition(mAdapter.itemCount - 1)

    }

    private fun getTime(): String {

        val now = System.currentTimeMillis()
        val date = Date(now)

        val sdf = SimpleDateFormat("yyyy-MM-dd")

        return sdf.format(date)
    }

    override fun onStickerSelected(sticker: SPSticker): Boolean {
        println(sticker.stickerImg)
        sendSticker(sticker.stickerImg)
        return true
    }

    override fun canDownload(spPackage: SPPackage): Boolean {
        print(spPackage)

        return true
    }

    override fun onResume() {
        super.onResume()

    }
}
