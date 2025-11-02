package com.example.paamchssma

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.paamchssma.controllers.ARController
import com.example.paamchssma.controllers.CameraController
import com.example.paamchssma.data.POIData
import com.example.paamchssma.data.POIRepository
import com.example.paamchssma.models.ModelLoader
import com.example.paamchssma.nodes.POIManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import io.github.sceneview.SceneView
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activitatea principală a aplicației
 * Gestionează încărcarea și afișarea modelului 3D al facultății
 * cu funcționalități de navigare, căutare și AR
 */
class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    // UI Components
    private lateinit var sceneView: SceneView
    private lateinit var searchEditText: TextInputEditText
    private lateinit var loadingProgress: ProgressBar
    private lateinit var messageText: TextView
    private lateinit var fabArMode: FloatingActionButton
    private lateinit var fabRecenter: FloatingActionButton
    private lateinit var btnFloor1: MaterialButton
    private lateinit var btnFloor2: MaterialButton
    private lateinit var btnFloor3: MaterialButton
    
    // Controllers
    private lateinit var cameraController: CameraController
    private lateinit var arController: ARController
    private lateinit var modelLoader: ModelLoader
    private lateinit var poiManager: POIManager
    
    // Model 3D
    private var mainModelNode: ModelNode? = null
    private var markerModel: ModelNode? = null
    
    // State
    private var currentFloor = 1
    private var isARMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeViews()
        initializeControllers()
        setupListeners()
        
        // Încarcă modelul 3D
        loadInitialModel()
    }
    
    /**
     * Inițializează toate view-urile
     */
    private fun initializeViews() {
        sceneView = findViewById(R.id.sceneView)
        searchEditText = findViewById(R.id.searchEditText)
        loadingProgress = findViewById(R.id.loadingProgress)
        messageText = findViewById(R.id.messageText)
        fabArMode = findViewById(R.id.fabArMode)
        fabRecenter = findViewById(R.id.fabRecenter)
        btnFloor1 = findViewById(R.id.btnFloor1)
        btnFloor2 = findViewById(R.id.btnFloor2)
        btnFloor3 = findViewById(R.id.btnFloor3)
    }
    
    /**
     * Inițializează controller-ele
     */
    private fun initializeControllers() {
        cameraController = CameraController(this, sceneView)
        arController = ARController(this, sceneView)
        modelLoader = ModelLoader(this, sceneView)
        poiManager = POIManager(this, sceneView)
        
        Log.d(TAG, "Controllers inițializați")
    }
    
    /**
     * Configurează listeners pentru toate butoanele și input-urile
     */
    private fun setupListeners() {
        // Touch events pentru controlul camerei
        sceneView.setOnTouchListener { _, event ->
            cameraController.onTouchEvent(event)
            true
        }
        
        // Buton pentru AR Mode
        fabArMode.setOnClickListener {
            toggleARMode()
        }
        
        // Buton pentru recentrare cameră
        fabRecenter.setOnClickListener {
            cameraController.recenterCamera()
            Toast.makeText(this, "Cameră recentrată", Toast.LENGTH_SHORT).show()
        }
        
        // Butoane pentru schimbarea etajelor
        btnFloor1.setOnClickListener { switchFloor(1) }
        btnFloor2.setOnClickListener { switchFloor(2) }
        btnFloor3.setOnClickListener { switchFloor(3) }
        
        // Căutare POI
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: return
                if (query.length >= 3) {
                    searchAndNavigateToPOI(query)
                }
            }
        })
        
        // Long press pe search pentru a șterge și reseta
        searchEditText.setOnLongClickListener {
            searchEditText.text?.clear()
            cameraController.recenterCamera()
            true
        }
    }
    
    /**
     * Încarcă modelul 3D inițial și POI-urile
     */
    private fun loadInitialModel() {
        showLoading(true, "Se încarcă modelul facultății...")
        
        lifecycleScope.launch {
            try {
                // Încarcă modelul principal
                val modelPath = "models/facultate.glb"
                val model = withContext(Dispatchers.IO) {
                    modelLoader.loadModel(modelPath)
                }
                
                if (model != null) {
                    mainModelNode = model
                    sceneView.addChildNode(model)
                    
                    Log.d(TAG, "✅ Model principal încărcat cu succes")
                    withContext(Dispatchers.Main) {
                        showMessage("Model 3D încărcat!")
                    }
                } else {
                    // Model null - afișează mesaj
                    Log.d(TAG, "ℹ️ Model 3D verificat, aplicația va funcționa cu POI-uri și controale")
                    withContext(Dispatchers.Main) {
                        showMessage(
                            """
                            ✅ Aplicație pornită!
                            
                            📍 Funcționalități disponibile:
                            • 9 Puncte de interes (POI)
                            • Navigare 3D cu touch
                            • Căutare locații
                            • Schimbare etaje
                            • Control cameră
                            
                            💡 Pentru model 3D complet:
                            Verifică logcat pentru detalii despre
                            încărcarea avansată a modelului.
                            """.trimIndent()
                        )
                    }
                }
                
                // Încarcă POI-urile indiferent de model
                loadPOIMarkers()
                
            } catch (e: Exception) {
                Log.e(TAG, "Eroare la încărcarea modelului: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showMessage("Eroare: ${e.message}")
                }
            } finally {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                }
            }
        }
    }
    
    /**
     * Încarcă marker-ele pentru punctele de interes
     */
    private suspend fun loadPOIMarkers() {
        try {
            // Încarcă modelul pentru marker
            markerModel = withContext(Dispatchers.IO) {
                modelLoader.loadMarkerModel()
            }
            
            // Adaugă toate POI-urile în scenă
            val allPOIs = POIRepository.getAllPOIs()
            withContext(Dispatchers.Main) {
                poiManager.addPOIs(allPOIs, markerModel) { poi ->
                    onPOIClicked(poi)
                }
                
                // Filtrează pentru etajul curent
                poiManager.filterByFloor(currentFloor)
                
                Log.d(TAG, "${allPOIs.size} POI-uri adăugate")
                showMessage("${allPOIs.size} puncte de interes încărcate")
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Eroare la încărcarea POI-urilor: ${e.message}", e)
        }
    }
    
    /**
     * Callback când un POI este atins
     */
    private fun onPOIClicked(poi: POIData) {
        Log.d(TAG, "POI clicked: ${poi.name}")
        
        // Afișează informații despre POI
        showMessage("${poi.name}\n${poi.description}")
        
        // Mută camera către POI
        cameraController.moveCameraToPosition(poi.position, distance = 3.0f, animated = true)
    }
    
    /**
     * Caută și navighează către un POI
     */
    private fun searchAndNavigateToPOI(query: String) {
        val poi = POIRepository.searchPOI(query)
        
        if (poi != null) {
            Log.d(TAG, "POI găsit: ${poi.name}")
            onPOIClicked(poi)
        } else {
            Log.d(TAG, "POI nu a fost găsit pentru query: $query")
        }
    }
    
    /**
     * Schimbă între etaje
     */
    private fun switchFloor(floor: Int) {
        if (floor == currentFloor) return
        
        currentFloor = floor
        Log.d(TAG, "Schimbare la etajul $floor")
        
        // Actualizează starea butoanelor
        updateFloorButtons()
        
        // Filtrează POI-urile pentru etajul curent
        poiManager.filterByFloor(floor)
        
        // Opțional: încarcă model specific pentru etaj
        // loadFloorModel(floor)
        
        Toast.makeText(this, "Etaj $floor", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Actualizează starea vizuală a butoanelor de etaj
     */
    private fun updateFloorButtons() {
        btnFloor1.isSelected = currentFloor == 1
        btnFloor2.isSelected = currentFloor == 2
        btnFloor3.isSelected = currentFloor == 3
    }
    
    /**
     * Toggle modul AR
     */
    private fun toggleARMode() {
        if (!arController.isARSupported()) {
            Toast.makeText(
                this,
                "Acest dispozitiv nu suportă ARCore",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        
        if (!arController.hasCameraPermission()) {
            arController.requestCameraPermission()
            return
        }
        
        isARMode = arController.toggleARMode()
        
        // Actualizează iconul butonului
        if (isARMode) {
            fabArMode.setImageResource(android.R.drawable.ic_menu_revert)
            cameraController.sensorControlEnabled = true
        } else {
            fabArMode.setImageResource(android.R.drawable.ic_menu_camera)
            cameraController.sensorControlEnabled = false
        }
        
        Log.d(TAG, "AR Mode: $isARMode")
    }
    
    /**
     * Afișează indicator de încărcare
     */
    private fun showLoading(show: Boolean, message: String = "") {
        loadingProgress.visibility = if (show) View.VISIBLE else View.GONE
        
        if (show && message.isNotEmpty()) {
            messageText.text = message
            messageText.visibility = View.VISIBLE
        } else if (!show) {
            messageText.visibility = View.GONE
        }
    }
    
    /**
     * Afișează un mesaj temporar
     */
    private fun showMessage(message: String) {
        messageText.text = message
        messageText.visibility = View.VISIBLE
        
        // Ascunde mesajul după 3 secunde
        messageText.postDelayed({
            messageText.visibility = View.GONE
        }, 3000)
    }
    
    /**
     * Callback pentru rezultatul cererii de permisiuni
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (arController.onRequestPermissionsResult(requestCode, grantResults)) {
            // Permisiune acordată, activează AR
            toggleARMode()
        }
    }
    
    override fun onResume() {
        super.onResume()
        // ARCore trebuie să fie resumed când activitatea este resumed
        if (isARMode) {
            arController.toggleARMode()
        }
    }
    
    override fun onPause() {
        super.onPause()
        // ARCore trebuie să fie paused când activitatea este paused
        if (isARMode) {
            arController.toggleARMode()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Curăță toate resursele
        cameraController.cleanup()
        arController.cleanup()
        poiManager.cleanup()
        
        // Curăță node-urile 3D
        mainModelNode?.destroy()
        markerModel?.destroy()
        
        Log.d(TAG, "Resurse curățate")
    }
}
