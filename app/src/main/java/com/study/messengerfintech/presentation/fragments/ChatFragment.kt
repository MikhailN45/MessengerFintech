package com.study.messengerfintech.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ChatFragmentBinding
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.UnitedReaction
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.getComponent
import com.study.messengerfintech.presentation.adapters.MessagesAdapter
import com.study.messengerfintech.presentation.events.ChatEvent
import com.study.messengerfintech.presentation.state.ChatState
import com.study.messengerfintech.presentation.viewmodel.ChatViewModel
import javax.inject.Inject

class ChatFragment : FragmentMvi<ChatState>(R.layout.chat_fragment) {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val chatViewModel: ChatViewModel by viewModels { viewModelFactory }
    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!

    private val streamId: Int? by lazy { arguments?.getInt(STREAM) }
    private val topicName: String? by lazy { arguments?.getString(TOPIC) }
    private val userName: String? by lazy { arguments?.getString(USER_NAME) }
    private val userEmail: String? by lazy { arguments?.getString(USER_MAIL) }

    private val adapter: MessagesAdapter by lazy {
        MessagesAdapter(
            onEmojiAddClick = { messageId, emojiName ->
                chatViewModel.processEvent(ChatEvent.Emoji.Add(messageId, emojiName))
            },
            onEmojiDeleteClick = { messageId, emojiName ->
                chatViewModel.processEvent(ChatEvent.Emoji.Remove(messageId, emojiName))
            },
            onMessageLongClick = { position ->
                showBottomSheet(position)
            },
            onBind = { position ->
                if (
                    position == ((chatViewModel.state.value?.messages?.size ?: 0) - 5)
                    &&
                    chatViewModel.state.value?.isAllChatMessageAreLoaded == false
                )
                    loadMessages()
            }
        )
    }

    private val layoutManager = LinearLayoutManager(context).apply {
        reverseLayout = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().chatComponent().create().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadMessages()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScreen()

        chatViewModel.scrollEvent.observe(viewLifecycleOwner) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.chatRecycler.scrollToPosition(0)
            }, 100)
        }

        chatViewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }


    override fun render(state: ChatState) {
        binding.progressBar.isVisible = state.isLoading
        adapter.submitList(state.messages)
    }

    private fun showBottomSheet(position: Int) {
        SmileBottomSheet().apply { arguments = bundleOf(SmileBottomSheet.MESSAGE_KEY to position) }
            .show(childFragmentManager, SmileBottomSheet.TAG)
    }

    private fun initScreen() {
        with(binding) {
            chatTitle.text = "#%s".format(topicName ?: userName)
            backButtonChat.setOnClickListener { parentFragmentManager.popBackStack() }

            chatRecycler.apply {
                adapter = this@ChatFragment.adapter
                layoutManager = this@ChatFragment.layoutManager
            }

            sendMessageButton.setOnClickListener {
                sendMessageDraftText.apply {
                    if (this.text.isNullOrBlank()) return@apply
                    sendMessage(text.toString())
                    setText(BLANK_STRING)
                }
            }

            sendMessageDraftText.doAfterTextChanged {
                sendMessageButton.isVisible = !it.isNullOrBlank()
                addFileButton.isVisible = it.isNullOrBlank()
            }
        }

        childFragmentManager.setFragmentResultListener(
            SmileBottomSheet.SMILE_RESULT, this
        ) { _, bundle ->
            val messagePosition = bundle.getInt(SmileBottomSheet.MESSAGE_KEY)
            val smileKey = bundle.getString(SmileBottomSheet.SMILE_KEY)!!
            val name = bundle.getString(SmileBottomSheet.SMILE_NAME)!!
            val emoji = Reaction(code = smileKey, name = name, userId = User.ME.id)
            val emojisOnMessage: UnitedReaction? =
                chatViewModel.state.value?.messages?.get(messagePosition)?.emojiCodeReactionMap?.get(
                    emoji.getUnicode()
                )
            if (emojisOnMessage == null || !emojisOnMessage.usersId.contains(User.ME.id)) {
                chatViewModel.processEvent(
                    ChatEvent.ReactionClick(emoji, messagePosition)
                )
            }
            adapter.notifyItemChanged(messagePosition)
        }
    }

    private fun loadMessages() {
        val anchor =
            if (!chatViewModel.state.value?.messages.isNullOrEmpty()) {
                "${chatViewModel.state.value?.messages?.lastOrNull()?.id}"
            } else {
                "newest"
            }

        chatViewModel.processEvent(
            if (userEmail != null) {
                ChatEvent.LoadMessages.Private(userEmail!!, anchor)
            } else if (streamId != null && topicName != null) {
                ChatEvent.LoadMessages.Topic(streamId!!, topicName!!, anchor)
            } else {
                parentFragmentManager.popBackStack()
                return
            }
        )
    }

    private fun sendMessage(content: String) {
        chatViewModel.processEvent(
            if (!userEmail.isNullOrBlank()) {
                ChatEvent.SendMessage.Private(userEmail!!, content)
            } else {
                ChatEvent.SendMessage.Topic(streamId!!, topicName!!, content)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val USER_MAIL = "user_email"
        const val USER_NAME = "user_name"
        const val STREAM = "stream"
        const val TOPIC = "topic"
        const val BLANK_STRING = ""
        fun newInstance(bundle: Bundle) = ChatFragment().apply { arguments = bundle }
    }
}