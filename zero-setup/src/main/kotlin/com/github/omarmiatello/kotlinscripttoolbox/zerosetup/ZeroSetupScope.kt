package com.github.omarmiatello.kotlinscripttoolbox.zerosetup

import com.github.omarmiatello.kotlinscripttoolbox.core.BaseScope
import com.github.omarmiatello.kotlinscripttoolbox.telegram.TelegramScope
import com.github.omarmiatello.kotlinscripttoolbox.twitter.TwitterScope

open class ZeroSetupScope(
    baseScope: BaseScope = BaseScope.from(),
    telegramScope: TelegramScope = TelegramScope.from(baseScope),
    twitterScope: TwitterScope = TwitterScope.from(baseScope),
) : BaseScope by baseScope,
    TelegramScope by telegramScope,
    TwitterScope by twitterScope
