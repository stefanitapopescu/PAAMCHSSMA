package com.example.paamchssma.data

import com.example.paamchssma.math.Float3

/**
 * Data class pentru un punct de interes (Point of Interest)
 * Reprezintă o locație specifică în modelul 3D al facultății
 */
data class POIData(
    val id: String,
    val name: String,
    val position: Float3, // Poziția în spațiul 3D
    val description: String,
    val category: POICategory
)

/**
 * Categorii de puncte de interes
 */
enum class POICategory {
    SECRETARIAT,
    DECANAT,
    SALA_PROFESORI,
    LABORATOR,
    SALA_CURS,
    BIBLIOTECA,
    AMFITEATRU,
    ALTE
}

/**
 * Obiect care conține toate punctele de interes predefinite
 * Acestea sunt coordonate exemplu - trebuie ajustate în funcție de modelul 3D real
 */
object POIRepository {
    
    fun getAllPOIs(): List<POIData> {
        return listOf(
            // Etajul 1
            POIData(
                id = "secretariat_1",
                name = "Secretariat",
                position = Float3(-2.0f, 0.5f, 3.0f),
                description = "Secretariatul studenților - Etaj 1, Cameră 101",
                category = POICategory.SECRETARIAT
            ),
            POIData(
                id = "decanat_1",
                name = "Decanat",
                position = Float3(3.0f, 0.5f, 4.0f),
                description = "Biroul Decanului - Etaj 1, Cameră 105",
                category = POICategory.DECANAT
            ),
            POIData(
                id = "sala_prof_1",
                name = "Sala Profesori A",
                position = Float3(0.0f, 0.5f, -3.0f),
                description = "Sala Profesorilor - Etaj 1, Cameră 110",
                category = POICategory.SALA_PROFESORI
            ),
            
            // Etajul 2
            POIData(
                id = "laborator_1",
                name = "Laborator Informatică",
                position = Float3(-3.5f, 3.5f, 2.0f),
                description = "Laborator Informatică - Etaj 2, Cameră 201",
                category = POICategory.LABORATOR
            ),
            POIData(
                id = "laborator_2",
                name = "Laborator Electronică",
                position = Float3(2.5f, 3.5f, -1.0f),
                description = "Laborator Electronică - Etaj 2, Cameră 205",
                category = POICategory.LABORATOR
            ),
            POIData(
                id = "sala_curs_1",
                name = "Sala C201",
                position = Float3(0.0f, 3.5f, 3.5f),
                description = "Sala de curs - Etaj 2, Cameră 210",
                category = POICategory.SALA_CURS
            ),
            
            // Etajul 3
            POIData(
                id = "biblioteca",
                name = "Biblioteca",
                position = Float3(-1.5f, 6.5f, 0.0f),
                description = "Biblioteca Facultății - Etaj 3",
                category = POICategory.BIBLIOTECA
            ),
            POIData(
                id = "amfiteatru",
                name = "Amfiteatru A",
                position = Float3(2.0f, 6.5f, 2.5f),
                description = "Amfiteatru Mare - Etaj 3",
                category = POICategory.AMFITEATRU
            ),
            POIData(
                id = "sala_prof_2",
                name = "Sala Profesori B",
                position = Float3(-3.0f, 6.5f, -2.0f),
                description = "Sala Profesorilor - Etaj 3",
                category = POICategory.SALA_PROFESORI
            )
        )
    }
    
    /**
     * Caută un POI după nume sau descriere
     */
    fun searchPOI(query: String): POIData? {
        val lowercaseQuery = query.lowercase()
        return getAllPOIs().firstOrNull { 
            it.name.lowercase().contains(lowercaseQuery) ||
            it.description.lowercase().contains(lowercaseQuery)
        }
    }
    
    /**
     * Obține POI-urile pentru un etaj specific
     */
    fun getPOIsForFloor(floor: Int): List<POIData> {
        return when(floor) {
            1 -> getAllPOIs().filter { it.position.y < 2.0f }
            2 -> getAllPOIs().filter { it.position.y in 2.0f..5.0f }
            3 -> getAllPOIs().filter { it.position.y > 5.0f }
            else -> emptyList()
        }
    }
}

