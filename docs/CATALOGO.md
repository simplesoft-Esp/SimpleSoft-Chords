# Catálogo musical 0.2.0 — esquema 1

37 tipos, 472 posiciones. No se promete un catálogo universal de todos los acordes.
Todas las fórmulas se reproducen completas, en temperamento igual, La4=440 Hz.
MIDI 48 = Do3/C3; MIDI 72 = Do5/C5. Algunos fabricantes numeran las octavas de otra manera.

La tabla cuenta únicamente posiciones en estado fundamental. Para una fórmula F, las fundamentales
válidas son todos los enteros entre 48 y 72−max(F), ambos incluidos.
El constructor añade inversiones elevando por octavas las notas anteriores al bajo
solicitado, ordenándolas y descartando las que salgan de 48–72 o no produzcan ese bajo.
No omite notas ni comprime extensiones. La validación inicial sí admite duplicaciones
y cambios de octava; el reconocimiento compara clases módulo 12, conservando el bajo real.
Ninguno de los 37 tipos incluidos queda sin posiciones. Cualquier tipo futuro con
max(F)>24 debe excluirse por no caber ni desde Do3.

«Sexta menor» se implementa como acorde menor con sexta mayor (m6 = 0,3,7,9),
convención habitual del símbolo m6; no significa un intervalo de sexta menor.
Los intervalos de dos notas incluidos son quinta justa y terceras mayor/menor.
Las extensiones 11 y 13 conservan todas las notas de la fórmula; pueden resultar
densas o disonantes. No se simplifican según las costumbres de interpretación.

