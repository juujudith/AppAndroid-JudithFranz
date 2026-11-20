# AppAndroid-JudithFranz · Moon Album

Moon Album es una app Android hecha en Kotlin para llevar la colección de cartas de **My Little Pony Moon Edition Series 3**.
La hemos hecho Judith y Franz para la asignatura de Programación Móvil (DAM2, Institut TIC de Barcelona).

Con la app puedes ver todas las cartas de la edición, marcar las que tienes, contar las repetidas, buscar una carta por su código y cambiar los colores de la app.

[AQUI CAPTURA] (pantalla de Home con la lista de cartas)

## Cómo ejecutarla

1. Abrir la carpeta del proyecto con Android Studio.
2. Esperar a que Gradle sincronice (la primera vez tarda un poco porque descarga las librerías).
3. Elegir un emulador o un móvil con Android 7.0 (API 24) o más.
4. Darle a **Run**.

La app no necesita cuenta de verdad. En el login hay que escribir un correo con formato válido y una contraseña, y ya entra.
Las cartas vienen de un JSON que está en `app/src/main/assets/cartas_moon_edition_3.json`. Lo que cambias se guarda en el móvil con SharedPreferences.

Para pasar los tests: botón derecho en `app/src/test` y **Run Tests**, o `gradlew test` desde la terminal.

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Splash | Sale el logo con una animación y luego va al login. |
| Login (`MainActivity`) | Correo y contraseña. Si falta algo te lo marca en rojo. |
| Registro | Nombre, correo y contraseña dos veces. Comprueba que todo esté bien y vuelve al login. |
| Home | La lista de cartas con el buscador y los filtros. |
| Detalle (`CardActivity`) | La carta en grande. Puedes marcar que la tienes, sumar o restar copias, modificarla o borrarla. |
| Editar / Añadir | Formulario para cambiar los datos de una carta o crear una nueva. |
| Escáner | Escribes o pegas el código de una carta y te abre su ficha. |
| Repetidas | Lista de las cartas que tienes más de una vez. |
| Ajustes | Opciones del escáner, ver repetidas, tema de colores, ayuda y cerrar sesión. |
| Temas | Eliges uno de los 13 temas de colores. |
| Ayuda | Explica cómo se usa la app. |

[AQUI CAPTURA] (login y registro)

[AQUI CAPTURA] (detalle de una carta)

[AQUI CAPTURA] (editar carta)

## El menú

La app tiene dos barras.

**Barra inferior.** Está en `fragment_bottom_bar.xml` y tiene tres botones: Home, Escáner y Ajustes. La lógica está en `BarraInferior.kt`.
Cuando pulsas un botón se abre esa pantalla con `FLAG_ACTIVITY_CLEAR_TOP` y `FLAG_ACTIVITY_SINGLE_TOP`. Así no se van apilando pantallas repetidas.
Si estás en una pantalla que no es Home, esa pantalla se cierra, y al darle atrás siempre vuelves a Home.

**Barra superior.** Es un `MaterialToolbar` que está en casi todas las pantallas. La configuramos en `BarraSuperior.kt`.
Pone el título y la flecha de volver, y pinta los iconos con el color del tema.
En Home la barra tiene un botón para añadir carta y un menú con Cartas repetidas, Ayuda y Ajustes.

[AQUI CAPTURA] (barra superior con el menú abierto)

## La lista: adapter y holder

La lista de Home es un `RecyclerView` en forma de rejilla (2 columnas en vertical y 4 en horizontal).

- **`CartaAdapter`** hereda de `ListAdapter`. Le pasamos la lista nueva con `submitList()` y él solo calcula qué ha cambiado.
- **`DiffUtil`**: dos cartas son "la misma" si tienen el mismo código, y tienen "el mismo contenido" si son iguales del todo (`Carta` es un `data class`). Así solo se repintan las tarjetas que cambian.
- **`CartaViewHolder`** guarda las vistas de cada tarjeta (imagen, nombre, código, rareza y si la tienes) y las rellena en `mostrar()`. La imagen se carga de internet con Coil.
- **Clic**: abre el detalle de la carta.
- **Pulsación larga**: sale un diálogo con Abrir, Modificar y Eliminar. Eliminar pide confirmación.
- **Deslizar (swipe)**: con `ItemTouchHelper`, si deslizas una carta a un lado se borra y sale una Snackbar con **Deshacer** que la vuelve a poner en su sitio.

La pantalla de Repetidas tiene su propio adapter (`RepetidaAdapter`) con su propio holder y su propio diseño de fila (`item_repetida.xml`).

