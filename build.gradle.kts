import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.4.21-2"
    id("io.realm.kotlin") version "0.10.0"
    id("org.jetbrains.dokka") version "1.7.20"
}

group = "pl.patrykzaucha"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.seleniumhq.selenium:selenium-java:2.41.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("io.realm.kotlin:library-base:0.10.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")

                // Decompose
                val decomposeVersion = "0.2.5"
                implementation("com.arkivanov.decompose:decompose-jvm:$decomposeVersion")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains-jvm:$decomposeVersion")

                // Navigation
                implementation("androidx.navigation:navigation-compose:2.5.3")

                // Logger
                api("com.ToxicBakery.logging:arbor-jvm:1.34.109")

                implementation("com.afollestad.material-dialogs:core:3.3.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "IFSC Manager"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.20")
}
