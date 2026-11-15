package com.simplesoft.chords

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** One overlapping touch gesture; the last chord is latched until the next gesture. */
internal class RecognitionState(private val catalog:Catalog) {
 private val heldNotes=mutableMapOf<Int,Int>()
 var played by mutableStateOf(emptySet<Int>())
  private set
 val matches get()=Harmony.recognize(played,catalog)
 fun press(note:Int) {
  if(heldNotes.isEmpty())played=emptySet()
  heldNotes[note]=(heldNotes[note]?:0)+1
  played=played+note
 }
 fun release(note:Int) {
  val count=(heldNotes[note]?:1)-1
  if(count<=0)heldNotes.remove(note) else heldNotes[note]=count
  // Do not clear played here: the chord must remain readable after key-up/cancel.
 }
 fun reset(){heldNotes.clear();played=emptySet()}
}
