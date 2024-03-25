package com.study.messengerfintech

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.chatRecycler.DateItemDecorator
import com.study.messengerfintech.chatRecycler.MessagesAdapter
import com.study.messengerfintech.chatRecycler.SmileBottomSheet
import com.study.messengerfintech.data.Message
import com.study.messengerfintech.data.Reaction
import com.study.messengerfintech.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val sampleMessages: MutableList<Message> = with("Darrell Steward") {
        mutableListOf(
            Message(0, false, "One, two, three, four", this),
            Message(1, true, "Who is knocking at the door?", this),
            Message(2, false, "Five, six, seven, eight", this),
            Message(3, true, "Hurry up and don't be late.", this, mutableListOf(Reaction(false, 1, 7, 0))),
            Message(4, false, "Nine, ten, eleven, twelve", this, mutableListOf(Reaction(true, 5, 1, 0))),
            Message(5, false, "Into the garden, let's delve.", this, mutableListOf(Reaction(false, 7, 15, 0)))
        )
    }

    private val modalBottomSheet = SmileBottomSheet()
    private val adapter = MessagesAdapter(sampleMessages) { position ->
        modalBottomSheet.show(supportFragmentManager, SmileBottomSheet.TAG)
        modalBottomSheet.arguments = bundleOf(SmileBottomSheet.MESSAGE_KEY to position)
    }
    private val layoutManager = LinearLayoutManager(this).apply {
        stackFromEnd = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sendMessageButton = binding.sendMessageButton
        val addFileButton = binding.attachFileButton
        val messageText = binding.sendMessageDraftText
        val messageRecycler = binding.chatRecycler

        messageRecycler.apply {
            adapter = this@MainActivity.adapter
            layoutManager = this@MainActivity.layoutManager
            addItemDecoration(DateItemDecorator())
        }

        sendMessageButton.setOnClickListener {
            messageText.apply {
                sampleMessages.add(Message(sampleMessages.size, true, text.toString()))
                setText("")
                layoutManager.scrollToPosition(sampleMessages.size - 1)
            }
            messageRecycler.adapter?.notifyItemInserted(sampleMessages.size)
        }

        messageText.doAfterTextChanged {
            if (it?.length == 0) {
                sendMessageButton.visibility = View.GONE
                addFileButton.visibility = View.VISIBLE
            } else {
                sendMessageButton.visibility = View.VISIBLE
                addFileButton.visibility = View.GONE
            }
        }

        supportFragmentManager.setFragmentResultListener(
            SmileBottomSheet.SMILE_RESULT, this
        ) { _, bundle ->
            val messagePosition = bundle.getInt(SmileBottomSheet.MESSAGE_KEY)
            val smileNum = bundle.getInt(SmileBottomSheet.SMILE_KEY)
            sampleMessages[messagePosition].reactions.add(
                Reaction(isMine = true, smile = smileNum, num = 1, 0)
            )
            adapter.notifyItemChanged(messagePosition)
        }
    }
}
