# 🏛️ Navigare 3D Facultate - Aplicație Android

Aplicație Android în **Kotlin** pentru vizualizarea și explorarea interactivă a unui model 3D al unei facultăți, exportat din **Polycam**.

![Android](https://img.shields.io/badge/Android-7.0%2B-green)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-purple)
![License](https://img.shields.io/badge/License-Educational-blue)

---

## ✨ Caracteristici Principale

- 🎮 **Navigare Interactivă 3D** - Rotire, zoom, deplasare prin touch
- 📍 **Puncte de Interes (POI)** - 9 locații predefinite cu săgeți 3D
- 🔍 **Căutare Locații** - Bară de search cu navigare automată
- 🏢 **3 Etaje** - Schimbare între etaje cu filtrare POI-uri
- 📱 **Modul AR** - Suprapunere model în mediul real (ARCore)
- 🧭 **Control Senzori** - Giroscop și accelerometru pentru navigare
- 🎨 **UI Modern** - Material Design 3

---

## 🚀 Quick Start

### 1️⃣ Pregătește Modelul
```bash
# Exportă din Polycam ca .glb
# Redenumește în: facultate.glb
```

### 2️⃣ Adaugă în Proiect
```bash
app/src/main/assets/models/facultate.glb
```

### 3️⃣ Build & Run
```bash
# Windows
.\build_and_install.bat

# Linux/Mac
./build_and_install.sh

# SAU în Android Studio
Click pe ▶️ Run
```

### 4️⃣ Explorează!
- Swipe pentru rotire
- Pinch pentru zoom
- Caută locații în search bar
- Schimbă etaje cu E1/E2/E3

---

## 📚 Documentație

| Fișier | Descriere |
|--------|-----------|
| [📖 README_APP.md](README_APP.md) | Documentație tehnică completă |
| [📖 INSTRUCTIUNI_FOLOSIRE.md](INSTRUCTIUNI_FOLOSIRE.md) | Ghid detaliat de folosire |
| [📖 QUICK_START.md](QUICK_START.md) | Start rapid în 5 pași |
| [📖 COORDONATE_POI_EXEMPLE.md](COORDONATE_POI_EXEMPLE.md) | Ajustare coordonate POI |
| [📖 STRUCTURA_PROIECT.md](STRUCTURA_PROIECT.md) | Structura detaliată proiect |

---

## 🏗️ Arhitectură

```kotlin
PaamChsSma/
├── MainActivity.kt              // Activitate principală
├── controllers/
│   ├── CameraController.kt      // Control cameră 3D + senzori
│   └── ARController.kt          // Funcționalitate AR
├── models/
│   └── ModelLoader.kt           // Încărcare modele .glb
├── nodes/
│   └── POINode.kt               // Gestionare POI-uri
└── data/
    └── POIData.kt               // Date și repository POI
```

**~2000 linii de cod Kotlin** | **Modular** | **Bine documentat**

---

## 🎯 Funcționalități

### Navigare 3D
- ✅ Rotire cameră (drag)
- ✅ Zoom (pinch)
- ✅ Recentrare (double tap)
- ✅ Animații smooth

### POI-uri Predefinite
1. Secretariat (Etaj 1)
2. Decanat (Etaj 1)
3. Sala Profesori A (Etaj 1)
4. Laborator Informatică (Etaj 2)
5. Laborator Electronică (Etaj 2)
6. Sala C201 (Etaj 2)
7. Biblioteca (Etaj 3)
8. Amfiteatru A (Etaj 3)
9. Sala Profesori B (Etaj 3)

### Căutare & Navigare
- Căutare după nume/descriere
- Navigare automată cu animație
- Afișare info la click

### Modul AR (Opțional)
- Suport ARCore
- Suprapunere model în mediul real
- Control giroscop/accelerometru

---

## 🛠️ Tehnologii

| Categorie | Tehnologie |
|-----------|-----------|
| **Limbaj** | Kotlin |
| **3D Rendering** | SceneView 2.2.1 + Filament 1.51.5 |
| **AR** | ARCore 1.44.0 |
| **UI** | Material Design 3 + XML Layouts |
| **Async** | Kotlin Coroutines |
| **Build** | Gradle 8+ |

---

## 📋 Cerințe

### Dezvoltare
- **Android Studio**: Hedgehog (2023.1.1)+
- **JDK**: 11+
- **Gradle**: 8.0+

### Runtime
- **Android**: 7.0+ (API 24+)
- **RAM**: 2GB minimum
- **Spațiu**: 100MB
- **ARCore**: Opțional (pentru AR mode)

---

## 🎮 Controale

| Acțiune | Gestură |
|---------|---------|
| **Rotire cameră** | Swipe cu un deget |
| **Zoom In/Out** | Pinch cu două degete |
| **Recentrare** | Double tap |
| **Selectare POI** | Tap pe săgeată |
| **Căutare** | Tastează 3+ caractere |
| **Schimbare etaj** | Butoane E1/E2/E3 |
| **Toggle AR** | FAB cameră 📷 |

---

## 🐛 Troubleshooting

### Model nu se încarcă
```bash
✓ Verifică: app/src/main/assets/models/facultate.glb
✓ Rebuild: Build → Clean Project → Rebuild
```

### POI-uri nu apar
```bash
✓ Ajustează coordonate în POIData.kt
✓ Zoom out pentru a vedea mai mult
```

### AR nu funcționează
```bash
✓ Instalează ARCore Services din Play Store
✓ Acordă permisiune cameră
✓ Verifică suport ARCore: https://developers.google.com/ar/devices
```

### Performance issues
```bash
✓ Reduce dimensiunea modelului 3D
✓ Compresie texturi
✓ Folosește modele separate per etaj
```

---

## 🎓 Exemple de Cod

### Adaugă un POI nou

```kotlin
// În POIData.kt → POIRepository.getAllPOIs()
POIData(
    id = "laborator_nou",
    name = "Laborator Fizică",
    position = Float3(4.0f, 3.5f, -2.0f),
    description = "Laborator Fizică - Etaj 2",
    category = POICategory.LABORATOR
)
```

### Navigare la POI programatic

```kotlin
// În MainActivity.kt
val poi = POIRepository.searchPOI("laborator")
poi?.let {
    cameraController.moveCameraToPosition(
        targetPosition = it.position,
        distance = 3.0f,
        animated = true
    )
}
```

### Toggle AR Mode

```kotlin
// În MainActivity.kt
fabArMode.setOnClickListener {
    val isAREnabled = arController.toggleARMode()
    cameraController.sensorControlEnabled = isAREnabled
}
```

---

## 📦 Build și Deploy

### Debug Build
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Install pe Device
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔧 Personalizare

### Schimbă culori
```xml
<!-- res/values/colors.xml -->
<color name="purple_700">#FF3700B3</color>
```

### Modifică viteza camerei
```kotlin
// CameraController.kt
private const val ROTATION_SPEED = 0.3f  // Schimbă aici
```

### Adaugă categorii POI noi
```kotlin
// POIData.kt
enum class POICategory {
    SECRETARIAT,
    DECANAT,
    // ... adaugă aici
    NOUA_CATEGORIE
}
```

---

## 📊 Statistici Proiect

```
📝 Linii de cod:      ~2000
📁 Clase Kotlin:      8
📱 Layout-uri XML:    1
🎨 Resurse:           20+
📦 Dependințe:        10+
⏱️ Timp dezvoltare:  ~8-12 ore
```

---

## 🤝 Contribuții

Contribuțiile sunt binevenite!

1. Fork repository
2. Creează branch: `git checkout -b feature/nume-feature`
3. Commit: `git commit -m 'Adaugă feature'`
4. Push: `git push origin feature/nume-feature`
5. Creează Pull Request

---

## 📝 Licență

Acest proiect este pentru **uz educațional**.

Librăriile folosite:
- SceneView: Apache 2.0
- Filament: Apache 2.0
- ARCore: Google Terms of Service
- Material Design: Apache 2.0

---

## 📞 Suport

**Probleme?** Deschide un [Issue](../../issues)

**Întrebări?** Consultă [Documentația](README_APP.md)

---

## 🌟 Features Viitoare

- [ ] Rute între două puncte
- [ ] Mini-map 2D
- [ ] Audio guide
- [ ] Fotografii locații
- [ ] Salvare locații favorite
- [ ] Mod noapte pentru UI
- [ ] Suport multiple limbi

---

## 👨‍💻 Dezvoltat cu

- ❤️ **Pasiune pentru educație**
- ☕ **Multă cafea**
- 🎯 **Kotlin best practices**
- 📚 **Android modern architecture**

---

**Versiune:** 1.0.0  
**Ultima actualizare:** Noiembrie 2025  
**Status:** ✅ Funcțional și testat

---

<p align="center">
  <strong>Mulțumim că folosești această aplicație! 🚀</strong><br>
  Dacă îți place, dă-ne un ⭐ pe GitHub!
</p>

