package com.study.messengerfintech.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ActivityMainBinding
import com.study.messengerfintech.view.fragments.ChatFragment
import com.study.messengerfintech.view.fragments.MainFragment
import com.study.messengerfintech.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_fragment_container, MainFragment.newInstance())
                .commit()
        }

        viewModel.chat.observe(this) { bundle ->
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.activity_fragment_container,
                    ChatFragment.newInstance(bundle)
                )
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}