package com.example.paamchssma

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * MainActivity COMPLETĂ cu toate funcționalitățile
 */
class MainActivityFull : Activity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    // UI Components
    private lateinit var webView: WebView
    private lateinit var searchEditText: EditText
    private lateinit var loadingProgress: ProgressBar
    private lateinit var messageText: TextView
    private lateinit var fabRecenter: View
    private lateinit var btnFloor1: Button
    private lateinit var btnFloor2: Button
    private lateinit var btnFloor3: Button
    
    // Butoane navigare
    private lateinit var btnUp: View
    private lateinit var btnDown: View
    private lateinit var btnLeft: View
    private lateinit var btnRight: View
    private lateinit var btnForward: View
    private lateinit var btnBackward: View
    
    // Fallback: Scene Viewer native
    private var useNativeViewer = false
    
    // State
    private var currentFloor = 1
    
    // Coroutine scope pentru async operations
    private val mainScope = CoroutineScope(Dispatchers.Main + Job())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "=== APLICAȚIE PORNEȘTE (FULL MODE) ===")
            
            setContentView(R.layout.activity_main_working)
            Log.d(TAG, "✅ Layout setat (working version)")
            
            initializeViews()
            initializeControllers()
            setupListeners()
            loadInitialData()
            
            Toast.makeText(this, "✅ Aplicație pornită! Explorează modelul 3D", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ EROARE în onCreate: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(this, "EROARE: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun initializeViews() {
        try {
            webView = findViewById(R.id.webView)
            searchEditText = findViewById(R.id.searchEditText)
            loadingProgress = findViewById(R.id.loadingProgress)
            messageText = findViewById(R.id.messageText)
            fabRecenter = findViewById(R.id.fabRecenter)
            btnFloor1 = findViewById(R.id.btnFloor1)
            btnFloor2 = findViewById(R.id.btnFloor2)
            btnFloor3 = findViewById(R.id.btnFloor3)
            
            // Butoane navigare
            btnUp = findViewById(R.id.btnUp)
            btnDown = findViewById(R.id.btnDown)
            btnLeft = findViewById(R.id.btnLeft)
            btnRight = findViewById(R.id.btnRight)
            btnForward = findViewById(R.id.btnForward)
            btnBackward = findViewById(R.id.btnBackward)
            
            // Buton înapoi la hartă
            findViewById<Button>(R.id.backToMapButton).setOnClickListener {
                finish() // Închide activity-ul și revine la CampusMapActivity
            }
            
            // Configurează WebView pentru model 3D
            setupWebView()
            
            Log.d(TAG, "✅ Views inițializate")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare la inițializare views: ${e.message}", e)
            throw e
        }
    }
    
    private fun setupWebView() {
        try {
            Log.d(TAG, "🌐 Configurare WebView pentru model 3D...")
            
            // WebViewClient pentru logging și debug
            webView.webViewClient = object : android.webkit.WebViewClient() {
                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d(TAG, "✅ WebView: Pagină încărcată: $url")
                }
                
                override fun onReceivedError(
                    view: android.webkit.WebView?,
                    request: android.webkit.WebResourceRequest?,
                    error: android.webkit.WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    Log.e(TAG, "❌ WebView ERROR: ${error?.description} (cod: ${error?.errorCode})")
                    Log.e(TAG, "   URL: ${request?.url}")
                }
            }
            
            // WebChromeClient pentru console.log
            webView.webChromeClient = object : android.webkit.WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                    Log.d(TAG, "📱 JS Console: ${consoleMessage?.message()} (${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()})")
                    return true
                }
            }
            
            webView.settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                allowFileAccessFromFileURLs = true
                allowUniversalAccessFromFileURLs = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                displayZoomControls = false
                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            
            // Arată search bar pentru căutare locații
            searchEditText.visibility = View.VISIBLE
            fabRecenter.visibility = View.VISIBLE
            btnFloor1.visibility = View.GONE
            btnFloor2.visibility = View.GONE
            btnFloor3.visibility = View.GONE
            
            // Setup search functionality
            setupSearch()
            
            Log.d(TAG, "✅ WebView configurat cu logging complet")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare configurare WebView: ${e.message}", e)
        }
    }
    
    private fun initializeControllers() {
        try {
            Log.d(TAG, "✅ Controllers nu sunt necesari pentru WebView")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare: ${e.message}", e)
        }
    }
    
    private fun setupListeners() {
        try {
            // Buton recentrare
            fabRecenter.setOnClickListener {
                webView.evaluateJavascript("recenterCamera();", null)
                Toast.makeText(this, "Camera recentrată", Toast.LENGTH_SHORT).show()
            }
            
            // Butoane navigare - touch & hold pentru mișcare continuă
            setupMovementButton(btnForward, "startMove('forward')", "stopMove('forward')")
            setupMovementButton(btnBackward, "startMove('backward')", "stopMove('backward')")
            setupMovementButton(btnLeft, "startMove('left')", "stopMove('left')")
            setupMovementButton(btnRight, "startMove('right')", "stopMove('right')")
            setupMovementButton(btnUp, "startMove('up')", "stopMove('up')")
            setupMovementButton(btnDown, "startMove('down')", "stopMove('down')")
            
            Log.d(TAG, "✅ Listeners configurați (inclusiv navigare)")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare: ${e.message}", e)
        }
    }
    
    private fun setupMovementButton(button: View, startAction: String, stopAction: String) {
        button.setOnTouchListener { view, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    view.isPressed = true
                    webView.evaluateJavascript(startAction, null)
                    true
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    view.isPressed = false
                    webView.evaluateJavascript(stopAction, null)
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupSearch() {
        try {
            searchEditText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    val query = v.text.toString().trim()
                    if (query.isNotEmpty()) {
                        searchLocation(query)
                    }
                    true
                } else {
                    false
                }
            }
            
            Log.d(TAG, "✅ Search configurat")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare setup search: ${e.message}", e)
        }
    }
    
    private fun searchLocation(query: String) {
        Log.d(TAG, "🔍 Căutare locație: $query")
        
        // Trimite căutarea la WebView
        webView.evaluateJavascript("searchAndFlyTo('$query');", null)
        
        // Ascunde keyboard
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        
        Toast.makeText(this, "🔍 Căutare: $query", Toast.LENGTH_SHORT).show()
    }
    
    private fun loadInitialData() {
        showLoading(true, "Încărcare MODEL 3D REAL...")
        
        mainScope.launch {
            try {
                Log.d(TAG, "🏛️ Încărcare facultate.glb din assets")
                
                // Verifică dacă modelul există
                val modelExists = try {
                    assets.open("models/facultate.glb").use { true }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ facultate.glb NU există!")
                    false
                }
                
                if (!modelExists) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showMessage("❌ EROARE: facultate.glb lipsește din assets/models/")
                    }
                    return@launch
                }
                
                Log.d(TAG, "✅ facultate.glb găsit!")
                
                withContext(Dispatchers.Main) {
                    // Încarcă MODELUL TĂU REAL cu markere!
                    loadModelWithCache()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Eroare: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showMessage("Eroare: ${e.message}")
                }
            }
        }
    }
    
    /**
     * Dialog pentru alegerea metodei de vizualizare
     */
    private fun loadModelWithCache() {
        try {
            Log.d(TAG, "📦 Copiere GLB în cache pentru încărcare")
            
            // Copiază GLB din assets în cache
            val cacheFile = File(cacheDir, "facultate.glb")
            
            if (!cacheFile.exists()) {
                Log.d(TAG, "📋 Copiere facultate.glb în cache...")
                assets.open("models/facultate.glb").use { input ->
                    FileOutputStream(cacheFile).use { output ->
                        input.copyTo(output)
                    }
                }
                Log.d(TAG, "✅ GLB copiat: ${cacheFile.absolutePath}")
            } else {
                Log.d(TAG, "✅ GLB deja în cache: ${cacheFile.absolutePath}")
            }
            
            // Încarcă HTML cu Three.js care citește din cache
            loadThreeJSWithCacheFile(cacheFile)
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare: ${e.message}", e)
            showLoading(false)
            showMessage("Eroare copiere model: ${e.message}")
        }
    }
    
    private fun loadThreeJSWithCacheFile(glbFile: File) {
        try {
            Log.d(TAG, "🚀 Încărcare Three.js cu model REAL")
            
            // Creează URI pentru fișier
            val fileUri = "file://${glbFile.absolutePath}"
            Log.d(TAG, "📍 Model URI: $fileUri")
            
            val html = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Facultate 3D</title>
    <style>
        * { margin: 0; padding: 0; }
        body { 
            width: 100vw; 
            height: 100vh; 
            overflow: hidden;
            background: #000;
        }
        #container { width: 100%; height: 100%; }
        #status {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(0,0,0,0.8);
            color: white;
            padding: 30px;
            border-radius: 15px;
            text-align: center;
            font-family: Arial;
            font-size: 18px;
            z-index: 1000;
        }
        #info {
            position: absolute;
            top: 10px;
            left: 10px;
            background: rgba(0,0,0,0.7);
            color: #0f0;
            padding: 10px;
            border-radius: 5px;
            font-family: monospace;
            font-size: 12px;
            z-index: 100;
        }
    </style>
