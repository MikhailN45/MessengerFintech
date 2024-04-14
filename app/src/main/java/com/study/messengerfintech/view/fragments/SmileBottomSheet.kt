package com.study.messengerfintech.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.study.messengerfintech.databinding.SmilesBottomSheetContentBinding
import com.study.messengerfintech.model.data.emojiNameUnicodeHashMap

class SmileBottomSheet : BottomSheetDialogFragment() {
    private var _binding: SmilesBottomSheetContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SmilesBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initButtons() {
        val emojiMap = emojiNameUnicodeHashMap
        for (key in emojiMap.keys)
            Button(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                text = emojiMap[key]
                textSize = SMILE_SIZE
                binding.smileChoiceBottomsheetPanel.addView(this)
                setOnClickListener {
                    parentFragmentManager.setFragmentResult(
                        SMILE_RESULT, bundleOf(
                            SMILE_KEY to emojiMap[key],
                            SMILE_NAME to key,
                            MESSAGE_KEY to arguments?.getInt(MESSAGE_KEY)
                        )
                    )
                    dismiss()
                }
            }
    }

    companion object {
        const val SMILE_SIZE = 24f
        const val TAG = "SMILE_TAG"
        const val MESSAGE_KEY = "MESSAGE_KEY"
        const val SMILE_KEY = "SMILE_KEY"
        const val SMILE_RESULT = "RESULT"
        const val SMILE_NAME = "SMILE_NAME"
    }
}