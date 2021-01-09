package com.verkoopapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import io.socket.client.Ack
import io.socket.client.Socket
import com.google.gson.Gson
import com.verkoopapp.models.Currency
import com.verkoopapp.models.SocketCheckConnectionEvent
import com.verkoopapp.models.SocketOnReceiveEvent
import com.verkoopapp.utils.AppConstants
import com.verkoopapp.utils.Loading
import com.verkoopapp.utils.Utils
import io.branch.referral.Branch
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

import org.greenrobot.eventbus.EventBus


//
// @ReportsCrashes(mailTo = "taranjeet.singh@mobilecoderz.com", mode = ReportingInteractionMode.TOAST, resToastText = R.string.application_crash)// my email here

class VerkoopApplication : Application() {
     private var loadDialog: Loading? = null
    private var currenyList: ArrayList<Currency>? = null
    private val socket: Socket? = io.socket.client.IO.socket(AppConstants.SOCKET_URL)
    val loader: Loading
        get() {
            if (loadDialog == null)
                loadDialog = Loading()
            return loadDialog!!
        }

    val currencies : ArrayList<Currency>
        get() {
            if(currenyList == null)
                currenyList = ArrayList()
            return currenyList!!
        }

     override fun onCreate() {
         super.onCreate()
         instance = this
         Fabric.with(this, Crashlytics())
         Realm.init(this)
         // Branch logging for debugging
         Branch.enableDebugMode()

         // Branch object initialization
         Branch.getAutoInstance(this)
         initSocket()
         val config = RealmConfiguration.Builder()
                 .deleteRealmIfMigrationNeeded()
                 .name("verkoop.db")
                 .schemaVersion(0)
                 .build()
         Realm.setDefaultConfiguration(config)
     }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
//        ACRA.init(this)

    }
    companion object {
        lateinit var instance: VerkoopApplication
        operator fun get(context: Context): VerkoopApplication {
            return context.applicationContext as VerkoopApplication
        }

        @JvmStatic fun getToken(): String {
            return Utils.getPreferencesString(instance, AppConstants.API_TOKEN)
        }

        fun getAppSocket(): Socket? {
            return if (instance.socket != null) {
                instance.socket
            } else {
                instance.initSocket()
                instance.socket
            }
        }
    }

    private fun initSocket() {
        if (socket != null ) {
            socket.on(Socket.EVENT_CONNECT) { _ ->
                        /* Socket Init */
               /* socket.emit(AppConstants.INIT_USER_ID, getObj(), Ack {
                    Log.e("<<<ACKRESPONSE--5>>>", Gson().toJson(it[0]))
                })*/
                EventBus.getDefault().post(SocketCheckConnectionEvent("connect",AppConstants.SOCKET_CONNECT))

                    }
                    .on(AppConstants.RECEIVE_MESSAGE){
                        //* Room event listener *//*
                        Log.e("<<<receiveChat>>>",  Gson().toJson(it[0]))
                        EventBus.getDefault().post(SocketOnReceiveEvent(it,AppConstants.RECEIVE_MESSAGE))
                    }
                    .on(Socket.EVENT_DISCONNECT) { _ ->
                        /* Socket disconnect listener */
                        Log.e("<<<<<SOCKET>>>>>", "DISCONNECTED")
                    }
            socket.connect()
        }

    }


}