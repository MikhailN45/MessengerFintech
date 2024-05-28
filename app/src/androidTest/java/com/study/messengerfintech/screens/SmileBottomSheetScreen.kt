package com.study.messengerfintech.screens

import com.kaspersky.kaspresso.screens.KScreen
import com.study.messengerfintech.R
import io.github.kakaocup.kakao.text.KButton

object SmileBottomSheetScreen : KScreen<SmileBottomSheetScreen>() {
    override val layoutId: Int = R.layout.smiles_bottom_sheet_content
    override val viewClass: Class<*> = SmileBottomSheetScreen::class.java

    val firstEmoji = KButton {
        withParent {
            withId(R.id.smile_choice_bottomsheet_panel)
            isFirst()
        }
    }
}