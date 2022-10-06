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
#Amazon SDK rules
-keepclassmembers enum com.amazonaws.** { *; }
-keepnames class com.amazonaws.**
-keep class com.amazonaws.services.**.*Handler
-dontwarn org.apache.http.**
-dontwarn com.amazonaws.http.**
-dontwarn com.amazonaws.metrics.**
#end amazon sdk rules
#--------------- Gson ----------
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

-keep class com.enflash.mobile.storeapp.main.config.models.** { *; }
-keep class com.enflash.mobile.storeapp.mqtt.model.** { *; }
-keep class com.enflash.mobile.storeapp.ordenes.data.** { *; }
-keep class com.enflash.mobile.storeapp.main.model.** { *; }
-keep class com.enflash.mobile.storeapp.main.enums.** { *; }
-keep class com.enflash.mobile.storeapp.ordenes.enums.** { *; }

-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Room
-keep class * extends androidx.room.RoomDatabase

-keep class android.a.a.** {
   *;
}

-keepclassmembers class android.a.a.** {
   *;
}

-keep class android.misc.** {
   *;
}

-keepclassmembers class android.misc.** {
   *;
}

-keep class android.newland.** {
   *;
}

-keepclassmembers class android.newland.** {
   *;
}

-keep class android.psam.** {
   *;
}

-keepclassmembers class android.psam.** {
   *;
}



# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}


# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>