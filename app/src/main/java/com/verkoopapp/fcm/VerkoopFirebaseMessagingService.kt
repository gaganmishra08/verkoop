package com.verkoopapp.fcm

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.verkoopapp.activity.HomeActivity

import com.verkoopapp.activity.SplashActivity
import com.verkoopapp.models.DisLikeResponse
import com.verkoopapp.models.UpdateDeviceInfoRequest
import com.verkoopapp.network.ServiceHelper
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Utils
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class VerkoopFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        // Get updated InstanceID token.
        Log.d(VerkoopFirebaseMessagingService.TAG, "Refreshed token: " + p0!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //   AppPreferences.deviceId = refreshedToken
        /*Utils.sendDeviceId(this)*/


        callUpdateDeviceInfoApi(p0)
    }

    private fun callUpdateDeviceInfoApi(refreshedToken: String) {
        ServiceHelper().updateDeviceInfo(UpdateDeviceInfoRequest(Utils.getPreferences(this@VerkoopFirebaseMessagingService, AppConstants.USER_ID), refreshedToken, "1"),
                object : ServiceHelper.OnResponse {
                    override fun onSuccess(response: Response<*>) {
                        val loginResponse = response.body() as DisLikeResponse
                    }

                    override fun onFailure(msg: String?) {
                    }
                })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(p0: RemoteMessage) {
        if (p0!!.data.isNotEmpty()) {

            try {
                val json = JSONObject(p0.data as Map<*, *>)
                sendPushNotification(json)
//                sendPushNotification(remoteMessage.getData().get("title")!!, remoteMessage.getData().get("body")!!)
            } catch (e: Exception) {

            }

        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendPushNotification(json: JSONObject) {
        var title: String = ""
        var message: String = ""
        var imageUrl: String = ""
        var type: Int = 0
        var item_id: Int = 0
//    private fun sendPushNotification(name: String,message:String) {
        //optionally we can display the json into log
        //  Log.e(TAG, "Notification JSON " + json.toString())
        try {
            //getting the json data
//            val data = json.getJSONObject("data")

            //parsing json data
            if (json.has("title")) {
                title = json.getString("title")
            }
            //            String type = data.getString("type");
            if (json.has("message")) {
                message = json.getString("message")
            }
            if (json.has("image")) {
                imageUrl = json.getString("image")
            }
            if (json.has("type")) {
                type = json.getString("type").toString().toInt()
            }
            if (json.has("item_id")) {
                item_id = json.getString("item_id").toString().toInt()
            }
            if (json.has("user_id")) {
                item_id = json.getString("user_id").toString().toInt()
            }

            //creating MyNotificationManager object
            val mNotificationManager = KSMNotificationManager(applicationContext)

            //creating an intent for the notification
            val intent = Intent(this, HomeActivity::class.java)
            if (type != null) {
                if (type == 1 || type == 3 || type == 6) {
                    intent.putExtra(AppConstants.TYPE, 1)
                } else if (type == 2 || type == 4){
                    intent.putExtra(AppConstants.TYPE, 2)
                } else if(type == 5){
                    intent.putExtra(AppConstants.TYPE, 3)
                } else if(type == 7){
                    intent.putExtra(AppConstants.TYPE, 4)
                }
            }
            intent.putExtra("titleNoti", title)
            intent.putExtra("messageNoti", message)
            intent.putExtra("imageNoti", imageUrl)
            intent.putExtra(AppConstants.ID, item_id)

            //if there is no image
            if (imageUrl == "") {
                //displaying small notification
                mNotificationManager.showSmallNotification(title, message, intent)
            } else {
                //if there is an image
                //displaying a big notification
                mNotificationManager.showBigNotification(title, message, imageUrl, intent)
            }
        } catch (e: JSONException) {

        } catch (e: Exception) {

        }

    }

    companion object {
        private const val TAG = "FirebaseMsgService"
    }
}