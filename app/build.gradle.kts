plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.nba"
    compileSdk = 35

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true

            all { test ->
                test.useJUnitPlatform()

                // OBRIGATÓRIO para o JaCoCo capturar a cobertura de testes com Robolectric
                test.extensions.configure(JacocoTaskExtension::class.java) {
                    isIncludeNoLocationClasses = true
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        }
    }

    defaultConfig {
        applicationId = "com.example.nba"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BALLDONTLIE_API_KEY", "\"${project.findProperty("BALLDONTLIE_API_KEY") ?: ""}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testCoverage {
        jacocoVersion = libs.versions.jacoco.get()
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}


dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.test.core)
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    // Paging
    implementation(libs.paging.common)
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Retrofit / OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Unit tests
    testImplementation(libs.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.paging.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.room.testing)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.junit5)
    testImplementation(libs.junit.vintage)
    testImplementation(libs.robolectric)
    testImplementation(kotlin("test"))

    // Instrumented tests
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
}


tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<JacocoReport>("jacocoTestReport") {
    // Mantemos apenas testDebugUnitTest no dependsOn para não exigir um emulador aberto
    // toda vez que você rodar apenas os testes unitários.
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        // Android padrão & ViewBinding
        "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*",
        "**/*Test*.*", "android/**/*.*", "**/databinding/*",
        "**/*Binding.class", "**/*BindingImpl.class",

        // Hilt / Dagger
        "**/dagger/hilt/**", "**/hilt_aggregated_deps/**",
        "**/*_Hilt*.class", "**/Hilt_*.class", "**/Dagger*.class",
        "**/*_Factory.class", "**/*_Factory$*.class",
        "**/*_MembersInjector.class", "**/*_MembersInjector$*.class",
        "**/*Module_*.class", "**/*_Provide*Factory.class",
        "**/*_GeneratedInjector.class", "**/*_ComponentTreeDeps.class",

        // Room
        "**/*_Impl.class", "**/*_Impl$*.class",

        // Compose & Kotlin sintetizados
        "**/ComposableSingletons*.class",
        "**/*${'$'}Lambda${'$'}*.class",
        "**/*${'$'}inlined${'$'}*.class",
        "**/*${'$'}Companion.class",
        "**/*${'$'}WhenMappings.class",
        "**/*${'$'}DefaultImpls.class"
    )

    val buildDir = layout.buildDirectory.get()

    val kotlinClasses = fileTree("$buildDir/tmp/kotlin-classes/debug") { exclude(fileFilter) }
    val javaClasses = fileTree("$buildDir/intermediates/javac/debug/classes") { exclude(fileFilter) }
    val asmClasses = fileTree("$buildDir/intermediates/classes/debug/transformDebugClassesWithAsm/dirs") { exclude(fileFilter) }

    classDirectories.setFrom(
        if (asmClasses.files.isNotEmpty()) files(asmClasses)
        else files(kotlinClasses, javaClasses)
    )

    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    // Unifica os dados de execução dos testes unitários (.exec) + instrumentados (.ec)
    executionData.setFrom(fileTree(buildDir) {
        include(
            // 1. Testes unitários (src/test)
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
            "jacoco/testDebugUnitTest.exec",
            // 2. Testes instrumentados (src/androidTest - emulador/dispositivo)
            "outputs/code_coverage/debugAndroidTest/connected/**/*.ec"
        )
    })
}