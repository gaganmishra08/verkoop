package com.verkoopapp.models


data class ChatData(
        val id: Int,
        val senderId: Int,
        val receiverId: Int,
        val message: String,
        val timeStamp: String,
        val type: Int,
        val item_id: Int,
        val chat_user_id: Int,
        val is_read: Int
)

data class ChatInboxResponse(
        val is_archive: Int,
        val is_delete: Int,
        val chat_user_id: String,
        val timestamp: String,
        val types: Int,
        val unread_count: Int,
        val sender_id: Int,
        val receiver_id: Int,
        val item_id: Int,
        val message_id: Int,
        val username: String,
        val profile_pic: String,
        val message: String,
        val user_id: Int,
        val is_sold: Int,
        val item_name: String,
        val offer_status: Int,
        val url: String,
        val offer_price: String,
        val item_price: String,
        val is_rate: Int = 0,
        val min_price: String? = null,
        val max_price: String? = null,
        val category_id: Int = 0,
        val is_block: Int = 0,
        val block_id: Int = 0,
        val user_block_id: Int = 0
)