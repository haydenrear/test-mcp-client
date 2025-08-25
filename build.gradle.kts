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
    implementation(project(":mcp-tool-gateway"))
    implementation(project(":tracing"))
    implementation(project(":graphql"))
    implementation(project(":commit-diff-model"))
    implementation("org.awaitility:awaitility:4.2.0")
}

tasks.bootJar {
    archiveFileName = "test-mcp-server.jar"
    enabled = true
}

tasks.register<Copy>("copyToolGateway") {
    dependsOn(project(":mcp-tool-gateway").tasks.named("bootJar"))
    val sourcePaths = file(project(":mcp-tool-gateway").layout.buildDirectory).resolve("libs/mcp-tool-gateway.jar")
    from(sourcePaths)
    into(file(layout.buildDirectory).resolve("libs"))
    // Optionally rename it to a fixed name
    rename { "mcp-tool-gateway.jar" }
}

tasks.compileJava {
    dependsOn("copyToolGateway")
}
tasks.test {
    dependsOn("copyToolGateway")
}
