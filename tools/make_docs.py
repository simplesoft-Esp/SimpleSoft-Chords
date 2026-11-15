from pathlib import Path
import json,hashlib
R=Path(__file__).resolve().parents[1];D=R/'docs';C=json.loads((R/'shared/catalog.json').read_text())
def doc(name,text): (D/name).write_text(text.strip()+'\n')
rows=['| ID | Familia | Castellano / English | Símbolo | Semitonos | Grados diatónicos | Fundamental MIDI válida | Posiciones |','|---|---|---|---|---|---|---|---|']
for t in C['types']:
 top=72-max(t['semitones']);rows.append(f"| {t['id']} | {t['family']} | {t['names']['es']} / {t['names']['en']} | {t['symbol'] or '(mayor)'} | {', '.join(map(str,t['semitones']))} | {', '.join(map(str,t['degrees']))} | 48–{top} | {top-47} |")
doc('CATALOGO.md','''# Catálogo musical 0.1.0 — esquema 1

37 tipos, 472 posiciones. No se promete un catálogo universal de todos los acordes.
Todas las fórmulas se reproducen completas, en temperamento igual, La4=440 Hz.
MIDI 48 = Do3/C3; MIDI 72 = Do5/C5. Algunos fabricantes numeran las octavas de otra manera.

La fundamental es siempre la altura más grave. Para una fórmula F, las fundamentales
válidas son todos los enteros entre 48 y 72−max(F), ambos incluidos.
No hay inversiones, notas omitidas, octavas comprimidas ni duplicaciones.
Ninguno de los 37 tipos incluidos queda sin posiciones. Cualquier tipo futuro con
max(F)>24 debe excluirse por no caber ni desde Do3.

«Sexta menor» se implementa como acorde menor con sexta mayor (m6 = 0,3,7,9),
convención habitual del símbolo m6; no significa un intervalo de sexta menor.
Los intervalos de dos notas incluidos son quinta justa y terceras mayor/menor.
Las extensiones 11 y 13 conservan todas las notas de la fórmula; pueden resultar
densas o disonantes. No se simplifican según las costumbres de interpretación.

'''+ '\n'.join(rows)+'''

## Escritura y equivalencias

`semitones` determina sonido y validación; `degrees` determina función y escritura.
Por ejemplo Do dim7: Do3, Mi♭3, Sol♭3, Si♭♭3. Si♭♭ comparte tecla con La.
Fa♯ maj7 utiliza Mi♯, no Fa, en la explicación. Se admiten dobles alteraciones.
Las etiquetas del piano usan nombres canónicos: C, C♯, D, E♭, E, F, F♯, G, A♭, A, B♭, B.
La notación Do/Re o C/D es independiente del idioma.
Enarmónicos se validan por MIDI, nunca por texto.
`aliases` admite símbolos y nombres adicionales; no genera entradas duplicadas.

En reconocimiento de tipo se excluyen como distractores todos los tipos seleccionados
que podrían describir el mismo conjunto de clases de altura. En nombre completo se
excluyen conjuntos de clases equivalentes aunque cambie el registro. Es una política
conservadora, especialmente útil para aumentados, disminuidos y suspendidos.
Con menos de dos respuestas distintas se pide ampliar filtros o construir.

Datos fuente: `shared/catalog.json`. Regenerador: `tools/make_data.py`.
''')
doc('GUIA_ES.md','''# SimpleSoft Chords — Guía de uso

Usa el móvil en horizontal. El piano siempre tiene Do3–Do5: 15 blancas y 10 negras.
La parte superior se desplaza verticalmente; el piano permanece fijo y ocupa la mitad inferior.

1. Selecciona Solo notas, Solo acordes o Todo.
2. En acordes, elige Identificar por oído, Construir en el piano o Mezclar ambas.
3. Abre Contenido y marca familias o tipos. Empieza con mayores y menores.
   Aplica para guardar; Cancelar conserva la selección anterior.
4. Escucha. Si el audio aún está cargando, espera al mensaje de preparación.

## Notas

La app reproduce una nota automáticamente al iniciar una pregunta. Busca su tecla.
Cada pulsación suena y se evalúa: ✓ acierto, ✕ error. Puedes reintentar, Repetir y
escuchar Do4 de referencia. No necesitas oído absoluto. Los filtros de notas están
en Ajustes: blancas/todas y Do3–Si3/Do3–Do5. No afectan a los acordes.

## Identificar acordes

Repite el conjunto y elige una respuesta. Ajustes permite reconocer tipo o nombre
completo. Las alternativas se reducen a lo realmente disponible. Si no hay alternativas
sonoras distintas, amplía Contenido o elige Construir. La referencia Do4 está disponible.

## Construir

La instrucción indica la fundamental con su octava. Toca cada tecla para seleccionarla
(● azul); una segunda pulsación la deselecciona y vuelve a sonar. Puedes tocar varias
teclas a la vez. Escuchar selección toca el conjunto; Limpiar lo borra.
Comprobar exige exactamente las alturas solicitadas, sin duplicaciones de octava.
Las correctas llevan ✓, las sobrantes ✕. Puedes corregir; Ver solución muestra con ★
las alturas objetivo y enumera las ausentes. Siguiente crea otra pregunta.

## Aprender y configurar

Pista revela funciones, semitonos desde la fundamental, nombres y por último teclas.
Cómo se construye muestra y reproduce cada nota, su función y distancias desde la
fundamental y desde la nota anterior. Después reproduce el conjunto. Cerrar conserva
la pregunta y selección. Estas ayudas quedan registradas en ese ejercicio.

Ajustes cambia castellano/inglés/sistema, notación Do/Re o C/D y nombres en teclas.
Restaurar ajustes recupera los valores iniciales con confirmación.
Estadísticas muestra comprobaciones, errores, resueltos, aciertos sin errores ni ayudas
y resueltos con ayuda, por actividad y familia. Reiniciar progreso exige confirmación
y conserva ajustes. Las repeticiones tras resolver no suman nuevos resultados.
Las preguntas abandonadas con Siguiente no cuentan como resueltas; sus errores sí cuentan.
No hay vidas, cuentas, red ni niveles bloqueados. Al desinstalar se borran los datos.

El sonido es un piano sintetizado original, no una grabación de piano acústico.
Si otra app interrumpe el audio, se silencian las voces. Al volver pulsa Repetir.
No se necesita micrófono ni se conceden permisos.
''')
doc('USER_GUIDE_EN.md','''# SimpleSoft Chords — User guide

Hold your phone in landscape. The fixed C3–C5 keyboard has 15 white and 10 black keys.
Only the top training panel scrolls; the bottom half remains playable.

Choose Notes only, Chords only or All. Chords offers Identify by ear, Build on piano,
or Mix both. Content contains family and individual checkboxes, Select all and
Deselect all. Apply saves; Cancel keeps previous filters. Major and minor are selected initially.

For notes, listen and locate the pitch. Each press plays and checks it: ✓ correct,
✕ incorrect. Retry freely. Repeat and reference C4 are available; absolute pitch is
not required. Settings restricts note questions to white/all keys and C3–B3/C3–C5.
The keyboard stays fixed and chord questions are unaffected by note filters.

For chord listening, choose an answer. Settings selects type or full name recognition.
Equivalent pitch-class alternatives are excluded. If too few distinct answers remain,
expand Content or choose Build. Reference C4 is available in both recognition variants.

For building, the prompt states the root and octave. Tap to select (blue ●), tap again
to play and deselect. Multiple simultaneous touches work independently. Play selection
sounds the set; Clear removes it. Check requires the exact MIDI notes, including register,
with no extra octave doubling. Correct notes show ✓ and extra notes ✕. Correct and retry;
Show solution highlights target keys ★ and lists missing notes. Next changes question.

Hint reveals functions, root-relative semitones, spelled notes and keyboard keys in order.
How it is built walks through each note, its function, distance from the root and distance
from the previous note, then plays the full chord. Close preserves the exercise.
Help use is recorded separately from unassisted first-try success.

Settings offers Spanish/English/system language, independent solfège/letter notation,
key labels, chord recognition variant and note filters. Restore settings requires confirmation.
Statistics tracks checks, errors, solved, error-free unassisted success and helped solutions
by activity and family. Reset progress requires confirmation and preserves settings.
Leaving an exercise does not mark it solved; already recorded wrong checks remain.
There are no lives, compulsory levels, accounts or services. Uninstall deletes local data.

The included timbre is an original synthesized piano, not an acoustic piano recording.
Audio focus loss and app backgrounding stop voices. Use Repeat after returning.
No microphone or runtime permissions are needed.
''')
doc('ARQUITECTURA_WEB.md','''# Arquitectura Android y futura web

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
''')
doc('LICENCIAS.md','''# Recursos y licencias

## Código y datos originales

El código específico de SimpleSoft Chords, catálogo, traducciones, documentación,
regeneradores y WAV sintetizados de esta entrega se ofrecen bajo MIT (ver LICENSE).
No incorporan canciones, grabaciones comerciales ni bancos de muestras de terceros.
Los WAV son síntesis aditiva determinista generada por tools/make_data.py:
6 parciales armónicos, ataque de 6 ms, decaimiento por parcial y final suave,
mono PCM16 a 22050 Hz, 2,3 segundos. Son reutilizables en Android y web.
A4=440 Hz. Es un timbre sintético de tipo piano, no una grabación acústica.

## Logo oficial

Archivo original SimpleSoft-logo.png, ID Drive 1E4C7nuw557T2WaNy7JJFL__ivmLlCavc,
carpeta principal de SimpleSoft. Se incorpora byte por byte, sin rediseño.
Su titularidad y permiso de marca pertenecen al usuario; MIT no concede a terceros
una licencia sobre la marca SimpleSoft. Su uso en esta app está autorizado por el encargo.

## Dependencias

Kotlin stdlib y plugin Compose: Apache-2.0, JetBrains.
AndroidX Activity, Compose UI/Foundation/Material3, Lifecycle: Apache-2.0, Google/AndroidX.
Gradle wrapper y scripts copiados de Converter 006: Apache-2.0, Gradle.
Android Gradle Plugin: Apache-2.0. SDK Android: términos de Android SDK (herramienta,
no incluida como código redistribuible de esta app).
org.json en Android: API de plataforma; org.json de pruebas: licencia JSON con cláusula
«Good, not Evil», solo testImplementation y no empaquetado en APK ni necesario para web.
JUnit 4: EPL-1.0, dependencia exclusiva de pruebas.
No se incluyen tipografías ni SDK externos de publicidad o analítica.
Consultar los POM y archivos NOTICE de dependencias resueltas para atribuciones
transitivas; conservar sus licencias al redistribuir sus binarios.
''')
(R/'LICENSE').write_text('''MIT License

Copyright (c) 2026 SimpleSoft Chords contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

The SimpleSoft name and official logo are excluded from this license.
''')
