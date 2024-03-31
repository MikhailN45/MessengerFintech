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
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_fragment_container, MainFragment.newInstance())
                .commitAllowingStateLoss()

        viewModel.chat.observe(this) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.activity_fragment_container,
                    ChatFragment.newInstance(it.first, it.second)
                )
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}