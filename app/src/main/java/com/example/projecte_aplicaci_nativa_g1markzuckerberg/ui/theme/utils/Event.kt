package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

/**
 * Clase para encapsular eventos de un único uso.
 */
open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    /**
     * Devuelve el contenido si aún no ha sido manejado.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Permite acceder al contenido, incluso si ya fue manejado.
     */
    fun peekContent(): T = content
}