El buscador y los dos desplegables filtran la lista. El filtro está en `FiltroCartas.kt`, que no depende de Android, y por eso lo hemos podido probar con tests.
Si el filtro no encuentra nada, sale un mensaje en vez de la lista vacía.

[AQUI CAPTURA] (diálogo de pulsación larga)

[AQUI CAPTURA] (Snackbar de deshacer después de deslizar)

## Añadir, modificar y borrar

- Añadir y modificar se hacen en `EditarCartaActivity`. Cada acción enseña su propio Toast ("añadida" o "modificada") y escribe un `Log.d`.
- Borrar se hace desde el detalle (con un diálogo de confirmación) o desde la lista (pulsación larga o deslizar). También queda en el Log.
- Todo se guarda en `CartasRepositorio`, que pasa las cartas a JSON y las guarda en SharedPreferences.

## Splash

Usamos la SplashScreen API (`androidx.core:core-splashscreen`). En `SplashActivity` llamamos a `installSplashScreen()`.
El splash del sistema solo pone el color de fondo, sin icono, para que el logo no salga dos veces.
Después, `SplashActivity` anima el logo con `res/anim/splash_logo.xml`: aparece, se hace grande y hace un pequeño "latido".
Cuando termina la animación (`onAnimationEnd`) se abre el login. No hay un tiempo fijo.
Si el móvil tiene las animaciones desactivadas, se salta la animación y va directo al login.

[AQUI CAPTURA] (splash)

## Accesibilidad

- **contentDescription**: todos los botones que solo tienen icono (añadir, buscar, sumar, restar, volver, los de la barra inferior) tienen una descripción para TalkBack. Las imágenes que solo decoran tienen `importantForAccessibility="no"`.
- **Tamaño de toque**: los botones y opciones miden como mínimo 48dp (`objetivo_tactil_minimo` en `dimens.xml`).
- **Autofill**: el correo y la contraseña del login y el registro tienen `autofillHints`. Los campos que no tienen sentido autocompletar (código de carta, buscador...) tienen `importantForAutofill="no"`.
- **Contraste**: revisamos los colores de los 13 temas para que el texto tenga al menos 4,5:1 con su fondo. Oscurecimos el morado principal y el rojo del botón de borrar. El texto pequeño ya no usa la fuente ExtraLight.
- **Acción alternativa al swipe**: deslizar no es fácil para todo el mundo, así que borrar también se puede hacer con la pulsación larga, o con el botón Borrar del detalle. Con TalkBack la acción de pulsación larga se anuncia como "Más acciones".
- Los títulos de las secciones de Ajustes son cabeceras (`accessibilityHeading`), y los mensajes de lista vacía se anuncian solos (`accessibilityLiveRegion`).
- Los textos están en `strings.xml` y las medidas en `dimens.xml`.

## Temas

Hay 13 temas (Luna, y un claro y un oscuro para cada pony). Cada tema es un estilo en `themes.xml` que da valor a unos atributos propios (`temaTexto`, `temaPrincipal`, `temaSuperficie`...), declarados en `attrs.xml`.
Los layouts usan esos atributos (`?attr/temaTexto`) y no colores fijos, así cada pantalla cambia de color con el tema.
`Temas.kt` guarda el tema elegido y lo aplica a cada pantalla cuando se crea. Si cambias de tema, las pantallas abiertas se recrean al volver.
La app no cambia sola con el modo oscuro del sistema: el tema lo eliges tú en Ajustes.

[AQUI CAPTURA] (pantalla de temas)

[AQUI CAPTURA] (la app con un tema oscuro)

## Cómo lo hemos probado

- **Tests unitarios** en `app/src/test`: `CartaTest` (copy, `conTengo` y `conCantidad`) y `FiltroCartasTest` (buscar por nombre y código, filtrar por rareza y por las que tienes).
- **En el emulador** hemos probado a mano:
  - Login y registro con campos vacíos y con datos bien puestos.
  - Buscar y filtrar en Home, y que salga el mensaje cuando no hay resultados.
  - Añadir, modificar y borrar cartas, y deshacer el borrado.
  - El escáner con un código que existe, uno que no existe y el portapapeles vacío.
  - Girar el móvil en Home, Detalle y Editar.
  - Cambiar de tema y poner el sistema en modo oscuro.
  - TalkBack en la barra inferior y en Ajustes.

[AQUI CAPTURA] (resultado de los tests en Android Studio)
