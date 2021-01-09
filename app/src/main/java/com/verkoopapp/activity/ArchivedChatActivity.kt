package com.verkoopapp.activity

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.daimajia.swipe.SwipeLayout
import io.socket.client.Ack
import io.socket.client.Socket
import com.google.gson.Gson
import com.verkoopapp.R
import com.verkoopapp.VerkoopApplication
import com.verkoopapp.adapter.ChatInboxAdapter
import com.verkoopapp.models.ChatInboxResponse
import com.verkoopapp.offlinechatdata.DbHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.favourites_activity.*
import kotlinx.android.synthetic.main.toolbar_location.*
import org.json.JSONException
import org.json.JSONObject

class ArchivedChatActivity : AppCompatActivity(), ChatInboxAdapter.DeleteChatCallBack {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private var chatInboxList = ArrayList<ChatInboxResponse>()
    override fun deleteChat(senderId: Int, receiverId: Int, itemId: Int, type: Int, adapterPosition: Int, swipe: SwipeLayout) {
        if (socket!!.connected()) {
            if (senderId == Utils.getPreferencesString(this@ArchivedChatActivity, AppConstants.USER_ID).toInt()) {
                setArchiveChatEvent(senderId, receiverId, itemId, adapterPosition, swipe)
            } else {
                setArchiveChatEvent(receiverId, senderId, itemId, adapterPosition, swipe)
            }

        }
    }

    private var dbHelper: DbHelper? = null
    private lateinit var chatInboxAdapter: ChatInboxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favourites_activity)
        dbHelper = DbHelper()
        steAdapter()
        if (Utils.isOnline(this)) {
            if (socket!!.connected()) {
                setOfflineData()
            }
        } else {
            setOfflineData()
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }

    private fun steAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvFavouriteList.layoutManager = layoutManager
        chatInboxAdapter = ChatInboxAdapter(this, 1)
        rvFavouriteList.adapter = chatInboxAdapter
        tvHeaderLoc.text = getString(R.string.arch_chat)
        ivLeftLocation.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setOfflineData() {
        chatInboxList = ArrayList<ChatInboxResponse>()
        val result = dbHelper!!.getArchiveList(1)
        for (i in result.indices) {
            val dataBean = ChatInboxResponse(
                    result[i]!!.is_archive,
                    result[i]!!.is_delete,
                    result[i]!!.chat_user_id!!,
                    result[i]!!.timestamp!!,
                    result[i]!!.types,
                    result[i]!!.unread_count,
                    result[i]!!.sender_id,
                    result[i]!!.receiver_id,
                    result[i]!!.item_id,
                    result[i]!!.message_id,
                    result[i]!!.username!!,
                    result[i]!!.profile_pic!!,
                    result[i]!!.message!!,
                    result[i]!!.user_id,
                    result[i]!!.is_sold,
                    result[i]!!.item_name!!,
                    result[i]!!.offer_status,
                    result[i]!!.url!!,
                    result[i]!!.offer_price!!,
                    result[i]!!.item_price!!
            )
            chatInboxList.add(dataBean)
        }
        chatInboxAdapter.setData(chatInboxList)
        chatInboxAdapter.notifyDataSetChanged()
    }

    private fun setArchiveChatEvent(senderId: Int, receiverId: Int, itemId: Int, adapterPosition: Int, swipe: SwipeLayout) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", senderId)
            jsonObject.put("receiver_id", receiverId)
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.UN_ARCHIVE_CHAT_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            dbHelper!!.unArchiveChat(senderId, receiverId, itemId)
                            chatInboxAdapter.mItemManger.removeShownLayouts(swipe)
                            if (chatInboxList.size >= adapterPosition) {
                                chatInboxList.removeAt(adapterPosition)
                                chatInboxAdapter.notifyItemRemoved(adapterPosition)
                                chatInboxAdapter.notifyItemRangeChanged(adapterPosition, chatInboxList.size)
                            }
                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}