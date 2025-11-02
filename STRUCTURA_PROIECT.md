# 📂 Structura Completă a Proiectului

## Arborele de Fișiere

```
PaamChsSma/
│
├── 📄 build.gradle.kts                    # Configurare Gradle (project-level)
├── 📄 settings.gradle.kts                 # Settings Gradle
├── 📄 .gitignore                          # Git ignore rules
│
├── 📖 README_APP.md                       # Documentație tehnică completă
├── 📖 INSTRUCTIUNI_FOLOSIRE.md            # Ghid detaliat de folosire
├── 📖 QUICK_START.md                      # Start rapid în 5 pași
├── 📖 COORDONATE_POI_EXEMPLE.md           # Ghid ajustare coordonate POI
├── 📖 STRUCTURA_PROIECT.md                # Acest fișier
│
├── 🔧 build_and_install.bat               # Script build Windows
├── 🔧 build_and_install.sh                # Script build Linux/Mac
│
└── app/
    │
    ├── 📄 build.gradle.kts                # Configurare Gradle (app-level)
    ├── 📄 proguard-rules.pro              # Reguli ProGuard
    │
    └── src/
        │
        └── main/
            │
            ├── 📄 AndroidManifest.xml     # Manifest aplicație
            │
            ├── java/com/example/paamchssma/
            │   │
            │   ├── 📱 MainActivity.kt                    [PRINCIPAL]
            │   │   └── Activitatea principală
            │   │       ├── Inițializare SceneView
            │   │       ├── Gestionare UI
            │   │       ├── Încărcare model 3D
            │   │       └── Coordonare controllere
            │   │
            │   ├── 📁 controllers/
            │   │   │
            │   │   ├── 🎮 CameraController.kt            [CORE]
            │   │   │   └── Control cameră 3D
            │   │   │       ├── Touch gestures (rotire, zoom, pan)
            │   │   │       ├── Senzori (giroscop, accelerometru)
            │   │   │       ├── Animații smooth
            │   │   │       └── Navigare către POI
            │   │   │
            │   │   └── 📷 ARController.kt                [AR]
            │   │       └── Modul Augmented Reality
            │   │           ├── Verificare suport ARCore
            │   │           ├── Gestionare permisiuni cameră
            │   │           ├── Inițializare sesiune AR
            │   │           └── Toggle AR mode
            │   │
            │   ├── 📁 models/
            │   │   │
            │   │   └── 📦 ModelLoader.kt                 [LOADER]
            │   │       └── Încărcare modele 3D
            │   │           ├── Load .glb din assets
            │   │           ├── Suport multiple modele (etaje)
            │   │           ├── Marker-e pentru POI
            │   │           └── Gestionare erori
            │   │
            │   ├── 📁 nodes/
            │   │   │
            │   │   └── 📍 POINode.kt                     [POI]
            │   │       └── Gestionare puncte de interes
            │   │           ├── POINode (class)
            │   │           │   ├── Afișare marker 3D
            │   │           │   ├── Handling click events
            │   │           │   ├── Animații highlight
            │   │           │   └── Culori per categorie
            │   │           │
            │   │           └── POIManager (class)
            │   │               ├── Adăugare multiple POI-uri
            │   │               ├── Filtrare pe etaj
            │   │               ├── Căutare POI
            │   │               └── Cleanup resurse
            │   │
            │   ├── 📁 data/
            │   │   │
            │   │   └── 📊 POIData.kt                     [DATA]
            │   │       └── Date și modele
            │   │           ├── POIData (data class)
            │   │           │   ├── id, name, position
            │   │           │   ├── description
            │   │           │   └── category
            │   │           │
            │   │           ├── POICategory (enum)
            │   │           │   └── 8 categorii cu culori
            │   │           │
            │   │           └── POIRepository (object)
            │   │               ├── getAllPOIs() - 9 POI predefinite
            │   │               ├── searchPOI(query)
            │   │               └── getPOIsForFloor(floor)
            │   │
            │   └── 📁 ui/theme/
            │       ├── Color.kt
            │       ├── Theme.kt
            │       └── Type.kt
            │
            ├── res/
            │   │
            │   ├── 📁 layout/
            │   │   └── activity_main.xml              [UI LAYOUT]
            │   │       ├── SceneView (3D viewer)
            │   │       ├── Toolbar (titlu)
            │   │       ├── SearchCard (bară căutare)
            │   │       ├── Floor buttons (E1, E2, E3)
            │   │       ├── FAB AR mode
            │   │       ├── FAB recenter
            │   │       ├── ProgressBar (loading)
            │   │       └── TextView (messages)
            │   │
            │   ├── 📁 values/
            │   │   ├── strings.xml                    [STRINGS]
            │   │   ├── colors.xml                     [COLORS]
            │   │   └── themes.xml                     [THEMES]
            │   │
            │   ├── 📁 drawable/                       [ICONS]
            │   └── 📁 mipmap-*/                       [APP ICONS]
            │
            └── assets/
                │
                └── 📁 models/                         [3D MODELS]
                    ├── 📄 README.md                   Instrucțiuni modele
                    ├── 📄 .gitignore                  Ignore .glb files
                    │
                    ├── 🏛️ facultate.glb               ⚠️ ADAUGĂ TU
                    │                                  Model principal
                    │
                    ├── 🏛️ facultate_etaj1.glb         (opțional)
                    ├── 🏛️ facultate_etaj2.glb         (opțional)
                    ├── 🏛️ facultate_etaj3.glb         (opțional)
                    │
                    └── ➡️ arrow.glb                   (opțional)
                                                       Model săgeată
```

