package com.example.paamchssma

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

/**
 * MainActivity simplificată pentru testare
 * Pornește fără crash și testează pas cu pas
 */
class MainActivitySimple : Activity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "=== APLICAȚIE PORNEȘTE ===")
            
            // Setează layout-ul SIMPLU (fără SceneView)
            setContentView(R.layout.activity_main_simple)
            Log.d(TAG, "✅ Layout setat")
            
            // Afișează mesaj de succes
            Toast.makeText(this, "✅ Aplicație pornită cu succes!", Toast.LENGTH_LONG).show()
            Log.d(TAG, "✅ Toast afișat")
            
            Log.d(TAG, "✅ Aplicația funcționează - Debugging Mode")
            
            // TODO: Adaugă inițializări pas cu pas
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ EROARE în onCreate: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(this, "EROARE: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

