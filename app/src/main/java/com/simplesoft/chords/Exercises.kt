package com.simplesoft.chords

import kotlin.random.Random

data class Question(val position:Position?=null,val note:Int?=null,val build:Boolean=false,val options:List<Position> = emptyList(),val kind:String=if(build)"build" else "ear") {
 val notes get()=position?.notes?:listOfNotNull(note)
 val given get()=if(kind=="complete")notes.take(1).toSet() else emptySet()
 val family get()=position?.type?.family?:"notes"
 val key get()=position?.key?:"note:$note"
 val activity get()=kind
}
internal data class GeneratedExercise(val question:Question?=null,val error:String="")
internal class ExerciseGenerator(private val catalog:Catalog,private val random:Random=Random.Default) {
 private val bag=Bag<String>(random)
 fun next(prefs:Prefs):GeneratedExercise {
  val raw=catalog.positions(prefs.ids)
  val positions=if(prefs.activity=="ear")raw.filter{Harmony.earOptions(it,raw,random).isNotEmpty()} else raw
  val universe=buildList {
   if(prefs.mode!="chords")addAll(NoteFilter.notes(prefs.white,prefs.twoOctaves).map{"n:$it"})
   if(prefs.mode!="notes")addAll(positions.map{it.key})
  }
  val id=bag.next(universe)?:return GeneratedExercise(error=if(raw.isNotEmpty())"noChoices" else "noContent")
  if(id.startsWith("n:"))return GeneratedExercise(Question(note=id.substringAfter(':').toInt()))
  val p=positions.first{it.key==id}
  val build=prefs.activity=="build" || (prefs.activity=="mix" && random.nextBoolean())
  var kind=if(build)"build" else if(prefs.activity=="complete")"complete" else if(prefs.activity=="identify")"identify" else "ear"
  // Mixed mode remains usable when just one quality is selected: offer construction.
  if(kind=="ear" && prefs.activity=="mix" && Harmony.earOptions(p,positions,random).isEmpty())kind="build"
  val target=if(prefs.advanced && kind in listOf("build","complete"))
   p.type.semitones.indices.flatMap{Harmony.voicings(p.type,p.root%12,it)}.randomOrNull(random)?:p else p
  val keyboard=kind in listOf("build","complete")
  val options=if(keyboard)emptyList() else if(kind=="ear")Harmony.earOptions(target,positions,random) else Answers.options(target,positions,false,random)
  if(!keyboard && options.isEmpty())return GeneratedExercise(error="noChoices")
  return GeneratedExercise(Question(position=target,build=keyboard,options=options,kind=kind))
 }
}
