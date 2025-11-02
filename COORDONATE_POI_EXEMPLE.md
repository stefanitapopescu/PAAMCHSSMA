# 📍 Coordonate POI - Ghid de Ajustare

## Înțelegerea Sistemului de Coordonate 3D

În aplicație, poziția fiecărui punct de interes (POI) este definită de 3 valori:

```kotlin
Float3(x, y, z)
```

- **X**: Axa orizontală (stânga ← → dreapta)
  - Valori negative: stânga
  - Valori pozitive: dreapta
  
- **Y**: Axa verticală (jos ↓ ↑ sus)
  - Valori mici (0-2): Etaj 1
  - Valori medii (2-5): Etaj 2
  - Valori mari (>5): Etaj 3
  
- **Z**: Axa profunzimii (înainte ← → înapoi)
  - Valori negative: înapoi
  - Valori pozitive: înainte

---

## 📊 Exemple de Coordonate Predefinite

### Etaj 1 (Y: 0.0 - 2.0)

```kotlin
// Secretariat - În față-stânga
POIData(
    id = "secretariat_1",
    name = "Secretariat",
    position = Float3(-2.0f, 0.5f, 3.0f),
    // X=-2.0: stânga
    // Y=0.5: jos (etaj 1)
    // Z=3.0: în față
    description = "Secretariatul studenților - Etaj 1, Cameră 101",
    category = POICategory.SECRETARIAT
)

// Decanat - În față-dreapta
POIData(
    id = "decanat_1",
    name = "Decanat",
    position = Float3(3.0f, 0.5f, 4.0f),
    // X=3.0: dreapta
    // Y=0.5: jos (etaj 1)
    // Z=4.0: în față
    description = "Biroul Decanului - Etaj 1, Cameră 105",
    category = POICategory.DECANAT
)

// Sala Profesori - În spate
POIData(
    id = "sala_prof_1",
    name = "Sala Profesori A",
    position = Float3(0.0f, 0.5f, -3.0f),
    // X=0.0: centru
    // Y=0.5: jos (etaj 1)
    // Z=-3.0: în spate
    description = "Sala Profesorilor - Etaj 1, Cameră 110",
    category = POICategory.SALA_PROFESORI
)
```

---

### Etaj 2 (Y: 2.0 - 5.0)

```kotlin
// Laborator - Stânga
POIData(
    id = "laborator_1",
    name = "Laborator Informatică",
    position = Float3(-3.5f, 3.5f, 2.0f),
    // X=-3.5: stânga departe
    // Y=3.5: etaj 2
    // Z=2.0: ușor în față
    description = "Laborator Informatică - Etaj 2, Cameră 201",
    category = POICategory.LABORATOR
)

// Laborator Electronică - Dreapta
POIData(
    id = "laborator_2",
    name = "Laborator Electronică",
    position = Float3(2.5f, 3.5f, -1.0f),
    // X=2.5: dreapta
    // Y=3.5: etaj 2
    // Z=-1.0: ușor în spate
    description = "Laborator Electronică - Etaj 2, Cameră 205",
    category = POICategory.LABORATOR
)

// Sala de Curs - Centru
POIData(
    id = "sala_curs_1",
    name = "Sala C201",
    position = Float3(0.0f, 3.5f, 3.5f),
    // X=0.0: centru
    // Y=3.5: etaj 2
    // Z=3.5: în față
    description = "Sala de curs - Etaj 2, Cameră 210",
    category = POICategory.SALA_CURS
)
```

---

### Etaj 3 (Y: > 5.0)

```kotlin
// Biblioteca - Centru-stânga
POIData(
    id = "biblioteca",
    name = "Biblioteca",
    position = Float3(-1.5f, 6.5f, 0.0f),
    // X=-1.5: ușor stânga
    // Y=6.5: etaj 3
    // Z=0.0: centru
    description = "Biblioteca Facultății - Etaj 3",
    category = POICategory.BIBLIOTECA
)

// Amfiteatru - Dreapta-față
POIData(
    id = "amfiteatru",
    name = "Amfiteatru A",
    position = Float3(2.0f, 6.5f, 2.5f),
    // X=2.0: dreapta
    // Y=6.5: etaj 3
    // Z=2.5: în față
    description = "Amfiteatru Mare - Etaj 3",
    category = POICategory.AMFITEATRU
)

// Sala Profesori - Stânga-spate
POIData(
    id = "sala_prof_2",
    name = "Sala Profesori B",
    position = Float3(-3.0f, 6.5f, -2.0f),
    // X=-3.0: stânga
    // Y=6.5: etaj 3
    // Z=-2.0: în spate
    description = "Sala Profesorilor - Etaj 3",
    category = POICategory.SALA_PROFESORI
)
```

---

## 🎯 Cum Găsești Coordonatele Reale?

### Metoda 1: Blender (Cel mai precis)

1. **Deschide modelul în Blender:**
   - File → Import → glTF 2.0 (.glb)
   - Selectează `facultate.glb`

2. **Activează Edit Mode:**
   - Selectează un punct (vertex) din locația dorită
   - Apasă `Tab` pentru Edit Mode

