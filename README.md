# AppAndroid-JudithFranz · Moon Album

Moon Album es una app Android hecha en Kotlin para llevar la colección de cartas de **My Little Pony Moon Edition Series 3**.
La hemos hecho Judith y Franz para la asignatura de Programación Móvil (DAM2, Institut TIC de Barcelona).

Con la app puedes:

- ver las 290 cartas de la edición,
- marcar las que tienes y contar las repetidas,
- buscar y filtrar,
- añadir, modificar y borrar cartas,
- buscar una carta por su código en el escáner,
- cambiar los colores de la app con 13 temas.

## Cómo ejecutarla

1. Abrir la carpeta del proyecto con Android Studio.
2. Esperar a que Gradle sincronice. La primera vez tarda porque descarga las librerías.
3. Elegir un emulador o un móvil. La app pide Android 7.0 (API 24) o más.
4. Darle a **Run**.

No hay cuenta de verdad. En el login hay que poner un correo con formato válido y una contraseña, y ya entra.

Las cartas salen de `app/src/main/assets/cartas_moon_edition_3.json`.
Los cambios se guardan en el móvil con SharedPreferences.

## Pantallas

| Pantalla | Qué hace |
|---|---|
| Splash | Sale el logo con una animación y luego va al login. |
| Login (`MainActivity`) | Correo y contraseña. Si falta algo, el campo sale en rojo con un mensaje. |
| Registro | Nombre, correo y contraseña dos veces. Lo comprueba todo y vuelve al login. |
| Home | Lista de cartas con buscador y dos desplegables para filtrar. |
| Detalle (`CardActivity`) | La carta en grande. Marcar que la tienes, sumar o restar copias, modificar o borrar. |
| Editar / Añadir | Formulario para cambiar una carta o crear una nueva. |
| Escáner | Escribes o pegas el código de una carta y abre su ficha. |
| Repetidas | Las cartas que tienes más de una vez. |
| Ajustes | Opciones del escáner, repetidas, tema de colores, ayuda y cerrar sesión. |
| Temas | Eliges uno de los 13 temas. |
| Ayuda | Explica cómo se usa la app. |

## El menú

La app tiene dos barras. Las dos están programadas en un `object` de Kotlin, para no repetir código en cada pantalla.

### Barra inferior (`BarraInferior.kt`)

1. En vertical es un `BottomNavigationView` dentro de una tarjeta redonda (`barra_inferior.xml`).
2. En horizontal usamos un `NavigationRailView` al lado izquierdo (`layout-land/barra_inferior.xml`). Así la lista tiene más alto.
3. Los dos tienen el mismo id (`bottomNav`) y el mismo menú (`menu_bottom.xml`) con tres botones: Home, Escáner y Ajustes.
4. En el código lo buscamos como `NavigationBarView`, que es la clase padre de los dos. Así el mismo código sirve para vertical y horizontal.
5. `configurar(activity, seleccionado)` marca el botón de la pantalla actual y pone el listener. El listener devuelve `true` cuando maneja el botón, y cada vez que la pantalla vuelve a primer plano se marca otra vez su botón, para que no se quede marcado otro.
6. Al pulsar otro botón se abre esa pantalla con `FLAG_ACTIVITY_CLEAR_TOP` y `FLAG_ACTIVITY_SINGLE_TOP`.
7. Si la pantalla actual no es Home, se cierra con `finish()`. Así no se apilan pantallas.
8. Resultado: Back siempre vuelve a Home, y desde Home sale de la app.
9. La barra solo está en Home, Escáner y Ajustes. Las demás pantallas son "secundarias" y tienen flecha atrás.
10. Cuando sale el teclado, la barra se esconde para que no tape la lista ni los botones.

### Barra superior (`BarraSuperior.kt`)

1. Es un `MaterialToolbar` con el estilo `BarraSuperior` (`styles.xml`).
2. `configurar(activity, conAtras)` pone la flecha atrás solo si `conAtras` es `true`.
3. Home, Escáner y Ajustes no tienen flecha. Detalle, Editar, Repetidas, Ayuda y Temas sí.
4. Pinta el título, la flecha y los iconos del menú con el color del tema (`temaTexto`).
5. En Home tiene el botón de añadir carta y un menú (`menu_home.xml`) con Cartas repetidas y Ayuda. A Ajustes solo se llega por la barra inferior, así hay un solo camino.

