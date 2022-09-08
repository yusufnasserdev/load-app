package com.udacity.ui

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.util.cancelNotifications
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotifications()

        val downloadedFile = intent.getStringExtra("file")
        val downloaded = intent.getBooleanExtra("status", false)

        file_name.text = downloadedFile

        if (downloaded) {
            status_view.text = getString(R.string.success)
            status_view.setTextColor(getColor(R.color.green))
        } else {
            status_view.text = getString(R.string.fail)
            status_view.setTextColor(getColor(R.color.red))
        }

        fab.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

}
