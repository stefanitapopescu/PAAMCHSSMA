# 📚 Instrucțiuni de Folosire - Aplicație Navigare 3D Facultate

## 🎯 Scopul Aplicației

Această aplicație Android în Kotlin permite vizualizarea și explorarea interactivă a unui model 3D al unei facultăți, exportat din Polycam. Poți naviga prin spațiu, căuta locații specifice și folosi modul AR pentru suprapunere în mediul real.

---

## 📋 Cerințe Înainte de Început

### Hardware:
- **Telefon Android** cu:
  - Android 7.0 (API 24) sau mai nou
  - Minim 2GB RAM
  - Procesor decent (Snapdragon 600+ sau echivalent)
  - *Opțional:* Suport ARCore pentru modul AR

### Software:
- **Android Studio**: Hedgehog (2023.1.1) sau mai nou
- **Polycam**: pentru exportul modelului 3D (gratuit/premium)

### Model 3D:
- Model 3D al facultății scanat cu Polycam
- Format: `.glb` (GLTF Binary)
- Dimensiune recomandată: < 50 MB

---

## 🚀 Pașii de Instalare

### Pasul 1: Pregătește Modelul 3D

1. **Deschide Polycam** pe telefon/computer
2. **Selectează scanarea** facultății
3. **Exportă modelul:**
   - Format: `GLB` sau `GLTF Binary`
   - Opțiuni: Include texturi
   - Calitate: Medium/High (după preferință)
4. **Redenumește fișierul** în `facultate.glb`

### Pasul 2: Deschide Proiectul în Android Studio

1. Deschide Android Studio
2. Click pe `Open` → selectează folder-ul `PaamChsSma`
3. Așteaptă Gradle sync (descarcă dependințele automat)

### Pasul 3: Adaugă Modelul în Proiect

1. În Android Studio, găsește în stânga:
   ```
   app → src → main → assets → models
   ```

2. **Copiază `facultate.glb`** în acest folder:
   - Drag & drop din File Explorer
   - SAU: Click dreapta pe `models` → `Show in Explorer` → Copiază

3. Verifică: Ar trebui să vezi:
   ```
   assets/models/
   ├── facultate.glb      ✅ (modelul tău)
   ├── README.md
   └── .gitignore
   ```

### Pasul 4 (Opțional): Adaugă Modele Pentru Etaje

Dacă ai scanat fiecare etaj separat:

```
assets/models/
├── facultate.glb           # Model complet
├── facultate_etaj1.glb     # Doar etajul 1
├── facultate_etaj2.glb     # Doar etajul 2
└── facultate_etaj3.glb     # Doar etajul 3
```

### Pasul 5 (Opțional): Adaugă Model pentru Săgeți

Dacă vrei săgeți custom în loc de markere implicite:

1. Creează/Descarcă un model mic de săgeată (< 1 MB)
2. Salvează ca `arrow.glb` în `assets/models/`

---

## ⚙️ Configurare POI-uri (Puncte de Interes)

### Ajustează Coordonatele POI-urilor

POI-urile sunt definite în `POIData.kt` cu coordonate **exemplu**. Pentru modelul tău real:

#### Metoda 1: Blender (Recomandat)

1. Deschide `facultate.glb` în Blender
2. Selectează locația dorită (ex: ușa secretariatului)
3. Notează coordonatele X, Y, Z din panelul Transform
4. Actualizează în cod:

```kotlin
// În POIRepository.getAllPOIs()
POIData(
    id = "secretariat_1",
    name = "Secretariat",
    position = Float3(-2.5f, 0.5f, 3.2f), // Coordonatele tale reale
    description = "Secretariatul studenților",
    category = POICategory.SECRETARIAT
)
```

#### Metoda 2: Trial & Error în Aplicație

1. Rulează aplicația
2. Observă unde apar POI-urile
3. Ajustează coordonatele în cod
4. Re-build și testează

### Adaugă POI-uri Noi

În `POIData.kt`, adaugă în lista din `getAllPOIs()`:

```kotlin
POIData(
    id = "laborator_fizica",           // ID unic
    name = "Laborator Fizică",         // Nume afișat
    position = Float3(4.0f, 3.5f, -2.0f), // X, Y, Z
    description = "Laborator Fizică - Etaj 2, Cameră 215",
    category = POICategory.LABORATOR   // Categoria
)
```

