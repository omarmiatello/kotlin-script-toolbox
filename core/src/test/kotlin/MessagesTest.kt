import com.github.omarmiatello.kotlinscripttoolbox.core.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class MessagesTest(private val params: Params) {

    @Test
    fun test() {
        with(params) {
            val actual = messages.toStrings(maxLength)
            println("$description (maxLength: $maxLength)")
            println(buildString {
                append("actual:\t\t")
                append(actual.joinToString("\", \"", "\"", "\""))
                append("\nexpected:\t")
                append(expected.joinToString("\", \"", "\"", "\""))
            })
            if (!ignoreLengthRule && actual.any { it.length > maxLength }) {
                error("Longer than $maxLength: ${actual.first { it.length > maxLength }}")
            }
            if (actual != expected) {
                error("Unexpected message!")
            }
        }
    }

    companion object {
        @JvmStatic
        @get:Parameterized.Parameters(name = "{0}")
        val dataForTest = data
    }
}

data class Params(
    val description: String,
    val messages: Messages,
    val maxLength: Int,
    val expected: List<String>,
    val ignoreLengthRule: Boolean = false,
) {
    override fun toString() = "$description (maxLength: $maxLength): $messages"
}

private val data = listOf(
    Params(
        description = "break space, 1 long text, limit 10",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi! How are you? Does it works fine? We should definitely try it!!!",
                    breakStrategy = BreakOnChar(' ')
                )
            }
        },
        maxLength = 10,
        expected = listOf("Hi! How", "are you?", "Does it", "works", "fine? We", "should", "definitely", "try it!!!"),
        ignoreLengthRule = true
    ),
    Params(
        description = "break space, 1 long text, limit 11",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi! How are you? Does it works fine? We should definitely try it!!!",
                    breakStrategy = BreakOnChar(' ')
                )
            }
        },
        maxLength = 11,
        expected = listOf("Hi! How are", "you? Does", "it works", "fine? We", "should", "definitely", "try it!!!"),
        ignoreLengthRule = true
    ),
    Params(
        description = "break space, joinLeafs space, 2 texts, limit 10",
        messages = buildMessages {
            addMessage(joinLeafs = " ") {
                text(message = "Hi!", breakStrategy = BreakOnChar(' '))
                text(message = "How are you?", breakStrategy = BreakOnChar(' '))
            }
        },
        maxLength = 10,
        expected = listOf("Hi! How", "are you?"),
        ignoreLengthRule = true
    ),
    Params(
        description = "break space, 2 short texts, limit 10",
        messages = buildMessages {
            addMessage {
                text(message = "Hi!")
                text(message = "How are you?", breakStrategy = BreakOnChar(' '))
            }
        },
        maxLength = 10,
        expected = listOf("Hi!How are", "you?"),
        ignoreLengthRule = true
    ),
    Params(
        description = "fallback skip, 1 text, limit 3",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi! How are you? Does it works fine? We should definitely try it!!!",
                    breakStrategy = BreakOnChar(' ', fallback = Skip)
                )
            }
        },
        maxLength = 3,
        expected = listOf(),
        ignoreLengthRule = true
    ),
    Params(
        description = "AppendIfRoom ok, 2 messages, limit 14",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi!",
                )
            }
            addMessage(appendIf = AppendIfRoom) {
                text(
                    message = "How are you?",
                    breakStrategy = BreakOnChar(' ')
                )
            }
        },
        maxLength = 14,
        expected = listOf("Hi!\nHow are", "you?"),
        ignoreLengthRule = true
    ),
    Params(
        description = "AppendIfRoom ko, 2 messages, limit 6",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi!",
                )
            }
            addMessage(appendIf = AppendIfRoom) {
                text(
                    message = "How are you?",
                    breakStrategy = BreakOnChar(' ')
                )
            }
        },
        maxLength = 6,
        expected = listOf("Hi!", "How", "are", "you?"),
        ignoreLengthRule = true
    ),
    Params(
        description = "AppendIfNotDivide ok, 2 messages, limit 16",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi!",
                )
            }
            addMessage(appendIf = AppendIfNotDivide) {
                text(
                    message = "How are you?",
                    breakStrategy = BreakOnChar(' ')
                )
            }
        },
        maxLength = 16,
        expected = listOf("Hi!\nHow are you?"),
        ignoreLengthRule = true
    ),
    Params(
        description = "AppendIfNotDivide ko, 2 messages, limit 14",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi!",
                )
            }
            addMessage(appendIf = AppendIfNotDivide) {
                text(
                    message = "How are you?",
                    breakStrategy = BreakOnChar(' ')
                )
            }
        },
        maxLength = 14,
        expected = listOf("Hi!", "How are you?"),
        ignoreLengthRule = true
    ),
    Params(
        description = "AppendNever, 2 messages, limit 16",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi!",
                )
            }
            addMessage(appendIf = AppendNever) {
                text(
                    message = "How are you?",
                    breakStrategy = BreakOnChar(' ')
                )
            }
        },
        maxLength = 16,
        expected = listOf("Hi!", "How are you?"),
        ignoreLengthRule = true
    ),
    Params(
        description = "Complex, mixed rules, 3 messages, limit 16",
        messages = buildMessages {
            addMessage {
                text(
                    message = "Hi!",
                )
            }
            addMessage(appendIf = AppendIfRoom, joinGroups = "--*---", joinLeafs = " ") {
                text(
                    message = "How are you?",
                    breakStrategy = BreakOnChar(' ')
                )
                text(
                    message = "I'm fine, thanks!",
                    breakStrategy = BreakOnChar(' ')
                )
            }
            addMessage(appendIf = AppendIfNotDivide, joinLeafs = "..") {
                text(
                    message = "Sure?",
                    breakStrategy = BreakOnLastContent(Split)
                )
                text(
                    message = "Yes",
                    breakStrategy = BreakOnLastContent(Split)
                )
                text(
                    message = "!",
                    breakStrategy = BreakOnLastContent(Split)
                )
            }
        },
        maxLength = 16,
        expected = listOf("Hi!--*---How are", "you? I'm fine,", "thanks!", "Sure?..Yes..!"),
        ignoreLengthRule = true
    ),
    Params(
        description = "Custom URL size, 3 messages, limit 12",
        messages = buildMessages {
            addMessage {
                text(message = "Hi!")
            }
            addMessage {
                text(
                    message = "My url is: ",
                )
                url(
                    url = "https://www.google.com",
                    messageSize = 1,
                )
            }
            addMessage {
                text(
                    message = "This is a test",
                    breakStrategy = BreakOnChar(' '),
                )
            }
        },
        maxLength = 12,
        expected = listOf("Hi!", "My url is: https://www.google.com", "This is a", "test"),
        ignoreLengthRule = true,
    ),
    Params(
        description = "AppendIfNotDivide, 6 messages, limit 45",
        messages = buildMessages {
            (1..6).forEach {
                addMessage(appendIf = AppendIfNotDivide) {
                    text(message = "Message $it")
                }
            }
        },
        maxLength = 45,
        expected = listOf("Message 1\nMessage 2\nMessage 3\nMessage 4", "Message 5\nMessage 6"),
    ),
    Params(
        description = "AppendNever, 6 messages, limit 45",
        messages = buildMessages {
            (1..6).forEach {
                addMessage(appendIf = AppendNever) {
                    text(message = "Message $it")
                }
            }
        },
        maxLength = 45,
        expected = listOf("Message 1", "Message 2", "Message 3", "Message 4", "Message 5", "Message 6"),
    ),
)