plugins {
    java
}

group = "net.azisaba"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://nexus.velocitypowered.com/repository/maven-public/") }
    maven { url = uri("https://jitpack.io/") }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    compileOnly("com.github.AzisabaNetwork:VelocityRedisBridge:1.0.1")
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            include("**")

            val tokenReplacementMap = mapOf(
                "VERSION" to project.version,
            )

            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to tokenReplacementMap)
        }

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(projectDir) { include("LICENSE") }
    }
}
