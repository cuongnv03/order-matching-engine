plugins {
    java
    alias(libs.plugins.jmh)
}

dependencies {
    jmh(project(":engine"))
    jmh(libs.jmh.core)
    jmhAnnotationProcessor(libs.jmh.generator.annprocess)
}

jmh {
    fork.set(2)
    warmupIterations.set(5)
    iterations.set(10)
    timeOnIteration.set("2s")
    warmup.set("2s")
    resultFormat.set("JSON")
}
