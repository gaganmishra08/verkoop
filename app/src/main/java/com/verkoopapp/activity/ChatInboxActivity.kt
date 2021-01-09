package com.verkoopapp.activity


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.View
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
import com.verkoopapp.utils.DeleteCommentDialog
import com.verkoopapp.utils.SelectionListener
import com.verkoopapp.utils.Utils
import kotlinx.android.synthetic.main.chat_inbox_activity.rvChatInbox
import kotlinx.android.synthetic.main.chat_inbox_activity.llAll
import kotlinx.android.synthetic.main.chat_inbox_activity.llBuying
import kotlinx.android.synthetic.main.chat_inbox_activity.llSelling
import kotlinx.android.synthetic.main.chat_inbox_activity.tvAll
import kotlinx.android.synthetic.main.chat_inbox_activity.tvBuying
import kotlinx.android.synthetic.main.chat_inbox_activity.tvSelling
import kotlinx.android.synthetic.main.chat_inbox_activity.vAll
import kotlinx.android.synthetic.main.chat_inbox_activity.vBuying
import kotlinx.android.synthetic.main.chat_inbox_activity.vSelling
import kotlinx.android.synthetic.main.toolbar_chat.ivLeftLocation
import kotlinx.android.synthetic.main.toolbar_chat.tvRight
import org.json.JSONException
import org.json.JSONObject


public class ChatInboxActivity : AppCompatActivity(), ChatInboxAdapter.DeleteChatCallBack {
    private val socket: Socket? = VerkoopApplication.getAppSocket()
    private lateinit var chatInboxAdapter: ChatInboxAdapter
    private var chatInboxType: Int = 0
    private var dbHelper: DbHelper? = null
    private var chatInboxAllList = ArrayList<ChatInboxResponse>()
    private var adapterInboxAllList = ArrayList<ChatInboxResponse>()
    private var itemId = 0


    override fun deleteChat(senderId: Int, receiverId: Int, itemId: Int, type: Int, adapterPosition: Int, swipe: SwipeLayout) {
        if (Utils.isOnline(this)) {
            if (type == 0) {
                removeChatDialog(senderId, receiverId, itemId, adapterPosition, swipe)
            } else if (type == 1) {
                if (socket!!.connected()) {
                    if (senderId == Utils.getPreferencesString(this@ChatInboxActivity, AppConstants.USER_ID).toInt()) {
                        setArchiveChatEvent(senderId, receiverId, itemId, adapterPosition, swipe)
                    } else {
                        setArchiveChatEvent(receiverId, senderId, itemId, adapterPosition, swipe)
                    }
                }

            }
        } else {
            Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_inbox_activity)
        itemId = intent.getIntExtra(AppConstants.ITEM_ID, 0)
        dbHelper = DbHelper()
        setData()
        setAdapter()

    }

    override fun onResume() {
        super.onResume()
        if (Utils.isOnline(this)) {
            if (socket!!.connected()) {
                getInboxChatList()
            }
        } else {
            Handler().postDelayed({
                Utils.showSimpleMessage(this, getString(R.string.check_internet)).show()
            }, 300)
            setOfflineData()
        }
    }

