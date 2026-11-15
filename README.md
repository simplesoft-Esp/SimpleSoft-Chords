# SimpleSoft-Chords — Android 0.2.0

Versión 0.2.0, versionCode 2, com.simplesoft.chords. Android 8/API 26 o superior.
Kotlin + Jetpack Compose, horizontal, 100% offline. Sin cuentas, anuncios ni permisos.
37 tipos de acordes y conjuntos de dos notas, 472 posiciones fundamentales más inversiones ejecutables; piano fijo Do3–Do5.

Reconocimiento automático con alternativas e inversiones, constructor y cuatro actividades:
construir, identificar, completar y oído por cualidades. Validación explícita, aciertos
en verde, errores en azul, avance manual y pistas. Tres niveles: mayor/menor, tríadas
y séptimas. El nivel avanzado solicita el bajo expresamente. Tema claro fijo, con fondos blancos y texto oscuro.

## Abrir y compilar en Windows

1. Extrae el ZIP completo, por ejemplo en `C:\SimpleSoft\SimpleSoftChords`.
2. Android Studio: Open y selecciona la carpeta que contiene `settings.gradle.kts`.
3. Instala Android SDK Platform 36, Build Tools 35.0.0 y 36.0.0 desde SDK Manager.
   Acepta las licencias. Usa un **JDK completo 17 o 21**, con `javac`; el JBR de
   Android Studio sirve si incluye compilador. Configúralo en Gradle JDK.
4. Sincroniza Gradle. La primera compilación necesita Internet para herramientas
   y dependencias; la aplicación instalada no usa Internet.
5. Terminal PowerShell desde el proyecto:

```powershell
.\gradlew.bat --no-daemon testDebugUnitTest assembleDebug
```

APK resultante: `app\build\outputs\apk\debug\app-debug.apk`.
Versiones fijadas: AGP 8.13.0, Kotlin/Compose plugin 2.2.0, Gradle 8.13,
Compose BOM 2025.10.01. El wrapper incluye JAR y SHA-256 de la distribución.
No requiere claves API, servicios de Google, keystore privado ni archivos secretos.
Android Studio genera `local.properties` con tu SDK; no se incluye la ruta de este entorno.

## Instalar en Samsung Galaxy A16 SM-A165F

Activa opciones de desarrollador/depuración USB, conecta el móvil y autoriza tu ordenador.
Selecciona el teléfono y pulsa Run, o usa Android SDK platform-tools:

```powershell
adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

También puedes copiar una APK de depuración al teléfono y abrirla, autorizando la
instalación desde esa fuente. La APK debug no es una versión de publicación.
El SM-A165F no se ha probado físicamente en este entorno.

## Archivos

- `app/`: código Android, manifiesto sin permisos, logo oficial, 25 WAV originales y 25 bucles sostenidos.
- `shared/`: catálogo, traducciones, esquemas y ejemplos para Android y futura web.
- `docs/CATALOGO.md`: tabla de fórmulas, límites y escritura musical.
- `docs/GUIA_ES.md` y `docs/USER_GUIDE_EN.md`: uso.
- `docs/ARQUITECTURA_WEB.md`: lógica, estado y futura adaptación web, sin publicar.
- `docs/LICENCIAS.md`, `LICENSE`: recursos y dependencias.
- `docs/PRUEBAS.md`: verificaciones ejecutadas y pendientes; consultar antes de distribuir.
- `tools/`: regeneradores de datos, audio, documentación y verificación independiente.

## Verificación reproducible

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
python tools\verify_assets.py
```

Python 3 sin paquetes externos para las cinco pruebas de datos/audio.
`make_data.py` regenera catálogo y muestras originales; `make_sustain.py` los bucles; `make_i18n.py` regenera traducciones;
`make_contracts.py` regenera esquemas/ejemplos; `make_docs.py` regenera solo la tabla del catálogo.
Las guías y el informe de pruebas se mantienen manualmente desde 0.2.0.
No ejecutes regeneradores si has editado manualmente los archivos generados sin
trasladar también esos cambios a sus fuentes.

El timbre incluido es piano sintetizado original; no son muestras acústicas.
La interacción, latencia, accesibilidad y apariencia real deben revisarse en el teléfono.
No hay capturas reales si no figura una ejecución en el informe de pruebas.

## Estabilización tras la prueba en Galaxy A16

La pantalla inicial es reconocimiento: nombre grande, notas e inversión sobre el piano.
Menú da acceso al constructor, aprendizaje, estadísticas, ajustes e Información y ayuda.
La leyenda enarmónica y las alternativas detalladas ya no desplazan el nombre del acorde.
La versión sigue siendo 0.2.0/versionCode 2; la APK corregida usa la misma firma debug.
Consultar `docs/ESTABILIZACION_020.md` para diagnóstico y `docs/PRUEBAS.md` para evidencia,
pruebas ejecutadas y pruebas instrumentadas/físicas pendientes.

## Referencia SimpleSoft Converter

Inspeccionado sin modificarlo. Se adapta su paleta clara neutra (sin activar tema oscuro),
la selección de español/inglés con inglés de respaldo y la separación de lógica,
estado y Compose. Se conservan el logo oficial y las herramientas Gradle que Chords
ya compartía con Converter. No se incorpora el banner animado: no aporta valor a
la práctica musical y ocuparía espacio del piano. No se añaden dependencias.
