package com.study.messengerfintech.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ActivityMainBinding
import com.study.messengerfintech.presentation.fragments.ChatFragment
import com.study.messengerfintech.presentation.fragments.MainFragment
import com.study.messengerfintech.presentation.viewmodel.StreamsViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StreamsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_fragment_container, MainFragment.newInstance())
                .commit()
        }

        viewModel.chatInstance.observe(this) { bundle ->
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