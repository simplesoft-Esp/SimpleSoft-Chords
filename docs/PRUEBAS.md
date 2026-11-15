# Verificación 0.2.0 — 15 de noviembre de 2026

Base: commit bbbadc7 (0.1.0). Rama: feat/android-v0.2.0. Android nativo Kotlin/Compose;
no se ha modificado SimpleSoft-Converter ni main.

## Ejecutado en este entorno

- `bash gradlew --offline testDebugUnitTest assembleDebug lintDebug`: BUILD SUCCESSFUL.
- 13 pruebas JUnit: 7 de contratos previos y 6 nuevas de armonía, todas correctas.
- `python3 tools/verify_assets.py`: 6 pruebas correctas.
- `git diff --check`: correcto.
- APK debug: com.simplesoft.chords, 0.2.0/versionCode 2, API mínima 26.
- Lint: 0 errores, 7 avisos y 5 sugerencias; no se ocultan avisos.

Evidencia de esta versión: `docs/evidence/v0.2.0/`. Los ficheros de la carpeta superior
son evidencia histórica 0.1.0. El hash de la APK se guarda en apk-sha256.txt; una
recompilación con otra clave debug puede generar otro hash.

Las pruebas nuevas cubren clases de altura frente a disposición exacta; octavas y
duplicaciones válidas; notas ausentes/sobrantes; bajo solicitado; reconocimiento de
inversiones; alternativas simétricas y Do6/La menor séptima; todas las posiciones
ejecutables por raíz/tipo/inversión; inversiones extendidas y distractores auditivos
con raíz y registro idénticos. Los recursos se verifican en afinación, formato PCM,
margen de mezcla, periodicidad y afinación de los bucles (<1 cent), ausencia de
permisos, dos octavas y SHA-256 exacto del logo. Los 25 WAV originales no se modifican.

Los avisos de lint corresponden a actualización disponible de org.json (solo pruebas),
orientación fija desde Android 16, ubicación/forma del logo y tres sugerencias KTX.
Se conserva el recurso oficial solicitado. La orientación y el tamaño en pantallas
grandes deben revisarse porque Android 16 puede ignorar orientación fija.

## Pruebas manuales pendientes en dispositivo

No se ha ejecutado un emulador ni un teléfono en esta sesión. No se afirma haber
medido latencia ni haber realizado pruebas visuales, táctiles o acústicas reales.
Antes de distribuir, ejecutar especialmente en Samsung Galaxy A16 SM-A165F:

1. Instalar la APK y verificar las cuatro actividades en español e inglés y temas
   claro/oscuro, con fuente grande. El piano debe permanecer visible al desplazar.
2. Mantener un acorde al menos 10 s: sonido sostenido y teclas azules; soltar uno a
   uno y comprobar la terminación. Probar dos dedos en la misma tecla, arrastres,
   salida del teclado, cancelación, cambio de pregunta y cambio de herramienta.
3. Interrumpir con otra app, apagar pantalla y volver: sin voces bloqueadas. Medir
   latencia toque/sonido y revisar timbre, transición de liberación y volumen con
   auriculares y altavoz. SoundPool precargado no garantiza una latencia medida.
4. Reconocer Do mayor, su primera inversión, duplicaciones, Do6/La menor séptima y
   séptima disminuida simétrica; conservar alternativas y la última iluminación.
5. Construir Do mayor como Do4–Mi4–Sol4 o con duplicaciones: aceptar. Añadir Si♭:
   «Incorrecto», azul, sin rojo. Acierto verde, sin avance automático; Siguiente manual.
6. En Avanzado, confirmar que se muestra bajo/inversión y se rechaza el bajo distinto.
   Completar conserva la nota proporcionada al tocarla o pulsar Limpiar.
7. Identificar y oído: elegir no valida; pulsar Validar sí. Oído ofrece distintas
   cualidades a la misma altura, con progresión mayor/menor → tríadas → séptimas.
8. Contenido vacío y un solo tipo auditivo: explicación y acciones para ampliar o
   cambiar actividad. Constructor: tipos/inversiones solo ejecutables en Do3–Do5.
9. Pistas y guía: orden de las notas reales en las inversiones, funciones e intervalos.
   Comprobar persistencia de preferencias/progreso y actualización desde APK 0.1.0
   firmada con la misma clave, sin red y con TalkBack.
