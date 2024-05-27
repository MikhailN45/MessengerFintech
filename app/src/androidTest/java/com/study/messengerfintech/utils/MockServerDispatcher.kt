package com.study.messengerfintech.utils

import com.study.messengerfintech.utils.AssetsUtils.fromAssets
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockServerDispatcher : Dispatcher() {
    private val answer: MutableMap<String, MockResponse> = mutableMapOf()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return MockResponse().setResponseCode(404)

        return when {
            path.startsWith("/messages?anchor=newest") -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    fromAssets("chat_with_messages.json")
                )

            path.startsWith("/messages/type=stream&to") -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    fromAssets("chat_new_message.json")
                )

            path.startsWith("/users/me/432915/topics") -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    fromAssets("topics.json")
                )

            path.startsWith("/users/me/subscriptions") -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    fromAssets("subscribed_streams.json")
                )

            path.startsWith("/streams") -> MockResponse()
                .setResponseCode(200)
                .setBody(
                    fromAssets("all_streams.json")
                )

            else -> answer[request.path] ?: MockResponse().setResponseCode(404)
        }
    }
}