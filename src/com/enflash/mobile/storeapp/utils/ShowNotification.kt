package com.enflash.mobile.storeapp.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.enflash.mobile.storeapp.R
import com.enflash.mobile.storeapp.application.App
import com.enflash.mobile.storeapp.login.SplashScreenActivity
import com.enflash.mobile.storeapp.main.MainActivity
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random


class ShowNotification {

    companion object {
        private val random = Random(9999L)

        private val c: AtomicInteger = AtomicInteger(0)
        fun getID(): Int {
            return c.incrementAndGet()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(channelId: String, channelName: String): String {
            val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = App.getAppInstance().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            return channelId
        }

        fun sendNotification(message: String) {
            val m: Int = random.nextInt()
            sendNotification(m, message)
        }

        fun sendNotification(notificationId: Int, message: String) {
            if (message.isNotEmpty()) {

                val singleton = App.getAppInstance()
                val defaultSoundUri = Uri.parse("android.resource://" + singleton.packageName + "/" + R.raw.notification_high_intensity)
                val notificationManager = singleton!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notificationIntent = Intent(singleton.applicationContext, SplashScreenActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(singleton.applicationContext, 0,
                        notificationIntent, 0)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val notificationBuilder = Notification.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(Constants.APPLICATION_NAME)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setStyle(Notification.BigTextStyle()
                                    .bigText(message))

                    val notificationChannel = NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

                    val att = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()

                    notificationChannel.setSound(defaultSoundUri, att)
                    notificationChannel.description = message
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                    notificationManager.notify(notificationId, notificationBuilder.build())

                } else {
                    val notificationBuilderItem =
                            NotificationCompat.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle(Constants.APPLICATION_NAME)
                                    .setContentText(message)
                                    .setContentIntent(pendingIntent)
                                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                    notificationManager.notify(notificationId, notificationBuilderItem.build())
                }
            }
        }

        fun sendAlertByAccept(message: String) {
            if (message.isNotEmpty()) {

                val singleton = App.getAppInstance()
                val defaultSoundUri = Uri.parse("android.resource://" + singleton.packageName + "/" + R.raw.hero_decorative_celebration)
                val notificationManager = singleton!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notificationIntent = Intent(singleton.applicationContext, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(singleton.applicationContext, 0,
                        notificationIntent, 0)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val notificationBuilder = Notification.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_ok)
                            .setContentTitle(Constants.APPLICATION_NAME)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setStyle(Notification.BigTextStyle()
                                    .bigText(message))

                    val notificationChannel = NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

                    val att = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()

                    notificationChannel.setSound(defaultSoundUri, att)
                    notificationChannel.description = message
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                    notificationManager.notify(Constants.NOTIFICATION_MESSAGE_ID + 1, notificationBuilder.build())

                } else {

                    val notificationBuilderItem =
                            NotificationCompat.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_ok)
                                    .setContentTitle(Constants.APPLICATION_NAME)
                                    .setContentText(message)
                                    .setContentIntent(pendingIntent)
                                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)

                    notificationManager.notify(Constants.NOTIFICATION_MESSAGE_ID + 1, notificationBuilderItem.build())

                }
            }
        }


        fun sendAlertByMarkAsReady(message: String) {
            if (message.isNotEmpty()) {

                val singleton = App.getAppInstance()
                val defaultSoundUri = Uri.parse("android.resource://" + singleton.packageName + "/" + R.raw.hero_simple_celebration)
                val notificationManager = singleton!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notificationIntent = Intent(singleton.applicationContext, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(singleton.applicationContext, 0,
                        notificationIntent, 0)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val notificationBuilder = Notification.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_moto)
                            .setContentTitle(Constants.APPLICATION_NAME)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setStyle(Notification.BigTextStyle()
                                    .bigText(message))

                    val notificationChannel = NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

                    val att = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()

                    notificationChannel.setSound(defaultSoundUri, att)
                    notificationChannel.description = message
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                    notificationManager.notify(Constants.NOTIFICATION_MESSAGE_ID + 2, notificationBuilder.build())

                } else {

                    val notificationBuilderItem =
                            NotificationCompat.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_moto)
                                    .setContentTitle(Constants.APPLICATION_NAME)
                                    .setContentText(message)
                                    .setContentIntent(pendingIntent)
                                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)

                    notificationManager.notify(Constants.NOTIFICATION_MESSAGE_ID + 2, notificationBuilderItem.build())

                }
            }
        }

        fun sendAlertByOrderNotAssigned(message: String) {
            if (message.isNotEmpty()) {

                val singleton = App.getAppInstance()
                val defaultSoundUri = Uri.parse("android.resource://" + singleton.packageName + "/" + R.raw.notification_decorative)
                val notificationManager = singleton!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                val notificationIntent = Intent(singleton.applicationContext, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(singleton.applicationContext, 0,
                        notificationIntent, 0)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    val notificationBuilder = Notification.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_delete)
                            .setContentTitle(Constants.APPLICATION_NAME)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setStyle(Notification.BigTextStyle()
                                    .bigText(message))

                    val notificationChannel = NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

                    val att = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()

                    notificationChannel.setSound(defaultSoundUri, att)
                    notificationChannel.description = message
                    notificationChannel.enableLights(true)
                    notificationChannel.lightColor = Color.RED
                    notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    notificationChannel.enableVibration(true)
                    notificationManager.createNotificationChannel(notificationChannel)
                    val m: Int = random.nextInt()
                    notificationManager.notify(m, notificationBuilder.build())

                } else {

                    val notificationBuilderItem =
                            NotificationCompat.Builder(singleton.applicationContext, Constants.CHANNEL_ID)
                                    .setSmallIcon(R.drawable.ic_moto)
                                    .setContentTitle(Constants.APPLICATION_NAME)
                                    .setContentText(message)
                                    .setContentIntent(pendingIntent)
                                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                    val m: Int = random.nextInt()
                    notificationManager.notify(m, notificationBuilderItem.build())

                }
            }
        }

        fun hideKeyboard(activity: Context, view: EditText) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

    }

}