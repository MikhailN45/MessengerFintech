package com.study.messengerfintech.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.messengerfintech.R
import com.study.messengerfintech.databinding.ChatFragmentBinding
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.UnitedReaction
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.utils.EmojiAdd
import com.study.messengerfintech.utils.EmojiDelete
import com.study.messengerfintech.utils.OnEmojiClick
import com.study.messengerfintech.presentation.state.ChatState
import com.study.messengerfintech.presentation.viewmodel.MainViewModel
import com.study.messengerfintech.presentation.adapters.MessagesAdapter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class ChatFragment : Fragment(R.layout.chat_fragment) {
    private val viewModel: MainViewModel by activityViewModels()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    //todo extract messages to viewmodel
    private lateinit var messages: MutableList<Message>
    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter: MessagesAdapter by lazy {
        MessagesAdapter(messages,
            emojiClickListener = { parcel: OnEmojiClick ->
                processEmojiClick(parcel)
            },
            longClickListener = { position ->
                showBottomSheet(position)
            })
    }

    private val layoutManager = LinearLayoutManager(context).apply {
        stackFromEnd = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            viewModel.getMessagesForPrivateOrTopic(bundle)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    messages = it.toMutableList()
                    viewModel.chatScreenSuccessful()
                    initScreen()
                }, { error ->
                    viewModel.chatScreenError(error)
                }).addTo(compositeDisposable)
        }

        viewModel.chatScreenLoading()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentBinding.inflate(layoutInflater)

        viewModel.chatState.observe(viewLifecycleOwner) {
            with(binding) {
                when (it) {
                    is ChatState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        sendMessageButton.visibility = View.GONE
                        addFileButton.visibility = View.GONE
                    }

                    is ChatState.Error -> {
                        Log.e("ChatScreenError", it.error.message.toString())
                        progressBar.visibility = View.GONE
                    }

                    is ChatState.Success -> {
                        progressBar.visibility = View.GONE
                        addFileButton.visibility = View.VISIBLE
                    }
                    //todo put UI initialization in state
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showBottomSheet(position: Int) {
        SmileBottomSheet()
            .apply { arguments = bundleOf(SmileBottomSheet.MESSAGE_KEY to position) }
            .show(childFragmentManager, SmileBottomSheet.TAG)
    }

    private fun processEmojiClick(parcel: OnEmojiClick) {
        when (parcel) {
            is EmojiAdd ->
                viewModel.addReaction(parcel.messageId, parcel.name)

            is EmojiDelete ->
                viewModel.deleteReaction(parcel.messageId, parcel.name)
        }
    }

    private fun initScreen() {
        with(binding) {
            chatTitle.text = "#%s".format(
                arguments?.getString(TOPIC) ?: arguments?.getString(USER_NAME)
            )

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
                viewModel.addReaction(messages[messagePosition].id, emoji.name)
                adapter.notifyItemChanged(messagePosition)
            }
        }
    }

    private fun sendMessage(content: String) {
        val bundle = requireArguments()
        viewModel.chatScreenLoading()
        val singleId: Single<Int> = if (bundle.containsKey(USER)) {
            val userEmail = bundle.getString(USER)!!
            viewModel.sendMessageToUser(userEmail, content)
        } else {
            val streamId = bundle.getInt(STREAM)
            val chatName = bundle.getString(TOPIC)!!
            viewModel.sendMessageToTopic(streamId, chatName, content)
        }
        singleId
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { id ->
                    //todo delegate logic to adapter
                    messages.add(Message(id, content, User.ME.id))
                    adapter.notifyItemInserted(messages.size - 1)
                    layoutManager.scrollToPosition(messages.size - 1)
                    viewModel.chatScreenSuccessful()
                }, { viewModel.chatScreenError(it) }
            ).addTo(compositeDisposable)
    }

    companion object {
        const val USER = "user_email"
        const val USER_NAME = "user_name"
        const val STREAM = "stream"
        const val TOPIC = "topic"
        const val BLANK_STRING = ""
        fun newInstance(bundle: Bundle) = ChatFragment().apply { arguments = bundle }
    }
}