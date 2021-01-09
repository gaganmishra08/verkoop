package com.verkoopapp.offlinechatdata

import com.verkoopapp.models.ChatData
import com.verkoopapp.models.ChatInboxResponse
import io.realm.Realm
import io.realm.RealmResults


class DbHelper {
    var realm: Realm = Realm.getDefaultInstance()

    fun insertAllChatData(AllChatList: ArrayList<ChatInboxResponse>) {
        realm.executeTransaction { realm1 ->
            for (i in AllChatList.indices) {
                val allChatRealModel = realm1.createObject(ChatInboxRealmResponse::class.java)
                allChatRealModel.is_archive = AllChatList[i].is_archive
                allChatRealModel.is_delete = AllChatList[i].is_delete
                allChatRealModel.chat_user_id = AllChatList[i].chat_user_id
                allChatRealModel.timestamp = AllChatList[i].timestamp
                allChatRealModel.types = AllChatList[i].types
                allChatRealModel.unread_count = AllChatList[i].unread_count
                allChatRealModel.sender_id = AllChatList[i].sender_id
                allChatRealModel.receiver_id = AllChatList[i].receiver_id
                allChatRealModel.item_id = AllChatList[i].item_id
                allChatRealModel.message_id = AllChatList[i].message_id
                allChatRealModel.username = AllChatList[i].username
                allChatRealModel.profile_pic = AllChatList[i].profile_pic
                allChatRealModel.message = AllChatList[i].message
                allChatRealModel.user_id = AllChatList[i].user_id
                allChatRealModel.is_sold = AllChatList[i].is_sold
                allChatRealModel.item_name = AllChatList[i].item_name
                allChatRealModel.offer_status = AllChatList[i].offer_status
                allChatRealModel.url = AllChatList[i].url
                allChatRealModel.offer_price = AllChatList[i].offer_price
                allChatRealModel.item_price = AllChatList[i].item_price
                allChatRealModel.is_rate = AllChatList[i].is_rate
                allChatRealModel.min_price = AllChatList[i].min_price
                allChatRealModel.max_price = AllChatList[i].max_price
                allChatRealModel.category_id = AllChatList[i].category_id
            }
        }
    }

    fun chatHistoryInsertData(chatsBeans: ArrayList<ChatData>) {
        realm.executeTransaction { realm1 ->
            for (i in chatsBeans.indices) {
                val chatHistoryModel = realm1.createObject(ChatResponse::class.java)
                chatHistoryModel.sender_id = chatsBeans[i].senderId
                chatHistoryModel.receiver_id = chatsBeans[i].receiverId
                chatHistoryModel.message_id = chatsBeans[i].id
                chatHistoryModel.timestamp = chatsBeans[i].timeStamp
                chatHistoryModel.is_read = chatsBeans[i].is_read
                chatHistoryModel.message = chatsBeans[i].message
                chatHistoryModel.chat_user_id = chatsBeans[i].chat_user_id
                chatHistoryModel.type = chatsBeans[i].type
                chatHistoryModel.item_id = chatsBeans[i].item_id
            }
        }
    }

