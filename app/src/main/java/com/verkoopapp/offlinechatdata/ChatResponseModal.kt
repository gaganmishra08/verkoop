package com.verkoopapp.offlinechatdata

import androidx.annotation.VisibleForTesting
import io.realm.RealmObject

@VisibleForTesting
open class ChatInboxRealmResponse: RealmObject(){

     var is_archive: Int=0
     var is_delete: Int=0
     var chat_user_id: String?=null
     var timestamp: String?=null
     var types: Int=0
     var unread_count: Int=0
     var sender_id: Int=0
     var receiver_id: Int=0
     var item_id: Int=0
     var message_id: Int=0
     var username: String?=null
     var profile_pic: String?=null
     var message: String?=null
     var user_id: Int=0
     var is_sold: Int=0
     var item_name: String?=null
     var offer_status: Int=0
     var url: String?=null
     var offer_price: String?=null
     var item_price: String?=null
     var is_rate: Int?=0
     var min_price: String?=null
     var max_price: String?=null
     var category_id: Int?=0
}



open class ChatResponse: RealmObject(){
     var sender_id: Int=0
     var receiver_id: Int=0
     var message_id: Int=0
     var timestamp: String?=null
     var is_read: Int=0
     var id: Int=0
     var message: String?=null
     var chat_user_id: Int=0
     var type: Int=0
     var item_id: Int=0
}