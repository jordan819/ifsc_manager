package provider

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import scraping.model.speed.SpeedResult
import java.util.stream.Stream

internal class SpeedResultArgumentProvider : ArgumentsProvider {
    @Throws(Exception::class)
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        return Stream.of(
            Arguments.of(
                listOf(
                    SpeedResult(
                        null,
                        "123",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                    ),
                )
            ),
            Arguments.of(
                listOf(
                    SpeedResult(
                        null,
                        "123",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                    ),
                    SpeedResult(
                        10,
                        "123",
                        "12",
                        null,
                        "11",
                        "10.6",
                        "10",
                        "11",
                        null,
                    ),
                    SpeedResult(
                        1,
                        "123",
                        "10",
                        "12",
                        "11",
                        "11.2",
                        "12.1",
                        "10.5",
                        "11.1",
                    ),
                )
            ),
        )
    }
}
