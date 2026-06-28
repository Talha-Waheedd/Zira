# ==========================================================================
# Zira — ProGuard / R8 rules
# ==========================================================================

# --- Keep line numbers for readable crash stack traces ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Needed so Gson/Retrofit can read generic types and annotations at runtime
-keepattributes Signature,*Annotation*,EnclosingMethod,InnerClasses

# ==========================================================================
# Retrofit
# ==========================================================================
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Retrofit does reflection on generic parameters
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# ==========================================================================
# OkHttp
# ==========================================================================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**

# ==========================================================================
# Gson
# ==========================================================================
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
# Keep fields annotated with @SerializedName from being renamed
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ==========================================================================
# Zira data models (serialized via Gson / deserialized from Firestore)
# ==========================================================================
-keep class com.zira.app.data.remote.model.** { *; }
-keep class com.zira.app.data.model.** { *; }
-keep class com.zira.app.data.local.entity.** { *; }

# ==========================================================================
# Room
# ==========================================================================
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# ==========================================================================
# Firebase / Firestore
# ==========================================================================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
# Firestore uses reflection to (de)serialize model classes via no-arg constructors
-keepclassmembers class com.zira.app.data.model.** {
    <init>();
    <fields>;
}

# ==========================================================================
# MPAndroidChart
# ==========================================================================
-keep class com.github.mikephil.charting.** { *; }
-dontwarn com.github.mikephil.charting.**

# ==========================================================================
# Lottie
# ==========================================================================
-dontwarn com.airbnb.lottie.**
