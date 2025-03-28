package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture

// Extensión para obtener los nombres de los equipos
fun Fixture.getTeams(): Pair<String, String> {
    val teams = name.split(" vs ")
    return if (teams.size >= 2) teams[0] to teams[1] else (name to "")
}

// Extensión para formatear el timestamp a una cadena de fecha/hora
fun Fixture.getFormattedDateTime(): String {
    val instant = Instant.ofEpochSecond(starting_at_timestamp)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return formatter.withZone(ZoneId.systemDefault()).format(instant)
}