---

## 📋 Responsabilități Clase

### 🎯 MainActivity.kt (400+ linii)
**Rolul:** Orchestrator principal

- ✅ Inițializare toate componentele
- ✅ Gestionare lifecycle (onCreate, onResume, onDestroy)
- ✅ Setup UI listeners
- ✅ Încărcare model 3D principal
- ✅ Încărcare POI markers
- ✅ Handling search
- ✅ Schimbare etaje
- ✅ Toggle AR mode
- ✅ Afișare loading/messages

**Dependințe:**
- CameraController
- ARController
- ModelLoader
- POIManager

---

### 🎮 CameraController.kt (300+ linii)
**Rolul:** Control cameră 3D

- ✅ Touch gestures (drag, pinch, double-tap)
- ✅ Calcul poziție cameră (sferă virtuală)
- ✅ Animații smooth tranziție
- ✅ Navigare către POI
- ✅ Integrare giroscop
- ✅ Integrare accelerometru
- ✅ Recentrare cameră

**API Cheie:**
```kotlin
fun onTouchEvent(event: MotionEvent)
fun moveCameraToPosition(targetPosition: Float3, distance: Float, animated: Boolean)
fun recenterCamera()
var sensorControlEnabled: Boolean
```

---

### 📷 ARController.kt (200+ linii)
**Rolul:** Funcționalitate AR

- ✅ Verificare suport ARCore
- ✅ Request permisiuni cameră
- ✅ Inițializare AR session
- ✅ Toggle AR mode
- ✅ Gestionare lifecycle AR

**API Cheie:**
```kotlin
fun isARSupported(): Boolean
fun initializeARSession(): Boolean
fun toggleARMode(): Boolean
fun cleanup()
```

---

### 📦 ModelLoader.kt (150+ linii)
**Rolul:** Încărcare modele 3D

- ✅ Load .glb din assets
- ✅ Verificare existență fișier
- ✅ Load modele etaje separate
- ✅ Load marker pentru POI
- ✅ Fallback marker implicit
- ✅ Gestionare erori

**API Cheie:**
```kotlin
suspend fun loadModel(modelPath: String): ModelNode?
suspend fun loadMarkerModel(): ModelNode?
suspend fun loadFloorModels(floorNumber: Int): ModelNode?
```

---

### 📍 POINode.kt (250+ linii)
**Rolul:** Gestionare puncte de interes

**POINode (clasa):**
- ✅ Reprezentare vizuală POI
- ✅ Handling click events
- ✅ Animații highlight
- ✅ Culori categorii
- ✅ Show/Hide

**POIManager (clasa):**
- ✅ Adaugă multiple POI-uri
- ✅ Eliminare POI-uri
- ✅ Căutare POI după ID
- ✅ Filtrare pe etaj
- ✅ Cleanup resurse

**API Cheie:**
```kotlin
// POINode
fun handleClick()
fun setVisible(visible: Boolean)

// POIManager
fun addPOIs(poiDataList: List<POIData>, markerModel: ModelNode?, onPOIClicked: (POIData) -> Unit)
fun filterByFloor(floor: Int)
fun removeAllPOIs()
```

---

### 📊 POIData.kt (100+ linii)
**Rolul:** Date și modele

**POIData (data class):**
```kotlin
data class POIData(
    val id: String,
    val name: String,
    val position: Float3,
    val description: String,
    val category: POICategory
)
```

**POICategory (enum):**
- 8 categorii cu culori asociate

**POIRepository (object):**
- 9 POI-uri predefinite
- Funcții search și filtrare

---

## 🎨 UI Components

### Layout Principal (activity_main.xml)

