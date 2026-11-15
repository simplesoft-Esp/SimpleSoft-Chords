package com.simplesoft.chords

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateObserver
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class RecognitionStateTest {
 private val catalog=Catalog(File("../shared/catalog.json").readText())
 private val state=RecognitionState(catalog)
 private fun expect(id:String, root:Int, inversion:Int, name:String) {
  val match=state.matches.first()
  assertEquals(id,match.position.type.id)
  assertEquals(root,match.position.root%12)
  assertEquals(inversion,match.inversion)
  assertEquals(name,match.position.displayName("es",true))
 }
 @Test fun cMajorIsRecognizedWhileHeldAndRemainsAfterEveryRelease(){
  listOf(48,52,55).forEach(state::press)
  expect("major",0,0,"Do mayor")
  assertEquals("C major",state.matches.first().position.displayName("en",false))
  listOf(52,48,55).forEach{state.release(it);expect("major",0,0,"Do mayor")}
  assertEquals(setOf(48,52,55),state.played)
 }
 @Test fun aMinorIsRecognizedLiveAndLatched(){
  listOf(57,60,64).forEach(state::press)
  expect("minor",9,0,"La menor")
  listOf(64,60,57).forEach(state::release)
  expect("minor",9,0,"La menor")
 }
 @Test fun majorAndMinorInversionsUseTheActualBass(){
  for((notes,id,root,inversion,name) in listOf(
   Case(listOf(52,55,60),"major",0,1,"Do mayor"),
   Case(listOf(55,60,64),"major",0,2,"Do mayor"),
   Case(listOf(60,64,69),"minor",9,1,"La menor"),
   Case(listOf(52,57,60),"minor",9,2,"La menor")
  )) {
   state.reset();notes.forEach(state::press)
   expect(id,root,inversion,name)
   assertEquals(notes.min(),state.matches.first().bass)
   notes.forEach(state::release);expect(id,root,inversion,name)
  }
 }
 @Test fun octaveDuplicatesKeepIdentityAndRealBass(){
  listOf(48,52,55,60,64,67,72).forEach(state::press)
  expect("major",0,0,"Do mayor")
  assertEquals(setOf(0,4,7),Harmony.classes(state.played))
  listOf(48,52,55,60,64,67,72).forEach(state::release)
  expect("major",0,0,"Do mayor")
  state.reset();listOf(57,60,64,69,72).forEach(state::press)
  expect("minor",9,0,"La menor")
 }
 @Test fun twoFingersOnOneNoteDoNotStartANewGestureEarly(){
  state.press(48);state.press(48);state.release(48)
  state.press(52);state.press(55)
  expect("major",0,0,"Do mayor")
  listOf(48,52,55).forEach(state::release)
  state.press(57)
  assertEquals(setOf(57),state.played)
  assertFalse(state.matches.any{it.position.type.id=="major"})
 }
 @Test fun newGestureAndExplicitResetNeverReusePreviousChord(){
  listOf(48,52,55).forEach(state::press)
  listOf(48,52,55).forEach(state::release)
  listOf(57,60,64).forEach(state::press)
  expect("minor",9,0,"La menor")
  state.reset()
  assertTrue(state.played.isEmpty());assertTrue(state.matches.isEmpty())
 }
 @Test fun composeObserversAreNotifiedWhenAChordIsPlayed(){
  var changes=0
  val observer=SnapshotStateObserver{it()}
  observer.start()
  try {
   observer.observeReads(Unit,{_:Unit->changes++}){state.matches}
   Snapshot.withMutableSnapshot{listOf(48,52,55).forEach(state::press)}
   assertTrue(changes>0)
   expect("major",0,0,"Do mayor")
  } finally {observer.stop();observer.clear()}
 }
 private data class Case(val notes:List<Int>,val id:String,val root:Int,val inversion:Int,val name:String)
}
