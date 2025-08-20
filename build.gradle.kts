plugins {
    id("com.hayden.mcp")
    id("com.hayden.dgs-graphql")
    id("com.hayden.spring-app")
}

group = "com.hayden"
version = "1.0.0"

tasks.register("prepareKotlinBuildScriptModel") {}

dependencies {
    implementation(project(":utilitymodule"))
    implementation(project(":tracing"))
    implementation(project(":mcp-tool-gateway"))
    implementation(project(":graphql"))
}

tasks.register<Copy>("copyToolGateway") {
    dependsOn(project(":mcp-tool-gateway").tasks.named("bootJar"))
    val sourcePaths = file(project(":mcp-tool-gateway").layout.buildDirectory).resolve("libs/mcp-tool-gateway-1.0.0.jar")
    from(sourcePaths)
    into(file(layout.buildDirectory).resolve("libs"))
    // Optionally rename it to a fixed name
    rename { "mcp-tool-gateway.jar" }
}

tasks.generateJava {
    typeMapping = mutableMapOf(
        Pair("ServerByteArray", "com.hayden.testmcpclient.scalar.ByteArray"),
        Pair("Float32Array", "com.hayden.testmcpclient.scalar.FloatArray"),
    )
}

tasks.test {
    dependsOn("copyToolGateway")
}
