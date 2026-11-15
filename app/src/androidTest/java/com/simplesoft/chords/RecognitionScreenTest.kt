package com.simplesoft.chords

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Real Android MotionEvents → PianoView → shared recognition state → visible Compose text. */
@RunWith(AndroidJUnit4::class)
class RecognitionScreenTest {
 @get:Rule val compose=createAndroidComposeRule<MainActivity>()
 private lateinit var piano:PianoView
 private val held=mutableListOf<Pair<Int,Int>>()
 private var downTime=0L
 @Before fun prepare(){
  compose.runOnIdle{
   val model=ViewModelProvider(compose.activity)[TrainingModel::class.java]
   model.preferences(model.prefs.copy(language="es",latin=true))
   model.changeTool("recognize")
  }
  compose.waitForIdle()
  compose.runOnIdle{piano=findPiano(compose.activity.window.decorView)!!}
 }
 @Test fun cMajorVisibleLiveAndAfterKeyUp(){
  press(48,52,55)
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
  compose.onNodeWithTag("chordNotes").assertTextEquals("Do3 · Mi3 · Sol3").assertIsDisplayed()
  releaseAll()
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
 }
 @Test fun aMinorVisibleLiveAndAfterKeyUp(){
  press(57,60,64)
  compose.onNodeWithTag("chordName").assertTextEquals("La menor").assertIsDisplayed()
  releaseAll()
  compose.onNodeWithTag("chordName").assertTextEquals("La menor").assertIsDisplayed()
 }
 @Test fun inversionAndOctaveDoublingKeepTheName(){
  press(52,55,60,64)
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
  compose.onNodeWithTag("chordInversion").assertTextContains("Inversión: 1").assertIsDisplayed()
  releaseAll()
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
 }
 @Test fun cancellationRetainsResultAndTheNextGestureReplacesIt(){
  press(48,52,55)
  compose.runOnIdle{event(MotionEvent.ACTION_CANCEL);held.clear()}
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
  press(57,60,64)
  compose.onNodeWithTag("chordName").assertTextEquals("La menor").assertIsDisplayed()
  releaseAll()
 }
 @Test fun helpAndNavigationDoNotOccupyTheRecognitionPanel(){
  compose.onNodeWithText("Equivalencias:",substring=true).assertDoesNotExist()
  compose.onNodeWithText("Estadísticas").assertDoesNotExist()
  compose.onNodeWithText("Menú").performClick()
  compose.onNodeWithText("Información y ayuda").performClick()
  compose.onNodeWithText("Equivalencias:",substring=true).assertIsDisplayed()
  compose.onNodeWithText("Cerrar").performClick()
  compose.onNodeWithText("Menú").performClick()
  compose.onNodeWithText("Estadísticas").performClick()
  compose.onNodeWithText("Estadísticas").assertIsDisplayed()
 }
 @Test fun sharedKeyAndBothOctavesDoNotLosePointers(){
  press(48,48,52,55,60)
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
  releaseAll()
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
 }
 @Test fun compactLandscapeWithLargeTextKeepsTheNameAboveThePiano(){
  compose.runOnUiThread {
   val model=ViewModelProvider(compose.activity)[TrainingModel::class.java]
   val density=compose.activity.resources.displayMetrics.density
   compose.activity.setContent {
    SimpleSoftTheme {
     CompositionLocalProvider(LocalDensity provides Density(density,1.3f)) {
      Box(Modifier.requiredSize(640.dp,320.dp)){TrainingApp(model)}
     }
    }
   }
  }
  compose.waitForIdle()
  compose.runOnIdle{piano=findPiano(compose.activity.window.decorView)!!}
  press(48,52,55)
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
  compose.onNodeWithTag("chordNotes").assertIsDisplayed()
  val title=compose.onNodeWithTag("chordName").fetchSemanticsNode().boundsInRoot
  val keys=compose.onNodeWithTag("piano").fetchSemanticsNode().boundsInRoot
  assertTrue(title.bottom<=keys.top)
  releaseAll()
  compose.onNodeWithTag("chordName").assertTextEquals("Do mayor").assertIsDisplayed()
 }
 private fun press(vararg notes:Int){
  compose.runOnIdle {
   if(held.isEmpty())downTime=SystemClock.uptimeMillis()
   notes.forEach{note->
    val pointer=held.size;held+=pointer to note
    event(if(held.size==1)MotionEvent.ACTION_DOWN else MotionEvent.ACTION_POINTER_DOWN or (pointer shl MotionEvent.ACTION_POINTER_INDEX_SHIFT))
   }
  }
 }
 private fun releaseAll(){
  compose.runOnIdle{
   while(held.isNotEmpty()){
    event(if(held.size==1)MotionEvent.ACTION_UP else MotionEvent.ACTION_POINTER_UP or (held.lastIndex shl MotionEvent.ACTION_POINTER_INDEX_SHIFT))
    held.removeAt(held.lastIndex)
   }
  }
 }
 private fun event(action:Int){
  val whites=(48..72).filter{it%12 in Pitch.naturals}
  val properties=held.map{(id,_)->MotionEvent.PointerProperties().apply{this.id=id;toolType=MotionEvent.TOOL_TYPE_FINGER}}.toTypedArray()
  val coords=held.map{(_,note)->MotionEvent.PointerCoords().apply{
   x=(whites.indexOf(note)+.5f)*(piano.width/15f);y=piano.height*.85f;pressure=1f;size=1f
  }}.toTypedArray()
  val event=MotionEvent.obtain(downTime,SystemClock.uptimeMillis(),action,held.size,properties,coords,0,0,1f,1f,0,0,InputDevice.SOURCE_TOUCHSCREEN,0)
  try{assertTrue(piano.dispatchTouchEvent(event))}finally{event.recycle()}
 }
 private fun findPiano(view:View):PianoView? {
  if(view is PianoView)return view
  if(view is ViewGroup)for(i in 0 until view.childCount)findPiano(view.getChildAt(i))?.let{return it}
  return null
 }
}
