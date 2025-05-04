package com.example.projecte_aplicaci_nativa_g1markzuckerberg.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject

object SocketHandler {

    private var socket: Socket? = null

    fun connect(userId: Int, token: String?) {
        if (socket != null) return
        val opts = IO.Options().apply {
            transports = arrayOf(WebSocket.NAME, Polling.NAME)
            query = "userId=$userId"
            extraHeaders = token?.let { mapOf("Authorization" to listOf("Bearer $it")) } ?: emptyMap()
            reconnection = true
        }
        socket = IO.socket("http://10.0.2.2:3000", opts).also { socket ->
            socket.connect()

            socket.on(Socket.EVENT_CONNECT) {
                Log.d("SocketIO", "✅ Socket conectado")
                socket.emit("join", "user_$userId")
                Log.d("SocketIO", "📡 Emitido join a sala: user_$userId")
            }

            socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("SocketIO", "❌ Error de conexión: ${args.joinToString()}")
            }

            socket.on(Socket.EVENT_DISCONNECT) {
                Log.d("SocketIO", "🚪 Socket desconectado")
            }
        }
    }

    fun onNotification(listener: (JSONObject) -> Unit) {
        socket?.on("notification") { args ->
            (args.getOrNull(0) as? JSONObject)?.let(listener)
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }
}