</head>
<body>
    <div id="status">⏳ Încărcare model 3D REAL...<br><small>facultate.glb</small></div>
    <div id="info" style="display:none;">
        ✅ MODEL ÎNCĂRCAT!<br>
        🟢 Sala B624<br>
        🔴 Sala B613<br>
        👆 Drag = rotire<br>
        🔍 Search funcțional
    </div>
    <div id="container"></div>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/loaders/GLTFLoader.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/controls/OrbitControls.js"></script>
    
    <script>
        console.log('🚀 Încărcare MODEL 3D REAL pornită');
        
        const status = document.getElementById('status');
        const info = document.getElementById('info');
        
        const scene = new THREE.Scene();
        scene.background = new THREE.Color(0x87ceeb);
        
        const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
        camera.position.set(0, 2, 10);
        
        const renderer = new THREE.WebGLRenderer({ antialias: true });
        renderer.setSize(window.innerWidth, window.innerHeight);
        renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
        renderer.shadowMap.enabled = true;
        renderer.shadowMap.type = THREE.PCFSoftShadowMap;
        renderer.toneMapping = THREE.ACESFilmicToneMapping;
        renderer.toneMappingExposure = 1.2;
        document.getElementById('container').appendChild(renderer.domElement);
        
        console.log('✅ Renderer creat');
        
        // ILUMINARE PROFESIONALĂ
        const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
        scene.add(ambientLight);
        
        const hemisphereLight = new THREE.HemisphereLight(0x87ceeb, 0x808080, 0.6);
        scene.add(hemisphereLight);
        
        const dirLight = new THREE.DirectionalLight(0xffffff, 1.0);
        dirLight.position.set(10, 20, 10);
        dirLight.castShadow = true;
        dirLight.shadow.mapSize.width = 2048;
        dirLight.shadow.mapSize.height = 2048;
        dirLight.shadow.camera.near = 0.5;
        dirLight.shadow.camera.far = 50;
        dirLight.shadow.camera.left = -20;
        dirLight.shadow.camera.right = 20;
        dirLight.shadow.camera.top = 20;
        dirLight.shadow.camera.bottom = -20;
        scene.add(dirLight);
        
        const fillLight = new THREE.DirectionalLight(0xffffff, 0.4);
        fillLight.position.set(-10, 10, -10);
        scene.add(fillLight);
        
        console.log('✅ Lumini profesionale adăugate');
        
        // ÎNCĂRCARE MODEL 3D REAL (facultate.glb)
        const loader = new THREE.GLTFLoader();
        const modelPath = '$fileUri';
        
        console.log('📦 Încărcare model:', modelPath);
        status.innerHTML = '⏳ Încărcare model 3D...<br><small>Așteaptă...</small>';
        
        let loadedModel = null;
        let poiMarkers = [];
        
        // POZIȚII REALISTE ÎN MODEL (se vor ajusta automat după scale)
        const locations = [
            {
                id: 'b624',
                name: 'Sala B624',
                position: { x: 0, y: 0, z: 0 },  // VA FI CALCULAT DUPĂ ÎNCĂRCARE
                color: 0x00ff00,
                description: 'Sala de curs B624 - Etaj 2',
                offsetPercent: { x: 0.3, y: 0.65, z: 0.2 }  // 30% dreapta, 65% înălțime, 20% înainte
            },
            {
                id: 'b613',
                name: 'Sala B613',
                position: { x: 0, y: 0, z: 0 },  // VA FI CALCULAT DUPĂ ÎNCĂRCARE
                color: 0xff0000,
                description: 'Sala de curs B613 - Etaj 1',
                offsetPercent: { x: -0.3, y: 0.35, z: -0.2 }  // 30% stânga, 35% înălțime, 20% înapoi
            }
        ];
        
        loader.load(
            modelPath,
            function(gltf) {
                console.log('✅ MODEL ÎNCĂRCAT!', gltf);
                loadedModel = gltf.scene;
                
                // Calculează dimensiuni și centrează
                const box = new THREE.Box3().setFromObject(loadedModel);
                const size = box.getSize(new THREE.Vector3());
                const center = box.getCenter(new THREE.Vector3());
                
                loadedModel.position.x = -center.x;
                loadedModel.position.y = -center.y;
                loadedModel.position.z = -center.z;
                
                // Scalare
                const maxDim = Math.max(size.x, size.y, size.z);
                const scale = 20 / maxDim;
                loadedModel.scale.set(scale, scale, scale);
                
                // Shadows
                loadedModel.traverse((child) => {
                    if (child.isMesh) {
                        child.castShadow = true;
                        child.receiveShadow = true;
                        if (child.material) {
                            child.material.needsUpdate = true;
                        }
                    }
                });
                
                scene.add(loadedModel);
                console.log('✅ Model adăugat în scenă!');
                console.log('📊 Dimensiuni:', size);
                console.log('📊 Scale:', scale);
                
                // CALCULEAZĂ POZIȚII REALISTE PENTRU MARKERE (ÎN MODEL!)
                const modelBox = new THREE.Box3().setFromObject(loadedModel);
                const modelSize = modelBox.getSize(new THREE.Vector3());
                const modelCenter = modelBox.getCenter(new THREE.Vector3());
                
                console.log('📐 Bounding Box Model:', modelBox);
                console.log('📐 Model Size:', modelSize);
                console.log('📐 Model Center:', modelCenter);
                
                // Ajustează poziții markere ÎN interiorul modelului
                locations.forEach(loc => {
                    loc.position.x = modelCenter.x + (modelSize.x * loc.offsetPercent.x);
                    loc.position.y = modelBox.min.y + (modelSize.y * loc.offsetPercent.y);
                    loc.position.z = modelCenter.z + (modelSize.z * loc.offsetPercent.z);
                    
                    console.log('📍', loc.name, 'poziție calculată:', loc.position);
                });
                
                // ADAUGĂ MARKERE POI (cu poziții reale)
                addPOIMarkers();
                
                // Ascunde status, arată info
                status.style.display = 'none';
                info.style.display = 'block';
                
                setTimeout(() => {
                    info.style.display = 'none';
                }, 5000);
            },
            function(xhr) {
                const percent = (xhr.loaded / xhr.total * 100).toFixed(0);
                console.log(percent + '% încărcat');
                status.innerHTML = '⏳ Încărcare model 3D...<br><small>' + percent + '%</small>';
            },
            function(error) {
                console.error('❌ EROARE ÎNCĂRCARE:', error);
                status.innerHTML = '❌ EROARE!<br><small>' + error.message + '</small>';
            }
        );
        
        function addPOIMarkers() {
            console.log('📍 Adăugare markere POI în model (dimensiuni mici)...');
            
            locations.forEach(loc => {
                console.log('🎯 Creare marker pentru', loc.name, 'la', loc.position);
                
                // Marker (sferă) - MIC
                const markerSize = 0.3;  // Redus de la 0.8 la 0.3
                const markerGeometry = new THREE.SphereGeometry(markerSize, 16, 16);
                const markerMaterial = new THREE.MeshStandardMaterial({ 
                    color: loc.color,
                    emissive: loc.color,
                    emissiveIntensity: 0.9,
                    metalness: 0.3,
                    roughness: 0.4
                });
                const marker = new THREE.Mesh(markerGeometry, markerMaterial);
                marker.position.set(loc.position.x, loc.position.y, loc.position.z);
                marker.userData = { ...loc, pulsePhase: Math.random() * Math.PI * 2 };
                scene.add(marker);
                
                // Pin vertical MIC
                const pinHeight = 1.0;  // Redus de la 2.5 la 1.0
                const pinGeometry = new THREE.CylinderGeometry(0.05, 0.05, pinHeight, 8);  // Redus de la 0.15 la 0.05
                const pinMaterial = new THREE.MeshStandardMaterial({ 
                    color: loc.color,
                    emissive: loc.color,
                    emissiveIntensity: 0.6
                });
                const pin = new THREE.Mesh(pinGeometry, pinMaterial);
                pin.position.set(loc.position.x, loc.position.y - pinHeight/2, loc.position.z);
                scene.add(pin);
                
                // Text label MIC
                const canvas = document.createElement('canvas');
                const context = canvas.getContext('2d');
                canvas.width = 512;
                canvas.height = 128;
                context.fillStyle = '#ffffff';
                context.strokeStyle = '#000000';
                context.lineWidth = 8;  // Redus de la 10 la 8
                context.font = 'Bold 42px Arial';  // Redus de la 56px la 42px
                context.textAlign = 'center';
                context.strokeText(loc.name, 256, 80);
                context.fillText(loc.name, 256, 80);
                
                const texture = new THREE.CanvasTexture(canvas);
                const spriteMaterial = new THREE.SpriteMaterial({ map: texture });
                const sprite = new THREE.Sprite(spriteMaterial);
                sprite.position.set(loc.position.x, loc.position.y + 0.8, loc.position.z);  // Redus de la 2.0 la 0.8
                sprite.scale.set(3, 0.75, 1);  // Redus de la 5x1.25 la 3x0.75
                scene.add(sprite);
                
                // Ring de evidențiere la bază - MIC
                const ringGeometry = new THREE.RingGeometry(0.2, 0.3, 32);  // Redus de la 0.5-0.7 la 0.2-0.3
                const ringMaterial = new THREE.MeshBasicMaterial({ 
                    color: loc.color,
                    side: THREE.DoubleSide,
                    transparent: true,
                    opacity: 0.5
                });
                const ring = new THREE.Mesh(ringGeometry, ringMaterial);
                ring.position.set(loc.position.x, loc.position.y - pinHeight - 0.05, loc.position.z);
                ring.rotation.x = -Math.PI / 2;
                scene.add(ring);
                
                poiMarkers.push({ marker, pin, sprite, ring, location: loc });
                
                console.log('✅ POI MIC adăugat în model:', loc.name);
            });
            
            console.log('📍 Total POI-uri mici în model:', poiMarkers.length);
        }
        
        // ORBIT CONTROLS
        const controls = new THREE.OrbitControls(camera, renderer.domElement);
        controls.enableDamping = true;
        controls.dampingFactor = 0.05;
        controls.maxPolarAngle = Math.PI / 2 - 0.05;
        controls.minDistance = 2;
        controls.maxDistance = 100;
        controls.target.set(0, 0, 0);
        
        console.log('✅ OrbitControls active');
        
        // SEARCH FUNCTION - zbor FOARTE APROAPE
        window.searchAndFlyTo = function(query) {
            const queryLower = query.toLowerCase().trim();
            console.log('🔍 Căutare:', queryLower);
            
            const found = locations.find(loc => 
                loc.name.toLowerCase().includes(queryLower) ||
                loc.id.toLowerCase().includes(queryLower)
            );
            
            if (found) {
                console.log('✅ Găsit:', found.name);
                console.log('✈️ Zbor FOARTE APROAPE de marker...');
                
                // Poziție FOARTE APROAPE de marker (1.5 unități în față)
                const targetPos = {
                    x: found.position.x,
                    y: found.position.y,  // La aceeași înălțime cu markerul
                    z: found.position.z + 1.5  // Doar 1.5 unități în față (era 10!)
                };
                
                const startPos = {
                    x: camera.position.x,
                    y: camera.position.y,
                    z: camera.position.z
                };
                
                let progress = 0;
                const duration = 2.0;
                
                function animateFly() {
                    progress += 0.016 / duration;
                    
                    if (progress < 1.0) {
                        const t = easeInOutCubic(progress);
                        camera.position.x = startPos.x + (targetPos.x - startPos.x) * t;
                        camera.position.y = startPos.y + (targetPos.y - startPos.y) * t;
                        camera.position.z = startPos.z + (targetPos.z - startPos.z) * t;
                        
                        // Privește DIRECT la marker
                        controls.target.set(found.position.x, found.position.y, found.position.z);
                        controls.update();
                        
                        requestAnimationFrame(animateFly);
                    } else {
                        console.log('✅ Ajuns FOARTE APROAPE de:', found.name);
                        console.log('📍 Distanță finală: 1.5 unități');
                    }
                }
                
                animateFly();
            } else {
                console.log('❌ Nu s-a găsit:', query);
            }
        };
        
        function easeInOutCubic(t) {
            return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
        }
        
        window.recenterCamera = function() {
            console.log('🎯 Recentrare cameră');
            camera.position.set(0, 2, 10);
            controls.target.set(0, 0, 0);
            controls.update();
        };
        
        // ========== FIRST-PERSON MOVEMENT SYSTEM ==========
        
        const moveSpeed = 0.3;  // Viteză redusă pentru control mai precis
        const moveState = {
            forward: false,
            backward: false,
            left: false,
            right: false,
            up: false,
            down: false
        };
        
        // Funcții pentru mișcare
        window.startMove = function(direction) {
            moveState[direction] = true;
            console.log('🎮 Start move:', direction);
        };
        
        window.stopMove = function(direction) {
            moveState[direction] = false;
            console.log('🎮 Stop move:', direction);
        };
        
        // Update mișcare în animation loop
        function updateMovement() {
            const moveVector = new THREE.Vector3(0, 0, 0);
            
            if (moveState.forward) {
                const forward = new THREE.Vector3(0, 0, -1);
                forward.applyQuaternion(camera.quaternion);
                forward.y = 0; // Nu urca automat
                forward.normalize();
                moveVector.add(forward);
            }
            
            if (moveState.backward) {
                const backward = new THREE.Vector3(0, 0, 1);
                backward.applyQuaternion(camera.quaternion);
                backward.y = 0;
                backward.normalize();
                moveVector.add(backward);
            }
            
            if (moveState.left) {
                const left = new THREE.Vector3(-1, 0, 0);
                left.applyQuaternion(camera.quaternion);
                left.y = 0;
                left.normalize();
                moveVector.add(left);
            }
            
            if (moveState.right) {
                const right = new THREE.Vector3(1, 0, 0);
                right.applyQuaternion(camera.quaternion);
                right.y = 0;
                right.normalize();
                moveVector.add(right);
            }
            
            if (moveState.up) {
                moveVector.y += 1;
            }
            
            if (moveState.down) {
                moveVector.y -= 1;
            }
            
            // Normalizează dacă e nevoie (pentru diagonal)
            if (moveVector.length() > 0) {
                moveVector.normalize();
                moveVector.multiplyScalar(moveSpeed);
                camera.position.add(moveVector);
                controls.target.add(moveVector);
                controls.update();
            }
        }
        
        // ANIMATION LOOP (cu mișcare integrată)
        function animate() {
            requestAnimationFrame(animate);
            
            // Update mișcare (first-person)
            updateMovement();
            
            // Animează markere (pulse + ring rotate)
            if (poiMarkers.length > 0) {
                const time = performance.now() * 0.001;
                poiMarkers.forEach(poi => {
                    // Pulse marker
                    const scale = 1.0 + Math.sin(time * 2 + poi.marker.userData.pulsePhase) * 0.3;
                    poi.marker.scale.set(scale, scale, scale);
                    
                    // Rotate ring
                    if (poi.ring) {
                        poi.ring.rotation.z = time * 0.5;
                    }
                });
            }
            
            controls.update();
            renderer.render(scene, camera);
        }
        animate();
        
        console.log('✅ First-person movement system activ!');
        
        window.addEventListener('resize', () => {
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            renderer.setSize(window.innerWidth, window.innerHeight);
        });
        
        console.log('✅ Sistem de randare 3D activ!');
        console.log('⏳ Așteptare încărcare model...');
    </script>
