package provider

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import scraping.model.Climber
import scraping.model.RecordType
import scraping.model.Sex
import java.util.stream.Stream

internal class ClimberArgumentProvider : ArgumentsProvider {
    @Throws(Exception::class)
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        return Stream.of(
            Arguments.of(
                Climber(
                    "123",
                    "John Doe",
                    "url",
                    null,
                    null,
                    "USA",
                    "USAC",
                    RecordType.UNOFFICIAL
                )
            ),
            Arguments.of(
                Climber(
                    "321",
                    "Arkadiusz Justyński",
                    "url",
                    Sex.MAN,
                    "1987",
                    "POL",
                    "Polska Federacja Wspinaczki Sportowej",
                    RecordType.UNOFFICIAL
                )
            ),
            Arguments.of(
                Climber(
                    "420",
                    "Fanny Gibert",
                    "url",
                    Sex.WOMAN,
                    "2000-07-14",
                    "FRA",
                    "Federation Française de la Montagne et de l'Escalade",
                    RecordType.OFFICIAL
                )
            ),
        )
    }
}
