package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model
import com.google.gson.annotations.SerializedName

// Petición para crear el draft.
data class CreateDraftRequest(
    val formation: String,
    val ligaId: Int
)

data class TempPlantillaResponse(
    @SerializedName("id_plantilla") val idPlantilla: Int,
    @SerializedName("playerOptions") val playerOptions: List<List<PlayerOption>>?
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
    val playerOptions: List<PositionOptions>
)

// Petición para guardar el draft final.
data class SaveDraftRequest(
    @SerializedName("tempDraft")
    val tempDraft: TempDraftFinal
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
data class TempDraftData(
    @SerializedName("id_plantilla")
    val id_plantilla: Int,
    @SerializedName("playerOptions")
    val playerOptions: List<List<Any>>
)

data class SaveDraftData(
    @SerializedName("tempDraft")
    val tempDraft: TempDraftData
)
data class TempDraftFinal(
    @SerializedName("id_plantilla")
    val idPlantilla: Int,
    @SerializedName("playerOptions")
    val playerOptions: List<List<Any>> // Cada grupo: [jugador0, jugador1, jugador2, jugador3, índiceSeleccionado]
)



