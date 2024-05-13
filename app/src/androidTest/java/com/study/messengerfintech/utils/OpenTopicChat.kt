package com.study.messengerfintech.utils

import com.kaspersky.kaspresso.testcases.api.scenario.Scenario
import com.kaspersky.kaspresso.testcases.core.testcontext.TestContext
import com.study.messengerfintech.screens.StreamAndTopicListScreen

class OpenTopicChat : Scenario() {
    override val steps: TestContext<Unit>.() -> Unit = {
        step("streams -> topic") {
            StreamAndTopicListScreen {
                streamAndTopicsRecycler {
                    lastChild<StreamAndTopicListScreen.StreamListItem> {
                        stream.click()
                    }
                    lastChild<StreamAndTopicListScreen.TopicListItem> {
                        topic.click()
                    }
                }
            }
        }
    }
}