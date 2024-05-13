package com.study.messengerfintech.screens

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import com.study.messengerfintech.R
import com.study.messengerfintech.presentation.fragments.ChatFragment
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.progress.KProgressBar
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher


object ChatScreen : KScreen<ChatScreen>() {
    override val layoutId: Int = R.layout.chat_fragment
    override val viewClass: Class<*> = ChatFragment::class.java

    val backButton = KImageView { withId(R.id.back_button_chat) }
    val chatTitle = KTextView { withId(R.id.chat_title) }
    val sendMessageButton = KImageView { withId(R.id.send_message_button) }
    val addFileButton = KImageView { withId(R.id.add_file_button) }
    val messageEditText = KEditText { withId(R.id.send_message_draft_text) }
    val loadingProgressBar = KProgressBar { withId(R.id.progress_bar) }
    val chatRecycler = KRecyclerView({ withId(R.id.chat_recycler) }, { itemType(::MessageItem) })

    class MessageItem(matcher: Matcher<View>) : KRecyclerItem<MessageItem>(matcher) {
        val avatar = KImageView(matcher) { withId(R.id.sender_avatar) }
        val name = KTextView(matcher) { withId(R.id.message_sender_nickname) }
        val content = KTextView(matcher) { withId(R.id.message_text) }
        val reactions = KView(matcher) { withId(R.id.emoji_group) }
    }
}