Cerrar sesión abre el login con `FLAG_ACTIVITY_NEW_TASK` y `FLAG_ACTIVITY_CLEAR_TASK`. Así se borra todo lo de atrás y Back ya no vuelve a la app.

## La lista: Adapter y Holder

La lista de Home es un `RecyclerView` en rejilla. Tiene 2 columnas en vertical y 4 en horizontal (`integers.xml`).

Paso a paso:

1. **`CartaAdapter` hereda de `ListAdapter<Carta, CartaViewHolder>`.** No guardamos la lista a mano. Le pasamos la lista nueva con `submitList()`.
2. **`DiffUtil.ItemCallback`** compara la lista vieja y la nueva:
   - `areItemsTheSame`: son la misma carta si tienen el mismo código.
   - `areContentsTheSame`: no ha cambiado nada si las dos cartas son iguales (`Carta` es un `data class`).
   - Así solo se repintan las tarjetas que cambian, y con animación.
3. **`onCreateViewHolder`** infla `item_carta.xml` y crea el holder.
4. **`CartaViewHolder`** busca las vistas una sola vez (imagen, nombre, código, rareza y estado).
5. **`onBindViewHolder` → `mostrar()`** rellena las vistas con la carta de esa posición:
   - la imagen se carga con Coil,
   - si no hay internet o no hay imagen, sale `carta_placeholder`,
   - las cartas que no tienes salen más transparentes.
6. **Clic**: abre el detalle de la carta.
7. **Pulsación larga**: sale un diálogo con Abrir, Modificar y Eliminar. Eliminar pide confirmación.
8. **Deslizar (swipe)**: con `ItemTouchHelper` deslizas la carta a un lado. Mientras deslizas sale un fondo rojo con una papelera (`onChildDraw` con `ic_papelera.xml`). Al soltar sale el mismo diálogo de confirmación que en la pulsación larga. Si cancelas, la carta vuelve a su sitio (`notifyItemChanged`).
9. **Deshacer**: después de borrar (con swipe, con pulsación larga o desde el Detalle) sale una Snackbar con **Deshacer**. La carta vuelve a su sitio con `CartasRepositorio.restaurar()`.

El diálogo y la Snackbar están en `BorradoCarta.kt`, así todas las pantallas borran igual.
Cuando se borra desde el Detalle, el Detalle se cierra con `finish()` y la pantalla de la que venías (Home, Repetidas o Escáner) enseña la Snackbar.
La Snackbar solo se pega a la barra inferior si es la de abajo y se ve. En horizontal (barra lateral) o con el teclado abierto sale sin anclar.

La pantalla Repetidas tiene su propio adapter (`RepetidaAdapter`) con su propio holder y su propia fila (`item_repetida.xml`).

El buscador y los desplegables usan `FiltroCartas.filtrar()`. Es una función sin nada de Android, y por eso tiene tests.
Si el filtro no encuentra nada, sale un mensaje en vez de la lista vacía.

En horizontal hay layouts propios (`layout-land`) para Home, Detalle, Editar, Ayuda, Escáner y Ajustes.
Por ejemplo, en Detalle la imagen va a la izquierda y los datos y botones a la derecha.
El filtro se mantiene al girar el móvil.

## Añadir, modificar y borrar

- Añadir y modificar están en `EditarCartaActivity`.
- Cada acción tiene su Toast ("añadida" o "modificada") y escribe un `Log.d` con su `TAG`.
- Si el código ya existe en otra carta, sale un error en el campo del código.
- Borrar se puede hacer desde el detalle o desde la lista. Siempre pide confirmación, escribe un `Log.d` y enseña la Snackbar con Deshacer.
- Para ver los logs: Logcat en Android Studio, filtrando por `HomeActivity`, `CardActivity`, `EditarCartaActivity`, `EscanerActivity` o `BorradoCarta` (al deshacer).
- Todo pasa por `CartasRepositorio`. Las cartas se guardan como JSON con `CartasJson` en SharedPreferences.

## Splash

