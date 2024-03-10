package com.study.homework_1

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.IBinder
import android.text.format.Formatter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.hometaskmaincomponents.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_activity)

        val startJobButton: Button = findViewById(R.id.start_service_button)
        startJobButton.setOnClickListener { launchService() }
    }

    override fun onResume() {
        super.onResume()
        val filterIntent = IntentFilter("PROVIDE_LOCAL_IP")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filterIntent)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    private fun launchService() {
        startService(Intent(this, IpCheckService::class.java))
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            @SuppressLint("UnsafeIntentLaunch") intent: Intent
        ) {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}


class IpCheckService : Service() {
    private val intentToSend = Intent("PROVIDE_LOCAL_IP")

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            intentToSend.putExtra("IP", getLocalIp(this@IpCheckService))
            LocalBroadcastManager.getInstance(this@IpCheckService).sendBroadcast(intentToSend)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun getLocalIp(context: Context): String {
        val wifiManager: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
    }
}
