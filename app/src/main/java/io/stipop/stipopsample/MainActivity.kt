package io.stipop.stipopsample

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.stipop.Stipop
import io.stipop.StipopDelegate
import io.stipop.extend.StipopImageView
import io.stipop.model.SPPackage
import io.stipop.model.SPSticker
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), StipopDelegate {

    private lateinit var chatText: EditText
    private lateinit var sendButton: Button
    private lateinit var chat_recyclerview: RecyclerView

    var chatList = arrayListOf<ChatModel>()
    val mAdapter = ChatAdapter(this, chatList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter.setHasStableIds(true)

        val stipopIV = findViewById<StipopImageView>(R.id.stipopIV)

        chat_recyclerview = findViewById<RecyclerView>(R.id.chat_recyclerview);
        chat_recyclerview.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)


        chatText = findViewById(R.id.chatET)
//        sendButton = findViewById(R.id.sendButton)

        Stipop.connect(this, stipopIV, "9937", "en", "US", this)

        stipopIV.setOnClickListener {
//            Stipop.showSearch()
            Stipop.showKeyboard()
        }

        chatText.setOnEditorActionListener { textView, action, event ->
            sendMessage()
            true
        }

//        sendButton.setOnClickListener {
//            Stipop.showSearch()
//        }

    }

    private fun sendMessage() {

        if (chatText.text.toString().length !== 0) {

            val item = ChatModel("testName", chatText.text.toString(),"example", true, "")
            mAdapter.addItem(item)
            mAdapter.notifyItemInserted(mAdapter.itemCount-1)
            chatText.setText("")
            chat_recyclerview.scrollToPosition(mAdapter.itemCount-1)

        }
    }

    private fun sendSticker(stickerImg: String?) {

        val item = ChatModel("testName", "","example", false, stickerImg.toString())

        mAdapter.addItem(item)
        mAdapter.notifyItemInserted(mAdapter.itemCount-1)

        chat_recyclerview.scrollToPosition(mAdapter.itemCount-1)

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
}