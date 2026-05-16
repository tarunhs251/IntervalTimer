# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

# For using GSON @Expose annotation
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# Keep the BuildConfig
-keep class com.example.intervaltimer.BuildConfig { *; }

# Keep all application classes
-keep class com.example.intervaltimer.** { *; }

# Keep all public classes
-keep public class * {
    public protected *;
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom application classes
-keep class com.example.intervaltimer.MainActivity { *; }
-keep class com.example.intervaltimer.viewmodel.TimerViewModel { *; }
-keep class com.example.intervaltimer.service.TimerService { *; }
-keep class com.example.intervaltimer.data.PreferencesManager { *; }

# Keep all Composable functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *** *(...);
}

# Keep ViewModel classes
-keep class androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep StateFlow and Flow classes
-keep class kotlinx.coroutines.flow.** { *; }

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Optimization settings
-optimizationpasses 5
-dontusemixedcaseclassnames

# Rename attributes
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
