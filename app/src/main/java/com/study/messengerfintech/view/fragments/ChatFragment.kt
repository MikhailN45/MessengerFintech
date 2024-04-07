package com.study.messengerfintech.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.study.messengerfintech.databinding.ChatFragmentBinding
import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.Message
import com.study.messengerfintech.model.data.Reaction
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.utils.MessageSendingError
import com.study.messengerfintech.view.states.MessengerState
import com.study.messengerfintech.viewmodel.MainViewModel
import com.study.messengerfintech.viewmodel.chatRecycler.DateItemDecorator
import com.study.messengerfintech.viewmodel.chatRecycler.MessagesAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlin.random.Random

class ChatFragment : Fragment() {
    private lateinit var binding: ChatFragmentBinding
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var chat: Chat
    private lateinit var adapter: MessagesAdapter

    private val layoutManager = LinearLayoutManager(context).apply {
        stackFromEnd = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ChatFragmentBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            viewModel.getChat(bundle.getInt(STREAM_COUNT), bundle.getInt(CHAT_COUNT))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        chat = it
                        initScreen()
                        viewModel.resultChats()
                    }, { error ->
                        viewModel.messageSendingError(error)
                    }
                )
        }
        viewModel.onChatViewCreated()

        viewModel.messengerState.observe(viewLifecycleOwner) {
            when (it) {
                is MessengerState.Error -> {
                    val snackBar = Snackbar.make(
                        binding.root, it.error.message.toString(), Snackbar.LENGTH_SHORT
                    )
                    val params =
                        snackBar.view.layoutParams as FrameLayout.LayoutParams
                    params.setMargins(0, 0, 0, 190)
                    snackBar.view.layoutParams = params
                    snackBar.show()
                }

                is MessengerState.Loading -> {}
                is MessengerState.Success -> {}
            }
        }
    }

    private fun initScreen() {
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
    }

    private fun sendMessage() {
        binding.sendMessageDraftText.apply {
            if (this.text.isNullOrBlank()) return@apply
            if (Random.nextInt() % 5 == 0) {
                viewModel.messageSendingError(MessageSendingError())
                return@apply
            }
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
