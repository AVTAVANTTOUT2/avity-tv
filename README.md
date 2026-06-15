# Avity TV — Android TV Wrapper

Application Android TV / Google TV wrapper pour **tv.avity.fr**.
APK installable sur Google TV via ADB ou clé USB.

## Fichier APK

```
AvityTV.apk  (2.0 MB, debug)
```

## Installation

### Via ADB (recommandé)

```bash
adb connect <IP_DE_LA_TV>:5555
adb install AvityTV.apk
```

### Via clé USB

1. Copier `AvityTV.apk` sur une clé USB
2. Insérer la clé dans la Google TV
3. Ouvrir un explorateur de fichiers (ex: FX File Explorer)
4. Naviguer vers la clé USB et installer l'APK

## Fonctionnalités

| Fonction | Détail |
|----------|--------|
| URL | `https://tv.avity.fr` |
| User-Agent | `AvityTV/1.0` |
| JavaScript | Activé |
| DOM Storage | Activé |
| Mode plein écran | Immersif (pas de barres système) |
| Orientation | Paysage forcé |
| Télécommande D-pad | Flèche gauche / Back → retour page, Menu → recharger |
| Échelle | 60% (zoom réduit pour meilleure lisibilité sur TV) |
| Page d'erreur | Affichée si le site est inaccessible |

## Structure du projet

```
cinep/
├── app/
│   ├── build.gradle.kts          # Configuration module app
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml   # LEANBACK_LAUNCHER, permissions
│       ├── kotlin/fr/avity/tv/
│       │   └── MainActivity.kt   # WebView fullscreen + navigation D-pad
│       └── res/
│           ├── drawable/
│           │   └── banner.png    # Bannière TV 320×180
│           ├── layout/
│           │   └── activity_main.xml
│           ├── mipmap-*/         # Icônes de lancement
│           └── values/
│               ├── strings.xml
│               └── styles.xml
├── build.gradle.kts              # Configuration projet racine
├── settings.gradle.kts
├── gradle.properties
├── local.properties              # SDK path
└── AvityTV.apk                   # APK généré
```

## Build

Prérequis :
- JDK 17 (`brew install openjdk@17`)
- Android SDK dans `~/Library/Android/sdk`
- Gradle (via le wrapper inclus)

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
./gradlew clean assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk AvityTV.apk
```

## Technique

- **Langage** : Kotlin
- **minSdk** : 21 (Android 5.0)
- **targetSdk** : 34 (Android 14)
- **Package** : `fr.avity.tv`
- **Pas de dépendance Leanback lourde** — seul `androidx.core:core-ktx` est utilisé
- **Thème** : `@android:style/Theme.NoTitleBar.Fullscreen` avec fond noir

---

Créé le 14 juin 2026.
