-keepattributes *Annotation*
-keepnames class com.pla.agen.data.local.entity.** { *; }
-keepnames class com.pla.agen.data.remote.** { *; }
-keepclassmembers class com.pla.agen.data.local.entity.** {
    <init>(...);
}
-keepclassmembers class com.pla.agen.data.remote.** {
    <init>(...);
}
-dontwarn retrofit2.**
