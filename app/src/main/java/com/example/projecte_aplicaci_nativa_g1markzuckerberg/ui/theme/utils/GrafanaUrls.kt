package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient

fun grafanaUserUrl(ligaId: String, usuarioId: String) =
    "${RetrofitClient.BASE_URL}api/v1/grafana/graficoUser/$ligaId/$usuarioId"

fun grafanaPlayerUrl(jugadorId: Int) =
    "${RetrofitClient.BASE_URL}api/v1/grafana/grafico/$jugadorId"
