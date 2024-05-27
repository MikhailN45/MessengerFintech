package com.study.messengerfintech.screens

import com.kaspersky.kaspresso.screens.KScreen
import com.study.messengerfintech.R
import com.study.messengerfintech.presentation.fragments.StreamsFragment
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.tabs.KTabLayout

object StreamsScreen : KScreen<StreamsScreen>() {
    override val layoutId: Int = R.layout.streams_fragment
    override val viewClass: Class<*> = StreamsFragment::class.java

    val searchEditText = KEditText { withId(R.id.topbar_search_edit_text) }
    val streamsTabLayout = KTabLayout { withId(R.id.streams_tab_layout) }
}