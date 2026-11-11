package com.app.voicechanger.admin

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.voicechanger.R
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        val switchKillSwitch = findViewById<Switch>(R.id.switchKillSwitch)
        val btnPushNotice = findViewById<Button>(R.id.btnSendNotice)

        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val isDisabled = remoteConfig.getBoolean("is_app_disabled")
                switchKillSwitch.isChecked = isDisabled
            }
        }

        switchKillSwitch.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this, "Remote Maintenance State: $isChecked", Toast.LENGTH_SHORT).show()
        }

        btnPushNotice.setOnClickListener {
            Toast.makeText(this, "Global Broadcast Notice Dispatched to Clients", Toast.LENGTH_SHORT).show()
        }
    }
}
