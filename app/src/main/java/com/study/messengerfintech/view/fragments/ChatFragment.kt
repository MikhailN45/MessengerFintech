package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.databinding.ChatFragmentBinding
import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.Message
import com.study.messengerfintech.model.data.Reaction
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.viewmodel.MainViewModel
import com.study.messengerfintech.viewmodel.chatRecycler.DateItemDecorator
import com.study.messengerfintech.viewmodel.chatRecycler.MessagesAdapter

class ChatFragment : Fragment() {
    private lateinit var binding: ChatFragmentBinding
    private val model: MainViewModel by activityViewModels()
    private val modalBottomSheet = SmileBottomSheet()
    private lateinit var chat: Chat
    private lateinit var adapter: MessagesAdapter

    private val layoutManager = LinearLayoutManager(context).apply {
        stackFromEnd = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chat = model.getChat(it.getInt(STREAM_COUNT), it.getInt(CHAT_COUNT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(layoutInflater)
        binding.chatTitle.text = chat.title
        binding.backButtonChat.setOnClickListener { parentFragmentManager.popBackStack() }

        adapter = MessagesAdapter(chat.messages) { position ->
            modalBottomSheet.show(childFragmentManager, SmileBottomSheet.TAG)
            modalBottomSheet.arguments = bundleOf(SmileBottomSheet.MESSAGE_KEY to position)
        }

        binding.chatRecycler.apply {
            adapter = this@ChatFragment.adapter
            layoutManager = this@ChatFragment.layoutManager
            addItemDecoration(DateItemDecorator())
        }

        binding.sendMessageButton.setOnClickListener {
            binding.sendMessageDraftText.apply {
                if (this.length() == 0) return@apply
                chat.messages.add(Message(chat.messages.size, User.INSTANCE, text.toString()))
                setText("")
                layoutManager.scrollToPosition(chat.messages.size - 1)
            }
            binding.chatRecycler.adapter?.notifyItemInserted(chat.messages.size)
        }

        binding.sendMessageDraftText.doAfterTextChanged {
            if (it?.length == 0) {
                binding.sendMessageButton.visibility = View.GONE
                binding.addFileButton.visibility = View.VISIBLE
            } else {
                binding.sendMessageButton.visibility = View.VISIBLE
                binding.addFileButton.visibility = View.GONE
            }
        }

        childFragmentManager.setFragmentResultListener(
            SmileBottomSheet.SMILE_RESULT, this
        ) { _, bundle ->
            val messagePosition = bundle.getInt(SmileBottomSheet.MESSAGE_KEY)
            val smileNum = bundle.getInt(SmileBottomSheet.SMILE_KEY)
            chat.messages[messagePosition].reactions.add(Reaction(smile = smileNum, num = 1))
            adapter.notifyItemChanged(messagePosition)
        }

        return binding.root
    }

    companion object {
        private const val STREAM_COUNT = "STREAM_COUNT"
        private const val CHAT_COUNT = "CHAT_COUNT"
        fun newInstance(streamCount: Int, chatCount: Int) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putInt(STREAM_COUNT, streamCount)
                    putInt(CHAT_COUNT, chatCount)
                }
            }
    }
}
