package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ProfileFragmentBinding

class ProfileFragment : Fragment(R.layout.profile_fragment) {
    private lateinit var binding: ProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(layoutInflater)

        binding.logOutButton.setOnClickListener {
            binding.profileAppbar.visibility = View.VISIBLE
            binding.logOutButton.visibility = View.GONE
            val snackBar = Snackbar.make(
                binding.root, "Logout successfully!", Snackbar.LENGTH_SHORT
            )
            val params = snackBar.view.layoutParams as FrameLayout.LayoutParams
            params.setMargins(0, 0, 0, 190)
            snackBar.view.layoutParams = params
            snackBar.show()
        }

        binding.backButtonChat.setOnClickListener { parentFragmentManager.popBackStack() }

        return binding.root
    }
}