```xml
ConstraintLayout
├── SceneView (3D viewer)           → Full screen
├── MaterialToolbar                 → Top bar
├── SearchCard                      → Below toolbar
│   └── EditText + Icon
├── LinearLayout (Floor buttons)    → Right side
│   ├── Button E3
│   ├── Button E2
│   └── Button E1
├── FAB (AR mode)                   → Bottom-right
├── FAB (Recenter)                  → Bottom-left
├── ProgressBar (Loading)           → Center
└── TextView (Messages)             → Center
```

---

## 🔄 Flow Principal Aplicație

```
1. START (MainActivity.onCreate)
   │
   ├─> Inițializare Views
   ├─> Inițializare Controllers
   │   ├─> CameraController
   │   ├─> ARController
   │   ├─> ModelLoader
   │   └─> POIManager
   │
   ├─> Setup Listeners
   │   ├─> Touch events → CameraController
   │   ├─> Search → searchAndNavigateToPOI()
   │   ├─> Floor buttons → switchFloor()
   │   └─> FAB AR → toggleARMode()
   │
   └─> loadInitialModel() [Async]
       │
       ├─> ModelLoader.loadModel("facultate.glb")
       │   └─> Success → Adaugă în SceneView
       │
       └─> loadPOIMarkers() [Async]
           │
           ├─> ModelLoader.loadMarkerModel()
           │
           └─> POIManager.addPOIs(...)
               └─> Creează POINode pentru fiecare

2. USER INTERACTION
   │
   ├─> Touch Drag
   │   └─> CameraController.handleRotation()
   │       └─> updateCameraPosition()
   │
   ├─> Pinch Zoom
   │   └─> CameraController.handleZoom()
   │       └─> updateCameraPosition()
   │
   ├─> Search "laborator"
   │   └─> searchAndNavigateToPOI()
   │       ├─> POIRepository.searchPOI("laborator")
   │       └─> CameraController.moveCameraToPosition()
   │           └─> animateCameraTransition()
   │
   ├─> Click POI
   │   └─> POINode.handleClick()
   │       ├─> Show Toast
   │       ├─> animateHighlight()
   │       └─> onPOIClicked callback
   │           └─> MainActivity: moveCameraToPosition()
   │
   ├─> Switch Floor (E2)
   │   └─> switchFloor(2)
   │       └─> POIManager.filterByFloor(2)
   │           └─> Afișează doar POI-uri cu Y între 2.0-5.0
   │
   └─> Toggle AR
       └─> ARController.toggleARMode()
           ├─> Check support
           ├─> Request permissions
           ├─> Initialize AR session
           └─> Enable sensor control

3. CLEANUP (MainActivity.onDestroy)
   │
   ├─> CameraController.cleanup()
   ├─> ARController.cleanup()
   ├─> POIManager.cleanup()
   └─> Destroy model nodes
```

---

## 📊 Dependințe Externe

```kotlin
// Sceneform & Filament (3D Rendering)
io.github.sceneview:sceneview:2.2.1
io.github.sceneview:arsceneview:2.2.1
com.google.android.filament:filament-android:1.51.5
com.google.android.filament:gltfio-android:1.51.5

// ARCore (Augmented Reality)
com.google.ar:core:1.44.0

// Material Design
com.google.android.material:material:1.12.0

// Android X
androidx.core:core-ktx
androidx.appcompat:appcompat
androidx.constraintlayout:constraintlayout

// Coroutines
kotlinx-coroutines-android:1.8.0
```

---

## 🎯 Puncte de Intrare pentru Modificări

### Adaugă POI nou
→ `POIData.kt` → `POIRepository.getAllPOIs()`

### Schimbă comportament cameră
→ `CameraController.kt` → constante de viteză

### Modifică UI
→ `activity_main.xml` + `MainActivity.kt`

### Adaugă model etaj nou
→ `ModelLoader.kt` → `loadFloorModels()`

### Schimbă culori categorii
→ `POINode.kt` → `getCategoryColor()`

### Personalizare AR
→ `ARController.kt` → toate metodele

---

## 📁 Dimensiuni Estimate

```
Total proiect: ~50-100 MB (cu dependencies)
├── Code (Kotlin): ~2000 linii
├── Layout XML: ~200 linii
├── Assets (FĂRĂ modele): < 1 MB
└── Dependencies: ~40-80 MB

Cu modele 3D: +30-100 MB (depinde de complexitate)
```

---

## 🚀 Build Outputs

```
app/build/outputs/apk/debug/
└── app-debug.apk                  (20-50 MB)
    ├── Code
    ├── Resources
    ├── Dependencies
    └── Assets (inclusiv modele .glb)
```

---

**Structură creată: 2025-11-02**
**Framework: SceneView 2.2.1 + Filament + ARCore**
**Target: Android 7.0+ (API 24+)**