3. **Citește coordonatele:**
   - În panelul din dreapta (Properties)
   - Secțiunea Transform
   - Notează X, Y, Z

4. **Transferă în cod:**
   ```kotlin
   position = Float3(X_din_Blender, Y_din_Blender, Z_din_Blender)
   ```

### Metoda 2: Online Viewer (Rapid)

1. **Deschide:** https://gltf-viewer.donmccurdy.com/
2. **Drag & Drop** `facultate.glb`
3. **Click** pe locația dorită
4. **Notează** coordonatele din inspector
5. **Ajustează** în cod

### Metoda 3: Trial & Error în Aplicație

1. **Pune coordonate aproximative** în cod
2. **Build & Run** aplicația
3. **Observă** unde apare POI-ul
4. **Ajustează** coordonatele:
   - Prea stânga? → Crește X
   - Prea jos? → Crește Y
   - Prea înapoi? → Crește Z
5. **Repeat** până e perfect

---

## 🛠️ Template pentru POI Nou

Copiază și editează acest template în `POIData.kt`:

```kotlin
POIData(
    id = "id_unic_aici",              // IMPORTANT: Unic!
    name = "Numele Locației",         // Max 30 caractere
    position = Float3(
        0.0f,  // X: -10.0 la 10.0 (stânga-dreapta)
        0.5f,  // Y: 0.0-2.0 (etaj1), 2.0-5.0 (etaj2), >5.0 (etaj3)
        0.0f   // Z: -10.0 la 10.0 (spate-față)
    ),
    description = "Descriere detaliată - Etaj X, Cameră YYY",
    category = POICategory.XXXXX  // Vezi lista jos
)
```

---

## 🎨 Categorii POI și Culori

```kotlin
POICategory.SECRETARIAT     → 🔵 Albastru   (#2196F3)
POICategory.DECANAT         → 🟠 Orange     (#FF5722)
POICategory.SALA_PROFESORI  → 🟢 Verde      (#4CAF50)
POICategory.LABORATOR       → 🟣 Purple     (#9C27B0)
POICategory.SALA_CURS       → 🟡 Galben     (#FFEB3B)
POICategory.BIBLIOTECA      → 🟤 Maro       (#795548)
POICategory.AMFITEATRU      → 🩷 Pink       (#E91E63)
POICategory.ALTE            → ⚪ Gri        (#9E9E9E)
```

---

## 📐 Reguli de Scalare

Dacă modelul tău este prea mare/mic, ajustează scala în `ModelLoader.kt`:

```kotlin
modelNode.loadModel(
    context = context,
    glbFileLocation = modelPath,
    scaleToUnits = 1.0f,  // Schimbă aici
    // 0.5f = jumătate din dimensiune
    // 2.0f = dublează dimensiunea
    centerOrigin = Float3(0f, 0f, 0f)
)
```

---

## 🧭 Verificare Rapidă

După ce adaugi un POI:

1. **Build & Run**
2. **Caută** în search bar
3. **Verifică:**
   - [ ] Se vede POI-ul?
   - [ ] Este la locația corectă?
   - [ ] Culoarea corespunde categoriei?
   - [ ] Descrierea este corectă?
4. **Ajustează** dacă e necesar

---

## 💡 Tips & Tricks

### Găsește Originea Modelului

```kotlin
// Adaugă un POI la origine pentru referință
POIData(
    id = "origin_marker",
    name = "ORIGINE (0,0,0)",
    position = Float3(0.0f, 0.0f, 0.0f),
    description = "Punctul de referință",
    category = POICategory.ALTE
)
```

### Grid Mental

Imaginează modelul ca un grid:

```
         (+Z) Față
            ↑
            |
(-X) Stânga + ----→ (+X) Dreapta
            |
            ↓
         (-Z) Spate

(+Y) Sus ↑
(-Y) Jos ↓
```

### Valori Tipice

- **Clădire mică**: -5 la +5 pe fiecare axă
- **Clădire medie**: -10 la +10 pe fiecare axă
- **Clădire mare**: -20 la +20 pe fiecare axă

### Distanța între Etaje

Calculează înălțimea unui etaj:
```
Înălțime standard: ~3.0 unități
Etaj 1: Y = 0.5 (mijlocul etajului)
Etaj 2: Y = 3.5 (0.5 + 3.0)
Etaj 3: Y = 6.5 (3.5 + 3.0)
```

---

## 🔍 Debugging Coordonate

Adaugă logging temporar în `POINode.kt`:

```kotlin
init {
    Log.d("POINode", "POI: ${poiData.name} la ${poiData.position}")
}
```

Apoi filtrează Logcat după "POINode" pentru a vedea toate pozițiile.

---

## 📝 Checklist Final

Înainte de a publica aplicația:

- [ ] Toate POI-urile sunt la pozițiile corecte
- [ ] Niciun POI nu se suprapune cu altul
- [ ] Descrierile sunt clare și concise
- [ ] Căutarea funcționează pentru toate POI-urile
- [ ] Filtrarea pe etaje funcționează corect
- [ ] Camera se mută corect când selectezi un POI
- [ ] Culorile categoriilor au sens

---

**Succes la ajustarea POI-urilor! 🎯**

