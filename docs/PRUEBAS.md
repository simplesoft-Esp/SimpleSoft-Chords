# Verificación de usabilidad 0.2.0 — 15 de noviembre de 2026

Rama feat/android-v0.2.0 de la PR #1; base de esta revisión: 1c36881. Producto 0.2.0,
versionCode 2. No se modifica main ni ningún otro repositorio. Evidencia actual en
`docs/evidence/usability-020/`; v0.2.0 y stabilization-020 conservan las entregas anteriores.

## Automatizado y ejecutado

- `bash gradlew --offline testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug`:
  BUILD SUCCESSFUL. App debug y APK de pruebas instrumentadas compiladas.
- 35 pruebas JVM, sin errores ni omisiones: MusicTest 7, HarmonyTest 6, ContrastTest 2,
  PianoStateTest 9, LearningConfigTest 8 y PianoGeometryTest 3.
- Estado del piano: añadir/quitar con repetición, pulsación física distinta de selección,
  dos dedos sobre una nota, Do mayor/La menor, inversiones/octavas duplicadas, limpiar
  todas las 25 notas, soltar sin borrar selección, tokens de eventos anteriores, 100
  cambios rápidos de ejercicio y notificaciones de añadir/quitar a Compose.
- Configuración: individuales/familias y accesos rápidos; codec real de persistencia
  con IDs vacíos, filtros y Avanzado; migración de JSON anterior; generador real usando
  solo IDs seleccionados y aplicando cambios; filtros auditivos imposibles; referencia
  de Completar separada de selección; filtros de notas sin reducir el teclado.
- Geometría: centros de blancas/negras en 360×180, 800×120 y 1200×300; prioridad limitada
  al área real de negras y límites exteriores sin saltar a otra tecla. Son comprobaciones
  numéricas, no pruebas de comodidad física ni latencia.
- `python3 tools/verify_assets.py`: 6 pruebas correctas. WAV/bucles, afinación, margen de
  mezcla, traducciones, logo exacto, ausencia de permisos solicitados en el manifiesto
  fuente y orientación no forzada. No se modifica el logo ni las muestras.
- `git diff --check`: correcto. ZIP/SHA-256 de APK y `apksigner verify` correctos;
  certificado idéntico al debug anterior. Sigue 0.2.0, por lo que puede instalarse encima.
- Lint: 0 errores, 7 avisos y 8 sugerencias. Avisos: org.json de pruebas, forma/ubicación
  del logo, sugerencias KTX para Canvas y tres accesos SharedPreferences. No se suprimen.

## Instrumentadas compiladas, NO ejecutadas

Hay 16 pruebas en RecognitionScreenTest, incluyendo el recorrido MotionEvent → piano
→ modelo → texto, selección compartida de teclas, Clear con dedos apoyados y MOVE viejo,
limpieza en Completar, cambios rápidos de ejercicio, ambas orientaciones, controles de
las cuatro actividades sin scroll, feedback en Completar avanzado, guardar y cancelar
filtros individuales y accesos rápidos. La APK de tests compila; no se ha ejecutado en
emulador ni dispositivo durante esta revisión.

Con Android SDK, A16 conectado y autorizado, en un perfil/dispositivo de prueba:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

La suite modifica idioma, orientación, filtros y progreso local al comprobar respuestas;
no usar un perfil con datos que deban conservarse. Ejecutar una suite UI no sustituye
la comprobación física de la comodidad del teclado y audio.

## Pendiente en Samsung Galaxy A16 y tablets

1. Instalar la nueva APK sobre la anterior: misma firma/versión, distinguir por nombre
   usability y hash. Verificar progreso y ajustes anteriores, inicio sin selección.
2. En todas las herramientas tocar Do3/Mi3/Sol3, soltar y volver a tocar Mi3: deja de
   estar azul y se actualiza reconocimiento. Repetir con negras y octavas duplicadas.
3. Limpiar con selección, tras acertar, en Completar, con pistas y con dedos apoyados:
   ninguna selección azul permanece. Levantar y volver a pulsar funciona; movimientos
   viejos no restauran notas. Las referencias doradas se distinguen de selección azul.
4. Cambiar rápidamente de ejercicio/herramienta mientras hay notas pulsadas; ninguna
   voz o nota azul se hereda. Comprobar dos dedos sobre una tecla y cambios al deslizar.
5. En horizontal A16 comprobar las cuatro actividades, feedback Incorrecto, Completar
   avanzado y los botones Repetir/Pista/Validar/Limpiar/Siguiente sin desplazamiento,
   recorte ni superposición al piano. Repetir en vertical, fuente ampliada y tablet.
6. Seleccionar individuales, familias y todos/básicos/ninguno en Ajustes; aplicar,
   cancelar, cerrar/reabrir app y verificar los ejercicios. Filtros imposibles y vacíos
   deben mostrar explicación y Modificar filtros. Avanzado debe conservarse.
7. Probar Pista, Más, guía, solución, referencia Do4, escuchar selección, estadísticas,
   reconocimiento/constructor, español/inglés y notaciones; sin funciones perdidas.
8. Comprobar audio de notas mantenidas, volumen, latencia, interrupciones, TalkBack,
   modo avión y modo oscuro del sistema (la app debe seguir clara).

El panel de práctica ya no tiene contenedor de scroll en código. Eso NO demuestra por
sí solo que todas las combinaciones de pantalla/fuente tengan espacio; los límites de
controles y teclado se comprueban en la suite instrumentada aún pendiente y en físico.
No se declara la versión estable en hardware ni se fusiona esta PR con esta evidencia.
