package io.stipop.sample.models

import java.util.*

class ChatItem(
    val nickname: String,
    val profileUrl: String,
    val message: String? = null,
    val stickerUrl: String? = null,
    val isMine: Boolean = true,
    val timestamp: Date = Date()
)