**Categorii disponibile:**
- `SECRETARIAT` - Albastru
- `DECANAT` - Orange
- `SALA_PROFESORI` - Verde
- `LABORATOR` - Purple
- `SALA_CURS` - Galben
- `BIBLIOTECA` - Maro
- `AMFITEATRU` - Pink
- `ALTE` - Gri

---

## 🏗️ Build și Rulare

### Build APK

```bash
# În terminal (Android Studio → Terminal)
./gradlew assembleDebug
```

SAU:
- Click pe `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`

### Rulare pe Emulator

1. Click pe `AVD Manager`
2. Creează/Start emulator (API 24+)
3. Click pe butonul ▶️ `Run`

### Rulare pe Telefon Real

1. **Activează Developer Options** pe telefon:
   - Settings → About Phone
   - Apasă de 7 ori pe `Build Number`

2. **Activează USB Debugging:**
   - Developer Options → USB Debugging ✅

3. **Conectează telefonul** prin USB

4. Click ▶️ `Run` în Android Studio

---

## 🎮 Ghid de Utilizare

### Controale de Bază

| Acțiune | Gestură |
|---------|---------|
| **Rotire cameră** | Swipe cu un deget |
| **Zoom In/Out** | Pinch cu două degete |
| **Recentrare cameră** | Double tap / Buton ⊙ |
| **Selectare POI** | Tap pe săgeată |

### Căutare Locații

1. **Apasă pe bara de căutare** (sus)
2. **Tastează** numele locației (min. 3 caractere)
   - Ex: "secretariat", "laborator", "decanat"
3. Camera se va deplasa automat către locație
4. **Long press pe search** pentru reset

### Schimbare Etaje

- Apasă butoanele **E1**, **E2**, **E3** din dreapta ecranului
- POI-urile se filtrează automat pentru etajul selectat

### Modul AR (Opțional)

1. **Apasă butonul camerei** 📷 (jos-dreapta)
2. **Acordă permisiunea** pentru cameră
3. **Mișcă telefonul** pentru a plasa modelul în spațiu
4. **Giroscopul** va controla camera automat

**Dezactivare AR:**
- Apasă din nou butonul camerei

---

## 🐛 Rezolvare Probleme (Troubleshooting)

### ❌ "Model 3D nu a fost găsit!"

**Cauze:**
- Modelul nu este în `assets/models/`
- Numele fișierului nu este corect

**Soluții:**
1. Verifică dacă `facultate.glb` există în `app/src/main/assets/models/`
2. Asigură-te că numele este exact `facultate.glb` (lowercase)
3. Re-sync Gradle: `Build` → `Clean Project` → `Rebuild Project`

---

### ❌ POI-urile nu se văd

**Cauze:**
- Coordonatele sunt prea departe de model
- Camera nu este poziționată corect

**Soluții:**
1. Recentrează camera (double tap)
2. Zoom out (pinch)
3. Ajustează coordonatele în `POIData.kt`
4. Verifică logcat: Filtru "POIManager"

---

### ❌ "Acest dispozitiv nu suportă ARCore"

**Cauze:**
- Dispozitivul nu are suport ARCore
- ARCore Services nu este instalat

