# Estabilización inicial 0.2.0 tras la prueba en Samsung Galaxy A16

Informe histórico anterior a la revisión de usabilidad. El comportamiento de selección
y navegación vigente se documenta en USABILIDAD_020.md y GUIA_ES.md.

## Diagnóstico

El usuario observó Do3 · Mi3 · Sol3 y las tres teclas azules sin un nombre visible.
Eso indica que las alturas 48, 52 y 55 habían llegado al estado mostrado. El motor
`Harmony.recognize` no se ha cambiado: con ese conjunto produce el tipo major,
fundamental Do, bajo Do3 e inversión 0. La regresión nueva comprueba ese resultado y
el texto «Do mayor» antes y después de soltar, además de La menor e inversiones.

En la UI anterior el nombre se colocaba después de la cabecera, selector de herramienta,
instrucciones, notas y leyenda enarmónica completa. Todo estaba dentro de la mitad
superior desplazable. En horizontal compacto esa jerarquía dejaba el resultado detrás
de contenido secundario y obligaba a desplazar. Además, una simple modificación de
background no proporcionaba LocalContentColor a los Text: el tema oscuro podía combinar
fondo oscuro y el color de contenido predeterminado. Las etiquetas blancas de las
teclas azules claras tampoco alcanzaban el contraste requerido para texto normal.

Se identifican estos defectos en el código y se corrigen; no se afirma haber reproducido
visualmente el fallo del A16 en este entorno. El recorrido real MotionEvent → PianoView
→ modelo → Compose tiene pruebas instrumentadas compiladas, pendientes de ejecución.

## Correcciones

- Nombre grande como primer elemento de reconocimiento, seguido de notas y, cuando
  corresponde, inversión/bajo. La pantalla inicial abre reconocimiento.
- Menú fijo fuera del resultado/piano con todas las herramientas, estadísticas,
  ajustes e Información. Ayuda enarmónica y alternativas completas se muestran ahí.
- Estado de reconocimiento compartido y observable probado en JVM: reconoce mientras
  se mantienen las notas, conserva el acorde al soltar/cancelar y lo sustituye cuando
  empieza otro gesto. Se conserva el conteo de dedos que comparten la misma nota.
- Tema claro neutro adaptado del Converter de referencia, Surface con color de contenido
  explícito, barras claras y Force Dark desactivado desde API 29. Sin cambios en Converter.
- Azul más oscuro para etiquetas blancas, tonos distintos para blancas/negras pulsadas
  y fondo de pista con mayor contraste. Se conserva el logotipo byte por byte.

No se añaden actividades, fórmulas, permisos de usuario ni servicios. No se cambia
el audio. La versión sigue en 0.2.0/versionCode 2 y se conserva el certificado debug,
por lo que la APK de estabilización puede reemplazar la APK anterior con `adb install -r`.
La instalación y el comportamiento final en A16 están pendientes de comprobación.

## Evidencia y límites

Ver PRUEBAS.md y `docs/evidence/stabilization-020/`. Las pruebas JVM comprueban motor,
formateo y estado observable; no sustituyen una prueba real de captura multitáctil,
medición de latencia, renderizado ni percepción visual. Los contrastes calculados
cubren los pares de texto activos de la paleta y las etiquetas del piano, no una
inspección visual completa del teléfono. Las pruebas instrumentadas incluyen un panel
horizontal de 640×320 dp con fuente al 130%, pero aún no se han ejecutado.
