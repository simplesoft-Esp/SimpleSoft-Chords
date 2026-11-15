package com.simplesoft.chords

import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateObserver
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class PianoStateTest {
 private val catalog=Catalog(File("../shared/catalog.json").readText())
 private val state=PianoState()
 private val matches get()=Harmony.recognize(state.selected,catalog)
 private fun tap(note:Int){state.down(0,note);state.up(0)}
 private fun expect(id:String,root:Int,inversion:Int,name:String){
  val match=matches.first();assertEquals(id,match.position.type.id);assertEquals(root,match.position.root%12)
  assertEquals(inversion,match.inversion);assertEquals(name,match.position.displayName("es",true))
 }
 @Test fun repeatTapTogglesAndReleaseDoesNotChangeSelection(){
  state.down(0,48);assertEquals(setOf(48),state.selected);assertEquals(mapOf(0 to 48),state.pressed)
  state.up(0);assertEquals(setOf(48),state.selected);assertTrue(state.pressed.isEmpty())
  state.down(0,48);assertTrue(state.selected.isEmpty());assertEquals(mapOf(0 to 48),state.pressed)
  state.up(0);assertTrue(state.selected.isEmpty())
 }
 @Test fun cMajorIsLiveLatchedAndChangesWhenANoteIsRemoved(){
  listOf(48,52,55).forEachIndexed{i,n->state.down(i,n)}
  expect("major",0,0,"Do mayor");assertEquals("C major",matches.first().position.displayName("en",false))
  listOf(0,2,1).forEach{state.up(it);expect("major",0,0,"Do mayor")}
  tap(52);assertEquals(setOf(48,55),state.selected);assertFalse(matches.any{it.position.type.id=="major"})
  tap(52);expect("major",0,0,"Do mayor")
 }
 @Test fun minorInversionsAndOctaveDoublingsUseSelectedMidi(){
  for((notes,id,root,inversion,name) in listOf(
   Case(listOf(57,60,64),"minor",9,0,"La menor"),
   Case(listOf(52,55,60),"major",0,1,"Do mayor"),
   Case(listOf(55,60,64),"major",0,2,"Do mayor"),
   Case(listOf(60,64,69),"minor",9,1,"La menor"),
   Case(listOf(52,57,60),"minor",9,2,"La menor"),
   Case(listOf(48,52,55,60,64,67,72),"major",0,0,"Do mayor")
  )){
   state.clear();notes.forEach(::tap);expect(id,root,inversion,name);assertEquals(notes.min(),matches.first().bass)
  }
 }
 @Test fun simultaneousFingersOnSameKeyToggleOnlyOnce(){
  assertTrue(state.down(10,49));assertFalse(state.down(11,49));assertFalse(state.down(10,49))
  assertEquals(setOf(49),state.selected);assertEquals(2,state.pressed.size)
  state.up(10);assertEquals(mapOf(11 to 49),state.pressed)
  state.up(11);tap(49);assertTrue(state.selected.isEmpty())
 }
 @Test fun clearHasNoExceptionsIncludingBlackNotesAndBothOctaves(){
  (48..72).forEachIndexed{i,n->state.down(i,n)}
  state.clear();assertTrue(state.selected.isEmpty());assertTrue(state.pressed.isEmpty());assertTrue(matches.isEmpty())
  (48..72).forEachIndexed{i,_->state.up(i)}
  assertTrue(state.selected.isEmpty())
 }
 @Test fun staleMoveAndKeyUpCannotChangeTheNextExercise(){
  val previous=state.generation;state.down(0,48,previous);state.startExercise()
  assertTrue(state.selected.isEmpty());assertTrue(state.pressed.isEmpty())
  assertFalse(state.down(0,52,previous))
  state.down(0,55,state.generation);state.up(0,previous)
  assertEquals(setOf(55),state.selected);assertEquals(mapOf(0 to 55),state.pressed)
 }
 @Test fun repeatedRapidExerciseChangesStayEmptyEvenForTheSameQuestion(){
  repeat(100){
   val old=state.generation;state.down(0,49,old);state.startExercise();state.startExercise()
   state.up(0,old);assertFalse(state.down(1,54,old))
   assertTrue(state.selected.isEmpty());assertTrue(state.pressed.isEmpty())
  }
 }
 @Test fun partialReleaseDoesNotChangeOtherFingersOrSelection(){
  state.down(20,48);state.down(7,52);state.down(42,55);state.up(7)
  assertEquals(mapOf(20 to 48,42 to 55),state.pressed)
  expect("major",0,0,"Do mayor")
 }
 @Test fun composeIsNotifiedOnBothAddAndRemove(){
  var changes=0;val observer=SnapshotStateObserver{it()};observer.start()
  try {
   observer.observeReads(Unit,{_:Unit->changes++}){matches}
   Snapshot.withMutableSnapshot{tap(48);tap(52);tap(55)}
   assertTrue(changes>0);expect("major",0,0,"Do mayor")
   observer.observeReads(Unit,{_:Unit->changes++}){matches};val before=changes
   Snapshot.withMutableSnapshot{tap(52)}
   assertTrue(changes>before);assertEquals(setOf(48,55),state.selected)
  } finally {observer.stop();observer.clear()}
 }
 private data class Case(val notes:List<Int>,val id:String,val root:Int,val inversion:Int,val name:String)
}
