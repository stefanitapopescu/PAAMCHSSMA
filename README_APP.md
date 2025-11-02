# Aplicație Android - Navigare 3D Facultate

Aplicație Android în Kotlin pentru vizualizarea și explorarea interactivă a unui model 3D al unei facultăți, exportat din Polycam.

## 📱 Caracteristici

### Funcționalități Principale:
- ✅ Încărcare și afișare modele 3D în format `.glb`
- ✅ Navigare interactivă prin touch (rotire, zoom, deplasare)
- ✅ Puncte de interes (POI) cu săgeți 3D
- ✅ Căutare locații prin bară de search
- ✅ Navigare automată către POI-uri
- ✅ Schimbare între etaje (3 etaje)
- ✅ Mod AR opțional cu ARCore
- ✅ Control prin senzori (giroscop, accelerometru)
- ✅ UI modern cu Material Design 3

### Puncte de Interes Predefinite:
1. **Secretariat** - Etaj 1, Cameră 101
2. **Decanat** - Etaj 1, Cameră 105
3. **Sala Profesori A** - Etaj 1, Cameră 110
4. **Laborator Informatică** - Etaj 2, Cameră 201
5. **Laborator Electronică** - Etaj 2, Cameră 205
6. **Sala C201** - Etaj 2, Cameră 210
7. **Biblioteca** - Etaj 3
8. **Amfiteatru A** - Etaj 3
9. **Sala Profesori B** - Etaj 3

## 🏗️ Arhitectură

### Structura Proiectului:

```
app/src/main/
├── java/com/example/paamchssma/
│   ├── MainActivity.kt              # Activitatea principală
│   ├── controllers/
│   │   ├── CameraController.kt      # Controlul camerei 3D
│   │   └── ARController.kt          # Controlul modului AR
│   ├── models/
│   │   └── ModelLoader.kt           # Încărcarea modelelor 3D
│   ├── nodes/
│   │   └── POINode.kt               # Gestionarea punctelor de interes
│   └── data/
│       └── POIData.kt               # Date pentru POI-uri
├── res/
│   ├── layout/
│   │   └── activity_main.xml        # Layout UI principal
│   └── values/
│       ├── strings.xml              # String resources
│       ├── colors.xml               # Culori
│       └── themes.xml               # Teme UI
└── assets/
    └── models/
        ├── facultate.glb            # Model principal (adaugă tu)
        ├── facultate_etaj1.glb      # Opțional: Model etaj 1
        ├── facultate_etaj2.glb      # Opțional: Model etaj 2
        ├── facultate_etaj3.glb      # Opțional: Model etaj 3
        └── arrow.glb                # Opțional: Model săgeată
```

### Clase Principale:

#### 1. **MainActivity.kt**
- Activitatea principală
- Coordonează toate componentele
- Gestionează lifecycle-ul aplicației

#### 2. **CameraController.kt**
- Controlul camerei 3D
- Gesturi touch (rotire, zoom, pan)
- Integrare senzori (giroscop, accelerometru)
- Animații smooth pentru navigare

#### 3. **ModelLoader.kt**
- Încărcare modele `.glb` din assets
- Suport pentru modele multiple (etaje)
- Gestionare erori și fallback

#### 4. **POINode.kt** & **POIManager**
- Gestionare puncte de interes
- Afișare markere 3D
- Interacțiuni (click, hover)
- Filtrare după etaj

#### 5. **ARController.kt**
- Integrare ARCore
- Verificare suport AR
- Gestionare permisiuni cameră
- Toggle AR mode

#### 6. **POIData.kt**
- Model de date pentru POI-uri
- Repository cu POI-uri predefinite
- Funcții de căutare

## 🚀 Setup și Instalare

### Cerințe:
- **Android Studio**: Hedgehog (2023.1.1) sau mai nou
- **Kotlin**: 1.9+
- **Target SDK**: 34+
- **Min SDK**: 24 (Android 7.0)

