package com.simplesoft.chords

import java.io.File
import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class LearningConfigTest {
 private val catalog=Catalog(File("../shared/catalog.json").readText())
 @Test fun individualAndFamilySelectionAreIndependent(){
  var ids=ChordSelection.toggle(setOf("major"),"minor")
  assertEquals(setOf("major","minor"),ids)
  ids=ChordSelection.toggle(ids,"major");assertEquals(setOf("minor"),ids)
  val sevenths=catalog.types.filter{it.family=="sevenths"}
  ids=ChordSelection.family(ids,sevenths,true)
  assertTrue("minor" in ids);assertTrue(sevenths.all{it.id in ids})
  assertEquals(setOf("minor"),ChordSelection.family(ids,sevenths,false))
 }
 @Test fun quickSelectionsContainExactlyTheIntendedTypes(){
  assertEquals(37,ChordSelection.all(catalog).size)
  assertEquals(setOf("major","minor","dim","aug"),ChordSelection.basics)
  assertTrue(catalog.types.map{it.id}.containsAll(ChordSelection.basics))
 }
 @Test fun jsonPersistencePreservesEmptyAndIndividualSelectionsAndFilters(){
  for(ids in listOf(emptySet(),setOf("minor","seven-flat9"),ChordSelection.all(catalog))){
   val prefs=Prefs(language="en",latin=false,labels=false,mode="all",activity="complete",white=false,twoOctaves=false,ids=ids,advanced=true)
   assertEquals(prefs,PrefsCodec.read(PrefsCodec.write(prefs)))
  }
 }
 @Test fun legacyPreferencesAndCorruptDataStayReadable(){
  assertEquals(Prefs(),PrefsCodec.read("{}"));assertEquals(Prefs(),PrefsCodec.read("invalid"))
  val legacy=PrefsCodec.read("""{"ids":[],"activity":"build"}""")
  assertTrue(legacy.ids.isEmpty());assertFalse(legacy.advanced);assertEquals("build",legacy.activity)
 }
 @Test fun generatorUsesOnlySelectedChordIdsAndAppliesChangesImmediately(){
  val generator=ExerciseGenerator(catalog,Random(3))
  repeat(60){assertEquals("minor",generator.next(Prefs(activity="build",ids=setOf("minor"))).question!!.position!!.type.id)}
  repeat(60){assertEquals("seven",generator.next(Prefs(activity="build",ids=setOf("seven"))).question!!.position!!.type.id)}
  val none=generator.next(Prefs(ids=emptySet()))
  assertNull(none.question);assertEquals("noContent",none.error)
 }
 @Test fun impossibleEarFiltersExplainTheProblemWithoutLooping(){
  val generator=ExerciseGenerator(catalog,Random(9))
  val result=generator.next(Prefs(activity="ear",ids=setOf("major")))
  assertNull(result.question);assertEquals("noChoices",result.error)
  repeat(30){assertNotNull(generator.next(Prefs(activity="ear",ids=setOf("major","minor"))).question)}
 }
 @Test fun completionReferenceIsNotALockedSelectedNote(){
  val q=ExerciseGenerator(catalog,Random(2)).next(Prefs(activity="complete",ids=setOf("major"))).question!!
  val piano=PianoState();piano.startExercise()
  assertTrue(piano.selected.isEmpty());assertEquals(1,q.given.size)
  val note=q.given.first();piano.toggle(note);piano.clear()
  assertTrue(piano.selected.isEmpty())
  q.notes.filter{it!=note}.forEach(piano::toggle)
  assertTrue(Harmony.accepts(piano.selected+q.given,q.position!!))
 }
 @Test fun noteFiltersDoNotShrinkThePiano(){
  val generator=ExerciseGenerator(catalog,Random(3))
  repeat(30){
   val q=generator.next(Prefs(mode="notes",ids=emptySet(),white=true,twoOctaves=false)).question!!
   assertTrue(q.note in listOf(48,50,52,53,55,57,59))
  }
  assertEquals(25,(PianoGeometry.whites+PianoGeometry.blacks).size)
 }
}
