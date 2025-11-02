# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Filament classes
-keep class com.google.android.filament.** { *; }

# Keep SceneView classes
-keep class io.github.sceneview.** { *; }

# Keep ARCore classes
-keep class com.google.ar.** { *; }

# Keep model classes
-keep class com.example.paamchssma.data.** { *; }
-keep class com.example.paamchssma.models.** { *; }

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keep class kotlin.coroutines.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Material Design
-keep class com.google.android.material.** { *; }

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
