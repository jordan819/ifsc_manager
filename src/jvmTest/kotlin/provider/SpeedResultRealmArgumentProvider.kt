package provider

import io.realm.model.SpeedResultRealm
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import java.util.stream.Stream

internal class SpeedResultRealmArgumentProvider : ArgumentsProvider {
    @Throws(Exception::class)
    override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
        return Stream.of(
            Arguments.of(
                listOf(
                    SpeedResultRealm().apply {
                        id = "1352"
                        year = 2000
                        rank = null
                        climberId = "235-f"
                        laneA = null
                        laneB = null
                        oneEighth = null
                        quarter = null
                        semiFinal = null
                        smallFinal = null
                        final = null
                    },
                )
            ),
            Arguments.of(
                listOf(
                    SpeedResultRealm().apply {
                        id = "1352"
                        year = 2000
                        rank = null
                        climberId = "235-f"
                        laneA = null
                        laneB = null
                        oneEighth = null
                        quarter = null
                        semiFinal = null
                        smallFinal = null
                        final = null
                    },
                    SpeedResultRealm().apply {
                        id = "1352"
                        year = 2000
                        rank = null
                        climberId = "235-f"
                        laneA = null
                        laneB = null
                        oneEighth = null
                        quarter = null
                        semiFinal = null
                        smallFinal = null
                        final = null
                    },
                    SpeedResultRealm().apply {
                        id = "1352"
                        year = 2000
                        rank = null
                        climberId = "235-f"
                        laneA = null
                        laneB = null
                        oneEighth = null
                        quarter = null
                        semiFinal = null
                        smallFinal = null
                        final = null
                    },
                )
            ),
        )
    }
}
