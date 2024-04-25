package com.study.messengerfintech.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ChatFragmentBinding
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.UnitedReaction
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.presentation.adapters.MessagesAdapter
import com.study.messengerfintech.presentation.events.ChatEvent
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.presentation.viewmodel.ChatViewModel
import com.study.messengerfintech.utils.EmojiAdd
import com.study.messengerfintech.utils.EmojiDelete
import com.study.messengerfintech.utils.OnEmojiClick

class ChatFragment : FragmentMVI<State.Chat>(R.layout.chat_fragment) {
    private val chatViewModel: ChatViewModel by activityViewModels()
    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!

    private val streamId: Int? by lazy { arguments?.getInt(STREAM) }
    private val topicName: String? by lazy { arguments?.getString(TOPIC) }
    private val userName: String? by lazy { arguments?.getString(USER_NAME) }
    private val userEmail: String? by lazy { arguments?.getString(USER_MAIL) }

    private var chatStateLocal = State.Chat("", listOf())
    private val messages
        get() = chatStateLocal.messages

    private val adapter: MessagesAdapter by lazy {
        MessagesAdapter(
            onEmojiClick = { parcel: OnEmojiClick -> processEmojiClick(parcel) },
            onLongClick = { position -> showBottomSheet(position) }
        )
    }

    private val layoutManager = LinearLayoutManager(context).apply {
        reverseLayout = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel.setChatTitle(userName ?: topicName!!)
        loadMessages()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentBinding.inflate(layoutInflater)

        chatViewModel.chatScreenState.observe(viewLifecycleOwner) {
            with(binding) {
                when (it) {
                    is State.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        sendMessageButton.visibility = View.GONE
                        addFileButton.visibility = View.GONE
                    }

                    is State.Error -> {
                        progressBar.visibility = View.GONE
                        sendMessageButton.visibility = View.VISIBLE
                    }

                    is State.Success -> {
                        progressBar.visibility = View.GONE
                        addFileButton.visibility = View.VISIBLE
                        chatViewModel.positionToScroll.observe(viewLifecycleOwner) {
                            adapter.submitList(messages) {
                                binding.chatRecycler.scrollToPosition(it)
                            }
                        }
                    }

                    else -> State.Error
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScreen()
        chatViewModel.chat.observe(viewLifecycleOwner) { render(it) }
        chatViewModel.positionToScroll.observe(viewLifecycleOwner) {
            adapter.submitList(messages) {
                binding.chatRecycler.scrollToPosition(it)
            }
        }
    }

    override fun render(state: State.Chat) {
        this.chatStateLocal = state
        binding.chatTitle.text = state.name
        adapter.submitList(messages)
    }

    private fun showBottomSheet(position: Int) {
        SmileBottomSheet()
            .apply { arguments = bundleOf(SmileBottomSheet.MESSAGE_KEY to position) }
            .show(childFragmentManager, SmileBottomSheet.TAG)
    }

    private fun processEmojiClick(parcel: OnEmojiClick) {
        when (parcel) {
            is EmojiAdd ->
                ChatEvent.Emoji.Add(parcel.messageId, parcel.name)

            is EmojiDelete ->
                ChatEvent.Emoji.Remove(parcel.messageId, parcel.name)
        }
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
                if (it.isNullOrBlank()) {
                    sendMessageButton.visibility = View.GONE
                    addFileButton.visibility = View.VISIBLE
                } else {
                    sendMessageButton.visibility = View.VISIBLE
                    addFileButton.visibility = View.GONE
                }
            }
        }

        childFragmentManager.setFragmentResultListener(
            SmileBottomSheet.SMILE_RESULT,
            this
        ) { _, bundle ->
            val messagePosition = bundle.getInt(SmileBottomSheet.MESSAGE_KEY)
            val smileKey = bundle.getString(SmileBottomSheet.SMILE_KEY)!!
            val name = bundle.getString(SmileBottomSheet.SMILE_NAME)!!
            val emoji = Reaction(code = smileKey, name = name, userId = User.ME.id)
            val emojisOnMessage: UnitedReaction? =
                messages[messagePosition].emojiCodeReactionMap[emoji.getUnicode()]
            if (emojisOnMessage == null || !emojisOnMessage.usersId.contains(User.ME.id)) {
                messages[messagePosition].addEmoji(emoji)
                chatViewModel.processEvent(
                    ChatEvent.Emoji.Add(
                        messages[messagePosition].id,
                        emoji.name
                    )
                )
                adapter.notifyItemChanged(messagePosition)
            }
        }
    }

    private fun loadMessages() {
        chatViewModel.processEvent(
            if (userEmail != null) {
                ChatEvent.LoadMessages.Private(userEmail!!)
            } else if (streamId != null && topicName != null) {
                ChatEvent.LoadMessages.Topic(streamId!!, topicName!!)
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