package com.example.paamchssma.controllers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException
import io.github.sceneview.SceneView

/**
 * Controller pentru funcționalitatea AR (Augmented Reality)
 * Gestionează ARCore și permite suprapunerea modelului 3D peste mediul real
 */
class ARController(
    private val activity: Activity,
    private val sceneView: SceneView
) {
    
    companion object {
        private const val TAG = "ARController"
        private const val CAMERA_PERMISSION_CODE = 100
    }
    
    private var arSession: Session? = null
    private var isAREnabled = false
    
    /**
     * Verifică dacă dispozitivul suportă ARCore
     */
    fun isARSupported(): Boolean {
        return try {
            when (ArCoreApk.getInstance().checkAvailability(activity)) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED,
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD,
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> true
                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Eroare la verificarea suportului AR: ${e.message}", e)
            false
        }
    }
    
    /**
     * Verifică permisiunile pentru cameră
     */
    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Solicită permisiunea pentru cameră
     */
    fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }
    
    /**
     * Inițializează sesiunea AR
     */
    fun initializeARSession(): Boolean {
        if (!isARSupported()) {
            Toast.makeText(
                activity,
                "Acest dispozitiv nu suportă ARCore",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        
        if (!hasCameraPermission()) {
            requestCameraPermission()
            return false
        }
        
        return try {
            // Verifică dacă ARCore este instalat și actualizat
            when (ArCoreApk.getInstance().requestInstall(activity, true)) {
                ArCoreApk.InstallStatus.INSTALLED -> {
                    // ARCore este instalat și gata de folosit
                    createARSession()
                    true
                }
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    // ARCore necesită instalare sau actualizare
                    Toast.makeText(
                        activity,
                        "ARCore necesită instalare. Te rugăm să instalezi.",
                        Toast.LENGTH_LONG
                    ).show()
                    false
                }
            }
        } catch (e: UnavailableException) {
            Log.e(TAG, "Eroare la inițializarea AR: ${e.message}", e)
            Toast.makeText(
                activity,
                "Nu se poate inițializa ARCore: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }
    
    /**
     * Creează sesiunea AR
     */
    private fun createARSession() {
        try {
            arSession = Session(activity)
            Log.d(TAG, "Sesiune AR creată cu succes")
        } catch (e: UnavailableException) {
            Log.e(TAG, "Nu se poate crea sesiunea AR: ${e.message}", e)
            arSession = null
        }
    }
    
    /**
     * Activează/Dezactivează modul AR
     */
    fun toggleARMode(): Boolean {
        if (!isARSupported()) {
            Toast.makeText(
                activity,
                "Modul AR nu este disponibil pe acest dispozitiv",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        
        isAREnabled = !isAREnabled
        
        if (isAREnabled) {
            enableAR()
        } else {
            disableAR()
        }
        
        return isAREnabled
    }
    
    /**
     * Activează modul AR
     */
    private fun enableAR() {
        if (arSession == null) {
            if (!initializeARSession()) {
                isAREnabled = false
                return
            }
        }
        
        try {
            // Configurează ARCore pentru tracking
            arSession?.resume()
            
            Toast.makeText(
                activity,
                "Modul AR activat - Mișcă telefonul pentru a plasa modelul",
                Toast.LENGTH_LONG
            ).show()
            
            Log.d(TAG, "Modul AR activat")
        } catch (e: Exception) {
            Log.e(TAG, "Eroare la activarea AR: ${e.message}", e)
            isAREnabled = false
            Toast.makeText(
                activity,
                "Nu se poate activa modul AR",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    /**
     * Dezactivează modul AR
     */
    private fun disableAR() {
        try {
            arSession?.pause()
            
            Toast.makeText(
                activity,
                "Modul AR dezactivat",
                Toast.LENGTH_SHORT
            ).show()
            
            Log.d(TAG, "Modul AR dezactivat")
        } catch (e: Exception) {
            Log.e(TAG, "Eroare la dezactivarea AR: ${e.message}", e)
        }
    }
    
    /**
     * Returnează starea curentă a modului AR
     */
    fun isARModeEnabled(): Boolean = isAREnabled
    
    /**
     * Obține sesiunea AR curentă
     */
    fun getARSession(): Session? = arSession
    
    /**
     * Curăță resursele AR
     */
    fun cleanup() {
        try {
            arSession?.close()
            arSession = null
            Log.d(TAG, "Resurse AR curățate")
        } catch (e: Exception) {
            Log.e(TAG, "Eroare la curățarea resurselor AR: ${e.message}", e)
        }
    }
    
    /**
     * Callback pentru rezultatul cererii de permisiuni
     */
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray): Boolean {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permisiune acordată
                return initializeARSession()
            } else {
                Toast.makeText(
                    activity,
                    "Permisiunea pentru cameră este necesară pentru modul AR",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return false
    }
}

