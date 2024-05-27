package com.study.messengerfintech.screens

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import com.study.messengerfintech.R
import com.study.messengerfintech.presentation.fragments.StreamsTopicsListFragment
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher

object StreamAndTopicListScreen : KScreen<StreamAndTopicListScreen>() {
    override val layoutId: Int = R.layout.streams_and_chats_fragment
    override val viewClass: Class<*> = StreamsTopicsListFragment::class.java

    val streamAndTopicsRecycler = KRecyclerView(
        { withId(R.id.streams_and_chats_recycler) },
        {
            itemType(::StreamListItem)
            itemType(::TopicListItem)
        }
    )

    class StreamListItem(matcher: Matcher<View>) : KRecyclerItem<StreamListItem>(matcher) {
        val stream = KTextView(matcher) { withId(R.id.stream_name) }
    }

    class TopicListItem(matcher: Matcher<View>) : KRecyclerItem<TopicListItem>(matcher) {
        val topic = KTextView(matcher) { withId(R.id.chat_title) }
    }
}