</body>
</html>
            """.trimIndent()
            
            webView.loadDataWithBaseURL(
                "file://${cacheDir.absolutePath}/",
                html,
                "text/html",
                "UTF-8",
                null
            )
            
            showLoading(false)
            
            Log.d(TAG, "✅ HTML cu MODEL REAL încărcat!")
            
            showMessage(
                """
                ✅ MODELUL TĂU 3D SE ÎNCARCĂ!
                
                🏛️ facultate.glb (model real)
                🟢 Sala B624 (marker verde)
                🔴 Sala B613 (marker roșu)
                
                ⏳ Așteaptă câteva secunde...
                👆 Drag pentru rotire
                🔍 Search "B624" sau "B613"
                
                🎯 MODELUL TĂU REAL!
                """.trimIndent()
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare: ${e.message}", e)
        }
    }
    
    /**
     * OPȚIUNEA 1: Scene Viewer (GARANTAT funcționează)
     */
    private fun openInSceneViewer() {
        mainScope.launch {
            try {
                showLoading(true, "Pregătire Scene Viewer...")
                
                Log.d(TAG, "🚀 Deschidere Scene Viewer...")
                
                // Copiază GLB în cache pentru acces extern
                val glbFile = withContext(Dispatchers.IO) {
                    copyAssetToCache("models/facultate.glb", "facultate.glb")
                }
                
                if (glbFile == null) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        Toast.makeText(this@MainActivityFull, "❌ Eroare la copiere fișier", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }
                
                Log.d(TAG, "✅ Fișier copiat: ${glbFile.absolutePath}")
                
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    
                    try {
                        // Creează URI cu FileProvider
                        val uri = FileProvider.getUriForFile(
                            this@MainActivityFull,
                            "${packageName}.fileprovider",
                            glbFile
                        )
                        
                        Log.d(TAG, "📦 URI: $uri")
                        
                        // Intent pentru Scene Viewer
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "model/gltf-binary")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        
                        // Verifică dacă există aplicație care poate deschide GLB
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                            Log.d(TAG, "✅ Scene Viewer deschis!")
                            
                            Toast.makeText(
                                this@MainActivityFull,
                                "🎉 Model deschis în Scene Viewer!\n\n👆 Drag pentru rotire\n🤏 Pinch pentru zoom",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Log.e(TAG, "❌ Nicio aplicație pentru GLB")
                            Toast.makeText(
                                this@MainActivityFull,
                                "❌ Scene Viewer nu este disponibil.\nÎncearcă WebView.",
                                Toast.LENGTH_LONG
                            ).show()
                            
                            // Fallback la WebView
                            loadModelInWebViewWithCopy()
                        }
                        
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Eroare Scene Viewer: ${e.message}", e)
                        Toast.makeText(
                            this@MainActivityFull,
                            "❌ Eroare: ${e.message}\nÎncearcă WebView.",
                            Toast.LENGTH_LONG
                        ).show()
                        
                        // Fallback la WebView
                        loadModelInWebViewWithCopy()
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Eroare openInSceneViewer: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivityFull, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * OPȚIUNEA 2: WebView cu fișier copiat în cache
     */
    private fun loadModelInWebViewWithCopy() {
        mainScope.launch {
            try {
                showLoading(true, "Încărcare în WebView...")
                
                Log.d(TAG, "🌐 Copiere fișier pentru WebView...")
                
                // Copiază GLB în cache
                val glbFile = withContext(Dispatchers.IO) {
                    copyAssetToCache("models/facultate.glb", "facultate_webview.glb")
                }
                
                if (glbFile == null) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        Toast.makeText(this@MainActivityFull, "❌ Eroare la copiere", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }
                
                val fileUri = Uri.fromFile(glbFile).toString()
                Log.d(TAG, "✅ File URI: $fileUri")
                
                withContext(Dispatchers.Main) {
                    loadModelInWebView(fileUri)
                    showLoading(false)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Eroare: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivityFull, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * SOLUȚIA FINALĂ: Three.js cu model BASE64 embedded
     */
    private fun loadModelWithThreeJS() {
        mainScope.launch {
            try {
                showLoading(true, "Pregătire model 3D...")
                
                Log.d(TAG, "🚀 Three.js: Citire facultate.glb...")
                
                // Citește GLB ca ByteArray
                val glbBytes = withContext(Dispatchers.IO) {
                    try {
                        assets.open("models/facultate.glb").use { it.readBytes() }
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ facultate.glb NU există!")
                        null
                    }
                }
                
                if (glbBytes == null) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        showMessage("❌ facultate.glb lipsește din assets/models/")
                    }
                    return@launch
                }
                
                Log.d(TAG, "✅ GLB citit: ${glbBytes.size / 1024 / 1024} MB")
                
                // Convertește în Base64
                val base64 = withContext(Dispatchers.IO) {
                    android.util.Base64.encodeToString(glbBytes, android.util.Base64.NO_WRAP)
                }
                
                Log.d(TAG, "✅ Base64 encoding complet")
                
                withContext(Dispatchers.Main) {
                    loadThreeJSInWebView(base64)
                    showLoading(false)
                    
                    showMessage(
                        """
                        🎉 MODEL 3D ÎNCĂRCAT!
                        
                        🏛️ Three.js + facultate.glb
                        
                        🎮 CONTROALE:
                           👆 Drag - Rotește
                           🖱️ Scroll - Zoom
                           ✌️ Right-click drag - Pan
                        
                        ✨ MODELUL TĂU E VIZIBIL! ✨
                        """.trimIndent()
                    )
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Eroare: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivityFull, "Eroare: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * Încarcă Three.js cu model embedded ca Base64
     */
    private fun loadThreeJSInWebView(base64Model: String) {
        try {
            Log.d(TAG, "🌐 Încărcare Three.js în WebView...")
            
            val html = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Facultate 3D - Three.js</title>
    <style>
        * { margin: 0; padding: 0; }
        body { 
            width: 100vw; 
            height: 100vh; 
            overflow: hidden;
            background: #1a1a1a;
            font-family: Arial, sans-serif;
        }
        #container {
            width: 100%;
            height: 100%;
        }
        #status {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(255,255,255,0.95);
            color: #000;
            padding: 30px 40px;
            border-radius: 15px;
            text-align: center;
            z-index: 1000;
            font-size: 18px;
        }
        #info {
            position: absolute;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background: rgba(0,0,0,0.9);
            color: white;
            padding: 15px 25px;
            border-radius: 10px;
            text-align: center;
            z-index: 999;
            display: none;
            max-width: 90%;
            font-size: 14px;
            line-height: 1.6;
        }
        #controls-help {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(0,0,0,0.95);
            color: #fff;
            padding: 30px 40px;
            border-radius: 15px;
            text-align: center;
            z-index: 998;
            font-size: 16px;
            line-height: 1.8;
            max-width: 90%;
            display: none;
        }
        #fps {
            position: absolute;
            top: 10px;
            right: 10px;
            background: rgba(0,0,0,0.7);
            color: #0f0;
            padding: 5px 10px;
            border-radius: 5px;
            font-family: monospace;
            font-size: 12px;
            z-index: 997;
        }
        
        /* SĂGEȚI DE NAVIGARE */
        .nav-button {
            position: absolute;
            width: 70px;
            height: 70px;
            background: rgba(255,255,255,0.9);
            border: 3px solid #333;
            border-radius: 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32px;
            cursor: pointer;
            user-select: none;
            z-index: 999;
            transition: all 0.2s;
            box-shadow: 0 4px 8px rgba(0,0,0,0.3);
        }
        .nav-button:active {
            background: rgba(100,255,100,0.9);
            transform: scale(0.95);
        }
        #btnForward {
            bottom: 200px;
            right: 90px;
        }
        #btnBackward {
            bottom: 120px;
            right: 90px;
        }
        #btnLeft {
            bottom: 120px;
            right: 170px;
        }
        #btnRight {
            bottom: 120px;
            right: 10px;
        }
        #btnUp {
            bottom: 280px;
            right: 90px;
        }
        #btnDown {
            bottom: 40px;
            right: 90px;
        }
        
        
        #position {
            position: absolute;
            top: 40px;
            right: 10px;
            background: rgba(0,0,0,0.7);
            color: #fff;
            padding: 5px 10px;
            border-radius: 5px;
            font-family: monospace;
            font-size: 10px;
            z-index: 997;
            text-align: right;
            line-height: 1.4;
        }
        
        /* QUALITY SETTINGS MENU */
        #qualityMenu {
            position: absolute;
            top: 20px;
            left: 20px;
            background: rgba(0,0,0,0.9);
            color: #fff;
            padding: 15px;
            border-radius: 10px;
            z-index: 1000;
            font-size: 13px;
            display: none;
        }
        #qualityToggle {
            position: absolute;
            top: 20px;
            left: 20px;
            background: rgba(0,0,0,0.8);
            color: #fff;
            padding: 10px 15px;
            border-radius: 8px;
            z-index: 999;
            cursor: pointer;
            font-size: 14px;
            border: 2px solid #666;
        }
        #qualityToggle:hover {
            background: rgba(50,50,50,0.9);
        }
        .quality-option {
            margin: 8px 0;
            cursor: pointer;
            padding: 8px;
            border-radius: 5px;
            background: rgba(255,255,255,0.1);
        }
        .quality-option:hover {
            background: rgba(255,255,255,0.2);
        }
        .quality-option.active {
            background: rgba(100,255,100,0.3);
            border: 2px solid #0f0;
        }
    </style>
</head>
<body>
    <div id="status">⏳ Încărcare model 3D...</div>
    <div id="info">
        🏛️ <strong>FACULTATE 3D ÎNCĂRCAT!</strong><br><br>
        🎮 <strong>PLIMBĂ-TE PRIN FACULTATE!</strong><br><br>
        ➡️ Folosește săgețile pentru navigare<br>
        👀 Drag pe ecran pentru a te uita<br>
        🏃 Mergi înainte/înapoi/stânga/dreapta<br>
        🪜 Sus/jos pentru a urca/coborî<br><br>
        💡 Explorează fiecare colț!
    </div>
    <div id="controls-help">
        🎮 <strong>PLIMBĂ-TE PRIN FACULTATE!</strong><br><br>
        ➡️ Săgeți pentru mișcare controlabilă<br>
        👀 <strong>DRAG SMOOTH</strong> - rotire cu INERTIE!<br>
        🔍 <strong>SEARCH BAR</strong> - caută locații!<br><br>
        📍 <strong>LOCAȚII DISPONIBILE:</strong><br>
        🟢 Sala B624 (verde)<br>
        🔴 Sala B613 (roșu)<br><br>
        💡 <strong>TESTEAZĂ:</strong> Scrie "B624" sau "B613"<br>
        și apasă SEARCH → zbori la locație! ✈️<br><br>
        🌊 Physics realiste + Animații smooth!<br><br>
        <button onclick="this.parentElement.style.display='none'" style="margin-top: 15px; padding: 10px 20px; font-size: 14px; border-radius: 8px; background: #4CAF50; color: white; border: none; cursor: pointer;">
            ✅ Start!
        </button>
    </div>
    <div id="fps">FPS: --</div>
    <div id="position">POS: --</div>
    
    <!-- QUALITY TOGGLE -->
    <div id="qualityToggle" onclick="toggleQualityMenu()">⚙️ Calitate</div>
    
    <!-- QUALITY MENU -->
    <div id="qualityMenu">
        <strong>⚙️ SETĂRI GRAFICE</strong><br><br>
        <div class="quality-option" onclick="setQuality('low')">
            🟢 <strong>LOW</strong> - Performanță maximă<br>
            <small>• Anti-aliasing: OFF<br>
            • Shadows: OFF<br>
            • Lights: 2<br>
            • Pixel ratio: 1.0</small>
        </div>
        <div class="quality-option active" onclick="setQuality('medium')">
            🟡 <strong>MEDIUM</strong> - Echilibrat<br>
            <small>• Anti-aliasing: ON<br>
            • Shadows: 1024px<br>
            • Lights: 4<br>
            • Pixel ratio: 1.5</small>
        </div>
        <div class="quality-option" onclick="setQuality('high')">
            🔴 <strong>HIGH</strong> - Calitate maximă<br>
            <small>• Anti-aliasing: ON<br>
            • Shadows: 2048px<br>
            • Lights: 7<br>
            • Pixel ratio: 2.0</small>
        </div>
        <br>
        <button onclick="toggleQualityMenu()" style="width: 100%; padding: 8px; background: #4CAF50; color: white; border: none; border-radius: 5px; cursor: pointer;">
            ✅ Închide
        </button>
    </div>
    
    <div id="container"></div>
    
    <!-- SĂGEȚI DE NAVIGARE -->
    <div class="nav-button" id="btnForward">↑</div>
    <div class="nav-button" id="btnBackward">↓</div>
    <div class="nav-button" id="btnLeft">←</div>
    <div class="nav-button" id="btnRight">→</div>
    <div class="nav-button" id="btnUp">⬆️</div>
    <div class="nav-button" id="btnDown">⬇️</div>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/three.js/r128/three.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/loaders/GLTFLoader.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/three@0.128.0/examples/js/controls/OrbitControls.js"></script>
    
    <script>
        console.log('🚀 Three.js pornit');
        
        const status = document.getElementById('status');
        const info = document.getElementById('info');
        const container = document.getElementById('container');
        
        // Scene, Camera, Renderer
        const scene = new THREE.Scene();
        scene.background = new THREE.Color(0x1a1a1a);
        
        const camera = new THREE.PerspectiveCamera(
            75,
            window.innerWidth / window.innerHeight,
            0.1,
            1000
        );
        // Camera în interior, la înălțime de persoană
        camera.position.set(0, 1.6, 5);
        
        // QUALITY SETTINGS - OPTIMIZABIL
        let currentQuality = 'medium';
        let antialiasEnabled = true;
        let shadowsEnabled = true;
        let shadowMapSize = 1024;
        let pixelRatioMultiplier = 1.5;
        
        // Renderer cu setări configurabile
        const renderer = new THREE.WebGLRenderer({ 
            antialias: antialiasEnabled,
            alpha: false,
            powerPreference: "high-performance",
            precision: "highp"
        });
        renderer.setSize(window.innerWidth, window.innerHeight);
        renderer.setPixelRatio(Math.min(window.devicePixelRatio, pixelRatioMultiplier));
        renderer.shadowMap.enabled = shadowsEnabled;
        renderer.shadowMap.type = THREE.PCFSoftShadowMap;
        renderer.toneMapping = THREE.ACESFilmicToneMapping;
        renderer.toneMappingExposure = 1.2;
        renderer.outputEncoding = THREE.sRGBEncoding;
        container.appendChild(renderer.domElement);
        
        console.log('🎨 Renderer MEDIUM quality activat (default)');
        
        // FIRST-PERSON CONTROLS - plimbare prin facultate
        let moveForward = false;
        let moveBackward = false;
        let moveLeft = false;
        let moveRight = false;
        let moveUp = false;
        let moveDown = false;
        
        const moveSpeed = 0.8; // Viteză MAI LENTĂ (controlabil și precis)
        const lookSpeed = 0.005; // Privit mai rapid și smooth
        
        const euler = new THREE.Euler(0, 0, 0, 'YXZ');
        let isPointerDown = false;
        let pointerX = 0;
        let pointerY = 0;
        
        // INERTIA pentru rotire fluidă
        let rotationVelocityX = 0;
        let rotationVelocityY = 0;
        const dampingFactor = 0.92; // Cât de repede încetinește (0.92 = smooth)
        const velocityMultiplier = 0.8; // Cât de mult momentum are
        
        // Look controls (mouse/touch drag) - CU INERTIE
        renderer.domElement.addEventListener('pointerdown', (event) => {
            isPointerDown = true;
            pointerX = event.clientX;
            pointerY = event.clientY;
            // Oprește inertia când începi să tragi din nou
            rotationVelocityX = 0;
            rotationVelocityY = 0;
        });
        
        renderer.domElement.addEventListener('pointermove', (event) => {
            if (!isPointerDown) return;
            
            const movementX = event.clientX - pointerX;
            const movementY = event.clientY - pointerY;
            
            pointerX = event.clientX;
            pointerY = event.clientY;
            
            // Calculează velocity pentru inertie
            rotationVelocityX = movementX * velocityMultiplier;
            rotationVelocityY = movementY * velocityMultiplier;
            
            // Aplică rotirea INSTANT
            euler.setFromQuaternion(camera.quaternion);
            euler.y -= movementX * lookSpeed;
            euler.x -= movementY * lookSpeed;
            euler.x = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, euler.x));
            camera.quaternion.setFromEuler(euler);
        });
        
        renderer.domElement.addEventListener('pointerup', () => {
            isPointerDown = false;
            // La eliberare, velocity-ul continuă → inertie
        });
        
        // Funcție pentru aplicare inertie
        function applyRotationInertia() {
            if (Math.abs(rotationVelocityX) > 0.01 || Math.abs(rotationVelocityY) > 0.01) {
                euler.setFromQuaternion(camera.quaternion);
                euler.y -= rotationVelocityX * lookSpeed;
                euler.x -= rotationVelocityY * lookSpeed;
                euler.x = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, euler.x));
                camera.quaternion.setFromEuler(euler);
                
                // Încetinește velocity-ul (damping)
                rotationVelocityX *= dampingFactor;
                rotationVelocityY *= dampingFactor;
                
                // Oprește când e foarte mic
                if (Math.abs(rotationVelocityX) < 0.01) rotationVelocityX = 0;
                if (Math.abs(rotationVelocityY) < 0.01) rotationVelocityY = 0;
            }
        }
        
        // Săgeți de navigare
        const btnForward = document.getElementById('btnForward');
        const btnBackward = document.getElementById('btnBackward');
        const btnLeft = document.getElementById('btnLeft');
        const btnRight = document.getElementById('btnRight');
        const btnUp = document.getElementById('btnUp');
        const btnDown = document.getElementById('btnDown');
        
        // Touch events pentru butoane
        btnForward.addEventListener('pointerdown', () => moveForward = true);
        btnForward.addEventListener('pointerup', () => moveForward = false);
        btnForward.addEventListener('pointerleave', () => moveForward = false);
        
        btnBackward.addEventListener('pointerdown', () => moveBackward = true);
        btnBackward.addEventListener('pointerup', () => moveBackward = false);
        btnBackward.addEventListener('pointerleave', () => moveBackward = false);
        
        btnLeft.addEventListener('pointerdown', () => moveLeft = true);
        btnLeft.addEventListener('pointerup', () => moveLeft = false);
        btnLeft.addEventListener('pointerleave', () => moveLeft = false);
        
        btnRight.addEventListener('pointerdown', () => moveRight = true);
        btnRight.addEventListener('pointerup', () => moveRight = false);
        btnRight.addEventListener('pointerleave', () => moveRight = false);
        
        btnUp.addEventListener('pointerdown', () => moveUp = true);
        btnUp.addEventListener('pointerup', () => moveUp = false);
        btnUp.addEventListener('pointerleave', () => moveUp = false);
        
        btnDown.addEventListener('pointerdown', () => moveDown = true);
        btnDown.addEventListener('pointerup', () => moveDown = false);
        btnDown.addEventListener('pointerleave', () => moveDown = false);
        
        // Keyboard pentru emulator (WASD + Space/Shift)
        document.addEventListener('keydown', (event) => {
            switch(event.code) {
                case 'KeyW':
                case 'ArrowUp':
                    moveForward = true;
                    break;
                case 'KeyS':
                case 'ArrowDown':
                    moveBackward = true;
                    break;
                case 'KeyA':
                case 'ArrowLeft':
                    moveLeft = true;
                    break;
                case 'KeyD':
                case 'ArrowRight':
                    moveRight = true;
                    break;
                case 'Space':
                    moveUp = true;
                    break;
                case 'ShiftLeft':
                case 'ShiftRight':
                    moveDown = true;
                    break;
            }
        });
        
        document.addEventListener('keyup', (event) => {
            switch(event.code) {
                case 'KeyW':
                case 'ArrowUp':
                    moveForward = false;
                    break;
                case 'KeyS':
                case 'ArrowDown':
                    moveBackward = false;
                    break;
                case 'KeyA':
                case 'ArrowLeft':
                    moveLeft = false;
                    break;
                case 'KeyD':
                case 'ArrowRight':
                    moveRight = false;
                    break;
                case 'Space':
                    moveUp = false;
                    break;
                case 'ShiftLeft':
                case 'ShiftRight':
                    moveDown = false;
                    break;
            }
        });
        
        // Funcție de mișcare
        function updateMovement() {
            const direction = new THREE.Vector3();
            const right = new THREE.Vector3();
            
            camera.getWorldDirection(direction);
            right.crossVectors(camera.up, direction).normalize();
            
            if (moveForward) {
                camera.position.addScaledVector(direction, moveSpeed);
            }
            if (moveBackward) {
                camera.position.addScaledVector(direction, -moveSpeed);
            }
            if (moveLeft) {
                camera.position.addScaledVector(right, moveSpeed);
            }
            if (moveRight) {
                camera.position.addScaledVector(right, -moveSpeed);
            }
            if (moveUp) {
                camera.position.y += moveSpeed;
            }
            if (moveDown) {
                camera.position.y -= moveSpeed;
            }
        }
        
        // LIGHTING PROFESSIONAL - 3-Point Lighting Setup
        
        // 1. Ambient light (lumină generală)
        const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
        scene.add(ambientLight);
        
        // 2. Key light (lumină principală)
        const keyLight = new THREE.DirectionalLight(0xffffff, 1.2);
        keyLight.position.set(20, 30, 20);
        keyLight.castShadow = true;
        keyLight.shadow.mapSize.width = 2048;
        keyLight.shadow.mapSize.height = 2048;
        keyLight.shadow.camera.near = 0.5;
        keyLight.shadow.camera.far = 500;
        keyLight.shadow.camera.left = -50;
        keyLight.shadow.camera.right = 50;
        keyLight.shadow.camera.top = 50;
        keyLight.shadow.camera.bottom = -50;
        scene.add(keyLight);
        
        // 3. Fill light (lumină de umplere)
        const fillLight = new THREE.DirectionalLight(0xaaccff, 0.6);
        fillLight.position.set(-20, 20, -10);
        scene.add(fillLight);
        
        // 4. Back light (lumină din spate)
        const backLight = new THREE.DirectionalLight(0xffeecc, 0.4);
        backLight.position.set(0, 10, -20);
        scene.add(backLight);
        
        // 5. Point lights (lumini punctiforme pentru interioare)
        const pointLight1 = new THREE.PointLight(0xffddaa, 0.8, 50);
        pointLight1.position.set(10, 5, 10);
        scene.add(pointLight1);
        
        const pointLight2 = new THREE.PointLight(0xffddaa, 0.8, 50);
        pointLight2.position.set(-10, 5, -10);
        scene.add(pointLight2);
        
        // 6. Hemisphere light (cer și pământ)
        const hemiLight = new THREE.HemisphereLight(0x87ceeb, 0x8b7355, 0.4);
        scene.add(hemiLight);
        
        // Ground grid (grilă pentru referință)
        const gridHelper = new THREE.GridHelper(100, 100, 0x555555, 0x222222);
        gridHelper.position.y = -0.01; // Puțin sub nivel 0
        scene.add(gridHelper);
        
        console.log('💡 Lighting setup: 3-point + ambient + hemisphere + 2 point lights');
        
        // Încarcă model din Base64
        console.log('📦 Încărcare model din Base64...');
        
        const base64Data = '$base64Model';
        const binaryString = atob(base64Data);
        const bytes = new Uint8Array(binaryString.length);
        for (let i = 0; i < binaryString.length; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }
        
        const blob = new Blob([bytes], { type: 'model/gltf-binary' });
        const url = URL.createObjectURL(blob);
        
        console.log('✅ Blob URL creat:', url);
        
        const loader = new THREE.GLTFLoader();
        loader.load(
            url,
            function(gltf) {
                console.log('✅ Model încărcat cu succes!');
                
                const model = gltf.scene;
                
                // Center model
                const box = new THREE.Box3().setFromObject(model);
                const center = box.getCenter(new THREE.Vector3());
                model.position.sub(center);
                
                // Scale model optimal
                const size = box.getSize(new THREE.Vector3());
                const maxDim = Math.max(size.x, size.y, size.z);
                const scale = 30 / maxDim; // Scale mai mic pentru detalii mai bune
                model.scale.setScalar(scale);
                
                // Activează shadow pentru model
                model.traverse((child) => {
                    if (child.isMesh) {
                        child.castShadow = true;
                        child.receiveShadow = true;
                        
                        // Îmbunătățește materialele
                        if (child.material) {
                            child.material.needsUpdate = true;
                            // Dacă e MeshStandardMaterial, optimizează
                            if (child.material.isMeshStandardMaterial) {
                                child.material.envMapIntensity = 1.0;
                                child.material.roughness = Math.max(0.3, child.material.roughness);
                            }
                        }
                    }
                });
                
                console.log('📊 Model scale:', scale);
                console.log('✅ Shadows și materiale optimizate');
                
                scene.add(model);
                
                console.log('✅ Model adăugat în scenă');
                console.log('📊 Dimensiuni:', size);
                console.log('📊 Scale:', scale);
                
                // ADAUGĂ POI-uri (locații)
                addPOIMarkers();
                
                // Ascunde status, arată info și help
                status.style.display = 'none';
                info.style.display = 'block';
                
                // Arată help după 2 secunde
                const controlsHelp = document.getElementById('controls-help');
                setTimeout(() => {
                    controlsHelp.style.display = 'block';
                }, 2000);
                
                // Ascunde info după 8 secunde
                setTimeout(() => {
                    info.style.transition = 'opacity 1s';
                    info.style.opacity = '0';
                    setTimeout(() => {
                        info.style.display = 'none';
                    }, 1000);
                }, 8000);
                
                // Cleanup blob
                URL.revokeObjectURL(url);
            },
            function(xhr) {
                const progress = (xhr.loaded / xhr.total * 100).toFixed(0);
                console.log('📊 Progres:', progress + '%');
                status.innerHTML = '⏳ Încărcare model 3D...<br>' + progress + '%';
            },
            function(error) {
                console.error('❌ EROARE încărcare:', error);
                status.innerHTML = '❌ EROARE<br>' + error.message;
                status.style.background = 'rgba(255,100,100,0.95)';
                status.style.color = '#fff';
            }
        );
        
        // FPS și position counter
        let lastTime = performance.now();
        let frameCount = 0;
        const fpsElement = document.getElementById('fps');
        const posElement = document.getElementById('position');
        
        // Animation loop
        function animate() {
            requestAnimationFrame(animate);
            
            // Update movement
            updateMovement();
            
            // Aplică inertie pentru rotire SMOOTH
            applyRotationInertia();
            
            // Animează POI markers
            animatePOIMarkers();
            
            // Render scene
            renderer.render(scene, camera);
            
            // Update FPS și position
            frameCount++;
            const currentTime = performance.now();
            if (currentTime >= lastTime + 1000) {
                fpsElement.textContent = 'FPS: ' + frameCount;
                frameCount = 0;
                lastTime = currentTime;
            }
            
            // Update position
            const pos = camera.position;
            const rot = euler.setFromQuaternion(camera.quaternion);
            posElement.innerHTML = 
                'X: ' + pos.x.toFixed(1) + '<br>' +
                'Y: ' + pos.y.toFixed(1) + '<br>' +
                'Z: ' + pos.z.toFixed(1) + '<br>' +
                'ROT: ' + (rot.y * 180 / Math.PI).toFixed(0) + '°';
        }
        animate();
        
        console.log('✅ First-person controls active!');
        
        // ========== POI SYSTEM (LOCAȚII) ==========
        
        const locations = [
            {
                id: 'b624',
                name: 'Sala B624',
                position: { x: 8, y: 2, z: -10 }, // Poziție în model
                color: 0x00ff00, // Verde
                description: 'Sala de curs B624 - Etaj 2'
            },
            {
                id: 'b613',
                name: 'Sala B613',
                position: { x: -12, y: 1.5, z: 5 }, // Poziție diferită
                color: 0xff0000, // Roșu
                description: 'Sala de curs B613 - Etaj 1'
            }
        ];
        
        let poiMarkers = [];
        
        // Adaugă markere pentru POI-uri
        function addPOIMarkers() {
            console.log('📍 Adăugare markere POI...');
            
            locations.forEach(location => {
                // Marker principal (sferă)
                const markerGeometry = new THREE.SphereGeometry(0.5, 16, 16);
                const markerMaterial = new THREE.MeshStandardMaterial({ 
                    color: location.color,
                    emissive: location.color,
                    emissiveIntensity: 0.5,
                    metalness: 0.5,
                    roughness: 0.3
                });
                const marker = new THREE.Mesh(markerGeometry, markerMaterial);
                marker.position.set(location.position.x, location.position.y, location.position.z);
                marker.userData = location;
                scene.add(marker);
                
                // Pulse animation pentru marker
                marker.userData.originalScale = 1.0;
                marker.userData.pulsePhase = Math.random() * Math.PI * 2;
                
                // Pin (cilindru vertical)
                const pinGeometry = new THREE.CylinderGeometry(0.1, 0.1, 1.5, 8);
                const pinMaterial = new THREE.MeshStandardMaterial({ 
                    color: location.color,
                    emissive: location.color,
                    emissiveIntensity: 0.3
                });
                const pin = new THREE.Mesh(pinGeometry, pinMaterial);
                pin.position.set(location.position.x, location.position.y - 0.75, location.position.z);
                scene.add(pin);
                
                // Text label (sprite)
                const canvas = document.createElement('canvas');
                const context = canvas.getContext('2d');
                canvas.width = 512;
                canvas.height = 128;
                context.fillStyle = '#ffffff';
                context.strokeStyle = '#000000';
                context.lineWidth = 8;
                context.font = 'Bold 48px Arial';
                context.textAlign = 'center';
                context.strokeText(location.name, 256, 80);
                context.fillText(location.name, 256, 80);
                
                const texture = new THREE.CanvasTexture(canvas);
                const spriteMaterial = new THREE.SpriteMaterial({ map: texture });
                const sprite = new THREE.Sprite(spriteMaterial);
                sprite.position.set(location.position.x, location.position.y + 1.5, location.position.z);
                sprite.scale.set(4, 1, 1);
                scene.add(sprite);
                
                poiMarkers.push({ marker, pin, sprite, location });
                
                console.log('✅ POI adăugat:', location.name, 'la poziția', location.position);
            });
            
            console.log('📍 Total POI-uri:', poiMarkers.length);
        }
        
        // Animație pulse pentru markere
        function animatePOIMarkers() {
            const time = performance.now() * 0.001;
            poiMarkers.forEach(poi => {
                const scale = 1.0 + Math.sin(time * 2 + poi.marker.userData.pulsePhase) * 0.2;
                poi.marker.scale.set(scale, scale, scale);
            });
        }
        
        // Căutare și zbor la locație
        window.searchAndFlyTo = function(query) {
            const queryLower = query.toLowerCase().trim();
            console.log('🔍 Căutare:', queryLower);
            
            // Caută în locații
            const found = locations.find(loc => 
                loc.name.toLowerCase().includes(queryLower) ||
                loc.id.toLowerCase().includes(queryLower)
            );
            
            if (found) {
                console.log('✅ Găsit:', found.name);
                flyToLocation(found);
            } else {
                console.log('❌ Nu s-a găsit:', query);
                console.log('💡 Încearcă: B624, B613, Sala B624, Sala B613');
            }
        };
        
        // Zbor smooth la locație
        function flyToLocation(location) {
            console.log('✈️ Zbor către:', location.name);
            
            // Poziție țintă (în fața locației)
            const targetPos = {
                x: location.position.x,
                y: location.position.y,
                z: location.position.z + 5 // 5 unități în fața locației
            };
            
            // Animație smooth (lerp)
            const startPos = {
                x: camera.position.x,
                y: camera.position.y,
                z: camera.position.z
            };
            
            let progress = 0;
            const duration = 2.0; // 2 secunde
            
            function animateFly() {
                progress += 0.016 / duration; // ~60 FPS
                
                if (progress < 1.0) {
                    // Lerp position
                    camera.position.x = startPos.x + (targetPos.x - startPos.x) * easeInOutCubic(progress);
                    camera.position.y = startPos.y + (targetPos.y - startPos.y) * easeInOutCubic(progress);
                    camera.position.z = startPos.z + (targetPos.z - startPos.z) * easeInOutCubic(progress);
                    
                    // Privește către marker
                    const lookAtPos = new THREE.Vector3(
                        location.position.x,
                        location.position.y,
                        location.position.z
                    );
                    camera.lookAt(lookAtPos);
                    
                    requestAnimationFrame(animateFly);
                } else {
                    // Finalizare
                    camera.position.set(targetPos.x, targetPos.y, targetPos.z);
                    
                    // Privește către marker
                    const lookAtPos = new THREE.Vector3(
                        location.position.x,
                        location.position.y,
                        location.position.z
                    );
                    camera.lookAt(lookAtPos);
                    
                    // Update euler pentru controlul manual
                    euler.setFromQuaternion(camera.quaternion);
                    
                    console.log('✅ Ajuns la:', location.name);
                }
            }
            
            animateFly();
        }
        
        // Easing function pentru animație smooth
        function easeInOutCubic(t) {
            return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
        }
        
        // Recentrare cameră
        window.recenterCamera = function() {
            console.log('🎯 Recentrare cameră');
            camera.position.set(0, 1.6, 5);
            camera.lookAt(0, 1.6, 0);
            euler.setFromQuaternion(camera.quaternion);
        };
        
        // ========== QUALITY MANAGEMENT ==========
        
        let allLights = [];
        let qualityLights = {
            low: [],
            medium: [],
            high: []
        };
        
        // Salvează toate luminile
        function saveLights() {
            allLights = [
                ambientLight, keyLight, fillLight, backLight, 
                pointLight1, pointLight2, hemiLight
            ];
            
            // LOW: doar ambient + key
            qualityLights.low = [ambientLight, keyLight];
            
            // MEDIUM: ambient + key + fill + hemi
            qualityLights.medium = [ambientLight, keyLight, fillLight, hemiLight];
            
            // HIGH: toate luminile
            qualityLights.high = allLights;
        }
        saveLights();
        
        // Toggle quality menu
        window.toggleQualityMenu = function() {
            const menu = document.getElementById('qualityMenu');
            menu.style.display = menu.style.display === 'none' ? 'block' : 'none';
        };
        
        // Set quality
        window.setQuality = function(quality) {
            console.log('⚙️ Schimbare calitate la:', quality);
            currentQuality = quality;
            
            // Update UI
            document.querySelectorAll('.quality-option').forEach(opt => {
                opt.classList.remove('active');
            });
            event.target.closest('.quality-option').classList.add('active');
            
            // Apply settings
            switch(quality) {
                case 'low':
                    applyLowQuality();
                    break;
                case 'medium':
                    applyMediumQuality();
                    break;
                case 'high':
                    applyHighQuality();
                    break;
            }
            
            console.log('✅ Calitate aplicată:', quality);
        };
        
        function applyLowQuality() {
            console.log('🟢 LOW QUALITY - Performanță maximă');
            
            // Pixel ratio
            renderer.setPixelRatio(1.0);
            
            // Shadows OFF
            renderer.shadowMap.enabled = false;
            keyLight.castShadow = false;
            
            // Lights: doar 2
            allLights.forEach(light => scene.remove(light));
            qualityLights.low.forEach(light => scene.add(light));
            
            // Grid mai simplu
            scene.remove(gridHelper);
            const simpleGrid = new THREE.GridHelper(50, 25, 0x333333, 0x111111);
            simpleGrid.position.y = -0.01;
            scene.add(simpleGrid);
            
            console.log('📊 Lights active: 2');
            console.log('📊 Shadows: OFF');
            console.log('📊 Pixel ratio: 1.0');
        }
        
        function applyMediumQuality() {
            console.log('🟡 MEDIUM QUALITY - Echilibrat');
            
            // Pixel ratio
            renderer.setPixelRatio(Math.min(window.devicePixelRatio, 1.5));
            
            // Shadows ON (1024)
            renderer.shadowMap.enabled = true;
            keyLight.castShadow = true;
            keyLight.shadow.mapSize.width = 1024;
            keyLight.shadow.mapSize.height = 1024;
            
            // Lights: 4
            allLights.forEach(light => scene.remove(light));
            qualityLights.medium.forEach(light => scene.add(light));
            
            // Grid mediu
            scene.children.forEach(child => {
                if (child.type === 'GridHelper') scene.remove(child);
            });
            const mediumGrid = new THREE.GridHelper(75, 50, 0x444444, 0x222222);
            mediumGrid.position.y = -0.01;
            scene.add(mediumGrid);
            
            console.log('📊 Lights active: 4');
            console.log('📊 Shadows: 1024px');
            console.log('📊 Pixel ratio: 1.5');
        }
        
        function applyHighQuality() {
            console.log('🔴 HIGH QUALITY - Calitate maximă');
            
            // Pixel ratio
            renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2.0));
            
            // Shadows ON (2048)
            renderer.shadowMap.enabled = true;
            keyLight.castShadow = true;
            keyLight.shadow.mapSize.width = 2048;
            keyLight.shadow.mapSize.height = 2048;
            
            // Lights: toate (7)
            allLights.forEach(light => scene.remove(light));
            qualityLights.high.forEach(light => scene.add(light));
            
            // Grid full
            scene.children.forEach(child => {
                if (child.type === 'GridHelper') scene.remove(child);
            });
            const fullGrid = new THREE.GridHelper(100, 100, 0x555555, 0x222222);
            fullGrid.position.y = -0.01;
            scene.add(fullGrid);
            
            console.log('📊 Lights active: 7');
            console.log('📊 Shadows: 2048px');
            console.log('📊 Pixel ratio: 2.0');
        }
        
        // Resize handler
        window.addEventListener('resize', () => {
            camera.aspect = window.innerWidth / window.innerHeight;
            camera.updateProjectionMatrix();
            renderer.setSize(window.innerWidth, window.innerHeight);
        });
        
        console.log('✅ Three.js setup complet');
    </script>
</body>
</html>
            """.trimIndent()
            
            webView.loadDataWithBaseURL(
                "https://example.com",
                html,
                "text/html",
                "UTF-8",
                null
            )
            
            Log.d(TAG, "✅ HTML încărcat în WebView")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare: ${e.message}", e)
        }
    }
    
    /**
     * Copiază asset în cache
     */
    private fun copyAssetToCache(assetPath: String, fileName: String): File? {
        return try {
            val cacheFile = File(cacheDir, fileName)
            
            assets.open(assetPath).use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            Log.d(TAG, "✅ Copiat: ${cacheFile.absolutePath} (${cacheFile.length()} bytes)")
            cacheFile
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare copiere: ${e.message}", e)
            null
        }
    }
    
    private fun loadModelInWebView(modelUri: String = "file:///android_asset/models/facultate.glb") {
        try {
            Log.d(TAG, "🌐 Încărcare HTML cu model: $modelUri")
            
            val html = """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Facultate 3D</title>
    <script type="module" src="https://unpkg.com/@google/model-viewer@3.5.0/dist/model-viewer.min.js"></script>
    <style>
        * { margin: 0; padding: 0; }
        body { 
            width: 100vw; 
            height: 100vh; 
            overflow: hidden;
            background: #1a1a1a;
        }
        model-viewer {
            width: 100%;
            height: 100%;
            background-color: #1a1a1a;
        }
        #status {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(255,255,255,0.9);
            color: #000;
            padding: 20px 30px;
            border-radius: 10px;
            font-family: Arial, sans-serif;
            text-align: center;
            z-index: 1000;
        }
        #info {
            position: absolute;
            top: 20px;
            left: 50%;
            transform: translateX(-50%);
            background: rgba(0,0,0,0.8);
            color: white;
            padding: 15px 25px;
            border-radius: 10px;
            font-family: Arial, sans-serif;
            text-align: center;
            display: none;
        }
    </style>
</head>
<body>
    <div id="status">⏳ Încărcare model 3D...</div>
    
    <model-viewer 
        id="viewer"
        src="$modelUri"
        alt="Facultate 3D Model"
        camera-controls
        touch-action="pan-y"
        auto-rotate
        auto-rotate-delay="3000"
        rotation-per-second="30deg"
        shadow-intensity="1"
        environment-image="neutral"
        exposure="1.0"
        camera-orbit="0deg 75deg 10m"
        min-camera-orbit="auto auto 5m"
        max-camera-orbit="auto auto 50m">
    </model-viewer>
    
    <div id="info">
        🏛️ FACULTATE 3D<br>
        👆 Drag pentru rotire<br>
        🤏 Pinch pentru zoom
    </div>
    
    <script>
        console.log('🚀 Script pornit');
        
        const viewer = document.getElementById('viewer');
        const status = document.getElementById('status');
        const info = document.getElementById('info');
        
        console.log('📦 Model-viewer element:', viewer);
        console.log('📁 Model src:', viewer.src);
        
        viewer.addEventListener('load', () => {
            console.log('✅ Model încărcat cu succes!');
            status.style.display = 'none';
            info.style.display = 'block';
            setTimeout(() => {
                info.style.display = 'none';
            }, 8000);
        });
        
        viewer.addEventListener('error', (event) => {
            console.error('❌ EROARE încărcare model:', event);
            status.innerHTML = '❌ EROARE<br>Nu s-a putut încărca modelul<br><small>Vezi Logcat pentru detalii</small>';
            status.style.background = 'rgba(255,100,100,0.9)';
            status.style.color = '#fff';
        });
        
        viewer.addEventListener('progress', (event) => {
            const progress = event.detail.totalProgress * 100;
            console.log('📊 Progres încărcare:', progress.toFixed(1) + '%');
            status.innerHTML = '⏳ Încărcare model 3D...<br>' + progress.toFixed(0) + '%';
        });
        
        // Timeout pentru debug
        setTimeout(() => {
            if (status.style.display !== 'none') {
                console.warn('⚠️ Model nu s-a încărcat în 10 secunde');
            }
        }, 10000);
    </script>
</body>
</html>
            """.trimIndent()
            
            webView.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "UTF-8",
                null
            )
            
            Log.d(TAG, "✅ HTML încărcat în WebView")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Eroare loadModelInWebView: ${e.message}", e)
            showMessage("❌ Eroare: ${e.message}")
        }
    }
    
    
    private fun showLoading(show: Boolean, message: String = "") {
        loadingProgress.visibility = if (show) View.VISIBLE else View.GONE
        
        if (show && message.isNotEmpty()) {
            messageText.text = message
            messageText.visibility = View.VISIBLE
        } else if (!show) {
            messageText.visibility = View.GONE
        }
    }
    
    private fun showMessage(message: String) {
        messageText.text = message
        messageText.visibility = View.VISIBLE
        
        messageText.postDelayed({
            messageText.visibility = View.GONE
        }, 5000)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Curăță coroutines
        mainScope.coroutineContext[Job]?.cancel()
        
        // Curăță WebView
        webView.destroy()
        
        Log.d(TAG, "Resurse curățate")
    }
}

