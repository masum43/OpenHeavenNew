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
-keepattributes SourceFile,LineNumberTable
#===================================================================================================

-keepattributes EnclosingMethod
-keepattributes InnerClasses
#===================================================================================================

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
#===================================================================================================

-keep class androidx.appcompat.widget.SearchView { *; }
-keep class android.support.v7.widget.SearchView { *; }
#-keep class android.widget.TextView { *; }
#===================================================================================================

# For OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#===================================================================================================

# For Picasso
-dontwarn com.squareup.okhttp.**
#===================================================================================================

# For JSoup
-keep public class org.jsoup.** {public *;}
-keep class org.jsoup.** { *; }
-keepnames class org.jsoup.nodes.Entities
-keeppackagenames org.jsoup.nodes

#===================================================================================================

# For Crashlytics
#-keepattributes *Annotation*
#-keepattributes SourceFile,LineNumberTable
#-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

#===================================================================================================

# For OneSignal
# Ref. https://github.com/OneSignal/OneSignal-Android-SDK/issues/533
# -keep class android.support.v4.view.MenuCompat
# -keep class android.support.v4.content.WakefulBroadcastReceiver
# -keep class android.support.v4.app.NotificationManagerCompat
# -keep class android.support.v4.app.JobIntentService
# -keep class com.google.firebase.messaging.FirebaseMessaging

#===================================================================================================

# FACEBOOK AUDIENCE NETWORK - START
#-keep class com.facebook.** { *; }

-keep public class com.facebook.ads.** { public *; }
-keep class com.facebook.ads.** { *; }
-dontwarn com.facebook.ads.**
-keep class com.google.ads.mediation.facebook.FacebookAdapter { *; }
-dontwarn com.facebook.ads.internal.**
# FACEBOOK AUDIENCE NETWORK - END

#===================================================================================================

# MoPub Proguard Config
# NOTE: You should also include the Android Proguard config found with the build tools:
# $ANDROID_HOME/tools/proguard/proguard-android.txt

# Keep public classes and methods.
-keepclassmembers class com.mopub.** { public *; }
-keep public class com.mopub.**
-keep public class android.webkit.JavascriptInterface {}

# Explicitly keep any custom event classes in any package.
-keep class * extends com.mopub.mobileads.CustomEventBanner {}
-keep class * extends com.mopub.mobileads.CustomEventInterstitial {}
-keep class * extends com.mopub.mobileads.CustomEventRewardedAd {}
-keep class * extends com.mopub.nativeads.CustomEventNative {}

# Keep methods that are accessed via reflection
-keepclassmembers class ** { @com.mopub.common.util.ReflectionTarget *; }

# Support for Android Advertiser ID.
-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}

# Support for Google Play Services
# http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
#===================================================================================================