# Recursos y licencias

## Código y datos originales

El código específico de SimpleSoft Chords, catálogo, traducciones, documentación,
regeneradores y WAV sintetizados de esta entrega se ofrecen bajo MIT (ver LICENSE).
No incorporan canciones, grabaciones comerciales ni bancos de muestras de terceros.
Los WAV son síntesis aditiva determinista generada por tools/make_data.py:
6 parciales armónicos, ataque de 6 ms, decaimiento por parcial y final suave,
mono PCM16 a 22050 Hz, 2,3 segundos. Son reutilizables en Android y web.
Los 25 bucles sustain_*.wav adicionales se generan con tools/make_sustain.py:
40 períodos completos, cuatro armónicos, PCM16 mono a 22050 Hz. Son síntesis original
bajo MIT, sin recursos externos. El redondeo del tamaño mantiene la afinación a menos
de un cent de La4=440 Hz. Los originales piano_*.wav se conservan sin cambios.

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
