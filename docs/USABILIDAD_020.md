# Revisión de usabilidad y teclado 0.2.0

## Causa y corrección

La vista pintaba azul tanto por selected (respuesta) como por played (gesto reconocido).
Quitar una nota de la respuesta o Limpiar no borraba el segundo conjunto. Además,
Completar protegía una tecla y Limpiar retornaba sin hacer nada tras un acierto.

Ahora solo PianoState.selected determina el azul. El reconocimiento y las respuestas
leen ese mismo conjunto, que alterna cada nueva pulsación. Los dedos/audio se gestionan
aparte y soltar no cambia selección. Limpiar no tiene excepciones ni bloqueo por acierto.
Cada nuevo ejercicio limpia e invalida entradas anteriores por generation, incluso si
se repite exactamente la misma pregunta. Las referencias de Completar son doradas e
instructivas, no seleccionadas ni protegidas; validación combina referencia y respuesta.
Si se limpia con dedos apoyados, deben levantarse antes de volver a activar esas teclas.

## Práctica y ajustes

La práctica utiliza una distribución sin scroll vertical: ejercicio y aciertos de sesión,
instrucción, respuestas y Repetir/Pista/Validar/Limpiar/Siguiente. Más conserva referencia,
escuchar selección, guía y solución. Pista y explicaciones aparecen en diálogos.
El espacio se calcula según alto, ancho, tamaño de fuente, respuestas, referencia y bajo.
No se fuerza orientación, no se amplía el teclado y no se añade zoom ni scroll horizontal.

Ajustes tiene Aprendizaje e Interfaz. Las familias permiten seleccionar cada tipo o la
familia completa; todos/básicos/ninguno son accesos rápidos. El borrador se conserva
solo con Aplicar y se descarta al cancelar. Los IDs vacíos y Avanzado se guardan también.
Los filtros que no generan preguntas muestran una explicación y acceso directo a Ajustes.

## Alcance de la verificación

35 pruebas JVM y 6 pruebas de recursos ejecutadas, más compilación de app y APK de tests.
16 pruebas instrumentadas compiladas, no ejecutadas. Incluyen controles sin scroll,
ambas orientaciones, feedback en completar avanzado, limpieza mientras hay dedos
apoyados y cambios rápidos. La comodidad táctil y la visibilidad real en A16/tablets
NO se consideran verificadas por una compilación o por pruebas de geometría.
Consultar PRUEBAS.md y evidencia usability-020 antes de declarar estable esta versión.
