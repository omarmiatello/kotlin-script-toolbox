package com.github.omarmiatello.kotlinscripttoolbox.twitter

import com.github.omarmiatello.kotlinscripttoolbox.core.BaseScope
import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.github.omarmiatello.kotlinscripttoolbox.core.Messages
import com.github.omarmiatello.kotlinscripttoolbox.gson.fromJson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

interface TwitterScope : KotlinScriptToolboxScope {
    val twitterHttpClient: OkHttpClient

    suspend fun sendTweets(
        texts: List<String>,
        ignoreLimit: Boolean = false,
        request: (String, TweetMessageResponse?) -> TweetMessageRequest = { msg, lastTweet ->
            TweetMessageRequest(
                text = msg,
                reply = lastTweet?.let { TweetReply(it.data.id) },
            )
        },
    ) {
        var lastTweet: TweetMessageResponse? = null
        texts
            .map { if (ignoreLimit) it else it.take(MESSAGE_MAX_SIZE) }
            .forEach { msg ->
                println("🐣 --> $msg")

                val response: String? = twitterHttpClient.newCall(
                    Request.Builder()
                        .url("https://api.twitter.com/2/tweets")
                        .post(
                            request(msg, lastTweet).toString()
                                .toRequestBody("application/json".toMediaType())
                        )
                        .build()
                )
                    .execute()
                    .body
                    ?.string()

                try {
                    lastTweet = response!!.fromJson<TweetMessageResponse>().takeIf { it.data.id.isNotEmpty() }
                } catch (e: Exception) {
                    println("⚠️ Cannot parse $response")
                }
                println("🐥 <-- id: ${lastTweet?.data?.id}: $response")
            }
    }

    suspend fun sendTweet(
        text: String,
        maxMessages: Int = 1,
        ignoreLimit: Boolean = false,
        request: (String, TweetMessageResponse?) -> TweetMessageRequest = { msg, lastTweet ->
            TweetMessageRequest(
                text = msg,
                reply = lastTweet?.let { TweetReply(it.data.id) },
            )
        },
    ) = sendTweets(
        texts = text.chunked(MESSAGE_MAX_SIZE).take(maxMessages),
        ignoreLimit = ignoreLimit,
        request = request,
    )

    suspend fun sendTweets(
        messages: Messages,
        request: (String, TweetMessageResponse?) -> TweetMessageRequest = { msg, lastTweet ->
            TweetMessageRequest(
                text = msg,
                reply = lastTweet?.let { TweetReply(it.data.id) },
            )
        },
    ) = sendTweets(
        texts = messages.toStrings(MESSAGE_MAX_SIZE),
        ignoreLimit = true,
        request = request,
    )

    suspend fun deleteTweet(id: String): Boolean {
        println("💀 --> DELETE $id")
        val response: String? = twitterHttpClient.newCall(
            Request.Builder()
                .url("https://api.twitter.com/2/tweets/$id")
                .delete()
                .build()
        )
            .execute()
            .body
            ?.string()

        val res = try {
            response!!.fromJson<TweetDeleteResponse>().takeIf { it.data.deleted }
        } catch (e: Exception) {
            println("⚠️ Cannot parse $response")
            null
        }
        println("☠️ <-- DELETE $id? ${res?.data?.deleted} $response")
        return res?.data?.deleted ?: false
    }

    companion object {
        const val MESSAGE_MAX_SIZE = 280

        /**
         * A URL of any length will be altered to 23 characters, even if the link itself is less than 23 characters long.
         * Your character count will reflect this. - https://help.twitter.com/en/using-twitter/how-to-tweet-a-link
         */
        const val URL_MAX_SIZE = 23

        fun from(
            baseScope: BaseScope,
            consumerKey: String = baseScope.readSystemProperty("TWITTER_CONSUMER_KEY"),
            consumerSecret: String = baseScope.readSystemProperty("TWITTER_CONSUMER_SECRET"),
            accessKey: String = baseScope.readSystemProperty("TWITTER_ACCESS_KEY"),
            accessSecret: String = baseScope.readSystemProperty("TWITTER_ACCESS_SECRET"),
        ) = from(
            consumerKey = consumerKey,
            consumerSecret = consumerSecret,
            accessKey = accessKey,
            accessSecret = accessSecret,
        )

        fun from(
            consumerKey: String,
            consumerSecret: String,
            accessKey: String,
            accessSecret: String,
        ) = object : TwitterScope {
            override val twitterHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request()
                    fun String.encodeUtf8() = URLEncoder.encode(this, "UTF-8").replace("+", "%2B")
                    val nonce: String = UUID.randomUUID().toString()
                    val timestamp: Long = System.currentTimeMillis() / 1000L
                    val urlEncoded = request.url.toString().encodeUtf8()
                    val paramEncoded =
                        "oauth_consumer_key=$consumerKey&oauth_nonce=$nonce&oauth_signature_method=HMAC-SHA1&oauth_timestamp=$timestamp&oauth_token=$accessKey&oauth_version=1.0".encodeUtf8()
                    val signature = Mac.getInstance("HmacSHA1")
                        .apply { init(SecretKeySpec("$consumerSecret&$accessSecret".toByteArray(), "HmacSHA1")) }
                        .doFinal("${request.method}&$urlEncoded&$paramEncoded".toByteArray())
                        .let { ByteString.of(*it) }
                        .base64()
                        .encodeUtf8()

                    chain.proceed(
                        request.newBuilder().addHeader(
                            name = "Authorization",
                            value = """OAuth oauth_consumer_key="$consumerKey", oauth_nonce="$nonce", oauth_signature="$signature", oauth_signature_method="HMAC-SHA1", oauth_timestamp="$timestamp", oauth_token="$accessKey", oauth_version="1.0""""
                        ).build()
                    )
                }
                .build()
        }
    }
}
