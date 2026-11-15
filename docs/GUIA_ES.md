# Guía de SimpleSoft-Chords 0.2.0

Android 8 o superior, en horizontal. Todo funciona sin conexión, cuentas ni permisos.
El piano fijo abarca Do3–Do5: 15 teclas blancas y 10 negras, sin ampliar el rango.

La pantalla inicial es **Reconocer acordes**. **Menú**, en la cabecera, abre las otras
herramientas, Estadísticas, Ajustes e Información y ayuda. El nombre del acorde aparece
primero y en grande; la ayuda extensa no ocupa la zona principal.

## Herramientas

**Reconocer acordes:** pulsa varias notas a la vez o añádelas manteniendo alguna
pulsada. Las notas se oyen mientras las mantienes; al soltar todas, su resaltado
azul y el análisis permanecen. El siguiente gesto inicia un acorde nuevo. Desliza
un dedo para cambiar de tecla. Dos dedos sobre la misma nota comparten una voz.
El análisis muestra notas reales, nombres posibles, fundamental, bajo e inversión
(0 = estado fundamental). Las octavas duplicadas no cambian la identidad. Información y ayuda contiene las alternativas para conservar ambigüedades como Do6/La menor séptima. El orden favorece
el estado fundamental, pero no representa una certeza tonal. En Información y ayuda, la leyenda explica
las equivalencias de sostenidos y bemoles. Un conjunto fuera del catálogo se indica
sin inventar un nombre.

**Constructor:** elige fundamental, tipo e inversión. Solo se ofrecen disposiciones
completas que caben en Do3–Do5 y tienen el bajo solicitado. Las notas se marcan
con estrellas y puedes escucharlas. Algunas inversiones de acordes extendidos no
caben; se excluyen del menú. No se omiten notas para hacerlos caber.

**Aprendizaje:** selecciona actividad, dificultad y contenido. Los niveles proponen
mayor/menor, las cuatro tríadas y séptimas. Contenido permite filtros personalizados.
Los filtros de blancas/octavas en Ajustes se aplican a las preguntas de notas sueltas;
el teclado permanece siempre completo.

## Cuatro actividades

- Construir: toca las notas del acorde pedido. Cada toque selecciona o deselecciona
  una nota; la selección permanece azul, aunque el sonido termine al soltar.
- Identificar: lee las notas presentadas y selecciona el nombre completo.
- Completar: una nota aparece seleccionada y no se puede quitar; añade las restantes.
- Oído: escucha y selecciona la cualidad. Las opciones comparten fundamental y
  registro, de modo que no se exige oído absoluto. Empieza por mayor frente a menor;
  después compara disminuido, aumentado y séptimas. Repetir vuelve a reproducir.

En las cuatro actividades de acordes pulsa **Validar**. Una elección por sí sola
no se evalúa. «Incorrecto» conserva la selección azul; no hay teclas rojas. Puedes
reintentar o pedir pistas. Un acierto marca las notas en verde y bloquea la respuesta;
pulsa **Siguiente** para avanzar. Nunca hay avance automático. También puedes saltar
una pregunta con Siguiente. Limpiar conserva la nota proporcionada al completar.

La validación inicial compara clases de altura: acepta otras octavas, inversiones
y duplicaciones, siempre que no falten ni sobren clases. En **Avanzado**, construir
y completar indican explícitamente inversión y bajo requerido. Se exige esa clase
de bajo, conservando libertad de octava y duplicaciones. No se exige una disposición
exacta oculta. Si no hay preguntas o distractores válidos, amplía filtros con el botón
propuesto o cambia a construir; con el contenido vacío debes ampliarlo primero.

## Profesor, ajustes y privacidad

Pista progresa desde funciones a distancias, nombres y posiciones. Cómo se construye
reproduce cada nota y luego el acorde completo, con función e intervalos. La tercera
mayor tiene cuatro semitonos; la menor, tres. Una inversión cambia el bajo sin cambiar
las clases de altura. Las ayudas se registran en las estadísticas de esa actividad.

Idioma español/inglés, notación Do–Si/C–B, etiquetas y filtros se guardan localmente,
al igual que las estadísticas. El tema es siempre claro, con fondo blanco y texto oscuro, incluso si el sistema usa modo oscuro. No se transmiten datos.
Los ajustes antiguos de 0.1.0 siguen siendo legibles. Herramienta, constructor y
opción Avanzado son estado de sesión. El audio se detiene al salir o perder el foco.
La latencia depende del dispositivo; consulta PRUEBAS.md antes de distribuir.
