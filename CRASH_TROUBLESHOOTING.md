# 🚨 CRASH: "App Keeps Stopping" - Soluții

## 🔍 VERIFICĂ LOGCAT URGENT!

### În Android Studio:

1. Click pe tab-ul **"Logcat"** (jos)
2. Filtrează după: **`AndroidRuntime`**
3. **Caută linia cu "FATAL EXCEPTION"**
4. **Copiază stack trace-ul complet**

---

## 🚨 Cauze Probabilă și Soluții

### **Cauza #1: ViewBinding Nu Este Activat** ⭐ CEL MAI PROBABIL

**Eroare în Logcat:**
```
java.lang.NullPointerException: findViewById returns null
```

**Soluție RAPIDĂ:**

Verifică că ViewBinding este activat în `build.gradle.kts`:
```kotlin
buildFeatures {
    compose = true
    viewBinding = true  // ← Trebuie să fie true
}
```

---

### **Cauza #2: SceneView Nu Poate Inițializa**

**Eroare în Logcat:**
```
java.lang.RuntimeException: Unable to start activity
Caused by: java.lang.IllegalStateException
```

**Soluție:** Schimbă layout-ul la versiune simplificată.