    private fun setOfflineData() {
        val chatInboxList = ArrayList<ChatInboxResponse>()
        val result = dbHelper!!.getAddChatList(itemId)
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
                    result[i]!!.item_price!!,
                    result[i]!!.is_rate!!,
                    result[i]!!.min_price!!,
                    result[i]!!.max_price!!,
                    result[i]!!.category_id!!
            )
            chatInboxList.add(dataBean)
        }
        getArchiveLIst(chatInboxList)
    }

    private fun getInboxChatList() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put(AppConstants.SENDER_ID, Utils.getPreferencesString(this, AppConstants.USER_ID))
            jsonObject.put(AppConstants.ITEM_ID, itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.INBOX_LIST_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                var data: JSONObject? = null
                try {
                    data = it[0] as JSONObject?
                } catch (e: Exception) {
                }
                runOnUiThread {
                    val chatInboxList = ArrayList<ChatInboxResponse>()
                    if (data?.getString("status") == "1") {
                        runOnUiThread {
                            try {
                                val listdata = java.util.ArrayList<JSONObject>()
                                try {
                                    val chatHistory = data.getJSONArray("data")

                                    if (chatHistory != null) {
                                        for (i in 0 until chatHistory.length()) {
                                            listdata.add(chatHistory.getJSONObject(i))
                                        }
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }

                                for (data2 in listdata) {
                                    try {
                                        val inboxData = ChatInboxResponse(
                                                data2.getInt("is_archive"),
                                                data2.getInt("is_delete"),
                                                data2.getString("chat_user_id"),
                                                data2.getString("timestamp"),
                                                data2.getInt("types"),
                                                data2.getInt("unread_count"),
                                                data2.getInt("sender_id"),
                                                data2.getInt("receiver_id"),
                                                data2.getInt("item_id"),
                                                data2.getInt("message_id"),
                                                data2.getString("username"),
                                                data2.getString("profile_pic"),
                                                data2.getString("message"),
                                                data2.getInt("user_id"),
                                                data2.getInt("is_sold"),
                                                data2.getString("item_name"),
                                                data2.getInt("offer_status"),
                                                data2.getString("url"),
                                                data2.getString("offer_price"),
                                                data2.getString("item_price"),
                                                data2.getInt("is_rate"),
                                                data2.getString("min_price"),
                                                data2.getString("max_price"),
                                                data2.getInt("category_id"))
                                        chatInboxList.add(inboxData)
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                                dbHelper!!.deleteAllChatData()
                                dbHelper!!.insertAllChatData(chatInboxList)
                                getArchiveLIst(chatInboxList)
                            } catch (e: JSONException) {
                                e.printStackTrace()
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

    private fun getArchiveLIst(chatInboxList: ArrayList<ChatInboxResponse>) {
        chatInboxAllList.clear()
        for (i in chatInboxList.indices) {
            if (chatInboxList[i].is_archive != 1) {
                chatInboxAllList.add(chatInboxList[i])
            } else {

                /*  if (chatInboxList[i].user_id == Utils.getPreferencesString(this, AppConstants.USER_ID).toInt()) {
                      sellerList.add(chatInboxList[i])
                  } else {
                      buyerList.add(chatInboxList[i])
                  }*/
            }
        }
        adapterInboxAllList.clear()
        adapterInboxAllList.addAll(chatInboxAllList)
        chatInboxAdapter.setData(adapterInboxAllList)
        chatInboxAdapter.notifyDataSetChanged()
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(this)
        rvChatInbox.layoutManager = layoutManager
        chatInboxAdapter = ChatInboxAdapter(this, chatInboxType)
        rvChatInbox.adapter = chatInboxAdapter
    }

    private fun setData() {
        ivLeftLocation.setOnClickListener { onBackPressed() }
        tvRight.setOnClickListener {
            tvRight.isEnabled = false
            Handler().postDelayed(Runnable {
                tvRight.isEnabled = true
            }, 700)
            val intent = Intent(this, ArchivedChatActivity::class.java)
            startActivity(intent)
        }
        llAll.setOnClickListener {
            setNothing()
            tvAll.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            vAll.visibility = View.VISIBLE
            chatInboxType = 0
            chatInboxAdapter.mItemManger.closeAllItems()
            setOfflineData()
        }
        llBuying.setOnClickListener {
            setNothing()
            tvBuying.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            vBuying.visibility = View.VISIBLE
            chatInboxType = 1
            chatInboxAdapter.mItemManger.closeAllItems()
            getChatBuyerList()
        }
        llSelling.setOnClickListener {
            setNothing()
            tvSelling.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            vSelling.visibility = View.VISIBLE
            chatInboxType = 2
            chatInboxAdapter.mItemManger.closeAllItems()
            getChatSellerList()
        }
    }

    private fun getChatSellerList() {
        val sellerList = ArrayList<ChatInboxResponse>()
        val result = dbHelper!!.getSellerList(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), itemId)
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
                    result[i]!!.item_price!!,
                    result[i]!!.is_rate!!,
                    result[i]!!.min_price!!,
                    result[i]!!.max_price!!,
                    result[i]!!.category_id!!

            )
            sellerList.add(dataBean)
        }
        adapterInboxAllList.clear()
        adapterInboxAllList.addAll(sellerList)
        chatInboxAdapter.setData(adapterInboxAllList)
        chatInboxAdapter.notifyDataSetChanged()
    }

    private fun getChatBuyerList() {
        val buyerList = ArrayList<ChatInboxResponse>()
        val result = dbHelper!!.getBuyerList(Utils.getPreferencesString(this, AppConstants.USER_ID).toInt(), itemId)
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
                    result[i]!!.item_price!!,
                    result[i]!!.is_rate!!,
                    result[i]!!.min_price!!,
                    result[i]!!.max_price!!,
                    result[i]!!.category_id!!
            )
            buyerList.add(dataBean)
        }
        adapterInboxAllList.clear()
        adapterInboxAllList.addAll(buyerList)
        chatInboxAdapter.setData(adapterInboxAllList)
        chatInboxAdapter.notifyDataSetChanged()
    }

    private fun setArchiveChatEvent(senderId: Int, receiverId: Int, itemId: Int, adapterPosition: Int, swipe: SwipeLayout) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", senderId)
            jsonObject.put("receiver_id", receiverId)
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.ARCHIVE_CHAT_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            dbHelper!!.archiveChat(senderId, receiverId, itemId)
                            chatInboxAdapter.mItemManger.removeShownLayouts(swipe)
                            adapterInboxAllList.removeAt(adapterPosition)
                            chatInboxAdapter.notifyItemRemoved(adapterPosition)
                            chatInboxAdapter.notifyItemRangeChanged(adapterPosition, adapterInboxAllList.size)
                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun removeChatDialog(senderId: Int, receiverId: Int, itemId: Int, adapterPosition: Int, swipe: SwipeLayout) {
        val shareDialog = DeleteCommentDialog(this, "Delete Chat", "Are you sure you want to delete this Chat?", object : SelectionListener {
            override fun leaveClick() {
                if (socket!!.connected()) {
                    if (senderId == Utils.getPreferencesString(this@ChatInboxActivity, AppConstants.USER_ID).toInt()) {
                        deleteChatEvent(senderId, receiverId, itemId, adapterPosition, swipe)
                    } else {
                        deleteChatEvent(receiverId, senderId, itemId, adapterPosition, swipe)
                    }

                }
            }
        })
        shareDialog.show()
    }

    private fun deleteChatEvent(senderId: Int, receiverId: Int, itemId: Int, adapterPosition: Int, swipe: SwipeLayout) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("sender_id", senderId)
            jsonObject.put("receiver_id", receiverId)
            jsonObject.put("item_id", itemId)
            Log.e("<<<ACKRESPONSE>>>", Gson().toJson(jsonObject))
            socket?.emit(AppConstants.DELETE_CHAT_EVENT, jsonObject, Ack {
                Log.e("<<<Response>>>", Gson().toJson(it[0]))
                val data = it[0] as JSONObject
                runOnUiThread {
                    if (data.getString("status") == "1") {
                        runOnUiThread {
                            dbHelper!!.deleteChat(senderId, receiverId, itemId)
                            chatInboxAdapter.mItemManger.removeShownLayouts(swipe)
                            adapterInboxAllList.removeAt(adapterPosition)
                            chatInboxAdapter.notifyItemRemoved(adapterPosition)
                            chatInboxAdapter.notifyItemRangeChanged(adapterPosition, adapterInboxAllList.size)
                        }
                    } else {

                    }
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun setNothing() {
        tvAll.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvBuying.setTextColor(ContextCompat.getColor(this, R.color.black))
        tvSelling.setTextColor(ContextCompat.getColor(this, R.color.black))
        vAll.visibility = View.INVISIBLE
        vBuying.visibility = View.INVISIBLE
        vSelling.visibility = View.INVISIBLE
    }
}