package com.github.omarmiatello.kotlinscripttoolbox.twitter

import com.github.omarmiatello.kotlinscripttoolbox.core.CacheKey
import com.github.omarmiatello.kotlinscripttoolbox.core.KotlinScriptToolboxScope
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import okio.ByteString
import java.net.URLEncoder
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private val gson = Gson()

data class TweetMessage(val text: String) {
    override fun toString() = gson.toJson(this)
}

object TwitterKey {
    val httpClient = CacheKey<HttpClient>()
}

fun KotlinScriptToolboxScope.setupTwitter(
    consumerKey: String,
    consumerSecret: String,
    accessKey: String,
    accessSecret: String,
) {
    cache.put(TwitterKey.httpClient, HttpClient(OkHttp) {
        engine {
            addInterceptor { chain ->
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
        }
    })
}

val KotlinScriptToolboxScope.twitterHttpClient: HttpClient
    get() = cache.getOrNull(TwitterKey.httpClient) ?: error("Have you run setupTwitter()?")

suspend fun KotlinScriptToolboxScope.sendTweet(text: String) {
    suspend fun sendTweetMessage(text: String) {
        println("🐣 --> $text")
        val response: String = twitterHttpClient.post("https://api.twitter.com/2/tweets") {
            header("Content-Type", "application/json")
            setBody(TweetMessage(text).toString())
        }.body()
        println("🐥 <-- $response")
    }
}