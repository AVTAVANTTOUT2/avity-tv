# Avity TV ProGuard rules
# WebView ne doit pas être obfusqué
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Conserver les classes de l'application
-keep class fr.avity.tv.** { *; }
