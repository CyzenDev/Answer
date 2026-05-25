#Made by Cyzen


#Apache POI
-dontwarn java.awt.**
-dontwarn javax.xml.stream.**
-dontwarn net.sf.saxon.**

-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**

-keep class org.openxmlformats.** { *; }
-dontwarn org.openxmlformats.**

-keep class org.apache.logging.** { *; }
-dontwarn org.apache.logging.**

-keep class org.apache.commons.** { *; }
-dontwarn org.apache.commons.**


#抛出异常时保留代码行号，在异常分析中可以方便定位
#-keepattributes SourceFile,LineNumberTable

#去除SourceFile名称
-keepattributes SourceFile
-renamesourcefileattribute

#替换包名
-repackageclasses

-optimizations !code/simplification/cast,!field/,!class/merging/
-optimizationpasses 5
-allowaccessmodification
-overloadaggressively
-verbose

#Keep Annotation
-keepattributes RuntimeVisible*Annotations

#避免混淆泛型
-keepattributes Signature

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}

-assumenosideeffects class java.util.Objects {
    public static ** requireNonNull(...);
}