package com.udacity.ui

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.R
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadedFile: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )

        custom_button.setOnClickListener {

            var selectedURL = ""

            when (radio_group.checkedRadioButtonId) {
                R.id.glide_button -> {
                    downloadedFile = getString(R.string.glide)
                    selectedURL = GLIDE_URL
                }
                R.id.retrofit_button -> {
                    downloadedFile = getString(R.string.retrofit)
                    selectedURL = RETROFIT_URL
                }
                R.id.repo_button -> {
                    downloadedFile = getString(R.string.repo)
                    selectedURL = REPO_URL
                }
                else -> {
                    Toast.makeText(this, "Please select a file to download", Toast.LENGTH_SHORT)
                        .show()
                    custom_button.doneLoading()
                    return@setOnClickListener
                }
            }

            if (URLUtil.isValidUrl(selectedURL))
                download(selectedURL)
        }
    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                // Requesting an instance of Download Manager
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                // Querying the downloads by our downloadID and storing the results
                val downloads = downloadManager.query(DownloadManager.Query().setFilterById(id))

                // Checking if the downloadID exists
                if (downloads.moveToFirst()) {
                    val colIndex = downloads.getColumnIndex(DownloadManager.COLUMN_STATUS)

                    when (downloads.getInt(colIndex)) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            notificationManager.sendNotification(
                                downloaded = true,
                                downloadedFile = downloadedFile,
                                context = context!!
                            )
                        }
                        DownloadManager.STATUS_FAILED -> {
                            notificationManager.sendNotification(
                                downloaded = false,
                                downloadedFile = downloadedFile,
                                context = context!!
                            )
                        }
                        DownloadManager.STATUS_PENDING -> {
                            Toast.makeText(context, "Download Pending", Toast.LENGTH_SHORT).show()
                        }
                        DownloadManager.STATUS_PAUSED -> {
                            Toast.makeText(context, "Download Paused", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            custom_button.doneLoading()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                getString(R.string.download_channel_description)

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    companion object {
        private const val REPO_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }
}
