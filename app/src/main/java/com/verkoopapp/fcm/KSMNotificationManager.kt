
package com.verkoopapp.fcm
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import android.text.Html
import androidx.core.app.NotificationCompat
import com.verkoopapp.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class KSMNotificationManager (private val mCtx: Context) {

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    fun showBigNotification(title: String, message: String, url: String, intent: Intent) {
        val resultPendingIntent = PendingIntent.getActivity(
                mCtx,
                ID_BIG_NOTIFICATION,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        val bigPictureStyle = NotificationCompat.BigPictureStyle()
        bigPictureStyle.setBigContentTitle(title)
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
        bigPictureStyle.bigPicture(getBitmapFromURL(url))
        val mBuilder = NotificationCompat.Builder(mCtx)
        val notification: Notification
        notification = mBuilder.setSmallIcon(R.mipmap.logo).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.logo)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.resources, R.mipmap.logo))
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build()

        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        val notificationManager = mCtx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
//the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    fun showSmallNotification(title: String, message: String, intent: Intent) {
        val resultPendingIntent = PendingIntent.getActivity(
                mCtx,
                ID_SMALL_NOTIFICATION,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(mCtx,NOTIFICATION_CHANNEL_ID)
        val notification: Notification
        notification = mBuilder.setSmallIcon(R.mipmap.logo).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(getNotificationIcon(mBuilder))
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.resources, R.mipmap.logo))
                .setContentText(message)
                .setTicker(message)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()

        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL

        val notificationManager = mCtx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        /*Oreo Notification channel*/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "VerkoopChannel", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = Notification.DEFAULT_LIGHTS
            notificationChannel.lockscreenVisibility
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification)
    }

    private fun getNotificationIcon(notificationBuilder: NotificationCompat.Builder): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val color = 0x008000
            notificationBuilder.color = color
            return R.mipmap.logo

        }
        return R.mipmap.logo
    }

    //The method will return Bitmap from an image URL
    private fun getBitmapFromURL(strURL: String): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

    }

    companion object {
        private const val ID_BIG_NOTIFICATION = 234
        private const val ID_SMALL_NOTIFICATION = 235
        const val NOTIFICATION_CHANNEL_ID = "10001"
    }
}
