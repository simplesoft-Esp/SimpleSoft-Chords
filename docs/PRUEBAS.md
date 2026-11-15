# Verificación de estabilización 0.2.0 — 15 de noviembre de 2026

Rama de la PR #1: feat/android-v0.2.0. Base de esta corrección: 3831170.
VersionName 0.2.0, versionCode 2; no se modifica main ni SimpleSoft Converter.
El informe inicial está en `docs/evidence/v0.2.0/`; la evidencia nueva está separada
en `docs/evidence/stabilization-020/`.

## Comprobaciones automatizadas ejecutadas

- `bash gradlew --offline testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug`:
  BUILD SUCCESSFUL. Genera APK debug de aplicación y APK de pruebas instrumentadas.
- 22 pruebas JVM: 7 MusicTest, 6 HarmonyTest, 7 RecognitionStateTest, 2 ContrastTest.
  Todas correctas, sin omisiones. Las pruebas del estado compartido cubren Do mayor,
  La menor, primera/segunda inversión, octavas duplicadas, dedos sobre la misma nota,
  resultado persistente tras key-up, inicio de otro gesto y notificación a Compose.
- Los contrastes calculados de texto activo y etiquetas del piano son ≥4,5:1.
  Tema con background/surface blancos y contenido oscuro. No equivale a probar la
  apariencia, escalado o legibilidad completa de cada pantalla en un dispositivo.
- `python3 tools/verify_assets.py`: 6 pruebas correctas; incluyen formato/afinación,
  headroom, bucles, dos octavas y hash exacto del logo oficial. Muestras sin cambios.
- `git diff --check`: correcto.
- `apksigner verify --print-certs`: APK válida y mismo certificado que la APK original
  0.2.0 (huella en la evidencia); ZIP y SHA-256 de la APK comprobados.
- Lint: 0 errores, 7 avisos y 5 sugerencias. Avisos: actualización de org.json de pruebas,
  orientación fija desde Android 16, forma/ubicación del logo y tres sugerencias KTX.

Las primeras descargas de dependencias AndroidX de pruebas requieren Internet. La
aplicación funciona sin conexión. No se añade Robolectric al proyecto: el intento de
prueba UI en JVM se bloqueó al obtener el runtime Android (HTTP 403 del espejo Maven).
No se cuenta ese intento como una prueba de interfaz ejecutada.

## Pruebas instrumentadas preparadas, NO ejecutadas

`app/src/androidTest/.../RecognitionScreenTest.kt` contiene 7 pruebas que envían
MotionEvent multitáctiles reales a PianoView y comprueban el texto visible en Compose:
Do mayor, La menor, inversión con duplicaciones, cancelación/nuevo acorde, ayuda y
navegación, dos dedos compartiendo tecla y un diseño de 640×320 dp con fuente al 130%.
La APK de pruebas compila; no se ha ejecutado en un teléfono ni emulador en esta sesión.

Para ejecutarlas con el A16 conectado y autorizado (SDK/Gradle instalados):

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Esto instala la app de depuración y el paquete de pruebas. Usa un dispositivo/perfil
de prueba: la suite cambia a español y notación latina en los ajustes locales.

## Comprobaciones físicas pendientes en Samsung Galaxy A16

1. Instalar la APK corregida sobre la anterior y confirmar que se conserva el progreso.
   La versión visible sigue siendo 0.2.0; diferenciarla por el archivo/hash del informe.
2. En reconocimiento, tocar Do3/Mi3/Sol3: «Do mayor» debe aparecer en grande sin desplazar
   mientras suenan, permanecer al soltar cada dedo y desaparecer al iniciar otro acorde.
   Repetir La3/Do4/Mi4 («La menor»), Mi3/Sol3/Do4 (primera inversión) y duplicaciones.
3. Probar 2–5 dedos, dos dedos en una tecla, deslizamiento, cancelación, pérdida de foco,
   cambio de herramienta y retorno. Confirmar ausencia de voces bloqueadas/duplicadas.
4. Mantener notas al menos 10 s; comprobar timbre, terminación, volumen, auriculares,
   altavoz e interrupciones. Medir latencia toque/sonido si se requiere una cifra.
5. Probar el teléfono en modo claro y oscuro, horizontal y fuente grande: la app debe
   seguir clara, con nombre/notas visibles, etiquetas legibles y piano siempre accesible.
   Comprobar menús, diálogos, opciones seleccionadas, desactivadas, pistas, verde y azul.
6. Abrir Información (leyenda y alternativas), Estadísticas, Ajustes, Constructor y las
   cuatro actividades desde Menú; comprobar que no se han perdido funciones.
7. Validación básica acepta octavas y duplicaciones correctas; «Incorrecto» mantiene azul
   sin rojo, acierto verde y avance manual. Completar conserva la nota proporcionada.
   Avanzado exige únicamente el bajo/inversión anunciados. Probar pistas y guía.
8. Probar nombres largos, ambigüedades, contenido vacío y filtros restrictivos, idioma
   inglés, notación C–B, TalkBack, modo avión y Android 16/pantallas grandes si disponibles.

No se declara corregido en hardware ni probado físicamente hasta completar estas
comprobaciones con la nueva APK. La prueba A16 aportada por el usuario corresponde
a la APK anterior y es el origen del informe de fallos.