1. Solo hay un splash: el del sistema, con la SplashScreen API (`androidx.core:core-splashscreen`). No hay una Activity de splash.
2. La app arranca directamente en el login (`MainActivity`). Su tema en el manifest es `Theme.JudithFranz.Splash`.
3. `MainActivity` llama a `installSplashScreen()` antes de `super.onCreate()`.
4. El splash pone el fondo lila y el logo de la app (`ic_launcher_foreground`).
5. Al abrir la app, el splash se queda como mucho 0,7 segundos (`tiempo_minimo_splash`) para que se vea el logo. La condición solo mira el reloj, así que nunca se queda parado.
6. Al salir, el logo se hace grande y se desvanece, y todo el splash hace un fundido (`setOnExitAnimationListener`).
7. Si el móvil tiene las animaciones desactivadas, no se espera ni se anima: el splash se quita directamente.

## Temas y modo claro

- Hay 13 temas: Luna, y un claro y un oscuro para cada pony (6 claros y 6 oscuros).
- Cada tema es un estilo en `themes.xml` que da valor a unos atributos nuestros (`temaTexto`, `temaPrincipal`, `temaSuperficie`...). Los atributos están en `attrs.xml`.
- Los layouts usan `?attr/temaTexto` y no colores fijos. Así toda la app cambia con el tema.
- `Temas.kt` guarda el tema elegido y lo aplica a cada pantalla. Si cambias de tema, las pantallas abiertas se recrean.
- La pantalla Temas es un `RecyclerView` en rejilla (2 columnas en vertical y 4 en horizontal) con su propio adapter, `TemaAdapter` (`ListAdapter` + `DiffUtil` + `TemaViewHolder`).
- Cada tarjeta se infla con los colores de su tema (`ContextThemeWrapper`), así ves la vista previa antes de elegirlo.
- El tema en uso tiene borde y la etiqueta "En uso". Al pulsar otro, se guarda y la pantalla se recrea.

¿Por qué forzamos el modo claro (`MODE_NIGHT_NO`)?
Porque los temas oscuros ya existen y los elige el usuario.
Si dejáramos que el sistema cambiara a oscuro, se mezclarían los colores del sistema con los de nuestros temas y algunas pantallas saldrían con poco contraste.
Así la app se ve igual siempre, y quien quiera la app oscura elige un tema "Dark".

## Accesibilidad

### Qué hicimos

- **contentDescription**: todos los botones que solo tienen icono tienen descripción (añadir, buscar, sumar, restar, volver, barra inferior). Las imágenes de adorno tienen `importantForAccessibility="no"`.
- **Tamaño de toque**: todo control mide como mínimo 48dp (`objetivo_tactil_minimo` en `dimens.xml`). Esto incluye los botones de icono, los interruptores, las opciones de Ajustes y los textos que se pueden pulsar.
- **Letra grande**: los textos usan `sp`, y las cajas `wrap_content` o `minHeight` en vez de altos fijos. Los nombres de carta pueden ocupar dos líneas. Ningún texto baja de 12sp (`texto_pequeno` en `dimens.xml`).
- **Tarjetas agrupadas**: cada carta de Home y de Repetidas, y cada tema, se lee de una vez con TalkBack. La tarjeta tiene una descripción con todo (por ejemplo nombre, código, rareza y si la tienes) y lo de dentro está oculto para TalkBack (`noHideDescendants`), así no se lee dos veces.
- **Autofill**: correo y contraseña tienen `autofillHints` (`emailAddress`, `password`, `newPassword`, `username`). Los campos que no tiene sentido autocompletar tienen `importantForAutofill="no"`.
- **Errores**: salen dentro del campo (`TextInputLayout`), con texto, y se borran al escribir.
- **Mensajes**: la lista vacía y los errores se anuncian solos (`accessibilityLiveRegion`).
- **Acción alternativa al swipe**: deslizar no es fácil para todo el mundo. Por eso borrar también se puede hacer con la pulsación larga o con el botón Borrar del detalle. Con TalkBack, la pulsación larga se anuncia como "Más acciones".
- **Cabeceras**: los títulos de las secciones de Ajustes son cabeceras (`accessibilityHeading`).
- **Fuente**: quitamos la fuente Nunito ExtraLight porque era muy fina. Ahora usamos la fuente del sistema (normal y medium).
- **Contraste**: todos los textos tienen al menos 4,5:1 con su fondo en los 13 temas (tabla de abajo).

