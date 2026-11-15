package com.simplesoft.chords

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Selection survives key-up. Physical pointers and their generation never define selection. */
internal class PianoState {
 var selected by mutableStateOf(emptySet<Int>());private set
 var pressed by mutableStateOf(emptyMap<Int,Int>());private set
 var generation by mutableStateOf(0);private set
 fun toggle(note:Int) {require(note in 48..72);selected=if(note in selected)selected-note else selected+note}
 fun down(pointer:Int,note:Int,token:Int=generation):Boolean {
  if(token!=generation || pointer in pressed || note !in 48..72)return false
  val first=note !in pressed.values
  pressed=pressed+(pointer to note)
  if(first)toggle(note)
  return first
 }
 fun up(pointer:Int,token:Int=generation) {if(token==generation)pressed=pressed-pointer}
 fun clear(){selected=emptySet();pressed=emptyMap();generation++}
 fun startExercise()=clear()
}

internal data class KeyBounds(val left:Float,val top:Float,val right:Float,val bottom:Float) {
 fun contains(x:Float,y:Float)=x>=left && x<right && y>=top && y<bottom
}
internal object PianoGeometry {
 val whites=(48..72).filter{it%12 in Pitch.naturals}
 val blacks=(48..72).filter{it%12 !in Pitch.naturals}
 fun bounds(note:Int,width:Float,height:Float):KeyBounds {
  val w=width/15f;val i=whites.indexOf(note)
  if(i>=0)return KeyBounds(i*w,0f,(i+1)*w,height)
  val edge=whites.count{it<note}*w
  return KeyBounds(edge-w*.33f,0f,edge+w*.33f,height*.62f)
 }
 fun keyAt(x:Float,y:Float,width:Float,height:Float):Int? =
  if(width<=0 || height<=0)null else (blacks+whites).firstOrNull{bounds(it,width,height).contains(x,y)}
}
