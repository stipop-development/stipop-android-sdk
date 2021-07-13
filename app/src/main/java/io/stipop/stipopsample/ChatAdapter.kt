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

    fun addItem(item: ChatModel) {//아이템 추가
        if (arrayList != null) {
            arrayList.add(item)
        }
    }

    override fun getItemId(position: Int): Long { // 특정 id 가 없을 시 position 값으로 대체
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

    //내가친 채팅 뷰홀더
    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //친구목록 모델의 변수들 정의하는부분
        val chat_Text: TextView = itemView?.findViewById<TextView>(R.id.chat_Text)
//        val chat_Time: TextView = itemView?.findViewById<TextView>(R.id.chat_Time)
    }

    inner class Holder3(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //친구목록 모델의 변수들 정의하는부분
        private val chat_sticker: ImageView = itemView.findViewById<ImageView>(R.id.my_sticker)
//        private val chat_Time: TextView = itemView?.findViewById<TextView>(R.id.chat_Time)

        fun bind(item: ChatModel) {
            Glide.with(itemView).load(item.sticker_url).into(chat_sticker)
//            chat_Time.text = item.date_time
        }
    }

    //상대가친 채팅 뷰홀더
    inner class Holder2(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //친구목록 모델의 변수들 정의하는부분
        val chat_You_Image: ImageView = itemView?.findViewById<ImageView>(R.id.chat_You_Image)
        val chat_You_Name: TextView = itemView?.findViewById<TextView>(R.id.chat_You_Name)
        val chat_Text: TextView = itemView?.findViewById<TextView>(R.id.chat_Text)
//        val chat_Time: TextView = itemView?.findViewById<TextView>(R.id.chat_Time)


    }

    override fun getItemViewType(position: Int): Int {//여기서 뷰타입을 1, 2로 바꿔서 지정해줘야 내채팅 너채팅을 바꾸면서 쌓을 수 있음

        return if (arrayList[position].chat_type) {
            1
        } else {
            2
        }
    }
}