package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model
import com.google.gson.annotations.SerializedName

// Petición para crear el draft.
data class CreateDraftRequest(
    val formation: String,
    val ligaId: Int
)

data class TempPlantillaResponse(
    @SerializedName("id_plantilla") val idPlantilla: Int,
    @SerializedName("playerOptions") val playerOptions: String?, // Cambiado a String
    @SerializedName("formation") val formation: String
)




data class PlayerOption(
    val id: Int,
    val displayName: String,
    val positionId: Int,
    val imagePath: String,
    val estrellas: Int,
    val puntos_totales: Int
)




data class PositionOptions(
    val player1: PlayerOption,
    val player2: PlayerOption,
    val player3: PlayerOption,
    val player4: PlayerOption,
    val chosenIndex: Int? // Puede ser null si aún no se ha seleccionado
)

// Petición para actualizar el draft.
data class UpdateDraftRequest(
    val plantillaId: Int,
    val playerOptions: List<List<Any?>>
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
    val formation: String,
    val name: String,
    val finalized: Boolean
)
data class Player(
    val id: String,
    val displayName: String,
    val positionId: Int,
    val imagePath: String,
    val estrellas: Int,
    val puntos_totales: String,
    @SerializedName("puntos_jornada")
    val puntos_jornada: String = "0"
)
data class TempDraftData(
    @SerializedName("id_plantilla")
    val id_plantilla: Int,
    @SerializedName("playerOptions")
    val playerOptions: List<List<Any>>
)

data class SaveDraftRequest(
    @SerializedName("tempDraft")
    val tempDraft: TempDraftFinal
)

data class TempDraftFinal(
    @SerializedName("id_plantilla")
    val idPlantilla: Int,
    @SerializedName("playerOptions")
    val playerOptions: List<List<Any?>> // Ahora acepta Any? en vez de solo Any
)
data class GetTempDraftResponse(
    @SerializedName("tempDraft")
    val tempDraft: TempPlantillaResponse
)





