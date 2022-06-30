package com.github.omarmiatello.kotlinscripttoolbox.zerosetup

import com.github.omarmiatello.kotlinscripttoolbox.core.BaseScope
import com.github.omarmiatello.kotlinscripttoolbox.telegram.TelegramScope
import com.github.omarmiatello.kotlinscripttoolbox.twitter.TwitterScope

open class ZeroSetupScope(
    baseScope: BaseScope = BaseScope.fromDefaults(),
    telegramScope: TelegramScope = TelegramScope.fromDefaults(baseScope),
    twitterScope: TwitterScope = TwitterScope.fromDefaults(baseScope),
) : BaseScope by baseScope,
    TelegramScope by telegramScope,
    TwitterScope by twitterScope
