# kotlin-script-toolbox

Kotlin Script Toolbox is a library for handling common operations with Kotlin Script - designed to run on GitHub Actions and locally.

## How to set up

Add this in your `build.gradle.ktx` file:
```kotlin
// all modules (Basic utils + gson + telegram + ...) + `ZeroSetupScope()`
implementation("com.github.omarmiatello.kotlin-script-toolbox:zero-setup:0.1.0")
```

Alternative, only basic support:
```kotlin
// Basic utils + kotlinx.coroutines
implementation("com.github.omarmiatello.kotlin-script-toolbox:core:0.1.0")
// core + gson utils
implementation("com.github.omarmiatello.kotlin-script-toolbox:gson:0.1.0")
// core + telegram client
implementation("com.github.omarmiatello.kotlin-script-toolbox:telegram:0.1.0")
// core + twitter client
implementation("com.github.omarmiatello.kotlin-script-toolbox:twitter:0.1.0")
```

## How to use (by examples)
See [Main.kt](example/src/main/kotlin/com/github/omarmiatello/kotlinscripttoolbox/example/Main.kt)
to launch all examples.

### Read System Property (available in `core` and `zero-setup`)

```kotlin
launchKotlinScriptToolbox(
    scope = BaseScope.fromDefaults(),
    scriptName = "Test read System Property",
) {
    // system env or 'local.properties'
    val secretData = readSystemProperty("SECRET_DATA")
    val secretData2 = readSystemPropertyOrNull("SECRET_DATA_OR_NULL")
    println("System Property 'SECRET_DATA': $secretData")
}
```

Result (see [local.properties](local.properties)):
```
🏁 Test read System Property - Start!
System Property 'SECRET_DATA': this is only for local env. NOTE: this file (local.properties) should be added to .gitignore
🎉 Test read System Property - Completed in 27ms
```

### Write/read file with text (available in `core` and `zero-setup`)
```kotlin
launchKotlinScriptToolbox(
    scope = BaseScope.fromDefaults(filepathPrefix = "example-data/"),
    scriptName = "Test write/read file with text",
) {
    // files: write text
    writeText("test1.txt", "Ciao")

    // files: read text
    println("test1.txt: ${readTextOrNull("test1.txt")}")
}
```

Result:
```
🏁 Test write/read file with text - Start!
test1.txt: Ciao
🎉 Test write/read file with text - Completed in 3ms
```

### Write/read file with objects (serialized as json) (available in `zero-setup`)
```kotlin
launchKotlinScriptToolbox(
    scope = BaseScope.fromDefaults(filepathPrefix = "example-data/"),
    scriptName = "Test write/read file with objects (serialized as json)",
) {
    data class MyExample(val p1: String, val p2: Int? = null)

    // files: write json objects
    writeJson("test2-a.json", MyExample(p1 = "test1"))
    writeJson("test2-b.json", MyExample(p1 = "test2", p2 = 3))

    // files: read json objects
    println("test2-a.json to object: ${readJsonOrNull<MyExample>("test2-a.json")}")
    println("test2-b.json to object: ${readJsonOrNull<MyExample>("test2-b.json")}")
}
```

Result:
```
🏁 Test write/read file with objects (serialized as json) - Start!
test2-a.json to object: MyExample(p1=test1, p2=null)
test2-b.json to object: MyExample(p1=test2, p2=3)
🎉 Test write/read file with objects (serialized as json) - Completed in 42ms
```

### Send Telegram message (available in `telegram` and `zero-setup`)
```kotlin
launchKotlinScriptToolbox(
    scope = TelegramScope.from(apiKey = "my api key", defaultChatIds = listOf("123321")),
    scriptName = "Test Telegram messages",
) {
    sendTelegramMessage("My message")
}
```

### Send Twitter message (tweet) (available in `twitter` and `zero-setup`)
```kotlin
launchKotlinScriptToolbox(
    scope = TwitterScope.fromDefaults(baseScope = BaseScope.fromDefaults()),
    scriptName = "Test Twitter messages",
) {
    sendTweet("My tweet")
}
```

### Set up of multiple scopes (available in `zero-setup`)
```kotlin
val baseScope = object : BaseScope by BaseScope.fromDefaults() {}
launchKotlinScriptToolbox(
    scope = object : BaseScope by baseScope,
        TelegramScope by TelegramScope.fromDefaults(baseScope),
        TwitterScope by TwitterScope.fromDefaults(baseScope) {},
) {
    sendTelegramMessage("My message")
    sendTelegramMessages(listOf("My message 1", "My message 2"))
    sendTweet("My tweet")
    sendTweets(listOf("My tweet 1", "My tweet 2"))
}
```

### Set up of multiple scopes with `ZeroSetupScope` (available in `zero-setup`)
```kotlin
launchKotlinScriptToolbox(scope = ZeroSetupScope()) {
    sendTelegramMessage("My message")
    sendTelegramMessages(listOf("My message 1", "My message 2"))
    sendTweet("My tweet")
    sendTweets(listOf("My tweet 1", "My tweet 2"))
}
```
