package provider

import io.realm.model.BoulderResultRealm
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import scraping.model.boulder.BoulderGeneral
import java.util.stream.Stream

internal class BoulderGeneralArgumentProvider : ArgumentsProvider {
    @Throws(Exception::class)
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        return Stream.of(
            Arguments.of(
                listOf(
                    BoulderGeneral(
                        rank = null,
                        climberId = "123",
                        qualification = "12",
                        semiFinal = null,
                        final = null,
                    ),
                )
            ),
            Arguments.of(
                listOf(
                    BoulderGeneral(
                        rank = null,
                        climberId = "123",
                        qualification = "12",
                        semiFinal = null,
                        final = null,
                    ),
                    BoulderGeneral(
                        rank = 1,
                        climberId = "123-M",
                        qualification = "12",
                        semiFinal = "10",
                        final = "10.5",
                    ),
                    BoulderGeneral(
                        rank = 3,
                        climberId = "123",
                        qualification = "12",
                        semiFinal = "11",
                        final = null,
                    ),
                )
            ),
        )
    }
}
