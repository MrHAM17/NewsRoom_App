// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.test) apply false

    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false

//    alias(libs.plugins.baselineprofile) apply false // <- Baseline profile definition with version
//    id("com.android.tools.build.baselineprofile") apply false
    id("androidx.baselineprofile") version "1.4.1" apply false


//    id 'androidx.navigation.safeargs.kotlin' version '2.7.7' apply false // ✅ add this

    //

//    task("clean", Delete::class) {
//        delete(rootProject.buildDir)
//    }

}
