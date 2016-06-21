# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-repackageclasses ''
-allowaccessmodification
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontwarn
-dontskipnonpubliclibraryclassmembers
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-libraryjars ../libs_mine/lib_base/libs/commons-codec-1.3.jar
-libraryjars ../libs_mine/lib_base/libs/event_bus.jar
-libraryjars ../libs_mine/lib_base/libs/gson-2.2.4.jar
-libraryjars ../libs_mine/lib_base/libs/libapshare.jar
-libraryjars ../libs_mine/lib_base/libs/MobLogCollector.jar
-libraryjars ../libs_mine/lib_base/libs/MobTools.jar
-libraryjars ../libs_mine/lib_base/libs/ShareSDK-Core-2.6.2.jar
-libraryjars ../libs_mine/lib_base/libs/ShareSDK-QQ-2.6.2.jar
-libraryjars ../libs_mine/lib_base/libs/ShareSDK-QZone-2.6.2.jar
-libraryjars ../libs_mine/lib_base/libs/ShareSDK-SinaWeibo-2.6.2.jar
-libraryjars ../libs_mine/lib_base/libs/ShareSDK-Wechat-2.6.2.jar
-libraryjars ../libs_mine/lib_base/libs/ShareSDK-Wechat-Core-2.6.2.jar
-libraryjars ../libs_mine/lib_base/libs/ShareSDK-Wechat-Moments-2.6.2.jar
-libraryjars ../libs_mine/lib_base/libs/volley.jar
-libraryjars ../libs_third/library_analysis/libs/mframework.jar
-libraryjars ../libs_third/library_indicator/libs/android-support-v4.jar

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class android.support.v4.** { *; }

##---------------Begin: ShareSDK  ----------
-keep class cn.sharesdk.**{ *; }
-keep class com.sina.**{ *; }
-keep class **.R$* { *; }
-keep class **.R{ *; }
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-keep class m.framework.** { *; }
-dontwarn m.framework.**
-dontwarn  com.mob.tools.**
-dontwarn com.viewpagerindicator.LinePageIndicator
##---------------End: ShareSDK  ----------

-dontwarn com.android.volley.**

##---------------Begin: proguard configuration for Gson  ----------
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; } 
##---------------End: proguard configuration for Gson  ----------

##---------------Begin: proguard configuration for easemob  ----------
-keep class org.apache.** { *; }
##---------------End: proguard configuration for easemob  ----------

##---------------Begin: proguard configuration for wechat  ----------
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage { *; }
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject { *; }
##---------------End: proguard configuration for wechat  ----------

-keep class uk.co.senab.photoview.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class com.wjwu.wpmain.uzwp.detail.FragmentDetailsCommon { *; }

-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

-keep public class * implements java.io.Serializable {*;}

