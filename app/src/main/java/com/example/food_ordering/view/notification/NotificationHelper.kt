package com.example.food_ordering.view.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.food_ordering.R

class NotificationHelper(private val context: Context) {

    private val channelId = "new_items_channel"
    private val channelName = "New Items"
    private val channelDescription = "Notifications for new dishes and vouchers"
    private val importance = NotificationCompat.PRIORITY_HIGH

    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @SuppressLint("WrongConstant")
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendNewDishNotification(dishName: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setContentTitle("New Dish Available!")
            .setContentText("Try our new dish: $dishName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notification = builder.build()
        notificationManager.notify(1, notification) // Unique notification ID
    }

    fun sendNewVoucherNotification(voucherCode: String, discount: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your icon
            .setContentTitle("New Voucher!")
            .setContentText("Use code $voucherCode for $discount discount!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notification = builder.build()
        notificationManager.notify(2, notification) // Unique notification ID
    }
}