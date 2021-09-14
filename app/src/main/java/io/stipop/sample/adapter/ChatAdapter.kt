package io.stipop.sample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.bumptech.glide.Glide
import io.stipop.sample.R
import io.stipop.sample.models.ChatItem

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val chatItems: ArrayList<ChatItem> = ArrayList()

    companion object {
        const val TYPE_MESSAGE = 1001
        const val TYPE_STICKER = 1002
    }

    inner class SimpleMessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chat_Text: TextView = itemView?.findViewById<TextView>(R.id.chat_Text)
        fun bind(item: ChatItem) {
            chat_Text.text = item.message
        }
    }

    inner class StickerMessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chat_sticker: ImageView = itemView.findViewById<ImageView>(R.id.my_sticker)
        fun bind(item: ChatItem) {
            Glide.with(itemView).load(item.stickerUrl).into(chat_sticker)
        }
    }

    fun addChatItem(item: ChatItem) {
        chatItems.add(item)
    }

    override fun getItemId(position: Int): Long {
        return chatItems[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            TYPE_STICKER -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_sticker, parent, false)
                StickerMessageItemHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_message, parent, false)
                SimpleMessageItemHolder(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatItems.size

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is SimpleMessageItemHolder) {
            viewHolder.bind(chatItems[position])
        } else if (viewHolder is StickerMessageItemHolder) {
            viewHolder.bind(chatItems[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatItems[position].stickerUrl.isNullOrEmpty()) {
            TYPE_MESSAGE
        } else {
            TYPE_STICKER
        }
    }
}
