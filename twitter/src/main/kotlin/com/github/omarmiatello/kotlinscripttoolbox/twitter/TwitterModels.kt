package com.github.omarmiatello.kotlinscripttoolbox.twitter

import com.github.omarmiatello.kotlinscripttoolbox.gson.toJson

data class TweetMessageRequest(
    val text: String,
    val quote_tweet_id: String? = null,
) {
    override fun toString() = toJson()
}

data class TweetMessageResponse(
    val id: String,
    val text: String,
)
