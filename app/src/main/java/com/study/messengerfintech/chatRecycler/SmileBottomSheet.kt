package com.study.messengerfintech.chatRecycler

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.SmilesBottomSheetContentBinding

class SmileBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: SmilesBottomSheetContentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = SmilesBottomSheetContentBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = binding.smileChoiceBottomsheetPanel
        val emojis = resources.getStringArray(R.array.emojis)

        for (i in emojis.indices)
            Button(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                text = emojis[i]
                textSize = SMILE_SIZE
                bottomSheet.addView(this)
                setOnClickListener {
                    parentFragmentManager.setFragmentResult(
                        SMILE_RESULT,
                        bundleOf(SMILE_KEY to i, MESSAGE_KEY to arguments?.getInt(MESSAGE_KEY))
                    )
                    dismiss()
                }
            }
    }

    companion object {
        const val SMILE_SIZE = 24f
        const val TAG = "TAG"
        const val SMILE_RESULT = "RESULT"
        const val SMILE_KEY = "SMILE"
        const val MESSAGE_KEY = "MESSAGE"
    }
}
