package provider

import io.realm.model.ClimberRealm
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import scraping.model.RecordType
import scraping.model.Sex
import java.util.stream.Stream

internal class ClimberRealmListArgumentProvider : ArgumentsProvider {
    @Throws(Exception::class)
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        return Stream.of(
            Arguments.of(
                listOf(
                    ClimberRealm().apply {
                        id = "123"
                        name = "John Doe"
                        sex = null
                        dateOfBirth = null
                        country = "USA"
                        federation = "USAC"
                        recordType = RecordType.UNOFFICIAL.name
                    }
                )
            ),
            Arguments.of(
                listOf(
                    ClimberRealm().apply {
                        id = "321"
                        name = "Arkadiusz Justyński"
                        sex = Sex.MAN.name
                        dateOfBirth = "1987"
                        country = "POL"
                        federation = "Polska Federacja Wspinaczki Sportowej"
                        recordType = RecordType.UNOFFICIAL.name
                    }
                )
            ),
            Arguments.of(
                listOf(
                    ClimberRealm().apply {
                        id = "420"
                        name = "Fanny Gibert"
                        sex = Sex.WOMAN.name
                        dateOfBirth = "2000-07-14"
                        country = "FRA"
                        federation = "Federation Française de la Montagne et de l'Escalade"
                        recordType = RecordType.OFFICIAL.name
                    }
                )
            ),
        )
    }
}
