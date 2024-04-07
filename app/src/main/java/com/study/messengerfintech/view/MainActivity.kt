package com.study.messengerfintech.view

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
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

        viewModel.state.observe(this) {
            when (it) {
                is State.Result -> binding.progressBar.visibility = GONE
                is State.Loading -> binding.progressBar.visibility = VISIBLE
                is State.Error -> {
                    binding.progressBar.visibility = GONE

                    val snackBar = Snackbar.make(
                        binding.root, it.error.message.toString(), Snackbar.LENGTH_SHORT
                    )
                    val params =
                        snackBar.view.layoutParams as FrameLayout.LayoutParams
                    params.setMargins(0, 0, 0, 190)
                    snackBar.view.layoutParams = params
                    snackBar.show()
                }
            }
        }

        viewModel.chat.observe(this) { (streamCount, chatCount) ->
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.activity_fragment_container,
                    ChatFragment.newInstance(streamCount, chatCount)
                )
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}