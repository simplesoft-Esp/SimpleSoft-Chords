# Guía de SimpleSoft-Chords 0.2.0

Android 8 o superior. Funciona sin conexión ni cuentas. El tema siempre es claro.
La distribución se adapta a vertical y horizontal; en móvil se recomienda horizontal
para disponer de teclas más anchas. El piano siempre abarca Do3–Do5, sin zoom ni scroll
horizontal. Menú abre Reconocimiento, Constructor, Aprendizaje, Ajustes, Información
y Estadísticas; se conserva el logotipo oficial.

## Piano común

Toca una nota para seleccionarla en azul. Vuelve a tocarla para deseleccionarla.
Soltar un dedo detiene su sonido, pero no cambia la selección. Puedes construir
un acorde con toques separados o simultáneos. Dos dedos sobre la misma nota comparten
una voz y no alternan la selección dos veces. Deslizar cambia de tecla al superar
el pequeño margen de movimiento; las teclas negras tienen prioridad en su área real.
Las etiquetas se recortan a su tecla para no invadir las vecinas.

Las pulsaciones físicas y las notas seleccionadas son estados distintos: una tecla
físicamente pulsada pero deseleccionada conserva solo el contorno. **Limpiar** borra
todas las notas seleccionadas, también después de resolver o en Completar, retira
pistas y aciertos del piano y detiene el sonido. Si había dedos apoyados, levántalos
y vuelve a pulsar; los movimientos antiguos no vuelven a seleccionar notas.
**Siguiente** inicia un ejercicio sin selección ni sonidos anteriores.

## Reconocimiento y constructor

Reconocimiento muestra primero el nombre grande, después las notas e inversión/bajo
cuando corresponde. Se recalcula al añadir y al quitar notas; permanece al soltar.
Para sustituir todo el acorde utiliza Limpiar, en vez de esperar a que otro gesto lo borre.
Las duplicaciones en otras octavas no cambian la identidad; el bajo real determina
la inversión. Información contiene la leyenda enarmónica y las interpretaciones
alternativas: el orden de resultados no implica certeza tonal.

Constructor conserva fundamental, tipo e inversión, posiciones dentro del rango,
notas de referencia doradas y reproducción. Las referencias no son notas seleccionadas
azules. El piano utiliza el mismo comportamiento de selección y Limpiar.

## Ajustes de aprendizaje

Abre Menú → Ajustes → Aprendizaje. Elige modo y actividad. Los acordes se agrupan
por familias; puedes marcar cada tipo o toda su familia. Los accesos rápidos son:
seleccionar todos, seleccionar básicos (mayor, menor, disminuido y aumentado) y
desmarcar todos. Los cambios son un borrador hasta **Aplicar**; Cancelar los descarta.
La selección vacía también se guarda, sin volver a activar acordes por defecto.

Otros filtros contiene Avanzado y los filtros de notas sueltas (blancas/cromáticas,
una/dos octavas). Estos últimos limitan preguntas de notas, nunca el piano. Interfaz
contiene idioma ES/EN, notación y etiquetas. Todos estos ajustes, incluido Avanzado,
se conservan localmente. Los ajustes antiguos siguen siendo legibles.
Para empezar el oído, selecciona mayor y menor; después amplía a otras tríadas o
séptimas utilizando sus familias. No es necesario distinguir Do mayor de Re mayor
por oído absoluto: los distractores comparten fundamental y registro.

## Práctica

La pantalla muestra actividad, número de ejercicio y aciertos de la sesión, instrucción,
respuestas cuando correspondan, Repetir, Pista, Validar, Limpiar, Siguiente y el piano.
No hay controles de configuración ni explicaciones permanentes en esta pantalla.
Si no se puede generar una pregunta válida, se explica y **Modificar filtros** abre Ajustes.

- Construir: selecciona las notas del acorde pedido y pulsa Validar.
- Identificar: lee las notas presentadas, elige el nombre y pulsa Validar.
- Completar: la nota proporcionada aparece como referencia dorada y en la instrucción;
  selecciona las notas restantes. Al validar se combina esa referencia con tu selección.
  No hay ninguna nota azul obligatoria o protegida; Limpiar siempre vacía la selección.
- Oído: Repetir reproduce el acorde. Elige su cualidad y pulsa Validar.

Las preguntas de notas sueltas también se responden seleccionando y pulsando Validar.
«Incorrecto» permite reintentar y mantiene azul, sin rojo. El acierto se muestra en
verde; Siguiente es manual. Puedes seguir alternando notas o limpiar después de un
acierto, sin contabilizar otro acierto para la misma pregunta.
La validación básica acepta clases de altura correctas en otras octavas y duplicadas.
Avanzado exige el bajo/inversión solo cuando la instrucción lo indica.

Pista abre una explicación progresiva, sin desplazar la pantalla de práctica. Más
conserva Escuchar selección, referencia Do4, Cómo se construye (guía paso a paso)
y Ver solución. Las ayudas se registran en las estadísticas como antes. Las opciones
por nombre quedan bloqueadas tras resolver hasta Siguiente; limpiar siempre borra
las notas del piano. No hay avance automático.

El audio se detiene al salir/perder foco. La latencia, la comodidad y la visibilidad
real en el A16 y tablets requieren comprobación física; consulta PRUEBAS.md.
