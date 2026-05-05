plugins {
    java
    application
}

dependencies {
    implementation(libs.bundles.jackson)
    implementation(libs.java.websocket)
    implementation(libs.bundles.logging)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.platform.launcher)
}

application {
    mainClass.set("com.cuong.ome.Main")
}
