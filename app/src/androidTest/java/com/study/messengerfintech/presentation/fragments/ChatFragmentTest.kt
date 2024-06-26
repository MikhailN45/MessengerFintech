package com.study.messengerfintech.presentation.fragments

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.study.messengerfintech.App
import com.study.messengerfintech.R
import com.study.messengerfintech.presentation.MainActivity
import com.study.messengerfintech.screens.ChatScreen
import com.study.messengerfintech.screens.SmileBottomSheetScreen
import com.study.messengerfintech.screens.StreamAndTopicListScreen
import com.study.messengerfintech.utils.MockServerDispatcher
import com.study.messengerfintech.utils.OpenTopicChat
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatFragmentTest : TestCase() {

    @get:Rule
    val mockServer = MockWebServer()

    @Before
    fun setUp() {
        val appComponent = App.INSTANCE.appComponent.getApiUrlProvider()
        appComponent.apiUrl = mockServer.url("/").toString()
        mockServer.dispatcher = MockServerDispatcher()
    }

    @After
    fun shutDown() {
        mockServer.shutdown()
    }

    @Test
    fun openChat() = run {
        ActivityScenario.launch(MainActivity::class.java)
        scenario(OpenTopicChat())
        ChatScreen {
            chatRecycler.isDisplayed()
        }
    }


    @Test
    fun sendMessage() = run {
        ActivityScenario.launch(MainActivity::class.java)
        scenario(OpenTopicChat())
        ChatScreen {
            step("Type text in send field") {
                messageEditText.typeText("k-test")
                Thread.sleep(1000)
            }

            step("Click send button") {
                sendMessageButton.click()
            }

            step("Message text is clear") {
                messageEditText.hasEmptyText()
            }
        }
    }

    @Test
    fun setReactionToMessage() = run {
        ActivityScenario.launch(MainActivity::class.java)
        scenario(OpenTopicChat())
        ChatScreen {
            step("Invoke bottom sheet") {
                chatRecycler.firstChild<ChatScreen.MessageItem> {
                    content.longClick()
                }
            }

            step("Set reaction") {
                SmileBottomSheetScreen.firstEmoji.click()
            }

            step("Check for reaction is added") {
                chatRecycler.firstChild<ChatScreen.MessageItem> {
                    reactions.isDisplayed()
                }
            }
        }
    }

    @Test
    fun exitTopicChatByBackButton() = run {
        ActivityScenario.launch(MainActivity::class.java)
        scenario(OpenTopicChat())
        ChatScreen {
            step("Press back button") {
                backButton.click()
            }
        }

        StreamAndTopicListScreen {
            step("Check chat is closed") {
                streamAndTopicsRecycler.isDisplayed()
            }
        }
    }

    @Test
    fun checkChatUiDisplaysCorrect() = run {
        val bundle = bundleOf(
            ChatFragment.STREAM to 436402,
            ChatFragment.TOPIC to "Test Topic"
        )

        launchFragmentInContainer<ChatFragment>(
            fragmentArgs = bundle,
            themeResId = R.style.Base_Theme_MessengerFintech
        )

        ChatScreen.apply {
            step("Back button is displayed") {
                backButton.isDisplayed()
            }

            step("Chat title is displayed") {
                chatTitle.isDisplayed()
            }

            step("Message list is displayed") {
                chatRecycler.isDisplayed()
            }

            step("Text input field is displayed") {
                messageEditText.isDisplayed()
            }

            step("Add button is displayed") {
                addFileButton.isDisplayed()
            }

            step("Type sample text") {
                messageEditText.typeText("test")
            }

            step("Send message button is displayed") {
                sendMessageButton.isDisplayed()
            }
        }
    }
}