# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }

# Keep OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Keep Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep POI
-dontwarn org.apache.poi.**
-dontwarn org.apache.xmlbeans.**
-dontwarn org.apache.commons.**
-dontwarn org.openxmlformats.**
-dontwarn org.apache.logging.log4j.**
-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn com.graphbuilder.**
-dontwarn org.slf4j.**
-keep class org.apache.poi.** { *; }
-keep class org.apache.xmlbeans.** { *; }
-keep class org.openxmlformats.** { *; }
-keep class org.apache.commons.** { *; }

# Keep iText
-dontwarn com.itextpdf.**
-keep class com.itextpdf.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

