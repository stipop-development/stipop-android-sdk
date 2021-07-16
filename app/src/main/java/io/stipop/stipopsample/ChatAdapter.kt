package io.stipop.stipopsample

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import com.bumptech.glide.Glide

class ChatAdapter(val context: Context, val arrayList: ArrayList<ChatModel>)
    :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal lateinit var preferences: SharedPreferences

    fun addItem(item: ChatModel) {
        if (arrayList != null) {
            arrayList.add(item)
        }
    }

    override fun getItemId(position: Int): Long {
        return arrayList[position].hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        return if(viewType == 1){
            view = LayoutInflater.from(context).inflate(R.layout.item_my_chat, parent, false)
            Holder(view)
        } else{
            view = LayoutInflater.from(context).inflate(R.layout.sticker_send, parent, false)
            Holder3(view)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder is Holder) {
            (viewHolder as Holder).chat_Text?.setText(arrayList[position].script)
        } else if(viewHolder is Holder2) {
            (viewHolder as Holder2).chat_You_Image?.setImageResource(R.mipmap.ic_launcher)
            (viewHolder as Holder2).chat_You_Name?.setText(arrayList[position].name)
            (viewHolder as Holder2).chat_Text?.setText(arrayList[position].script)
        } else if (viewHolder is Holder3) {
            (viewHolder as Holder3).bind(arrayList[position])
        }

    }


    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chat_Text: TextView = itemView?.findViewById<TextView>(R.id.chat_Text)
    }

    inner class Holder3(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chat_sticker: ImageView = itemView.findViewById<ImageView>(R.id.my_sticker)

        fun bind(item: ChatModel) {
            Glide.with(itemView).load(item.sticker_url).dontAnimate().into(chat_sticker)
        }
    }

    inner class Holder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chat_You_Image: ImageView = itemView?.findViewById<ImageView>(R.id.chat_You_Image)
        val chat_You_Name: TextView = itemView?.findViewById<TextView>(R.id.chat_You_Name)
        val chat_Text: TextView = itemView?.findViewById<TextView>(R.id.chat_Text)
    }

    override fun getItemViewType(position: Int): Int {

        return if (arrayList[position].chat_type) {
            1
        } else {
            2
        }
    }
}