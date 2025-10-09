# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# ------------------------------
# Keep UUID classes for kotlinx.serialization
# ------------------------------
-keep class kotlin.uuid.** { *; }
-keepclassmembers class kotlin.uuid.** { *; }

# ------------------------------
# Room
# ------------------------------
-keep class androidx.room.** { *; }
-keepclassmembers class androidx.room.** { *; }
-keepattributes *Annotation*

# ------------------------------
# Hilt / Dagger
# ------------------------------
-keep class dagger.** { *; }
-keep class hilt_*.** { *; }
-keep class androidx.hilt.** { *; }
-keepclassmembers class dagger.** { *; }
-keep class javax.inject.** { *; }

# ------------------------------
# Moshi
# ------------------------------
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }

# ------------------------------
# Glide
# ------------------------------
-keep class com.bumptech.glide.** { *; }
-keep interface com.bumptech.glide.** { *; }
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public class * extends com.bumptech.glide.LibraryGlideModule

# ------------------------------
# AndroidX / Jetpack
# ------------------------------
-keep class androidx.lifecycle.** { *; }
-keep class androidx.paging.** { *; }
-keep class androidx.room.** { *; }
-keep class androidx.work.** { *; }
-keep class androidx.fragment.** { *; }

# ------------------------------
# Optional: Keep line numbers for easier crash logs
# ------------------------------
-keepattributes SourceFile,LineNumberTable


# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn kotlin.uuid.ExperimentalUuidApi
-dontwarn kotlin.uuid.Uuid$Companion
-dontwarn kotlin.uuid.Uuid


# Additional performance rules
-optimizationpasses 5
-allowaccessmodification
-overloadaggressively
-repackageclasses ''

# Keep what's needed for performance
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}