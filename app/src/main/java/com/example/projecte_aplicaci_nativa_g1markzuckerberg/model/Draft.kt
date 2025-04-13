package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

// Representa la información de la ronda que se enviará en la petición.
data class RoundRequest(
    val ending_at: String,  // En formato ISO, por ejemplo "2025-04-10T23:59:59Z"
    val season_id: Int,
    val name: String
)

// Petición para crear el draft.
data class CreateDraftRequest(
    val formation: String,
    val ligaId: Int
)

// Respuesta al crear el draft: se retorna la plantilla temporal generada.
data class TempPlantillaResponse(
    @SerializedName("id_plantilla")
    val idPlantilla: Int,
    @SerializedName("playerOptions")
    private val playerOptionsRaw: String?
) {
    val playerOptions: List<List<PlayerOption>>? by lazy {
        if (playerOptionsRaw.isNullOrBlank()) null
        else {
            try {
                Gson().fromJson(
                    playerOptionsRaw,
                    object : TypeToken<List<List<PlayerOption>>>() {}.type
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

data class PlayerOption(
    val id: String,
    val displayName: String,
    val positionId: Int,
    val imagePath: String,
    val estrellas: Int,
    val puntos_totales: Int
)


// Declaramos PositionOptions: cada posición es un tuple de 5 elementos.
// Podrías definirlo de modo más estricto; a modo de ejemplo, lo definimos así:
typealias PositionOptions = List<Any>
// Se espera que los primeros 4 elementos sean de tipo Player
// y el quinto sea un Int? que indica el índice seleccionado (0 a 3).

// Petición para actualizar el draft.
data class UpdateDraftRequest(
    val plantillaId: Int,
    val playerOptions: List<PositionOptions>
)

// Petición para guardar el draft final.
data class SaveDraftRequest(
    val tempDraft: TempPlantillaResponse
)

// Respuesta genérica para algunas operaciones (por ejemplo, update o save).
data class ApiResponse(
    val message: String
)

// Respuesta para la consulta del draft.
data class GetDraftResponse(
    val plantilla: Plantilla,
    val players: List<Player>
)

// Otras clases de modelo (ajústalas según tus necesidades)
data class Plantilla(
    val id: Int,
    val teamId: Int,
    val seasonId: Int,
    val name: String,
    val finalized: Boolean
)
data class Player(
    val id: String,
    val displayName: String,
    val positionId: Int,
    val imagePath: String,
    val estrellas: Int,
    val puntos_totales: String
)

