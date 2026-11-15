# Arquitectura Android 0.2.0 y futura web

El catálogo conserva sus 37 fórmulas y versión de datos 0.1.0. El producto sigue en
0.2.0/versionCode 2. `Position` representa raíz, inversión y MIDI real; `Harmony`
reconoce por clases de altura y conserva el bajo/alternativas. La validación inicial
acepta duplicaciones y octavas; el bajo se exige solo si se anuncia. `Answers.exact`
se mantiene para los contratos históricos de disposición exacta.

## Estado compartido del piano

`PianoState` sustituye RecognitionState y las dos selecciones antes superpuestas.
`selected` es el único conjunto que se pinta azul en cualquier herramienta.
`pressed` mapea identificadores de dedo a MIDI y no determina la selección.
`down` alterna solo la primera pulsación física de una nota; `up` retira el dedo,
sin borrar la selección. Reconocimiento deriva de selected, incluso al quitar notas.
No se limpia automáticamente al empezar otro gesto.

Limpiar y Siguiente vacían selected/pressed e incrementan generation. Los eventos
con token anterior se ignoran: un key-up viejo no libera el dedo del ejercicio nuevo.
PianoView sincroniza generation también antes de procesar MotionEvent, para no depender
de que Compose ya haya recompuesto; retira las voces/admisiones anteriores y no admite
MOVE hasta una nueva pulsación. Un cambio de tamaño libera punteros conservando selección.
Dos dedos sobre una nota comparten stream, liberado por el último. No hay color azul
alternativo por notas históricas; pulsación física sin selección solo marca contorno.

`PianoGeometry` comparte dibujo/hit testing, negras primero en sus límites reales.
Las negras usan 0,66 de ancho de blanca y 0,62 de altura; no se agrandan áreas invisibles
sobre teclas vecinas. Touch slop filtra el pequeño temblor al deslizar; etiquetas
recortadas a cada tecla y tamaño adaptado. El piano utiliza todo el ancho seguro.
`PianoAudio` no cambia: SoundPool, hasta 32 voces, WAV/bucles originales, foco y liberación.

## Aprendizaje, persistencia e interfaz

`ExerciseGenerator` comparte con pruebas JVM la selección de preguntas y distractores.
Solo usa los IDs guardados. Explica filtros sin candidatos; el modo mixto con una sola
cualidad permite construir. Las referencias de completar están en Question.given,
no en selected: al validar se compara selected+given. Limpiar no exceptúa ninguna tecla.

`LearningConfig.kt` contiene Prefs, ChordSelection y PrefsCodec. Se conserva el formato
JSON v1 en SharedPreferences privado `chords.v1`: claves prefs y stats. advanced pasa
a ser un campo persistente con false de respaldo; una selección IDs vacía se conserva.
Ajustes usa borrador local y solo Aplicar persiste/genera otra pregunta. Cancelar no escribe.
Familias e individuales comparten los mismos IDs; básicos son las cuatro tríadas.
No se crean catálogos, modos ni servicios nuevos. Estadísticas previas siguen legibles.

`TrainingModel` coordina piano, audio, preguntas, intentos, ayudas y estadísticas.
El número de ejercicio y aciertos de sesión son progreso de sesión, sin objetivo finito
inventado. La puntuación solo cuenta una resolución por pregunta. Validar es explícito,
incluidas notas sueltas. Modificar teclas/limpiar no queda bloqueado al resolver.

`MainActivity` conserva menú fijo y reserva espacio de controles/feedback antes de
asignar altura al piano. PracticePanel no tiene scroll: ejercicio/progreso, instrucción,
opciones y FlowRow de acciones de al menos 48 dp. Filtrado/configuración solo en Ajustes;
Pista/guía/solución en diálogo y acciones secundarias en Más. Constructor/reconocimiento,
ajustes e información sí pueden desplazar su contenido secundario.
No se fuerza orientación: constraints y fontScale adaptan alturas, ancho y filas.
`AppTheme.kt` conserva paleta clara, Surface con contenido oscuro y logo intacto.

Para futura web: portar selección, punteros, generation, fórmulas y generador con estas
regresiones; adaptar Web Audio, Pointer Events, persistencia y UI. JSON, WAV y marca
con sus licencias siguen reutilizables. No se construye ni publica una web aquí.
