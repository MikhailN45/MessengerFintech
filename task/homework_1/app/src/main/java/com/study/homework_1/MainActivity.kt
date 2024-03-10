package com.study.homework_1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.hometaskmaincomponents.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val launchButton: Button = findViewById(R.id.launch_button)
        val resultTextView: TextView = findViewById(R.id.result_text_view)

        val serviceActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val mask = getString(R.string.mask)
                    val intent = result.data
                    val ip = intent?.getStringExtra("IP")
                    resultTextView.text = buildString {
                        append(mask)
                        append(ip)
                    }
                }
            }

        launchButton.setOnClickListener {
            serviceActivityResult.launch(Intent(this, ServiceActivity::class.java))
        }
    }
}