**Soluții:**
1. Verifică lista dispozitivelor suportate: [ARCore Devices](https://developers.google.com/ar/devices)
2. Instalează **ARCore Services** din Play Store
3. Actualizează ARCore la ultima versiune

---

### ❌ Aplicația se blochează (Crash)

**Verificări:**
1. Check Logcat pentru erori:
   ```
   Filtru: "AndroidRuntime" sau "FATAL"
   ```

2. Verifică dacă toate dependințele sunt descărcate:
   ```bash
   ./gradlew --refresh-dependencies
   ```

3. Clean & Rebuild:
   ```
   Build → Clean Project
   Build → Rebuild Project
   ```

---

### ❌ Performance slab (Lag)

**Optimizări:**

1. **Reduce dimensiunea modelului:**
   - Re-exportă din Polycam cu calitate mai mică
   - Compresie texturi
   - Target: < 30 MB

2. **Folosește modele separate per etaj:**
   - Mai ușor de procesat
   - Încarcă doar etajul curent

3. **Scade calitatea de rendering** (în `MainActivity.kt`):
   ```kotlin
   sceneView.renderer.msaaLevel = 1 // În loc de 4
   ```

---

## 📊 Specificații Tehnice

### Coordonate 3D

Sistemul de coordonate folosit:
- **X**: Orizontal (stânga ← → dreapta)
- **Y**: Vertical (jos ↓ ↑ sus)
- **Z**: Profunzime (înainte ← → înapoi)

Exemplu etaje:
- **Etaj 1**: Y între 0.0 și 2.0
- **Etaj 2**: Y între 2.0 și 5.0
- **Etaj 3**: Y peste 5.0

### Distanțe Cameră

- **Default**: 10 unități
- **Minim**: 3 unități (zoom max)
- **Maxim**: 30 unități (zoom out max)

---

## 🔧 Personalizare Avansată

### Schimbă Culorile UI

`app/src/main/res/values/colors.xml`:

```xml
<color name="purple_700">#FF3700B3</color>  <!-- Toolbar -->
```

### Modifică Layout-ul

`app/src/main/res/layout/activity_main.xml`:
- Poziții butoane
- Dimensiuni
- Elemente UI noi

### Adaugă Funcționalități

Exemple de extensii:
- **Audio Guide**: Redă audio când atingi un POI
- **Fotografii**: Afișează poze ale locațiilor
- **Rute**: Trasee între două puncte
- **Mini-map**: Hartă 2D suprapusă

---

## 📱 Testare pe Dispozitive Reale

### Dispozitive Recomandate

**Minim:**
- Samsung Galaxy A52 sau echivalent
- 2GB RAM, Snapdragon 665+

**Recomandat:**
- Samsung Galaxy S20+ sau mai nou
- 4GB+ RAM, Snapdragon 865+

**Pentru AR:**
- Google Pixel 4+ 
- Samsung S10+
- Orice dispozitiv certificat ARCore

---

## 🎓 Învățare și Documentație

### Resurse Utile

- **Sceneform**: [GitHub](https://github.com/SceneView/sceneview-android)
- **Filament**: [Google Filament](https://github.com/google/filament)
- **ARCore**: [ARCore Docs](https://developers.google.com/ar)
- **Polycam**: [Polycam Help](https://poly.cam/help)

### Video Tutorials

1. [Android 3D - Sceneform Basics](https://youtube.com)
2. [ARCore Integration](https://youtube.com)
3. [Polycam Export Tutorial](https://youtube.com)

---

## 💡 Tips & Tricks

### Optimizare Model Polycam

1. **Scanează cu lumină bună**: Mai multe detalii
2. **360° coverage**: Acoperire completă
3. **Evită suprafețe reflectorizante**: Sticlă, oglinzi
4. **Processing**: High detail pentru interior

### Testare Rapidă

```bash
# Build rapid (skip tests)
./gradlew assembleDebug -x test

# Install direct pe device
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Debug POI Coordinates

Adaugă log temporar în `CameraController`:

```kotlin
Log.d("CameraPos", "Camera: ($x, $y, $z)")
```

---

## ❓ FAQ

**Q: Pot folosi format `.obj` sau `.fbx`?**
A: Nu direct. Convertește la `.glb` cu Blender sau online tools.

**Q: Cum fac modelul mai mic?**
A: Blender → Decimate Modifier → Export cu compresie.

**Q: AR funcționează în exterior?**
A: Da, dar iluminarea poate afecta tracking-ul.

**Q: Câte POI-uri pot adăuga?**
A: Teoretic nelimitat, dar >100 poate afecta performanța.

**Q: Funcționează fără internet?**
A: Da, modelul este local. Internet doar pentru ARCore install.

---

## 📞 Suport și Contribuții

### Raportează Bug-uri

1. Deschide issue pe GitHub
2. Include:
   - Detalii telefon (model, Android version)
   - Pași de reproducere
   - Logcat (dacă e posibil)

### Contribuie

1. Fork repository
2. Creează branch: `feature/nume-feature`
3. Commit changes
4. Push și creează Pull Request

---

## ✅ Checklist Final

Înainte de deploy:

- [ ] Model `facultate.glb` adăugat în assets
- [ ] Coordonate POI-uri ajustate
- [ ] Testat pe device real
- [ ] Permisiuni verificate (Cameră pentru AR)
- [ ] Performance OK (no lag)
- [ ] UI responsive pe diferite ecrane
- [ ] Build APK funcțional
- [ ] README actualizat cu info specifică

---

**Mult succes cu aplicația ta! 🚀**

Pentru întrebări: deschide un issue sau contactează dezvoltatorul.

