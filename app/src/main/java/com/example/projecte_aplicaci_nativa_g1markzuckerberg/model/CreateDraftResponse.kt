package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

import com.google.gson.annotations.SerializedName

data class CreateDraftResponse(
    @SerializedName("tempDraft")
    val tempDraft: TempPlantillaResponse
)
