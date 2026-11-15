# Informe Android 001 — 0.1.0 (14/11/2026)

## Entrega implementada

Kotlin + Compose; com.simplesoft.chords; versionCode 1; minSdk 26; target/compileSdk 36.
Piano fijo de 25 teclas Do3–Do5, modo horizontal, selección/desselección multitáctil,
entrenamiento de notas y acordes, construcción exacta, dos variantes de reconocimiento,
filtros, pistas progresivas, explicación guiada, castellano/inglés y notación independiente.
Preferencias y estadísticas privadas locales con reinicio confirmado. Sin publicación web.
37 tipos, 472 posiciones, esquemas JSON y ejemplos compartidos, 25 WAV originales.

## Verificado mediante ejecución

| Comprobación | Resultado | Evidencia |
|---|---|---|
| Compilación Kotlin/Android y generación de APK debug | Correcta, BUILD SUCCESSFUL | evidence/build.txt |
| JUnit del motor musical, ejecutado por Gradle | 7 pruebas, 0 errores/fallos | evidence/kotlin-tests.xml |
| Datos, traducciones, audio y manifiesto, Python | 5 pruebas, todas correctas | evidence/assets-tests.txt |
| Android Lint | 0 errores; advertencias no bloqueantes detalladas en evidencia | evidence/lint.txt |
| Firma APK de depuración | apksigner verify correcto | Verificado sobre la APK entregada |
| Metadatos APK | 0.1.0 / 1, paquete correcto, minSdk 26, targetSdk 36 | aapt dump badging |
| Audio empaquetado | 25 WAV, sin compresión, aptos para assets.openFd | Inspección del ZIP APK |
| Catálogo empaquetado | 37 tipos; PNG del logo idéntico al original | Comparación de bytes APK |
| Permisos APK | Sin micrófono, ubicación, contactos, almacenamiento ni INTERNET | aapt dump permissions |

AndroidX añade una autorización interna signature `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`;
no solicita permisos al usuario ni concede acceso a sus datos.
La copia en nube y transferencia de datos están excluidas en el manifiesto y en
`res/xml/data_extraction_rules.xml`, incluidas las preferencias.

### Alcance real de las pruebas

JUnit verifica todas las posiciones: límites MIDI, fundamental mínima, fórmula completa,
472 combinaciones, ejemplos de Re menor/trecena y escritura enarmónica con dobles bemoles.
Comprueba igualdad exacta, rechazo de notas ausentes y duplicaciones de octava,
bolsa aleatoria y filtros vacíos/pequeños, las cuatro combinaciones de filtros de notas,
y opciones auditivas sin clases de altura equivalentes para las 472 posiciones.
Comprueba también los ejemplos JSON compartidos para la futura web.

Python recorre las fórmulas, verifica claves de ambos idiomas y todas las funciones,
los 25 PCM y sus formatos, ausencia de clipping individual, margen teórico de mezcla de
32 voces a ganancia 0,1, fundamental medida a menos de 3 cents del valor esperado,
arranque/final suave de muestras, ausencia de permisos en el manifiesto fuente y hash del logo.
No sustituye una escucha en altavoces reales ni una medición de latencia Android.

La compilación se realizó con Gradle 8.13, AGP 8.13.0, Kotlin 2.2.0 y JDK completo 21.
Se instaló el SDK de Android en el entorno; la primera descarga con el wrapper y el
Java inicial falló por red y ausencia de javac. Se resolvió con distribución Gradle,
JDK completo y proxy del entorno, sin cambiar la configuración de red del proyecto.
La comprobación final pudo compilar con dependencias ya almacenadas y --offline.

## Revisión visual y dispositivo: pendientes

No se ha ejecutado la app en un dispositivo Android ni en un emulador.
No hay capturas de pantalla reales ni maquetas presentadas como capturas.
La revisión de interfaz es de código, medidas y lógica de distribución, no visual en ejecución.
El logo original sí se inspeccionó visualmente antes de incorporarlo.
No se ha conectado ni probado el Samsung Galaxy A16 SM-A165F del usuario.

| Prueba manual en Galaxy A16 | Criterio de aceptación | Estado |
|---|---|---|
| Arranque e instalación | Abre sin error ni solicitudes de permisos | Pendiente |
| Pantalla horizontal, ambos sentidos | 25 teclas visibles, panel superior desplazable y piano fijo | Pendiente |
| Pulsar dos o más dedos | Cada nota suena; soltar/cancelar no deja notas activas | Pendiente |
| Construcción | Segundo toque deselecciona; comprobar, corregir, limpiar y selección sonora funcionan | Pendiente |
| Notas | Repetir/Do4, verde ✓ y rojo ✕, reintento y filtros funcionan | Pendiente |
| Opciones de acordes | Variantes tipo/nombre, referencia y aviso de filtros insuficientes funcionan | Pendiente |
| Ayudas | Cuatro pistas; guía por notas y conjunto; cerrar conserva contexto | Pendiente |
| Idiomas y notación | Castellano/inglés/sistema; Do/Re y C/D independientes, también ayudas y errores | Pendiente |
| Persistencia | Cerrar proceso y abrir conserva ajustes/progreso; reinicio confirmado | Pendiente |
| Audio | Volumen útil, sin clics perceptibles ni saturación, latencia aceptable | Pendiente |
| Interrupciones | Llamadas, otra app de audio, auriculares/Bluetooth y segundo plano silencian voces | Pendiente |
| Accesibilidad | TalkBack, teclas virtuales, foco y texto grande | Pendiente |
| Proporciones | Revisar 16:9, 20:9 y fuentes ampliadas; piano siempre accesible | Pendiente |

## Límites conocidos y advertencias

- APK firmada para depuración, no con una clave de publicación; no está publicada en ninguna tienda.
- Audio de piano sintetizado aditivo original, no grabaciones de piano acústico.
  Cada muestra dura 2,3 s y decae incluso manteniendo la tecla; al soltar se desvanece.
- Hay un límite real de 32 voces. Las interrupciones detienen voces y la repetición
  desvanece las anteriores. La respuesta del dispositivo y cambios de salida no están probados.
- Android 16 puede ignorar el bloqueo horizontal en pantallas grandes; revisar tablets
  y ventanas libres antes de anunciar soporte para ellas. El objetivo inicial es móvil horizontal.
- El logo original se conserva sin alterar como icono bitmap. Lint advierte sobre su forma
  y carpeta de densidad. No se rediseñó para silenciar esas advertencias.
- Lint también puede señalar versiones nuevas de dependencias fijadas y recomendaciones
  de estilo KTX/estado primitivo. Las APIs de nodos accesibles usadas para compatibilidad
  con API 26 producen avisos de obsolescencia del compilador.
- Distractores conservadores: con catálogos muy restringidos puede no haber una elección
  auditiva válida. La app pide ampliar filtros o practicar construcción.
- Cambiar ajustes inicia otra pregunta. Cerrar solo la explicación conserva la actual.
- Estadísticas y preferencias están implementadas, pero no se ha verificado su comportamiento
  en un proceso Android real. No hay tests instrumentados ejecutados de interfaz o persistencia.

Consulta README.md para compilar e instalar, y las guías de uso para probar cada flujo.