| ID | Familia | Castellano / English | Símbolo | Semitonos | Grados diatónicos | Fundamental MIDI válida | Posiciones |
|---|---|---|---|---|---|---|---|
| fifth | dyads | quinta justa / perfect fifth | 5 | 0, 7 | 1, 5 | 48–65 | 18 |
| third-major | dyads | tercera mayor / major third | (3) | 0, 4 | 1, 3 | 48–68 | 21 |
| third-minor | dyads | tercera menor / minor third | (b3) | 0, 3 | 1, 3 | 48–69 | 22 |
| major | triads | mayor / major | (mayor) | 0, 4, 7 | 1, 3, 5 | 48–65 | 18 |
| minor | triads | menor / minor | m | 0, 3, 7 | 1, 3, 5 | 48–65 | 18 |
| dim | triads | disminuido / diminished | dim | 0, 3, 6 | 1, 3, 5 | 48–66 | 19 |
| aug | triads | aumentado / augmented | + | 0, 4, 8 | 1, 3, 5 | 48–64 | 17 |
| sus2 | suspended | suspendido de segunda / suspended second | sus2 | 0, 2, 7 | 1, 2, 5 | 48–65 | 18 |
| sus4 | suspended | suspendido de cuarta / suspended fourth | sus4 | 0, 5, 7 | 1, 4, 5 | 48–65 | 18 |
| six | sixths | sexta mayor / major sixth | 6 | 0, 4, 7, 9 | 1, 3, 5, 6 | 48–63 | 16 |
| minor-six | sixths | menor con sexta / minor sixth | m6 | 0, 3, 7, 9 | 1, 3, 5, 6 | 48–63 | 16 |
| seven | sevenths | séptima dominante / dominant seventh | 7 | 0, 4, 7, 10 | 1, 3, 5, 7 | 48–62 | 15 |
| maj7 | sevenths | séptima mayor / major seventh | maj7 | 0, 4, 7, 11 | 1, 3, 5, 7 | 48–61 | 14 |
| min7 | sevenths | menor séptima / minor seventh | m7 | 0, 3, 7, 10 | 1, 3, 5, 7 | 48–62 | 15 |
| minmaj7 | sevenths | menor con séptima mayor / minor major seventh | m(maj7) | 0, 3, 7, 11 | 1, 3, 5, 7 | 48–61 | 14 |
| half-dim | sevenths | semidisminuido / half-diminished | m7b5 | 0, 3, 6, 10 | 1, 3, 5, 7 | 48–62 | 15 |
| dim7 | sevenths | séptima disminuida / diminished seventh | dim7 | 0, 3, 6, 9 | 1, 3, 5, 7 | 48–63 | 16 |
| add9 | added | mayor con novena añadida / major add ninth | add9 | 0, 4, 7, 14 | 1, 3, 5, 9 | 48–58 | 11 |
| madd9 | added | menor con novena añadida / minor add ninth | madd9 | 0, 3, 7, 14 | 1, 3, 5, 9 | 48–58 | 11 |
| add11 | added | mayor con oncena añadida / major add eleventh | add11 | 0, 4, 7, 17 | 1, 3, 5, 11 | 48–55 | 8 |
| madd11 | added | menor con oncena añadida / minor add eleventh | madd11 | 0, 3, 7, 17 | 1, 3, 5, 11 | 48–55 | 8 |
| six-nine | added | sexta con novena / six nine | 6/9 | 0, 4, 7, 9, 14 | 1, 3, 5, 6, 9 | 48–58 | 11 |
| nine | extensions | novena dominante / dominant ninth | 9 | 0, 4, 7, 10, 14 | 1, 3, 5, 7, 9 | 48–58 | 11 |
| maj9 | extensions | novena mayor / major ninth | maj9 | 0, 4, 7, 11, 14 | 1, 3, 5, 7, 9 | 48–58 | 11 |
| min9 | extensions | menor novena / minor ninth | m9 | 0, 3, 7, 10, 14 | 1, 3, 5, 7, 9 | 48–58 | 11 |
| eleven | extensions | oncena dominante / dominant eleventh | 11 | 0, 4, 7, 10, 14, 17 | 1, 3, 5, 7, 9, 11 | 48–55 | 8 |
| maj11 | extensions | oncena mayor / major eleventh | maj11 | 0, 4, 7, 11, 14, 17 | 1, 3, 5, 7, 9, 11 | 48–55 | 8 |
| min11 | extensions | menor oncena / minor eleventh | m11 | 0, 3, 7, 10, 14, 17 | 1, 3, 5, 7, 9, 11 | 48–55 | 8 |
| thirteen | extensions | trecena dominante / dominant thirteenth | 13 | 0, 4, 7, 10, 14, 17, 21 | 1, 3, 5, 7, 9, 11, 13 | 48–51 | 4 |
| maj13 | extensions | trecena mayor / major thirteenth | maj13 | 0, 4, 7, 11, 14, 17, 21 | 1, 3, 5, 7, 9, 11, 13 | 48–51 | 4 |
| min13 | extensions | menor trecena / minor thirteenth | m13 | 0, 3, 7, 10, 14, 17, 21 | 1, 3, 5, 7, 9, 11, 13 | 48–51 | 4 |
| seven-flat5 | altered | séptima con quinta bemol / seventh flat fifth | 7b5 | 0, 4, 6, 10 | 1, 3, 5, 7 | 48–62 | 15 |
| seven-sharp5 | altered | séptima con quinta aumentada / seventh sharp fifth | 7#5 | 0, 4, 8, 10 | 1, 3, 5, 7 | 48–62 | 15 |
| seven-flat9 | altered | séptima con novena bemol / seventh flat ninth | 7b9 | 0, 4, 7, 10, 13 | 1, 3, 5, 7, 9 | 48–59 | 12 |
| seven-sharp9 | altered | séptima con novena aumentada / seventh sharp ninth | 7#9 | 0, 4, 7, 10, 15 | 1, 3, 5, 7, 9 | 48–57 | 10 |
| nine-sharp11 | altered | novena con oncena aumentada / ninth sharp eleventh | 9#11 | 0, 4, 7, 10, 14, 18 | 1, 3, 5, 7, 9, 11 | 48–54 | 7 |
| seven-flat13 | altered | séptima con trecena bemol / seventh flat thirteenth | 7b13 | 0, 4, 7, 10, 20 | 1, 3, 5, 7, 13 | 48–52 | 5 |

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
