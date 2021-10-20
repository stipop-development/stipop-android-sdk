package io.stipop.sample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.bumptech.glide.Glide
import io.stipop.sample.R
import io.stipop.sample.models.ChatItem

class ChatAdapter(val guideDelegate: GuideDelegate) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_INTRO = 1000
        const val TYPE_MESSAGE_MINE = 1001
        const val TYPE_STICKER_MINE = 1002
        const val SAMPLE_STICKER =
            "https://img.stipop.io/2020/3/31/1585719674256_CookieArrow_size.gif"
    }

    interface GuideDelegate {
        fun onStickerSearchViewClick()
        fun onStickerPickerViewClick()
    }

    private val chatItems: ArrayList<ChatItem> = ArrayList()

    fun addChatItem(item: ChatItem) {
        chatItems.add(item)
    }

    override fun getItemId(position: Int): Long {
        if (position == 0) {
            return 0
        }
        return chatItems[position - 1].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return when (viewType) {
            TYPE_INTRO -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_guide, parent, false)
                GuideItemHolder(view)
            }
            TYPE_STICKER_MINE -> {
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
        return chatItems.size + 1

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is SimpleMessageItemHolder -> {
                viewHolder.bind(chatItems[position - 1])
            }
            is StickerMessageItemHolder -> {
                viewHolder.bind(chatItems[position - 1])
            }
            else -> {
                //
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_INTRO
        }
        return if (chatItems[position - 1].stickerUrl.isNullOrEmpty()) {
            TYPE_MESSAGE_MINE
        } else {
            TYPE_STICKER_MINE
        }
    }

    inner class SimpleMessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatTextView: TextView = itemView.findViewById<TextView>(R.id.chat_Text)
        fun bind(item: ChatItem) {
            chatTextView.text = item.message
        }
    }

    inner class StickerMessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatStickerImageView: ImageView =
            itemView.findViewById<ImageView>(R.id.my_sticker)

        fun bind(item: ChatItem) {
            Glide.with(itemView).load(item.stickerUrl).into(chatStickerImageView)
        }
    }

    inner class GuideItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sampleStickerImageView: AppCompatImageView =
            itemView.findViewById(R.id.guideImageView)
        private val searchViewTextView: AppCompatTextView =
            itemView.findViewById(R.id.guideTextView5)
        private val pickerViewTextView: AppCompatTextView =
            itemView.findViewById(R.id.guideTextView6)

        init {
            Glide.with(itemView).load(SAMPLE_STICKER).into(sampleStickerImageView)
            searchViewTextView.setOnClickListener {
                guideDelegate.onStickerSearchViewClick()
            }
            pickerViewTextView.setOnClickListener {
                guideDelegate.onStickerPickerViewClick()
            }
        }
    }
}
