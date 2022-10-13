package com.udacity.ui

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.R
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.util.cancelNotifications

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelNotifications()

        val downloadedFile = intent.getStringExtra("file")
        val downloaded = intent.getBooleanExtra("status", false)

        binding.fileName.text = downloadedFile

        if (downloaded) {
            binding.statusView.text = getString(R.string.success)
            binding.statusView.setTextColor(getColor(R.color.green))
        } else {
            binding.statusView.text = getString(R.string.fail)
            binding.statusView.setTextColor(getColor(R.color.red))
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

}
