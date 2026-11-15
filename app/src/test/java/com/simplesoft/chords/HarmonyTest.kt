package com.simplesoft.chords

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import kotlin.random.Random

class HarmonyTest {
 private val catalog=Catalog(File("../shared/catalog.json").readText())
 private fun type(id:String)=catalog.types.first{it.id==id}
 @Test fun octaveDoublingsAndInversionCriteria(){
  val c=Position(type("major"),48)
  assertTrue(Harmony.accepts(setOf(60,64,67,72),c))
  assertTrue(Harmony.accepts(setOf(52,55,60),c))
  assertFalse(Harmony.accepts(setOf(52,55,60),c,requireBass=true))
  assertTrue(Harmony.accepts(setOf(52,55,60),c.copy(inversion=1),requireBass=true))
  assertFalse(Harmony.accepts(setOf(48,52),c))
  assertFalse(Harmony.accepts(setOf(48,52,55,58),c))
  assertFalse(Harmony.accepts(emptySet(),c))
  assertFalse(Harmony.accepts(setOf(60,64,67),c,exactVoicing=true))
 }
 @Test fun recognitionSeparatesRootBassAndDuplicates(){
  val matches=Harmony.recognize(listOf(52,55,60,64),catalog)
  val c=matches.first{it.position.type.id=="major" && it.position.root%12==0}
  assertEquals(52,c.bass);assertEquals(1,c.inversion)
  assertEquals(Harmony.recognize(listOf(52,55,60),catalog),matches)
  assertTrue(Harmony.recognize(emptyList(),catalog).isEmpty())
 }
 @Test fun symmetricAndSixthChordsRemainAmbiguous(){
  assertEquals(4,Harmony.recognize(listOf(48,51,54,57),catalog).count{it.position.type.id=="dim7"})
  val names=Harmony.recognize(listOf(48,52,55,57),catalog).map{it.position.type.id}.toSet()
  assertTrue("six" in names && "min7" in names)
 }
 @Test fun everyOfferedVoicingFitsAndHasTheRequestedBass(){
  catalog.types.forEach{t->for(root in 0..11)for(i in t.semitones.indices){
   Harmony.voicings(t,root,i).forEach{p->
    assertTrue(p.notes.all{it in 48..72})
    assertEquals((root+t.semitones[i])%12,p.notes.first()%12)
    assertEquals(t.semitones.size,p.sound.size)
   }
  }}
  assertEquals(listOf(52,55,60),Harmony.voicings(type("major"),0,1).first().notes)
  assertTrue(Harmony.voicings(type("major"),0,3).isEmpty())
 }
 @Test fun extendedInversionsRaiseEveryToneAboveTheIntendedBass(){
  val ninth=Harmony.voicings(type("add9"),0,3)
  assertEquals(listOf(50,52,55,60),ninth.first().notes)
  assertEquals(setOf(0,2,4,7),ninth.first().sound)
 }
 @Test fun earDistractorsVaryQualityAtTheSameRoot(){
  val pool=catalog.positions(setOf("major","minor","dim","aug"))
  pool.forEach{p->val choices=Harmony.earOptions(p,pool,Random(7))
   if(choices.isEmpty()){assertEquals(1,pool.filter{it.root==p.root}.map{it.type.id}.distinct().size)} else assertTrue(choices.size in 2..4)
   assertTrue(choices.all{it.root==p.root})
   assertEquals(choices.size,choices.map{it.type.id}.distinct().size)
   assertEquals(choices.size,choices.map{it.sound}.distinct().size)
  }
  assertTrue(Harmony.earOptions(pool.first(),listOf(pool.first())).isEmpty())
 }
}
