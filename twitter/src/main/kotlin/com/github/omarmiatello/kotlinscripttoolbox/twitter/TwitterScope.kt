package com.github.omarmiatello.kotlinscripttoolbox.twitter

import com.github.omarmiatello.kotlinscripttoolbox.core.BaseScope
import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
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

    suspend fun sendTweet(
        text: String,
        maxMessages: Int = 1,
    ) {
        sendTweets(
            texts = text.chunked(MESSAGE_MAX_SIZE).take(maxMessages),
        )
    }

    suspend fun sendTweets(
        texts: List<String>,
    ) {
        var lastTweetId: String? = null
        texts
            .map { it.take(MESSAGE_MAX_SIZE) }
            .forEach { msg ->
                println("🐣 --> $msg")

                val response: String? = twitterHttpClient.newCall(
                    Request.Builder()
                        .url("https://api.twitter.com/2/tweets")
                        .post(
                            TweetMessageRequest(
                                text = msg,
                                quote_tweet_id = lastTweetId,
                            )
                                .toString()
                                .toRequestBody("application/json".toMediaType())
                        )
                        .build()
                )
                    .execute()
                    .body
                    ?.string()

                try {
                    lastTweetId = response!!.fromJson<TweetMessageResponse>().id
                } catch (e: Exception) {
                    println("⚠️ Cannot find the tweet id in $response")
                }
                println("🐥 <-- $response")
            }
    }

    companion object {
        const val MESSAGE_MAX_SIZE = 280

        fun fromDefaults(
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
