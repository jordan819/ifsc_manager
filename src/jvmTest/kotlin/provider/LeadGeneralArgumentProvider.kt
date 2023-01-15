package provider

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import scraping.model.lead.LeadGeneral
import java.util.stream.Stream

internal class LeadGeneralArgumentProvider : ArgumentsProvider {
    @Throws(Exception::class)
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        return Stream.of(
            Arguments.of(
                listOf(
                    LeadGeneral(
                        null,
                        "123",
                        qualification = "12",
                        semiFinal = null,
                        final = null,
                    )
                )
            ),
            Arguments.of(
                listOf(
                    LeadGeneral(
                        null,
                        "123",
                        qualification = "12",
                        semiFinal = null,
                        final = null,
                    ),
                    LeadGeneral(
                        5,
                        "42",
                        qualification = "12",
                        semiFinal = "13",
                        final = null,
                    ),
                    LeadGeneral(
                        2,
                        "32-M",
                        qualification = "12",
                        semiFinal = "11",
                        final = "fall",
                    ),
                )
            ),
        )
    }
}
