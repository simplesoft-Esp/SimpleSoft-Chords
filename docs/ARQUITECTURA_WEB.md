# Arquitectura Android y futura web

## Android 001

- `shared/catalog.json`: única fuente del catálogo, schemaVersion 1.
- `shared/i18n.json`: textos de interfaz y funciones, schemaVersion 1.
- `Music.kt`: catálogo, posiciones, escritura diatónica, afinación, bolsa aleatoria,
  validación exacta y distractores. Sin dependencias Android; usa org.json.
- `TrainingModel.kt`: estado de sesión, preferencias y estadísticas locales.
- `PianoAudio.kt`: SoundPool, 25 PCM precargados, hasta 32 voces, foco de audio y liberación.
- `PianoView.kt`: dibujo y hit testing negro primero, punteros independientes, teclas virtuales accesibles.
- `MainActivity.kt`: Compose, panel superior desplazable y teclado fijo al 50%.

El módulo app empaqueta `shared` como assets; no mantiene una segunda copia de los datos.
No se ha copiado SimpleSoft Core: el pie promocional no es necesario en un teclado
horizontal. Se mantienen logo, namespace, minSdk y herramientas compatibles con Converter 006.
La interfaz Compose no se traslada directamente a HTML.

## Contrato musical y estados

MIDI: enteros 48..72, f=440×2^((midi−69)/12).
Posición válida: root..root+max(formula) dentro del rango. Se suma cada semitono
exactamente, sin inversión, omisión ni reducción módulo 12.
Validar construcción: igualdad entre conjuntos de MIDI completos.
Las clases módulo 12 solo se usan para evitar alternativas auditivas equivalentes.
La bolsa baraja todas las posiciones/notas disponibles sin repetir hasta agotarlas;
al recargar evita que la última pregunta se repita si hay otra posibilidad.
Mezclar actividad decide oído/construcción aleatoriamente para acordes;
las notas siempre requieren escuchar y localizar. No se garantiza igual peso por familia.

Estado: pregunta → respuesta errónea/reintento o respuesta correcta/resuelto.
Seleccionar/desseleccionar no evalúa construcción; Comprobar sí.
Pista/guía/solución marcan helped antes de resolver. Contadores se guardan tras cada
comprobación; resolver solo incrementa una vez por pregunta. first requiere cero
errores y ninguna ayuda. Referencia y repetición sonora no se consideran ayuda penalizable.
Siguiente y cambios de ajustes crean otra pregunta y limpian selección/ayudas.
Cerrar guía conserva pregunta y selección. ViewModel conserva sesión en cambios de
configuración; tras cerrar proceso se crea otra pregunta, manteniendo preferencias y progreso.

Persistencia: SharedPreferences privado `chords.v1`, claves JSON `prefs` y `stats`.
No hay copia de seguridad de Android. prefs incluye idioma, notación, etiquetas,
modo, actividad, variante auditiva, filtro notas/octavas e IDs seleccionados.
stats usa claves actividad/familia con checks/errors/solved/first/helped.
Los esquemas futuros deben migrarse antes de cambiar IDs o semántica.

## Contratos para web

Reutilizar JSON, PNG original y 25 WAV con sus licencias. Reimplementar lógica en
TypeScript, comprobándola con `shared/examples.json` y las mismas fórmulas.
Los esquemas JSON versionados están en `shared/*.schema.json`.
Separar music, exercise generator, validation, audio adapter, persistence y UI.
No convertir Kotlin UI ni SharedPreferences a la web automáticamente.

Web Audio: crear/resumir AudioContext tras una acción explícita del usuario;
los navegadores suelen bloquear autoplay. Decodificar/preparar WAV una vez,
reproducir con AudioBufferSourceNode y GainNode; envelope de ataque/liberación,
limitador o ganancia conservadora, tope de voces y cancelación de fuentes viejas.
Usar tiempo de AudioContext para notas simultáneas; no depender de setTimeout para acordes.
Suspender/liberar voces al perder foco/visibilitychange y al cerrar vistas.
Comprobar Safari iOS, Chrome Android y escritorio antes de publicar.

Pointer Events: mapear cada pointerId a nota y fuente; capturar punteros, manejar
pointercancel y pérdida de foco. Hit test de negras antes de blancas. Añadir ratón,
foco accesible y mapa configurable de teclado físico con keyup y bloqueo de autorepeat.
Mantener dos octavas en móvil horizontal y escritorio; tratar pantalla vertical con
aviso de giro sin perder estado. El tamaño de fuente no debe tapar el piano.

Guardar prefs/progreso con claves versionadas en localStorage o IndexedDB,
validar entradas e incluir reinicio confirmado. No sincronizar con cuentas.
PWA opcional: manifest, iconos derivados del logo aprobado y service worker con
precaché versionada de HTML/JS/JSON/PNG/WAV. Actualizaciones atómicas, evitar mezclar
catálogos y código de versiones distintas. Offline solo tras la primera carga completa.

Para una web estática: TypeScript + Vite con build de producción, rutas relativas
al `base` configurado, subir `dist/`. No necesita servidor API ni claves.
Antes de publicar confirmar dominio, subruta, proveedor, acceso autorizado, HTTPS,
MIME/cache de WAV/JSON/JS, estrategia de actualizaciones y posibilidad de SPA fallback.
Probar instalación PWA y rutas desde subcarpeta. No se ha creado ni publicado una web.
