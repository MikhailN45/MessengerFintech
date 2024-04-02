package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var chat: Chat
    private lateinit var adapter: MessagesAdapter

    private val layoutManager = LinearLayoutManager(context).apply {
        stackFromEnd = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chat = viewModel.getChat(it.getInt(STREAM_COUNT), it.getInt(CHAT_COUNT))
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
            SmileBottomSheet(
                onItemClick = { smileKey ->
                    chat.messages[position].reactions.add(Reaction(smile = smileKey, num = 1))
                    adapter.notifyItemChanged(position)
                }
            ).show(childFragmentManager, SmileBottomSheet.TAG)
        }

        binding.chatRecycler.apply {
            adapter = this@ChatFragment.adapter
            layoutManager = this@ChatFragment.layoutManager
            addItemDecoration(DateItemDecorator())
        }

        binding.sendMessageButton.setOnClickListener {
            sendMessage()
            binding.chatRecycler.adapter?.notifyItemInserted(chat.messages.size)
        }

        binding.sendMessageDraftText.doAfterTextChanged {
            if (it.isNullOrBlank()) {
                binding.sendMessageButton.visibility = View.GONE
                binding.addFileButton.visibility = View.VISIBLE
            } else {
                binding.sendMessageButton.visibility = View.VISIBLE
                binding.addFileButton.visibility = View.GONE
            }
        }

        return binding.root
    }

    private fun sendMessage(){
        binding.sendMessageDraftText.apply {
            if (this.length() == 0) return@apply
            chat.messages.add(Message(chat.messages.size, User.INSTANCE, text.toString()))
            setText("")
            layoutManager.scrollToPosition(chat.messages.size - 1)
        }
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
