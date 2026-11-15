package com.simplesoft.chords

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.junit.Assert.*
import org.junit.Test

class ContrastTest {
 private fun contrast(a:Color,b:Color):Double {
  val la=a.luminance().toDouble();val lb=b.luminance().toDouble()
  return (maxOf(la,lb)+.05)/(minOf(la,lb)+.05)
 }
 @Test fun fixedLightThemeHasReadableEnabledText(){
  val s=SimpleSoftLight
  assertEquals(Color.White,s.background);assertEquals(Color.White,s.surface)
  listOf(s.onBackground to s.background,s.onSurface to s.surface,
   s.onSurfaceVariant to s.surfaceVariant,s.onPrimary to s.primary,
   s.onSecondary to s.secondary,s.onTertiary to s.tertiary,
   s.onPrimaryContainer to s.primaryContainer,s.onSecondaryContainer to s.secondaryContainer,
   s.onTertiaryContainer to s.tertiaryContainer,s.inverseOnSurface to s.inverseSurface
  ).forEach{(text,background)->assertTrue("Contrast $text on $background",contrast(text,background)>=4.5)}
 }
 @Test fun everyPianoLabelMeetsNormalTextContrast(){
  listOf(PianoColors.whiteSelected,PianoColors.blackSelected,PianoColors.correct,
   PianoColors.hint,PianoColors.blackKey).forEach{background->
   assertTrue("Piano contrast on $background",contrast(Color(PianoColors.lightLabel),Color(background))>=4.5)
  }
  assertTrue(contrast(Color(PianoColors.darkLabel),Color(PianoColors.whiteKey))>=4.5)
 }
}
