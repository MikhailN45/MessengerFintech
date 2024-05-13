package com.study.messengerfintech.screens

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import com.study.messengerfintech.R
import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import org.hamcrest.Matcher

object SmileBottomSheetScreen : KScreen<SmileBottomSheetScreen>() {
    override val layoutId: Int = R.layout.smiles_bottom_sheet_content
    override val viewClass: Class<*> = SmileBottomSheetScreen::class.java

    val emojiList = KRecyclerView({ withId(R.id.smile_choice_bottomsheet_panel) },
        { itemType { EmojiItem(it) } })

    class EmojiItem(matcher: Matcher<View>) : KRecyclerItem<EmojiItem>(matcher) {
        val emoji = KView(matcher) { isFirst() }
    }
}