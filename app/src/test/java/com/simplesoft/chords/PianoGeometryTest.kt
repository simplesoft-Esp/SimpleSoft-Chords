package com.simplesoft.chords

import org.junit.Assert.*
import org.junit.Test

class PianoGeometryTest {
 @Test fun everyWhiteAndBlackCenterHitsItsOwnKeyAtPhoneAndTabletSizes(){
  for((w,h) in listOf(360f to 180f,800f to 120f,1200f to 300f)){
   PianoGeometry.whites.forEach{n->val b=PianoGeometry.bounds(n,w,h);assertEquals(n,PianoGeometry.keyAt((b.left+b.right)/2,h*.85f,w,h))}
   PianoGeometry.blacks.forEach{n->val b=PianoGeometry.bounds(n,w,h);assertEquals(n,PianoGeometry.keyAt((b.left+b.right)/2,h*.3f,w,h))}
  }
 }
 @Test fun blackKeysHavePriorityOnlyWithinTheirActualBounds(){
  val w=800f;val h=160f
  PianoGeometry.blacks.forEach{n->
   val b=PianoGeometry.bounds(n,w,h);val x=(b.left+b.right)/2
   assertEquals(n,PianoGeometry.keyAt(x,b.bottom-1,w,h))
   assertNotEquals(n,PianoGeometry.keyAt(x,b.bottom+1,w,h))
   assertNotEquals(n,PianoGeometry.keyAt(b.left-.1f,10f,w,h))
   assertNotEquals(n,PianoGeometry.keyAt(b.right+.1f,10f,w,h))
  }
 }
 @Test fun edgeAndOutOfBoundsTouchesNeverWrapToAnotherNote(){
  assertEquals(48,PianoGeometry.keyAt(0f,159f,800f,160f))
  assertEquals(72,PianoGeometry.keyAt(799.9f,159f,800f,160f))
  for((x,y) in listOf(-1f to 80f,800f to 80f,40f to -1f,40f to 160f))assertNull(PianoGeometry.keyAt(x,y,800f,160f))
  assertNull(PianoGeometry.keyAt(0f,0f,0f,0f))
 }
}