    fun getAddChatList(itemId: Int): RealmResults<ChatInboxRealmResponse> {
        val value = 0
        val isArchive = 1
        return if (itemId != 0) {
            realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).and().notEqualTo("is_archive", isArchive).and().equalTo("item_id", itemId).findAll()
        } else {
            realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).and().notEqualTo("is_archive", isArchive).findAll()
        }
    }


    fun deleteAllChatData() {
        val results = realm.where(ChatInboxRealmResponse::class.java).findAll()
        realm.executeTransaction { realm1 -> results.deleteAllFromRealm() }
    }

    fun getSellerList(userId: Int, itemId: Int): RealmResults<ChatInboxRealmResponse> {
        val value = 0
        val isArchive = 1
        return if (itemId != 0) {
            realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).and().equalTo("user_id", userId).and().notEqualTo("is_archive", isArchive).and().equalTo("item_id", itemId).findAll()
        } else {
            realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).and().equalTo("user_id", userId).and().notEqualTo("is_archive", isArchive).findAll()
        }


    }

    fun getBuyerList(userId: Int, itemId: Int): RealmResults<ChatInboxRealmResponse> {
        val value = 0
        val isArchive = 1
        return if (itemId != 0) {
            realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).notEqualTo("user_id", userId).and().notEqualTo("is_archive", isArchive).and().equalTo("item_id", itemId).findAll()
        } else {
            realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).notEqualTo("user_id", userId).and().notEqualTo("is_archive", isArchive).findAll()
        }

    }

    fun getArchiveList(valueArc: Int): RealmResults<ChatInboxRealmResponse> {
        val value = 0
        return realm.where(ChatInboxRealmResponse::class.java).equalTo("is_delete", value).equalTo("is_archive", valueArc).findAll()
    }

    fun archiveChat(senderId: Int, receiverId: Int, itemId: Int) {
        val value = 0
        val archiveChat = realm.where(ChatInboxRealmResponse::class.java)
                .equalTo("is_delete", value)
                .and()
                .equalTo("item_id", itemId)
                .and()
                .beginGroup()
                .equalTo("receiver_id", receiverId).and()
                .equalTo("sender_id", senderId).or()
                .equalTo("receiver_id", senderId).and()
                .equalTo("sender_id", receiverId)
                .findFirst()
        realm.executeTransaction { realm1 ->
            archiveChat?.is_archive = 1
        }

    }

    fun unArchiveChat(senderId: Int, receiverId: Int, itemId: Int) {
        val value = 0
        val archiveChat = realm.where(ChatInboxRealmResponse::class.java)
                .equalTo("is_delete", value)
                .and()
                .equalTo("item_id", itemId)
                .and()
                .beginGroup()
                .equalTo("receiver_id", receiverId).and()
                .equalTo("sender_id", senderId).or()
                .equalTo("receiver_id", senderId).and()
                .equalTo("sender_id", receiverId)
                .findFirst()
        realm.executeTransaction { realm1 ->
            archiveChat?.is_archive = 0
        }

    }

    fun deleteChat(senderId: Int, receiverId: Int, itemId: Int) {
        val value = 0
        val archiveChat = realm.where(ChatInboxRealmResponse::class.java)
                .equalTo("item_id", itemId)
                .and()
                .equalTo("is_delete", value)
                .and()
                .beginGroup()
                .equalTo("receiver_id", receiverId).and()
                .equalTo("sender_id", senderId).or()
                .equalTo("receiver_id", senderId).and()
                .equalTo("sender_id", receiverId)
                .endGroup()
                .findFirst()
        realm.executeTransaction { realm1 ->
            archiveChat?.deleteFromRealm()
        }
    }

    fun getChatHistoryList(senderId: Int, receiverId: Int, itemId: Int): RealmResults<ChatResponse> {
        return realm.where(ChatResponse::class.java)
                .equalTo("item_id", itemId)
                .and()
                .beginGroup()
                .equalTo("receiver_id", receiverId).and()
                .equalTo("sender_id", senderId).or()
                .equalTo("receiver_id", senderId).and()
                .equalTo("sender_id", receiverId)
                .endGroup()
                .findAll()
    }

    fun getOfferPriceLast(senderId: Int, receiverId: Int, itemId: Int, type: Int): ChatResponse? {
        return realm.where(ChatResponse::class.java)
                .equalTo("item_id", itemId)
                .equalTo("type", type)
                .and()
                .beginGroup()
                .equalTo("receiver_id", receiverId).and()
                .equalTo("sender_id", senderId).or()
                .equalTo("receiver_id", senderId).and()
                .equalTo("sender_id", receiverId)
                .endGroup()
                .findAll()
                .last(null)
    }
}