### Pași de instalare:

1. **Clonează/Deschide proiectul în Android Studio**

2. **Sync Gradle:**
   - Android Studio va descărca automat dependințele
   - Sceneform, Filament, ARCore

3. **Adaugă modelul 3D:**
   ```
   - Exportă din Polycam ca .glb
   - Copiază în: app/src/main/assets/models/facultate.glb
   ```

4. **Build & Run:**
   ```bash
   ./gradlew assembleDebug
   ```
   sau folosește butonul Run din Android Studio

## 📦 Dependințe Principale

```kotlin
// Sceneform și Filament pentru 3D
implementation("io.github.sceneview:sceneview:2.2.1")
implementation("io.github.sceneview:arsceneview:2.2.1")

// Google Filament
implementation("com.google.android.filament:filament-android:1.51.5")
implementation("com.google.android.filament:gltfio-android:1.51.5")

// ARCore pentru AR
implementation("com.google.ar:core:1.44.0")

// Material Design 3
implementation("com.google.android.material:material:1.12.0")

// Coroutines pentru async
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
```

## 🎮 Cum să folosești aplicația

### Navigare:
- **Rotire:** Swipe cu un deget
- **Zoom:** Pinch cu două degete
- **Double tap:** Recentrare cameră

### Căutare:
1. Tastează în bara de căutare (minim 3 caractere)
2. Camera se va muta automat către POI găsit
3. Long press pe search pentru reset

### Schimbare Etaje:
- Apasă butoanele E1, E2, E3 din dreapta ecranului
- POI-urile se filtrează automat

### Modul AR (opțional):
1. Apasă butonul camerei (FAB jos-dreapta)
2. Acordă permisiunea pentru cameră
3. Mișcă telefonul pentru a plasa modelul în spațiu
4. Folosește giroscopul pentru control

### POI-uri:
- Atinge o săgeată pentru a vedea detalii
- Camera se va foca automat pe locație

## 🛠️ Personalizare

### Adaugă POI-uri noi:

Editează `POIData.kt`:

```kotlin
POIData(
    id = "id_unic",
    name = "Numele locației",
    position = Float3(x, y, z), // Coordonatele în model
    description = "Descriere detaliată",
    category = POICategory.SALA_CURS
)
```

### Ajustează coordonatele:

Coordonatele din `POIRepository` sunt exemple. Pentru modelul tău real:
1. Deschide modelul într-un viewer 3D (Blender, etc.)
2. Notează coordonatele punctelor de interes
3. Actualizează în `POIRepository.getAllPOIs()`

### Schimbă culorile:

Editează `res/values/colors.xml`:
```xml
<color name="purple_700">#FF6200EE</color>
```

### Personalizează UI:

Modifică `res/layout/activity_main.xml` pentru schimbări de layout.

## 🐛 Debugging

### Model nu se încarcă:
1. Verifică dacă `facultate.glb` există în `assets/models/`
2. Check logs: Filtru "ModelLoader" în Logcat
3. Asigură-te că formatul este `.glb` (nu `.gltf`)

### POI-uri nu apar:
1. Verifică coordonatele în `POIRepository`
2. Ajustează distanța camerei pentru vizibilitate
3. Check layer/visibility în Logcat

### AR nu funcționează:
1. Verifică dacă dispozitivul suportă ARCore
2. Instalează ARCore Services din Play Store
3. Acordă permisiunea pentru cameră

### Performance issues:
1. Reduce dimensiunea modelului 3D
2. Optimizează texturi (compresie)
3. Folosește modele separate per etaj

## 📝 Licență

Acest proiect este pentru uz educațional.

## 🤝 Contribuții

Pentru îmbunătățiri:
1. Fork repository
2. Creează branch pentru feature
3. Submit pull request

## 📞 Suport

Pentru probleme sau întrebări, deschide un issue pe GitHub.

---

**Dezvoltat cu ❤️ în Kotlin pentru Android**

