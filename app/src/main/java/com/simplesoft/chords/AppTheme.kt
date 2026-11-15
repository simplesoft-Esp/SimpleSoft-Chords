package com.simplesoft.chords

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Fixed light SimpleSoft palette, adapted from Converter. No dynamic/dark colors. */
internal val SimpleSoftLight = lightColorScheme(
 primary=Color(0xFF171717), onPrimary=Color.White,
 primaryContainer=Color(0xFFEAEAEA), onPrimaryContainer=Color(0xFF171717),
 secondary=Color(0xFF595959), onSecondary=Color.White,
 secondaryContainer=Color(0xFFEAEAEA), onSecondaryContainer=Color(0xFF171717),
 tertiary=Color(0xFF245F9C), onTertiary=Color.White,
 tertiaryContainer=Color(0xFFE8F0F8), onTertiaryContainer=Color(0xFF173E6C),
 background=Color.White, onBackground=Color(0xFF171717),
 surface=Color.White, onSurface=Color(0xFF171717),
 surfaceVariant=Color(0xFFF2F2F2), onSurfaceVariant=Color(0xFF595959),
 surfaceContainer=Color(0xFFF2F2F2), surfaceContainerHigh=Color(0xFFEDEDED),
 surfaceContainerHighest=Color(0xFFE7E7E7), surfaceContainerLow=Color(0xFFF7F7F7),
 surfaceContainerLowest=Color.White, surfaceTint=Color.Transparent,
 outline=Color(0xFF767676), outlineVariant=Color(0xFF767676),
 inverseSurface=Color(0xFF171717), inverseOnSurface=Color.White
)

@Composable internal fun SimpleSoftTheme(content:@Composable ()->Unit) {
 MaterialTheme(colorScheme=SimpleSoftLight) {
  // A background modifier alone does not set LocalContentColor for plain Text.
  Surface(color=SimpleSoftLight.background,contentColor=SimpleSoftLight.onBackground,content=content)
 }
}

/** Canvas colors live beside the Compose theme so both rendering paths are audited. */
internal object PianoColors {
 const val whiteKey=0xFFF9F9F6.toInt()
 const val blackKey=0xFF1E2227.toInt()
 const val whiteSelected=0xFF245F9C.toInt()
 const val blackSelected=0xFF173E6C.toInt()
 const val correct=0xFF26774C.toInt()
 const val hint=0xFF805708.toInt()
 const val lightLabel=0xFFFFFFFF.toInt()
 const val darkLabel=0xFF1E2227.toInt()
}
