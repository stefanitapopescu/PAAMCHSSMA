package com.example.paamchssma.nodes

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.paamchssma.data.POICategory
import com.example.paamchssma.data.POIData
import com.example.paamchssma.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode

/**
 * Clasa pentru gestionarea unui punct de interes (POI) în scenă
 * Stochează informații despre POI și gestionează interacțiunile utilizatorului
 */
class POINode(
    private val context: Context,
    val poiData: POIData, // PUBLIC pentru ca POIManager să îl acceseze
    private val onPOIClicked: ((POIData) -> Unit)? = null
) {
    
    companion object {
        private const val TAG = "POINode"
        private const val MARKER_SCALE = 0.3f
    }
    
    private var markerNode: ModelNode? = null
    
    // Vizibilitate POI (private backing field pentru control custom)
    private var _isVisible: Boolean = true
    var isVisible: Boolean
        get() = _isVisible
        set(value) {
            _isVisible = value
            markerNode?.isVisible = value
        }
    
    // Poziție (nu mai extendem Node)
    var position: Position = Position(poiData.position.x, poiData.position.y, poiData.position.z)
        private set
    
    var scale: Scale = Scale(MARKER_SCALE)
        private set
    
    /**
     * Inițializează marker-ul vizual pentru acest POI
     */
    fun initializeMarker(markerModel: ModelNode?) {
        markerModel?.let { model ->
            // Creează un marker pentru acest POI
            markerNode = ModelNode(modelInstance = model.modelInstance).apply {
                this.position = Position(poiData.position.x, poiData.position.y, poiData.position.z)
                this.scale = Scale(MARKER_SCALE)
            }
            
            // Marker-ul va fi adăugat direct în SceneView, nu ca child
            Log.d(TAG, "Marker inițializat pentru POI: ${poiData.name}")
        }
    }
    
    /**
     * Returnează marker-ul 3D (dacă există)
     */
    fun getMarkerNode(): ModelNode? {
        return markerNode
    }
    
    /**
     * Gestionează click-ul pe acest POI
     */
    fun handleClick() {
        Log.d(TAG, "POI clicked: ${poiData.name}")
        
        // Apelează callback-ul
        onPOIClicked?.invoke(poiData)
        
        // Afișează un Toast cu informații
        Toast.makeText(
            context,
            "${poiData.name}\n${poiData.description}",
            Toast.LENGTH_LONG
        ).show()
        
        // Animație de highlighting
        animateHighlight()
    }
    
    /**
     * Animație simplă pentru a evidenția POI-ul când e atins
     */
    private fun animateHighlight() {
        val originalScale = markerNode?.scale ?: Scale(MARKER_SCALE)
        markerNode?.scale = Scale(MARKER_SCALE * 1.3f)
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            markerNode?.scale = originalScale
        }, 300)
    }
    
    /**
     * Determină culoarea marker-ului în funcție de categoria POI-ului
     */
    fun getCategoryColor(): Int {
        return when(poiData.category) {
            POICategory.SECRETARIAT -> 0xFF2196F3.toInt() // Albastru
            POICategory.DECANAT -> 0xFFFF5722.toInt() // Orange
            POICategory.SALA_PROFESORI -> 0xFF4CAF50.toInt() // Verde
            POICategory.LABORATOR -> 0xFF9C27B0.toInt() // Purple
            POICategory.SALA_CURS -> 0xFFFFEB3B.toInt() // Galben
            POICategory.BIBLIOTECA -> 0xFF795548.toInt() // Maro
            POICategory.AMFITEATRU -> 0xFFE91E63.toInt() // Pink
            POICategory.ALTE -> 0xFF9E9E9E.toInt() // Gri
        }
    }
    
    
    /**
     * Verifică dacă POI-ul este pe un anumit etaj
     */
    fun isOnFloor(floor: Int): Boolean {
        return when(floor) {
            1 -> poiData.position.y < 2.0f
            2 -> poiData.position.y in 2.0f..5.0f
            3 -> poiData.position.y > 5.0f
            else -> true
        }
    }
    
    /**
     * Returnează datele POI-ului
     */
    fun getPOIData(): POIData {
        return poiData
    }
    
    /**
     * Actualizează poziția POI-ului
     */
    fun updatePosition(newPosition: Float3) {
        position = Position(newPosition.x, newPosition.y, newPosition.z)
        markerNode?.position = position
    }
    
    /**
     * Curăță resursele
     */
    fun cleanup() {
        Log.d(TAG, "Curățare POINode pentru: ${poiData.name}")
    }
}

