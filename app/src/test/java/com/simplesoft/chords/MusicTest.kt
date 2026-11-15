package com.simplesoft.chords

import org.junit.Test
import org.junit.Assert.*
import java.io.File
import kotlin.random.Random

class MusicTest {
 private fun file(name:String)=sequenceOf(File("../shared/$name"),File("shared/$name")).first{it.exists()}
 private val catalog=Catalog(file("catalog.json").readText())
 @Test fun allPositionsAreUnalteredRootPosition(){
  assertEquals(37,catalog.types.size);assertEquals(472,catalog.types.sumOf{catalog.positions(it).size})
  catalog.types.forEach { t ->catalog.positions(t).forEach { p ->
   assertEquals(p.root,p.notes.min());assertTrue(p.notes.all{it in 48..72})
   assertEquals(t.semitones,p.notes.map{it-p.root});assertEquals(t.semitones.size,p.notes.size)
  } }
 }
 @Test fun goldenMusicalExamples(){
  fun p(id:String,root:Int)=Position(catalog.types.first{it.id==id},root)
  assertEquals(listOf(50,53,57),p("minor",50).notes)
  assertEquals(listOf(48,52,55,58,62,65,69),p("thirteen",48).notes)
  assertTrue(catalog.positions(setOf("thirteen")).all{it.root<=51})
  assertEquals("F3",Pitch.spell(50,3,3,false))
  assertEquals("B♭♭3",Pitch.spell(48,9,7,false))
  assertEquals("E♯4",Pitch.spell(54,11,7,false))
  assertEquals(440.0,Pitch.frequency(69),.00001)
 }
 @Test fun exactValidationRejectsOctaveDoublingAndOmissions(){
  val p=Position(catalog.types.first{it.id=="minor"},50)
  assertTrue(Answers.exact(setOf(57,50,53),p));assertFalse(Answers.exact(setOf(50,53,57,69),p));assertFalse(Answers.exact(setOf(50,53),p))
 }
 @Test fun choicesHaveNoEquivalentDistractors(){
  val all=catalog.positions(catalog.types.map{it.id}.toSet())
  for(byType in listOf(false,true))for(p in all){
   val opts=Answers.options(p,all,byType,Random(11))
   assertTrue(opts.size in 2..4);assertEquals(1,opts.count{it.key==p.key})
   assertEquals(opts.size,opts.map{it.sound}.distinct().size)
   opts.filter{it.key!=p.key}.forEach{other->
    assertNotEquals(p.sound,other.sound)
    if(byType)assertFalse(all.any{it.type.id==other.type.id && it.sound==p.sound})
   }
  }
 }
 @Test fun restrictiveFiltersAndBagsDoNotHang(){
  assertTrue(catalog.positions(emptySet()).isEmpty())
  assertTrue(Answers.options(catalog.positions(setOf("major")).first(),catalog.positions(setOf("major")),true).isEmpty())
  val b=Bag<Int>(Random(1));assertNull(b.next(emptyList()));repeat(100){assertEquals(1,b.next(listOf(1)))}
  val values=(1..25).toList();val draws=(1..25).map{b.next(values)};assertEquals(values.toSet(),draws.toSet())
  val last=draws.last();assertNotEquals(last,b.next(values))
 }
 @Test fun noteFiltersKeepPianoRangeButRestrictQuestions(){
  assertEquals(listOf(48,50,52,53,55,57,59),NoteFilter.notes(true,false))
  assertEquals((48..59).toList(),NoteFilter.notes(false,false))
  assertEquals(15,NoteFilter.notes(true,true).size)
  assertEquals((48..72).toList(),NoteFilter.notes(false,true))
 }
 @Test fun sharedWebExamplesMatchAndroidLogic(){
  val fixtures=org.json.JSONObject(file("examples.json").readText())
  fun ints(a:org.json.JSONArray)=(0 until a.length()).map{a.getInt(it)}
  val cases=fixtures.getJSONArray("positionCases")
  for(i in 0 until cases.length()){
   val c=cases.getJSONObject(i);val p=Position(catalog.types.first{it.id==c.getString("type")},c.getInt("root"))
   assertEquals(ints(c.getJSONArray("notes")),p.notes)
   if(c.has("spellingsEn"))assertEquals((0 until p.notes.size).map{c.getJSONArray("spellingsEn").getString(it)},p.notes.indices.map{Pitch.spell(p.root,p.type.semitones[it],p.type.degrees[it],false)})
  }
  val validation=fixtures.getJSONArray("validationCases")
  for(i in 0 until validation.length()){
   val c=validation.getJSONObject(i);val p=Position(catalog.types.first{it.id==c.getString("type")},c.getInt("root"))
   assertEquals(c.getBoolean("correct"),Answers.exact(ints(c.getJSONArray("selected")).toSet(),p))
  }
 }
}
