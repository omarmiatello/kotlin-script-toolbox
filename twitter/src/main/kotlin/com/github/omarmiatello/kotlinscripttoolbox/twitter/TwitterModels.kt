package com.github.omarmiatello.kotlinscripttoolbox.twitter

import com.github.omarmiatello.kotlinscripttoolbox.gson.toJson

/**
 * @param text Text of the Tweet being created. This field is required if media.media_ids is not present.
 * @param direct_message_deep_link Tweets a link directly to a Direct Message conversation with an account. Example: {"text": "Tweeting a DM deep link!", "direct_message_deep_link": "https://twitter.com/messages/compose?recipient_id=2244994945"} https://business.twitter.com/en/help/campaign-editing-and-optimization/public-to-private-conversation.html
 * @param for_super_followers_only Allows you to Tweet exclusively for Super Followers. https://help.twitter.com/en/using-twitter/super-follows
 * @param geo A JSON object that contains location information for a Tweet. You can only add a location to Tweets if you have geo enabled in your profile settings. If you don't have geo enabled, you can still add a location parameter in your request body, but it won't get attached to your Tweet
 * @param media A JSON object that contains media information being attached to created Tweet. This is mutually exclusive from Quote Tweet ID and Poll.
 * @param poll A JSON object that contains options for a Tweet with a poll. This is mutually exclusive from Media and Quote Tweet ID.
 * @param quote_tweet_id Link to the Tweet being quoted. Example: {"text": "Yay!", "quote_tweet_id": "1455953449422516226"}
 * @param reply A JSON object that contains information of the Tweet being replied to.
 * @param reply_settings Settings to indicate who can reply to the Tweet. Options include "mentionedUsers" and "following". If the field isn’t specified, it will default to everyone. Example:{"text": "Tweeting with reply settings!", "reply_settings": "mentionedUsers"} https://blog.twitter.com/en_us/topics/product/2020/new-conversation-settings-coming-to-a-tweet-near-you
 */
data class TweetMessageRequest(
    val text: String?,
    val direct_message_deep_link: String? = null,
    val for_super_followers_only: Boolean? = null,
    val geo: TweetGeo? = null,
    val media: TweetMedia? = null,
    val poll: TweetPoll? = null,
    val quote_tweet_id: String? = null,
    val reply: TweetReply? = null,
    val reply_settings: String? = null,
) {
    override fun toString() = toJson()
}

/**
 * @param place_id Place ID being attached to the Tweet for geo location. Example: {"text": "Tweeting with geo!","geo": {"place_id": "5a110d312052166f"}}
 */
data class TweetGeo(
    val place_id: String,
)

/**
 * @param media_ids A list of Media IDs being attached to the Tweet. This is only required if the request includes the tagged_user_ids. Example: {"text": "Tweeting with media!", "media": {"media_ids": ["1455952740635586573"]}}
 * @param tagged_user_ids A list of User IDs being tagged in the Tweet with Media. If the user you're tagging doesn't have photo-tagging enabled, their names won't show up in the list of tagged users even though the Tweet is successfully created. Example: {"text": "Tagging users in images!", "media": {"media_ids": ["1455952740635586573"], "tagged_user_ids": ["2244994945","6253282"]}}
 */
data class TweetMedia(
    val media_ids: List<String>,
    val tagged_user_ids: List<String>,
)

/**
 * @param duration_minute Duration of the poll in minutes for a Tweet with a poll. This is only required if the request includes poll.options. Example: {"text": "Tweeting with polls!", "poll": {"options": ["yes", "maybe", "no"], "duration_minutes": 120}}
 * @param options A list of poll options for a Tweet with a poll. For the request to be successful it must also include duration_minutes too. Example: {"text": "Tweeting with polls!", "poll": {"options": ["yes", "maybe", "no"], "duration_minutes": 120}}"
 */
data class TweetPoll(
    val duration_minute: Int,
    val options: List<String>,
)

/**
 * @param in_reply_to_tweet_id Tweet ID of the Tweet being replied to. Please note that in_reply_to_tweet_id needs to be in the request if exclude_reply_user_ids is present. Example: {"text": "Excited!", "reply": {"in_reply_to_tweet_id": "1455953449422516226"}}
 * @param exclude_reply_user_ids A list of User IDs to be excluded from the reply Tweet thus removing a user from a thread. Example: {"text": "Yay!", "reply": {"in_reply_to_tweet_id": "1455953449422516226", "exclude_reply_user_ids": ["6253282"]}}
 */
data class TweetReply(
    val in_reply_to_tweet_id: String,
    val exclude_reply_user_ids: List<String>? = null,
)

data class TweetMessageResponse(
    val data: TweetMessageData,
)
data class TweetMessageData(
    val id: String,
    val text: String,
)

data class TweetDeleteResponse(
    val data: TweetDeleteData,
)
data class TweetDeleteData(
    val deleted: Boolean,
)