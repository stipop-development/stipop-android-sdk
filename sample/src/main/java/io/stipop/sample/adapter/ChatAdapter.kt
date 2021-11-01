package io.stipop.sample.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.bumptech.glide.Glide
import io.stipop.sample.R
import io.stipop.sample.models.ChatItem
import java.text.SimpleDateFormat
import java.util.*

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
        fun onSentStickerClick(packageId: Int?)
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
        return if (chatItems[position - 1].spSticker == null) {
            TYPE_MESSAGE_MINE
        } else {
            TYPE_STICKER_MINE
        }
    }

    inner class SimpleMessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatTextView: TextView = itemView.findViewById(R.id.chat_Text)
        fun bind(item: ChatItem) {
            chatTextView.text = item.message
        }
    }

    inner class StickerMessageItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatStickerImageView: ImageView = itemView.findViewById(R.id.my_sticker)
        private lateinit var chatItem: ChatItem

        init {
            chatStickerImageView.setOnClickListener {
                guideDelegate.onSentStickerClick(chatItem.spSticker?.packageId)
            }
        }

        fun bind(item: ChatItem) {
            chatItem = item
            Glide.with(itemView)
                .load(item.spSticker?.stickerImgLocalFilePath ?: item.spSticker?.stickerImg)
                .into(chatStickerImageView)
        }
    }

    @SuppressLint("SimpleDateFormat")
    inner class GuideItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ANIMATION_DURATION = 500L

        private val transitionSet =
            TransitionSet().addTransition(Fade()).addTransition(Slide(Gravity.LEFT))
                .setDuration(ANIMATION_DURATION).addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        super.onTransitionEnd(transition)
                        TransitionManager.beginDelayedTransition(viewGroup2, transitionSet2)
                        guideTextView4.isVisible = true
                    }
                })
        private val transitionSet2 =
            TransitionSet().addTransition(Fade()).addTransition(Slide(Gravity.LEFT))
                .setDuration(ANIMATION_DURATION).addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        super.onTransitionEnd(transition)
                        TransitionManager.beginDelayedTransition(viewGroup3, transitionSet3)
                        searchViewTextView.isVisible = true
                        pickerViewTextView.isVisible = true
                    }
                })
        private val transitionSet3 =
            TransitionSet().addTransition(Fade()).addTransition(Slide(Gravity.LEFT))
                .setDuration(ANIMATION_DURATION).addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        super.onTransitionEnd(transition)
                        searchViewTextView.startAnimation(focusAnimation)
                        pickerViewTextView.startAnimation(focusAnimation)
                    }
                })

        private val focusAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.shake)

        private val dateTimeTextView: AppCompatTextView =
            itemView.findViewById(R.id.datetimeTextView)
        private val sampleStickerImageView: AppCompatImageView =
            itemView.findViewById(R.id.guideImageView)
        private val viewGroup1: LinearLayout = itemView.findViewById(R.id.animationViewGroup1)
        private val viewGroup2: LinearLayout = itemView.findViewById(R.id.animationViewGroup2)
        private val viewGroup3: LinearLayout = itemView.findViewById(R.id.animationViewGroup3)
        private val guideTextView2: AppCompatTextView = itemView.findViewById(R.id.guideTextView2)
        private val guideImageView: AppCompatImageView = itemView.findViewById(R.id.guideImageView)
        private val guideTextView4: AppCompatTextView = itemView.findViewById(R.id.guideTextView4)
        private val searchViewTextView: AppCompatTextView =
            itemView.findViewById(R.id.guideTextView5)
        private val pickerViewTextView: AppCompatTextView =
            itemView.findViewById(R.id.guideTextView6)

        init {
            dateTimeTextView.text = SimpleDateFormat("a h:mm").format(Date())
            Glide.with(itemView).load(SAMPLE_STICKER).into(sampleStickerImageView)
            searchViewTextView.setOnClickListener {
                guideDelegate.onStickerSearchViewClick()
            }
            pickerViewTextView.setOnClickListener {
                guideDelegate.onStickerPickerViewClick()
            }
            viewGroup1.post {
                TransitionManager.beginDelayedTransition(viewGroup1, transitionSet)
                guideTextView2.isVisible = true
                guideImageView.isVisible = true
            }
        }
    }
}