/**
 * Manager pentru toate POI-urile din scenă
 * Gestionează colecția de POI-uri și operațiile pe ele
 */
class POIManager(
    private val context: Context,
    private val sceneView: SceneView
) {
    
    companion object {
        private const val TAG = "POIManager"
    }
    
    private val poiNodes = mutableListOf<POINode>()
    
    /**
     * Adaugă POI-uri în scenă
     */
    fun addPOIs(poiDataList: List<POIData>, markerModel: io.github.sceneview.node.ModelNode?, onPOIClicked: (POIData) -> Unit) {
        // Curăță POI-urile existente
        poiNodes.forEach { node ->
            val marker = node.getMarkerNode()
            if (marker != null) {
                sceneView.removeChildNode(marker)
            }
            node.cleanup()
        }
        poiNodes.clear()
        
        poiDataList.forEach { poiData ->
            val poiNode = POINode(context, poiData) { clickedPOI ->
                onPOIClicked(clickedPOI)
            }
            
            // Inițializează marker-ul dacă există
            if (markerModel != null) {
                poiNode.initializeMarker(markerModel)
                
                // Adaugă marker-ul în scenă
                val marker = poiNode.getMarkerNode()
                if (marker != null) {
                    sceneView.addChildNode(marker)
                }
            }
            
            // Adaugă POI-ul în listă (nu în scenă direct, doar marker-ul)
            poiNodes.add(poiNode)
            
            Log.d(TAG, "POI adăugat: ${poiData.name} la poziția ${poiData.position}")
        }
        
        Log.d(TAG, "Total POI-uri adăugate: ${poiNodes.size}")
    }
    
    /**
     * Elimină toate POI-urile
     */
    fun removeAllPOIs() {
        poiNodes.clear()
        Log.d(TAG, "Toate POI-urile au fost eliminate")
    }
    
    /**
     * Caută și returnează un POI node după ID
     */
    fun findPOINode(poiId: String): POINode? {
        return poiNodes.find { it.poiData.id == poiId }
    }
    
    /**
     * Caută un POI după nume
     */
    fun findPOIByName(name: String): POINode? {
        val lowercaseName = name.lowercase()
        return poiNodes.find { 
            it.poiData.name.lowercase().contains(lowercaseName) ||
            it.poiData.description.lowercase().contains(lowercaseName)
        }
    }
    
    /**
     * Afișează doar POI-urile pentru un anumit etaj
     */
    fun filterByFloor(floor: Int) {
        poiNodes.forEach { node ->
            val shouldShow = node.isOnFloor(floor)
            node.isVisible = shouldShow
        }
        
        val visibleCount = poiNodes.count { it.isVisible }
        Log.d(TAG, "Filtrat pentru etajul $floor: $visibleCount POI-uri vizibile")
    }
    
    /**
     * Obține toate POI-urile
     */
    fun getAllPOIs(): List<POINode> {
        return poiNodes.toList()
    }
    
    /**
     * Obține doar POI-urile vizibile
     */
    fun getVisiblePOIs(): List<POINode> {
        return poiNodes.filter { it.isVisible }
    }
    
    /**
     * Simulează click pe un POI
     */
    fun clickPOI(poiId: String) {
        findPOINode(poiId)?.handleClick()
    }
    
    /**
     * Gestionează evenimentele de touch pentru a detecta click-uri pe POI-uri
     */
    fun onTouch(event: android.view.MotionEvent): Boolean {
        if (event.action != android.view.MotionEvent.ACTION_DOWN) {
            return false
        }
        
        // TODO: Implementează raycast pentru detectarea POI-urilor
        // Pentru moment, returnăm false
        return false
    }
    
    /**
     * Curăță toate resursele
     */
    fun cleanup() {
        removeAllPOIs()
        Log.d(TAG, "POIManager cleanup complet")
    }
}