### Tabla de contraste

La tabla sale del script `docs/contraste.py`, que lee `themes.xml`.
Para cada color de texto se mira el peor fondo donde puede salir: tarjeta, superficie, el degradado de fondo, el cuadro translúcido y los campos.
El botón de borrar (blanco sobre `#B3261E`) tiene 6,5:1.

| Tema | Texto | Texto secundario | ✔ La tengo | ✖ Falta | Texto sobre botón principal | Texto sobre acento |
|---|---|---|---|---|---|---|
| Luna (original) | 7,6 | 4,7 | 4,7 | 4,7 | 5,1 | 5,1 |
| Applejack Light | 10,8 | 4,7 | 4,6 | 4,7 | 5,0 | 4,6 |
| Applejack Dark | 11,1 | 6,6 | 6,1 | 5,7 | 8,8 | 14,0 |
| Fluttershy Light | 12,2 | 4,7 | 4,5 | 5,0 | 5,7 | 4,8 |
| Fluttershy Dark | 10,9 | 6,5 | 5,8 | 5,5 | 10,2 | 13,4 |
| PinkiePie Light | 10,4 | 4,6 | 4,6 | 4,6 | 4,6 | 5,8 |
| PinkiePie Dark | 11,3 | 6,6 | 6,4 | 6,0 | 7,8 | 8,9 |
| RainbowDash Light | 10,7 | 4,6 | 4,6 | 4,6 | 4,8 | 4,6 |
| RainbowDash Dark | 11,2 | 6,7 | 6,1 | 5,7 | 11,6 | 4,5 |
| Rarity Light | 12,8 | 4,9 | 4,6 | 4,6 | 6,7 | 7,1 |
| Rarity Dark | 12,5 | 7,0 | 6,7 | 6,3 | 8,5 | 9,6 |
| Twilight Light | 10,9 | 4,6 | 4,6 | 4,6 | 6,0 | 4,7 |
| Twilight Dark | 11,6 | 6,7 | 6,5 | 6,1 | 8,4 | 5,0 |

Se ejecuta así: `python docs/contraste.py`.

### Cómo lo comprobamos

- Contraste: con el script de arriba. Todos los valores son 4,5 o más.
- 48dp: revisando los layouts (`minHeight` y `minWidth` con `objetivo_tactil_minimo`).
- Autofill: revisando que cada campo tiene `autofillHints` o `importantForAutofill="no"`.
- Pendiente de comprobar en el emulador:
  - letra al 200 %,
  - TalkBack,
  - pantalla en horizontal,
  - un móvil con API 24.

## Tests

En `app/src/test` hay tests unitarios que no necesitan emulador:

- `CartaTest`: `copy`, `conTengo` y `conCantidad`.
- `FiltroCartasTest`: buscar por nombre y por código, filtrar por rareza y por las que tienes o te faltan.
- `CartasJsonTest`: pasar las cartas a JSON y volver, leer el catálogo y leer un JSON viejo al que le faltan campos.

Para que `CartasJsonTest` funcione sin emulador añadimos `org.json` solo para los tests (`testImplementation`).

Se pasan con botón derecho en `app/src/test` → **Run Tests**, o con `gradlew test`.

## Pruebas

Lo que hay que comprobar a mano en el emulador antes de entregar (marcar cuando esté hecho):

- [ ] Letra al 200 % (Ajustes del móvil → Pantalla → Tamaño de fuente): nada se corta en Login, Home, Detalle, Editar y Ajustes.
- [ ] TalkBack en Home: se leen los filtros, las cartas y la barra inferior; la pulsación larga sale como "Más acciones".
- [ ] TalkBack en Detalle: se leen la imagen, el interruptor, sumar, restar, Modificar y Borrar.
- [ ] TalkBack en Editar: se leen los campos y los errores.
- [ ] Móvil o emulador con API 24: la app abre, el splash va al login y se puede navegar.
- [ ] Swipe en Home: la carta se borra, sale la Snackbar y Deshacer la vuelve a poner.
- [ ] Teclado abierto en el último campo de Login y Registro: se ven el campo, el error y el botón.
- [ ] Girar el móvil en Home, Detalle, Editar, Ayuda, Repetidas y Temas: se ve el contenido y los botones.
- [ ] `gradlew test` pasa en verde.
