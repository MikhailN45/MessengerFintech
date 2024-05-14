package com.study.messengerfintech.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ActivityMainBinding
import com.study.messengerfintech.getComponent
import com.study.messengerfintech.presentation.fragments.ChatFragment
import com.study.messengerfintech.presentation.fragments.MainFragment
import com.study.messengerfintech.presentation.viewmodel.StreamsViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: StreamsViewModel by viewModels { viewModelFactory }
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent().mainComponent().create().inject(this)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activity_fragment_container, MainFragment.newInstance())
                .commit()
        }

        viewModel.chatInstance.observe(this) { bundle ->
            supportFragmentManager.beginTransaction()
                .add(
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