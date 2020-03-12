# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Administrator\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If tk_wifi_you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-ignorewarnings

-keep class * extends **.View{*;}

#如果引用了v4或者v7包
-dontwarn android.support.**
-keep class android.support.**{*;}

#泛型
-keepattributes Signature

-keep public class **.R$*{
   public static final int *;
}

-keep class **$Properties


-keep class org.tkwebrtc.**{*;}
-keep class org.chromium.**{*;}
-keep class org.apache.**{*;}
-keep class org.xwalk.**{*;}

-keep class pl.droidsonroids.**{*;}
-keep class com.hitry.**{*;}
-keep class com.talkcloud.**{*;}
-keep class io.tksocket.**{*;}
-keep class io.socket.**{*;}
-keep class skin.support.**{*;}
-keep class net.hockeyapp.android.**{*;}

-keep class com.classroomsdk.**{*;}

-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-keepclassmembers class com.classroomsdk.JSWhitePadInterface{
  public *;
}


