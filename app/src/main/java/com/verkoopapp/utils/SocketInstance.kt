package com.verkoopapp.utils

import android.app.Application
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException


class SocketInstance : Application() {
    private var iSocket: Socket? = null
    override fun onCreate() {
        super.onCreate()
        try {


            iSocket = IO.socket(AppConstants.SOCKET_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }

    val socketInstance: Socket?
        get() = iSocket

    companion object {
        private const val URL = "http://your_socket_connection_url.com"
    }
}
