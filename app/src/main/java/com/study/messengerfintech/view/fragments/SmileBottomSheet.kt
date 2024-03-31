package com.study.messengerfintech.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.SmilesBottomSheetContentBinding

class SmileBottomSheet(val onItemClick: (smileKey: Int) -> Unit) : BottomSheetDialogFragment() {
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
        initButtons()
    }

    private fun initButtons() {
        val emojis = resources.getStringArray(R.array.emojis)
        emojis.forEachIndexed { i, emoji ->
            val buttonView = Button(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                text = emoji
                textSize = SMILE_SIZE
                setOnClickListener {
                    onItemClick(i)
                    dismiss()
                }
            }
            binding.smileChoiceBottomsheetPanel.addView(buttonView)
        }
    }

    companion object {
        const val SMILE_SIZE = 24f
        const val TAG = "TAG"
    }
}
