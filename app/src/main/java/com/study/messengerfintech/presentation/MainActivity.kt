package com.study.messengerfintech.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.Toast
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
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var isConnectionLost = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getComponent().mainComponent().create().inject(this)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        watchInternetConnection()

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
        connectivityManager.unregisterNetworkCallback(networkCallback)
        _binding = null
    }

    private fun watchInternetConnection() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                isConnectionLost = true
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.no_connection_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (isConnectionLost) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.restore_connection_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    isConnectionLost = false
                }
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}