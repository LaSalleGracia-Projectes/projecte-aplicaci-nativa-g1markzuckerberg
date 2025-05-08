
# ⚽Fanstasy Draft

## Índice

- [Introducción](#introducción)
- [💻 Tecnologías](#-tecnologías)
    - [Plugins de Gradle](#plugins-de-gradle)
    - [Configuración de Android](#configuración-de-android)
    - [Dependencias clave](#dependencias-clave)
- [🔒 Autenticación y Gestión de Tokens](#-autenticación-y-gestión-de-tokens)
- [Flujo de UI / ViewModel](#flujo-de-ui--viewmodel)
- [🚀 Navegación](#-navegación)
    - [1. Declaración de rutas (`Routes`)](#1-declaración-de-rutas-routes)
    - [2. Tipos de navegación](#2-tipos-de-navegación)
        - [a) Navegación por clics](#a-navegación-por-clics)
        - [b) Navegación con argumentos](#b-navegación-con-argumentos)
        - [c) Control del back-stack](#c-control-del-back-stack)
    - [3. Barra de navegación (`NavbarView`)](#3-barra-de-navegación-navbarview)
    - [Cuándo mostrar la Navbar](#cuándo-mostrar-la-navbar)
- [🎨 Home](#-home)
- [🏗️ Reparto de responsabilidades (Home)](#️-reparto-de-responsabilidades-home)
- [🚀 Acciones disponibles desde Home](#️-acciones-disponibles-desde-home)
- [🎨 LigaView](#-ligaview)
- [🏗️ Reparto de responsabilidades (LigaView)](#️-reparto-de-responsabilidades-ligaview)
- [👤 UserSelfScreen / Perfil](#-userselfscreen--perfil)
- [👥 UserDraftView](#-userdraftview)
- [⚽️ DraftScreen](#️-draftscreen)
- [🎮 Jugadores](#🎮-jugadores)
- [🎯 Detalle de Jugador](#🎯-detalle-de-jugador)
- [🔔 Notifications](#-notifications)
- [⚙️ Settings](#️-settings)
- [🔗 Módulo API / Retrofit – Resumen](#🔗-módulo-api--retrofit--resumen)
- [🎨 Color Reference](#-color-reference)
- [👥 Authors](#-authors)


Fantasy Draft: El Fantasy Fútbol con Draft Semanal es un proyecto académico desarrollado por dos estudiantes de DAM (Desarrollo de Aplicaciones Multiplataforma). Se trata de la propuesta final de su módulo de Desarrollo de Aplicaciones, en la que debían diseñar y programar una aplicación completa, desde la interfaz hasta la lógica de negocio y la conexión con la base de datos.

La idea principal de la app es ofrecer una experiencia de Fantasy Fútbol más dinámica: en lugar de gestionar un equipo fijo toda la temporada, cada semana los usuarios participan en un draft para seleccionar a sus 11 futbolistas. A través de un sistema de “puntos de estrellas” , cada jugador elegirá estratégicamente su alineación semanal y competirá en ligas personalizadas contra otros usuarios. Todo ello desde una plataforma multiplataforma (web y móvil) sincronizada por correo electrónico.


## 💻Tecnologias
### Plugins de Gradle
```groovy
plugins {
    alias(libs.plugins.android.application)       // com.android.application (AGP 8.8.0)
    alias(libs.plugins.kotlin.android)            // org.jetbrains.kotlin.android (Kotlin 2.0.0)
    alias(libs.plugins.kotlin.compose)            // org.jetbrains.kotlin.plugin.compose (Kotlin 2.0.0)
    id("com.google.gms.google-services")          // Google Services (Firebase)
}
```
### Configuración de Android

android {
compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projecte_aplicaci_nativa_g1markzuckerberg"
        minSdk        = 24
        targetSdk     = 35
        versionCode   = 1
        versionName   = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }



### Dependencias clave

- **Jetpack Compose**

    - androidx.compose.ui:ui

    - `androidx.compose.material3:material3`

    - `androidx.navigation:navigation-compose`

- **Kotlin Coroutines & Lifecycle**

    - `androidx.lifecycle:lifecycle-runtime-ktx`

    - `org.jetbrains.kotlinx:kotlinx-coroutines-android`

- **Networking**

    - `com.squareup.retrofit2:retrofit`

    - `com.squareup.retrofit2:converter-gson`

    - `com.squareup.okhttp3:logging-interceptor`

- **Imágenes & Animaciones**

    - `io.coil-kt:coil-compose:2.2.2`

    - `com.airbnb.android:lottie-compose:6.0.0`

- **Persistencia y Background**

    - `androidx.datastore:datastore-preferences:1.1.0`

    - `androidx.work:work-runtime-ktx:2.9.0`

- **Firebase & Google**

    - `com.google.firebase:firebase-messaging` (BoM 33.13.0)

    - `com.google.android.gms:play-services-auth:21.1.0`

- **Tiempo Real**

    - `io.socket:socket.io-client:2.1.0`
## 🔒 Autenticación y Gestión de Tokens

Nuestro cliente Android usa Jetpack Compose y Retrofit para:

1. **Endpoints clave**
    - `POST /api/v1/auth/loginMobile` → **login**
    - `POST /api/v1/auth/signupMobile` → **registro**
    - `POST /api/v1/auth/google/mobile/token` → **OAuth Google**
    - `POST /api/v1/auth/logoutMobile` → **logout**

2. **Almacenamiento seguro de JWT**
    - **`TokenManager`** (DataStore Preferences) guarda el `mobileToken`.
    - `AuthRepository` invoca `tokenManager.saveToken(token)` tras un login/registro/OAuth.
    - `AuthRepository.getToken()` expone el token para usarlo.

3. **Intercepción de peticiones**
   ```kotlin
   class AuthInterceptor(val tokenProvider: ()->String?) : Interceptor {
     override fun intercept(chain: Chain): Response {
       val token = tokenProvider() ?: return chain.proceed(chain.request())
       val req = chain.request().newBuilder()
         .addHeader("Authorization", "Bearer $token")
         .build()
       return chain.proceed(req)
     }
   }
   // OkHttpClient.Builder().addInterceptor(AuthInterceptor { tokenManager.getToken() })

##Flujo de UI / ViewModel

- **LoginViewModel** / **RegisterEmailViewModel** llaman a `authRepo.login(...)` o `.register(...)`.

- El repositorio guarda el JWT, activa navegación a la pantalla protegida.

- Para Google OAuth:

1. `viewModel.initGoogle(context)` → `GoogleSignInClient`

2. Lanzar `startActivityForResult(...)` → obtener `idToken`

3. `viewModel.handleGoogleToken(idToken)`

5\. **Uso de token en peticiones**

- Tras el login, cualquier llamada a servicios protegidos (p. ej. perfil de usuario) lleva el header `Authorization: Bearer <token>`.

- Si el token expira o no existe, el backend responderá 401 y debes redirigir al login.
## 🚀 Navegación

### 📖 1. Declaración de rutas (`Routes`)

- Cada pantalla se define como un objeto en una `sealed class Routes`.

- **Rutas sin parámetros**:

```kotlin
    object Home : Routes("home")
```

- **Rutas con parámetros**:

```kotlin

    object LigaView : Routes("liga_view/{ligaCode}") {

      fun createRoute(ligaCode: String) = "liga_view/$ligaCode"

    }
```

- Para navegar, se invoca:

```kotlin

    navController.navigate(Routes.LigaView.createRoute("ABC123"))

```

---

### 🔄 2. Tipos de navegación

#### a) Navegación por clics

- Cualquier botón, tarjeta o `IconButton` llama a:

```kotlin

    navController.navigate("ruta_destino")

```

- Con parámetros:

```kotlin

    navController.navigate(Routes.UserDraftView.createRoute(...))

```

#### b) Navegación con argumentos

- En el `NavHost` declaras:

```kotlin

    composable(

      "userdraft/{leagueId}/{userId}",

      arguments = listOf(

        navArgument("leagueId") { type = NavType.StringType },

        navArgument("userId")   { type = NavType.StringType }

      )

    ) { backStackEntry ->

      val leagueId = backStackEntry.arguments?.getString("leagueId")

      val userId   = backStackEntry.arguments?.getString("userId")

      // ...

    }
```

- El framework extrae los valores de `backStackEntry.arguments`.

#### c) Control del back-stack

- Puedes limpiar pantallas anteriores al navegar:

```kotlin
    navController.navigate(target) {

      popUpTo("login") { inclusive = true }

    }

 ```

→ útil tras un login exitoso para que el usuario no vuelva atrás.

---

### 📱 3. Barra de navegación (`NavbarView`)

- Se incluye como `bottomBar` en un `Scaffold`.

- Solo aparece en rutas concretas (lista blanca de pantallas donde sí mostrarla).

- Cada ítem es un pequeño componente:

```kotlin

    @Composable 

    fun NavBarItem(iconResId: Int, onClick: ()->Unit) { ... }

```

- Entre ítems colocamos divisores verticales con `VerticalDivider()`.

**Flujo de usuario típico**:

1\. Pulsa **Inicio** → `navController.navigate("home_loged")`

2\. Pulsa **Perfil** → `navController.navigate("user_self")`

3\. Pulsar **Jugadores**, **Notificaciones**, **Ajustes**, etc., llama a su respectiva ruta.

---

### 🧭 Cuándo mostrar la Navbar

- Observamos la ruta actual:

```kotlin

    val currentRoute = navController

      .currentBackStackEntryAsState()

      .value

      ?.destination

      ?.route

```

- Si `currentRoute` está en la lista permitida, mostramos la barra; si no (p.ej. login), la ocultamos.

---
## 🎨 Home

### Header

- Un bloque con el logo de la app y el título **"FantasyDraft"** sobre un degradado horizontal.

### Subtítulo

- Un recuadro bajo la cabecera con un mensaje tipo **"¡Crea o únete a tu liga!"**.

### Sección "Mis ligas"

- **Lista de ligas** (cada fila muestra):

- Nombre de la liga  

  - 👥 Número de usuarios (icono + contador)  

  - 🏆 Puntos totales  

- **Botones**:  

  - **Unirse** (abre diálogo de código)  

  - **Crear** (abre diálogo de creación con nombre + imagen)

### Diálogos modales

- **Crear liga**: formulario de nombre + selector de imagen  

- **Unirse a liga**: input de código  

- **Editar liga** (solo capitán): cambiar nombre / imagen  

- **Compartir código**: muestra el código de invitación  

- **Confirmaciones y errores**: alertas personalizadas

### Sección "Jornada actual"

- **Encabezado**: "Jornada X"  

- **Lista de partidos**:  

  - Equipos local y visitante  

  - Escudos  

  - Fecha y hora formateada

### Indicador de carga

- Mientras el ViewModel carga datos, una animación ocupa el contenido.

---

## 🏗️ Reparto de responsabilidades


**View**
- Renderiza listas, botones, cabeceras y diálogos
- Maneja visibilidad de modales
- Captura clics y navegación<br>- Se suscribe al ViewModel

**ViewModel**
- Orquesta llamadas a repositorios/red
- Exposa estados (`LiveData`/`State`): ligas, fixtures, flags de carga, eventos de error
- Gestiona creación, unión, edición y salida de ligas

---

## 🚀 Acciones disponibles desde **Home**

- **Ver mis ligas**: navegar al detalle de cada liga  

- **Crear liga**: abrir diálogo, enviar nombre e imagen al backend  

- **Unirse a liga**: abrir diálogo, introducir código y solicitar unión  

- **Editar liga**: (solo capitán) renombrar o cambiar imagen de liga  

- **Compartir código**: mostrar/copiar código de invitación  

- **Abandonar liga**: confirmar y notificar salida al backend  

- **Ver jornada**: consultar partidos de la jornada actual  

- **Refrescar datos**: recarga automática al entrar o tras cada acción  

- **Mostrar errores y confirmaciones**: validaciones, códigos inválidos, fallo de red, etc.

---
##  🎨 LigaView

### 1. Header
- **Botón de retroceso** (flecha) que vuelve a la pantalla anterior.

- **Título**: nombre de la liga, centrado sobre un degradado horizontal.

- **Icono de la liga**: imagen circular con borde, cargada desde el backend con autorización.

### 2. Selección de jornada & creación de draft
- **Dropdown de jornadas**: permite elegir desde la jornada de creación hasta la actual, más “Total”.

- **Botón “Crear draft”**: lanza la petición al backend; si ya existe draft, abre diálogo de formación.

### 3. Ranking de usuarios
- **Lista vertical** de usuarios ordenados por posición (🥇🥈🥉):
    - **Avatar** circular de cada usuario (cargado con token).
    - **Nombre de usuario** y **puntos** de la jornada o acumulados.
    - **Estilo de podio** (fondo degradado y borde) para los tres primeros.
    - **Destacado** del usuario actual con un borde degradado.

- **Cada fila es clicable**: navega a la vista de draft de ese usuario.

### 4. Diálogos modales
- **CreateDraftDialog**: seleccionar **formación** antes de crear draft.
- **LeagueCodeDialog**: muestra el código de la liga (para invitar).
- **CustomAlertDialogSingleButton**: muestra errores de draft (“ya tienes un draft…”).

### 5. Indicador de carga
- Mientras se obtienen datos (`isLoading` / `isFetching`), se muestra una animación (FancyLoading).

## 🧑‍💻 ProfileScreen

### 1. Header
- Barra superior con degradado horizontal y título **“Perfil”** centrado.

### 2. Avatar
- Imagen circular del usuario (cargada con token y timestamp para invalidar cache).
- Borde primario y clicable para abrir el selector de nueva foto.

### 3. Campos editables
- **Usuario**: fila clicable con label y nombre actual → abre diálogo para cambiarlo.
- **Fecha de nacimiento**: muestra la fecha formateada → abre `DatePickerDialog`.
- **Contraseña**: muestra “********” → abre diálogo para cambiar contraseña.

### 4. Ligas y gráfico
- Si tiene ligas:
    - **Selector de liga**: botón con icono y nombre de la liga → abre popup para elegir otra.
    - **Gráfico de puntos**: tarjeta con imagen de Grafana scrollable y animación de carga.
- Si no tiene ligas: muestra mensaje “No estás en ninguna liga”.

### 5. Diálogos modales
- **SimpleEditDialog**: editar nombre o contraseña (un solo campo).
- **DatePickerDialog**: elegir nueva fecha de nacimiento.
- **AvatarDialog**: previsualizar y seleccionar imagen de galería.
- **LeaguePopup**: lista de ligas propias para cambiar selección.

### 6. Indicador de carga
- Mientras el ViewModel está en estado **Loading** (datos o edición), muestra animación centralizada.


## 👤 UserDraftView

### 1. Header
- **Back button**: vuelve a la pantalla anterior.
- **Nombre de usuario**: centrado en el título.
- **Avatar** circular con borde, cargado con token y clicable.

### 2. Pestañas (Tabs)
- Dos pestañas: **Usuario** y **Draft**.
- Cambio de pestaña vía `HorizontalPager` o clic en el tab.

### 3. Sección “Usuario”
- **TrainerCard** con:
    - Avatar, nombre, fecha de nacimiento, indicador de capitán y puntos totales.
    - 🔴 **Expulsar** y 🟢 **Hacer capitán**: abren diálogo de confirmación.
- **Histórico**:
    - Gráfico de rendimiento (imagen de Grafana) scrollable horizontalmente.
    - Animación de carga mientras se descarga la gráfica.

### 4. Sección “Draft”
- **Selector de jornada**:
    - `LazyRow` con círculos “J1…Jn”, destaca la jornada activa y muestra sus puntos.
- **Plantilla**:
    - Fondo de cancha con overlay si no hay plantilla.
    - Si hay jugadores, `ReadonlyDraftLayout` dibuja filas según formación (4-3-3, 4-4-2, 3-4-3) y posiciona tarjetas de jugador.

### 5. Diálogos y overlays
- **Confirmación** (CustomAlertDialog): expulsar / hacer capitán.
- **Resultado** (CustomAlertDialogSingleButton): éxito o error.
- **OverlayLoading**: spinner semi-transparente mientras `isLoadingDraft` está activo.

### 6. Estados de carga
- `leagueUserResponse`: controla contenido de la pestaña Usuario.
- `isLoadingDraft`: controla overlay en la pestaña Draft.

---

## 🏗️ Reparto de responsabilidades

| Capa         | Qué hace                                                                                                                                              |
|--------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| **View**     | Renderiza header, tabs, TrainerCard, gráfico, selector de jornadas, plantilla y diálogos; maneja clics, animaciones y scroll en pager y LazyRow/LazyColumn. |
| **ViewModel**| Expone LiveData de pestaña seleccionada, datos de usuario en liga, jugadores y formación del draft, flags de carga; ejecuta peticiones de kick, makeCaptain y fetch de datos. |


## ⚽️ DraftScreen

### 1. Header
- **Back button** (flecha) en la esquina superior izquierda.
- **Título** centrado: “Draft”.
- **Botón “Guardar”** a la derecha:
    - Si la plantilla está completa, abre diálogo de confirmación
    - Si falta algún jugador, muestra alerta de error

### 2. Fondo de cancha
- Imagen de fondo escalada horizontalmente, clippeada bajo el header.

### 3. Plantilla editable
- **Column Layout** que ocupa el resto de la pantalla:
    - Genera filas según la formación seleccionada (4-3-3, 4-4-2, 3-4-3).
    - En cada posición aparece una **PositionCard**:
        - Si no hay jugador asignado → placeholder con nombre de posición y “0 pts”.
        - Si hay jugador → CompactPlayerCard con foto, nombre, puntos y estrellas.
        - Al pulsar, abre un **PlayerSelectionDialog** con hasta 4 candidatos; al elegir, actualiza en servidor.

### 4. Diálogos y overlays
- **LoadingTransitionScreen** mientras se guarda (`isSavingDraft`).
- **CustomAlertDialog** para confirmar “¿Guardar plantilla?”.
- **CustomAlertDialogSingleButton** para error de plantilla incompleta.
- **GuideDialog** (instrucciones de puntuación) al pulsar el botón flotante ℹ️.

### 5. Estados y datos
- `tempDraftResponse` → JSON bruto de opciones de jugadores.
- `parsedPlayerOptions` → lista de listas de `PlayerOption`.
- `selectedPlayers` → mapa mutable de claves (“Delantero_0”…“Portero_0”) a `PlayerOption?`.
- Cada selección dispara `updateDraftOnServer()` en el ViewModel.

---

## 🏗️ Reparto de responsabilidades

| Capa         | Qué hace                                                                                         |
|--------------|-------------------------------------------------------------------------------------------------|
| **View**     | • Renderiza header, fondo y cartas<br>• Gestiona animaciones de cambio de jugador<br>• Muestra y oculta diálogos y alertas  |
| **ViewModel**| • Expone flujo de datos (`tempDraft`, `isSavingDraft`)<br>• Convierte JSON a estructuras internas<br>• Envía actualizaciones de plantilla al backend |

## 🎮 Jugadores

1. **Header**
    - Título “Jugadores” centrado sobre un degradado horizontal.

2. **Filtros**
    - **Equipo**: botón que abre un popup para elegir un equipo (o “Todos”).
    - **Búsqueda**: campo de texto para filtrar por nombre.
    - **Orden puntos**: icono que alterna ascendente/descendente, manteniendo el scroll en la posición actual.

3. **Lista de jugadores**
    - Scroll vertical de tarjetas (**PlayerCard**) con:
        - Foto circular (carga remota con Coil + cache)
        - Nombre y equipo
        - Puntos totales
        - Fondo degradado según estrellas
    - Cada tarjeta es clicable → navega a `PlayerDetail`.

4. **Popup “Seleccionar equipo”**
    - Overlay semitransparente
    - Muestra loader / error / lista de equipos
    - Cada item cierra el popup y aplica el filtro

5. **Botón flotante de estrellas**
    - Al pulsar, cicla filtro de estrellas 0→1→2→…→5→0
    - Icono muestra “✖” si no hay filtro, o dibujo de hasta 5 estrellas

---

## 🏗️ Reparto de responsabilidades

| Capa         | Qué hace                                                                                               |
|--------------|--------------------------------------------------------------------------------------------------------|
| **View**     | • Dibuja header, filtros, lista y popup<br>• Maneja estados UI locales (showTeamPopup, scroll, etc.)<br>• Captura clics y navegación |
| **ViewModel**| • Expone `PlayersUiState` con lista, filtros, orden y loading<br>• Carga datos desde `PlayerRepository`<br>• Aplica filtros y orden en `filtered` |

---

## 🚀 Acciones disponibles

- **Ver detalle**: clic en tarjeta → `PlayerDetail`
- **Filtrar por equipo**: abre popup, selecciona equipo
- **Buscar por nombre**: teclea en el campo de búsqueda
- **Ordenar por puntos**: alterna asc/desc manteniendo scroll
- **Filtrar por estrellas**: botón flotante que recorre niveles de 1–5 estrellas

## 🎯 Detalle de Jugador

1. **Header**
    - Barra con degradado horizontal, botón “Atrás” y nombre del jugador (o texto por defecto).
    - El color del texto se ajusta al modo claro/oscuro.

2. **Estados de carga / error**
    - Mientras carga: animación `FancyLoadingAnimation` centrada.
    - Si hay error: mensaje en color de error, centrado.

3. **Información básica**
    - **Equipo**: logo + nombre en fila horizontal.
    - **Avatar**: imagen circular sobre fondo degradado según estrellas, con borde.
    - **Estrellas**: fila de hasta 5 estrellas doradas.
    - **Puntos totales**: gran texto centrado.

4. **Detalles adicionales**
    - Tarjeta con **posición** (etiqueta – valor).
    - Se traduce el `positionId` a recurso string.

5. **Gráfica de rendimiento**
    - Card que contiene imagen de Grafana en scroll horizontal.
    - Selector de tema (`?theme=dark|light`) según modo.
    - Carga con listener para ocultar la animación cuando termine.

---

## 🏗️ Reparto de responsabilidades

| Capa         | Qué hace                                                                                           |
|--------------|----------------------------------------------------------------------------------------------------|
| **View**     | • Dibuja header, estados (loading/error) y contenido detallado<br>• Maneja scroll y modo oscuro<br>• Captura clic en “Atrás” |
| **ViewModel**| • Expone `player`, `isLoading`, `errorMessage`<br>• Carga datos desde `PlayerRepository`<br>• Formatea URL de imagen y maneja errores |

---

## 🚀 Acciones disponibles

- **Volver atrás**: icono flecha → `popBackStack()`
- **Ver gráfico**: scroll horizontal de rendimiento
- **Interpretar posición**: lectura de texto traducido según ID
## 🔔 Notifications

1. **Header**
    - Título “Notificaciones” centrado sobre un degradado horizontal.

2. **Obtención de token y recarga automática**
    - Se usa `produceState` para esperar al token de `AuthRepository`.
    - Cuando hay token, `forceReloadIfTokenExists()` dispara la carga de notificaciones.

3. **Estados de la UI (`NotificationsUiState`)**
    - **Loading**: animación de carga con `LoadingTransitionScreen`.
    - **Error**: mensaje de error centrado en color de error.
    - **Success**:
        - Si la lista está vacía, muestra “Sin notificaciones”.
        - Si hay datos, `LazyColumn` de `NotificationItem`.

4. **Lista de notificaciones**
    - Cada `NotificationItem` recibe un objeto `Notifications` y muestra:
        - **Icono**: `Notifications`, tintado según tipo de mensaje.
        - **Texto enriquecido**:
            - Se parsea con `parseSpanishMessage()` para extraer tipo, usuario y liga/fecha.
            - `buildAnnotatedString` aplica estilos (colores y negrita) y soporta español, catalán e inglés.
        - **Fecha**: formateada de ISO a `dd/MM/yyyy`.

---

## 🏗️ Reparto de responsabilidades

| Capa           | Qué hace                                                                                                       |
|----------------|-----------------------------------------------------------------------------------------------------------------|
| **View**       | • Renderiza header, estados (loading/error) y lista de `NotificationItem`, • Gestiona token y re-carga automática |
| **ViewModel**  | • Expone `uiState` (Loading/Success/Error), • Lógica de retry y manejo de códigos HTTP e IO                   |
| **Repository** | • Llama a `NotificationsService` vía Retrofit y devuelve la lista o lanza excepción                            |

---

## 🚀 Interacciones disponibles

- **Lectura**: sólo consumo de notificaciones (sin acciones de usuario).
- **Refresco**: automático al disponerse del token o al recargar la vista.
- **Soporte multilenguaje**: adapta textos para es, ca e en.


## ⚙️ Settings

1. **Cabecera**
    - Título “Ajustes” sobre un degradado horizontal (componente `GradientHeader`).

2. **Tarjetas de configuración**
    - **Autores**: tarjeta expandible con información de los creadores.
    - **Contacto**: abre `ContactFormDialog` para enviar un mensaje al equipo.
    - **Modo oscuro**: switch que alterna entre claro/oscuro y persiste en `DataStore` (`ThemePreferences`).
    - **Política de privacidad**: abre `PrivacyPolicyDialog` con texto desplazable.
    - **API**: tarjeta expandible con detalles de la API usada.

3. **Botón de cierre de sesión**
    - Llama al `logout()` del ViewModel, limpia navegación hasta la pantalla inicial.

4. **Estados de carga y error**
    - Mientras `isLoading` es true: muestra un `CircularProgressIndicator`.
    - Si `errorMessage` no es nulo: muestra texto de error en rojo.

---

## 🏗️ Reparto de responsabilidades

| Capa            | Qué hace                                                                                                                                           |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| **View**        | • Renderiza header, `LazyColumn` de tarjetas y diálogos (`ContactFormDialog`, `PrivacyPolicyDialog`), • Captura clics: abrir diálogos, logout, • Lee/escribe el modo oscuro en `ThemePreferences` |
| **ViewModel**   | • Exposa `LiveData`: `isLoading`, `errorMessage`, `contactResult`, `isDarkTheme`, • Gestiona logout (AuthRepository), • Envía formulario de contacto (ContactRepository), • Controla el toggle de tema en memoria |

---

## 🚀 Acciones disponibles desde **Settings**

- **Ver autores**: desplegar/cerrar tarjeta de creadores.
- **Enviar mensaje**: abrir formulario de contacto y enviar al backend.
- **Alternar modo oscuro**: guarda la preferencia en `DataStore`.
- **Leer política**: abrir modal con texto legal.
- **Ver detalles de la API**: desplegar/cerrar tarjeta de info.
- **Cerrar sesión**: invocar logout y volver a la pantalla de inicio.


## 🔗 Módulo API / Retrofit – Resumen

Este módulo agrupa toda la configuración de red de la aplicación:

1. **RetrofitClient**
    - Define la URL base (`BASE_URL`) y el cliente HTTP con un **AuthInterceptor** que inyecta el token en cada petición.
    - Expone instancias perezosas (`lazy`) de todos los servicios Retrofit:
        - Autenticación (`AuthService`)
        - Ligas (`LigaService`)
        - Usuario / perfil (`UserService`)
        - Drafts (`DraftService`)
        - Jugadores (`PlayerService`)
        - Equipos (`TeamService`)
        - Notificaciones (`NotificationsService`)
        - Contacto (`ContactService`)
    - También construye repositorios que envuelven estos servicios (p. ej. `PlayerRepository`, `TeamRepository`).

2. **Interfaces de servicio**  
   Cada `interface` define los endpoints HTTP con anotaciones Retrofit:
    - Métodos `@GET`, `@POST`, `@PUT`, `@DELETE`, `@Multipart`
    - Parámetros en ruta (`@Path`), consulta (`@Query`) o cuerpo (`@Body`).
    - Respuestas tipadas como `Response<Modelo>` para manejar errores/excepciones.

3. **Flujo de petición**
    1. **ViewModel** solicita datos a su **Repository**.
    2. **Repository** invoca el método Retrofit correspondiente.
    3. **AuthInterceptor** añade el header `Authorization: Bearer <token>`.
    4. **Retrofit** envía la petición y parsea la respuesta JSON a objetos Kotlin.
    5. **Repository** devuelve el resultado o lanza excepción.
    6. **ViewModel** actualiza su estado (`LiveData`/`StateFlow`).
    7. **Compose UI** se re-renderiza con los nuevos datos o muestra errores.

## 🎨 Color Reference


| Color                   | Hex                                                              |
| ----------------------- | ---------------------------------------------------------------- |
| **PrimaryColor**        | #082FB9 |
| **SecondaryColor**      | #021149 |
| **TertiaryColor**       | #94AAFA |
| **BackgroundLight**     | #F5F5F5 |
| **SurfaceVariantLight** | #E0E0E0 |
| **OnSurfaceVariantLight** | #333333|
| **OutlineLight**        | #BBBBBB |

---
## 👥 Authors

- [@Albert Garrido](https://github.com/albertgarrido4)
- [@Joan Linares](https://github.com/JoanLinares)

