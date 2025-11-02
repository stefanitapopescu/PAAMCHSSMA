# Modele 3D

Acest folder conține modelele 3D pentru aplicație.

## Cum să adaugi modelul facultății:

1. **Exportă modelul din Polycam:**
   - Deschide Polycam
   - Selectează scanarea 3D a facultății
   - Exportă ca format `.glb` (GLTF Binary)
   - Salvează ca `facultate.glb`

2. **Plasează fișierul în acest folder:**
   ```
   app/src/main/assets/models/facultate.glb
   ```

3. **Opțional - Modele pentru etaje separate:**
   - `facultate_etaj1.glb` - Pentru etajul 1
   - `facultate_etaj2.glb` - Pentru etajul 2
   - `facultate_etaj3.glb` - Pentru etajul 3

4. **Marker pentru POI-uri (opțional):**
   - `arrow.glb` - Model de săgeată pentru punctele de interes
   - Dacă nu există, aplicația va folosi un marker implicit

## Formatul acceptat:
- **Format:** `.glb` (GLTF Binary)
- **Texturi:** Embedate în fișierul .glb
- **Dimensiune recomandată:** < 50 MB pentru performanță optimă

## Notă:
Modelele nu sunt incluse în repository din cauza dimensiunii.
Trebuie să le adaugi manual după exportul din Polycam.

