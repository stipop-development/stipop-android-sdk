package io.stipop.sample.models

class ChatItem(
    val nickname: String,
    val profileUrl: String,
    val message: String? = null,
    val stickerUrl: String? = null
)