package provider

import io.realm.model.LeadResultRealm
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

internal class LeadResultArgumentProvider : ArgumentsProvider {
        @Throws(Exception::class)
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
            return Stream.of(
                Arguments.of(
                    listOf(
                        LeadResultRealm().apply {
                            id = "1352"
                            year = 2000
                            competitionId = "321"
                            rank = null
                            climberId = "235-f"
                            qualification = "12"
                            semiFinal = null
                            final = null
                        }
                    )
                ),
                Arguments.of(
                    listOf(
                        LeadResultRealm().apply {
                            id = "1352"
                            year = 2000
                            competitionId = "321"
                            rank = null
                            climberId = "235-f"
                            qualification = "12"
                            semiFinal = null
                            final = null
                        },
                        LeadResultRealm().apply {
                            id = "1353"
                            year = 2020
                            competitionId = "321"
                            rank = 1
                            climberId = "235-f"
                            qualification = "12"
                            semiFinal = "13"
                            final = "11.1"
                        },
                        LeadResultRealm().apply {
                            id = "1356"
                            year = 2021
                            competitionId = "321"
                            rank = 2
                            climberId = "235-f"
                            qualification = "11"
                            semiFinal = "12"
                            final = "fall"
                        }
                    )
                ),
            )
        }
    }
