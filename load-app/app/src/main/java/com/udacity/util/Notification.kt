package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.udacity.R
import com.udacity.ui.DetailActivity
import com.udacity.ui.MainActivity


// Notification ID.
private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

/**
 * Builds and delivers the notification.
 *
 * @param downloaded file download status
 * @param downloadedFile downloaded file name
 * @param context activity context.
 */
fun NotificationManager.sendNotification(
    downloaded: Boolean,
    downloadedFile: String,
    context: Context
) {


    // Create the content intent for the notification, which returns to the main activity
    val contentIntent = Intent(context, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        FLAGS
    )

    val detailIntent = Intent(context, DetailActivity::class.java).apply {
        putExtra("status", downloaded)
        putExtra("file", downloadedFile)
    }

    val detailPendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        detailIntent,
        FLAGS
    )

    val statusImage: Bitmap
    val messageBody: String

    //determines if download is success or fail and sets variable attributes for the notification
    if (downloaded) {
        messageBody = context.getString(R.string.download_success)
        statusImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_done_24)?.toBitmap()!!
    } else {
        messageBody = context.getString(R.string.download_failure)
        statusImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_error_24)?.toBitmap()!!
    }

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(statusImage)
        .bigLargeIcon(null)

    // Build the notification
    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.download_notification_channel_id)
    )

        .setContentTitle(context.getString(R.string.notification_title))
        .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setStyle(bigPicStyle)
        .setLargeIcon(statusImage)
        .addAction(
            R.drawable.ic_baseline_insert_drive_file_24,
            context.getString(R.string.notification_button),
            detailPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}

/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
