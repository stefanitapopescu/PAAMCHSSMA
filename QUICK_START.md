# 🚀 Quick Start - Start Rapid

## În 5 Pași:

### 1️⃣ Exportă Modelul din Polycam
- Format: **GLB**
- Redenumește: `facultate.glb`

### 2️⃣ Copiază în Proiect
```
app/src/main/assets/models/facultate.glb
```

### 3️⃣ Deschide în Android Studio
- File → Open → selectează `PaamChsSma`
- Așteaptă Gradle sync

### 4️⃣ Build & Run
- Click pe ▶️ Run
- Selectează device/emulator

### 5️⃣ Ajustează POI-uri
- Editează `POIData.kt`
- Schimbă coordonatele Float3(x, y, z)

---

## 🎮 Controale Rapide

| Acțiune | Gestură |
|---------|---------|
| Rotire | Swipe |
| Zoom | Pinch |
| Reset | Double tap |
| POI | Tap săgeată |
| Căutare | Tastează 3+ caractere |
| Etaje | E1/E2/E3 |
| AR | Buton cameră 📷 |

---

## 📱 Prima Rulare

1. **Acordă permisiuni** (cameră pentru AR)
2. **Așteaptă încărcare** (5-15 sec)
3. **Explorează** cu gesturi touch
4. **Caută** o locație în search bar
5. **Testează AR** (dacă e suportat)

---

## 🐛 Probleme Frecvente

**Model nu se încarcă?**
→ Verifică `assets/models/facultate.glb`

**POI-uri prea departe?**
→ Ajustează în `POIData.kt`

**Lag/Performance?**
→ Reduce dimensiunea modelului

**AR nu merge?**
→ Instalează ARCore Services

---

## 📖 Documentație Completă

- `README_APP.md` - Documentație tehnică
- `INSTRUCTIUNI_FOLOSIRE.md` - Ghid detaliat
- `app/src/main/assets/models/README.md` - Info modele

---

**Timpul estimat:** 10-15 minute de la zero la aplicație funcțională! ⚡

