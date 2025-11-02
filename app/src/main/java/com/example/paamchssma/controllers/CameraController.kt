package com.example.paamchssma.controllers

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.example.paamchssma.math.Float3
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import kotlin.math.cos
import kotlin.math.sin

/**
 * Controller pentru gestionarea camerei 3D
 * Gestionează touch gestures, zoom, rotație și navigare
 */
class CameraController(
    private val context: Context,
    private val sceneView: SceneView
) : SensorEventListener {
    
    companion object {
        private const val TAG = "CameraController"
        private const val ROTATION_SPEED = 0.3f
        private const val ZOOM_SPEED = 0.1f
        private const val PAN_SPEED = 0.01f
        private const val SENSOR_SENSITIVITY = 0.5f
    }
    
    // Poziția și orientarea camerei
    private var cameraDistance = 15.0f // Mai departe pentru a vedea modelul complet
    private var cameraAngleX = 0.0f // Rotație orizontală
    private var cameraAngleY = 30.0f // Rotație verticală (privește de sus)
    private var cameraTarget = Float3(0f, 0f, -5f) // Privește către model
    
    // Gesture detectors
    private var scaleGestureDetector: ScaleGestureDetector
    private var gestureDetector: GestureDetector
    
    // Sensor manager pentru giroscop și accelerometru
    private val sensorManager: SensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    // Flag pentru a activa/dezactiva controlul prin senzori
    var sensorControlEnabled = false
        set(value) {
            field = value
            if (value) {
                startSensorListening()
            } else {
                stopSensorListening()
            }
        }
    
    init {
        // Inițializează scale gesture detector pentru zoom
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                handleZoom(detector.scaleFactor)
                return true
            }
        })
        
        // Inițializează gesture detector pentru drag și fling
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                handleRotation(distanceX, distanceY)
                return true
            }
            
            override fun onDoubleTap(e: MotionEvent): Boolean {
                recenterCamera()
                return true
            }
        })
        
        // Setează poziția inițială a camerei
        updateCameraPosition()
    }
    
    /**
     * Gestionează evenimente touch
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        var handled = scaleGestureDetector.onTouchEvent(event)
        handled = gestureDetector.onTouchEvent(event) || handled
        return handled
    }
    
    /**
     * Gestionează zoom-ul camerei
     */
    private fun handleZoom(scaleFactor: Float) {
        cameraDistance *= (1.0f / scaleFactor)
        cameraDistance = cameraDistance.coerceIn(3.0f, 30.0f)
        updateCameraPosition()
    }
    
    /**
     * Gestionează rotația camerei
     */
    private fun handleRotation(deltaX: Float, deltaY: Float) {
        cameraAngleX += deltaX * ROTATION_SPEED
        cameraAngleY -= deltaY * ROTATION_SPEED
        
        // Limitează unghiul vertical pentru a preveni inversarea
        cameraAngleY = cameraAngleY.coerceIn(-89.0f, 89.0f)
        
        updateCameraPosition()
    }
    
    /**
     * Actualizează poziția camerei bazată pe unghiuri și distanță
     */
    private fun updateCameraPosition() {
        // Convertește unghiurile în radiani
        val angleXRad = Math.toRadians(cameraAngleX.toDouble())
        val angleYRad = Math.toRadians(cameraAngleY.toDouble())
        
        // Calculează poziția camerei pe o sferă în jurul target-ului
        val x = cameraTarget.x + cameraDistance * cos(angleYRad) * sin(angleXRad)
        val y = cameraTarget.y + cameraDistance * sin(angleYRad)
        val z = cameraTarget.z + cameraDistance * cos(angleYRad) * cos(angleXRad)
        
        // Actualizează camera în SceneView
        sceneView.cameraNode.apply {
            position = Position(x.toFloat(), y.toFloat(), z.toFloat())
            lookAt(Position(cameraTarget.x, cameraTarget.y, cameraTarget.z))
        }
        
        Log.d(TAG, "Camera position: ($x, $y, $z), target: $cameraTarget")
    }
    
    /**
     * Recentrează camera la poziția inițială
     */
    fun recenterCamera() {
        cameraDistance = 10.0f
        cameraAngleX = 0.0f
        cameraAngleY = 30.0f
        cameraTarget = Float3(0f, 2f, 0f)
        updateCameraPosition()
        Log.d(TAG, "Camera recentered")
    }
    
    /**
     * Mută camera către un punct specific (pentru navigare la POI)
     */
    fun moveCameraToPosition(targetPosition: Float3, distance: Float = 5.0f, animated: Boolean = true) {
        if (animated) {
            // Animație smoothă către noua poziție
            animateCameraTransition(targetPosition, distance)
        } else {
            cameraTarget = targetPosition
            cameraDistance = distance
            updateCameraPosition()
        }
    }
    
    /**
     * Animație pentru tranziția camerei
     */
    private fun animateCameraTransition(targetPosition: Float3, targetDistance: Float) {
        // Implementare simplă - în producție ar trebui să folosești ValueAnimator
        val startTarget = cameraTarget
        val startDistance = cameraDistance
        
        val steps = 30
        var currentStep = 0
        
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (currentStep < steps) {
                    val progress = currentStep.toFloat() / steps
                    
                    // Interpolează poziția
                    cameraTarget = Float3(
                        startTarget.x + (targetPosition.x - startTarget.x) * progress,
                        startTarget.y + (targetPosition.y - startTarget.y) * progress,
                        startTarget.z + (targetPosition.z - startTarget.z) * progress
                    )
                    
                    // Interpolează distanța
                    cameraDistance = startDistance + (targetDistance - startDistance) * progress
                    
                    updateCameraPosition()
                    
                    currentStep++
                    handler.postDelayed(this, 16) // ~60 FPS
                } else {
                    // Finalizează la poziția exactă
                    cameraTarget = targetPosition
                    cameraDistance = targetDistance
                    updateCameraPosition()
                }
            }
        }
        
        handler.post(runnable)
    }
    
    /**
     * Pornește ascultarea senzorilor
     */
    private fun startSensorListening() {
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        Log.d(TAG, "Sensor listening started")
    }
    
    /**
     * Oprește ascultarea senzorilor
     */
    private fun stopSensorListening() {
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Sensor listening stopped")
    }
    
    /**
     * Callback pentru evenimente de la senzori
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (!sensorControlEnabled || event == null) return
        
        when (event.sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                // Giroscopul oferă viteza de rotație
                val deltaX = event.values[0] * SENSOR_SENSITIVITY
                val deltaY = event.values[1] * SENSOR_SENSITIVITY
                
                handleRotation(-deltaY * 10, deltaX * 10)
            }
            
            Sensor.TYPE_ACCELEROMETER -> {
                // Accelerometrul poate fi folosit pentru a detecta înclinarea
                // Implementare opțională pentru efecte suplimentare
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nu este necesar pentru această implementare
    }
    
    /**
     * Curăță resursele
     */
    fun cleanup() {
        stopSensorListening()
    }
}

