# Arquitectura Android 0.2.0 y futura web

`shared/catalog.json` conserva sus 37 fórmulas y versión de datos 0.1.0; la aplicación
es 0.2.0/versionCode 2. No se confunden versión de datos y versión de producto.
`Position` añade inversión (0 por defecto) y notas MIDI ordenadas. `Harmony` distingue
clases de altura, bajo, disposición y coincidencias múltiples. `Answers.exact` sigue
existiendo para contratos históricos de disposición exacta; el aprendizaje usa
`Harmony.accepts`, comparando clases y, solo cuando se anuncia, el bajo.

`Harmony.voicings` filtra registro, número de clases e inversión real. Eleva por octavas las notas anteriores hasta superar el bajo solicitado,
y permite una fundamental teórica fuera del registro si todas las notas reales caben.
No pliega extensiones arbitrariamente. `recognize` prueba cada raíz y fórmula, conserva todas
las coincidencias exactas de clases y calcula el bajo del MIDI mínimo. El orden
prioriza estado fundamental y fórmulas compactas, sin asignar probabilidades.

`RecognitionState` contiene las notas observables del gesto y el recuento por nota de
punteros activos. Su resultado se calcula a partir del conjunto observable; soltar el
último dedo conserva el acorde y el siguiente gesto lo sustituye. Lo usan el modelo
Android y las pruebas JVM. `Position.displayName` comparte el formateo probado con la UI.

`TrainingModel` administra preguntas, selección persistente, respuesta pendiente,
resolución, pistas, preferencias y estadísticas. Construir/completar validan con botón;
identificar/oído guardan la opción hasta Validar. La nota de completar es inmutable.
Oído usa distractores de otra cualidad con la misma fundamental y registro. Se filtran
preguntas auditivas que carezcan de alternativas; si no queda ninguna se explica.
Los tres niveles son preselecciones de filtros, no una evaluación automática del alumno.
El modo mixto histórico alterna construir y oído. Las preguntas de notas sueltas
mantienen su comportamiento anterior, fuera de las cuatro actividades de acordes.

`PianoView` realiza hit testing de negras primero, multitáctil, cambios de nota al
arrastrar, liberación al cancelar/perder foco/desconectar y nodos de accesibilidad.
Dos dedos sobre una tecla comparten stream; solo el último al soltar lo libera.
`played` representa el último gesto para reconocimiento; `selected` representa la
respuesta por toques de construir/completar. El sonido no depende del resaltado.
Al cambiar de pregunta o herramienta, la vista libera los punteros anteriores.

`PianoAudio`: SoundPool de hasta 32 voces, 25 muestras originales de 2,3 s para
preescucha y 25 bucles periódicos originales para pulsaciones sostenidas. Liberación
de 90 ms y cancelación de callbacks mediante epoch al detener; pérdida de foco y
pausa liberan audio. No hay micrófono ni red. La vía SoundPool precargada está pensada
para baja latencia; su latencia real requiere medición en un dispositivo.

`MainActivity`: Compose, panel superior desplazable y piano fijo al 50%, menú de herramientas y diálogos fuera del panel, opciones ejecutables, pistas,
tema claro fijo y traducciones ES/EN. `RecognitionPanel` coloca el nombre en primer
lugar; la leyenda y las alternativas extensas están en el diálogo de Información.
`AppTheme.kt` centraliza la paleta neutra y los colores auditados del Canvas.
La Surface raíz establece tanto fondo como color de contenido; `values-v29` desactiva
Force Dark sin exigir API 29 a los móviles con API 26.
Se adapta de Converter la paleta clara, la política ES/EN y la separación de
estado/UI; no se copia el banner animado ni se modifica el repositorio de referencia.
El logo permanece byte por byte. SharedPreferences privado `chords.v1` conserva las
claves JSON `prefs` y `stats` y los ajustes previos. Sin backup ni servicios externos.

Para una futura web, reutilizar catálogo, traducciones, fórmulas, logo autorizado y WAV.
Portar `Harmony` a TypeScript y cubrir inversiones, bajo, duplicaciones y ambigüedades
con los mismos ejemplos. Los contratos `shared/examples.json` siguen describiendo
validación MIDI exacta histórica; no sustituyen las pruebas nuevas de aprendizaje.
Usar Web Audio con fuentes precargadas, bucles, envolventes y cancelación al perder
visibilidad; Pointer Events con captura y cancelación. La UI Compose y el almacenamiento
Android necesitan adaptadores propios. No se ha construido ni publicado una web.
