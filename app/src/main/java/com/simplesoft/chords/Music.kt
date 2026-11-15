package com.simplesoft.chords

import org.json.JSONObject
import kotlin.random.Random

data class ChordType(val id: String, val family: String, val names: Map<String,String>, val symbol: String,
 val semitones: List<Int>, val degrees: List<Int>, val aliases: List<String>, val explanationKey: String)
data class Position(val type: ChordType, val root: Int) {
 val notes get() = type.semitones.map { root + it }
 val key get() = "${type.id}:$root"
 val sound get() = notes.map { it % 12 }.toSet()
}
class Catalog(json: String) {
 private val data = JSONObject(json)
 val min = data.getInt("midiMin"); val max = data.getInt("midiMax")
 val types: List<ChordType> = data.getJSONArray("types").let { a -> (0 until a.length()).map { i ->
  val t=a.getJSONObject(i)
  fun ints(k:String)=t.getJSONArray(k).let { b -> (0 until b.length()).map { b.getInt(it) } }
  ChordType(t.getString("id"),t.getString("family"), mapOf("es" to t.getJSONObject("names").getString("es"),"en" to t.getJSONObject("names").getString("en")),t.getString("symbol"),ints("semitones"),ints("degrees"),t.getJSONArray("aliases").let { b -> (0 until b.length()).map { b.getString(it) } },t.getString("explanationKey"))
 } }
 init {
  require(data.getInt("schemaVersion")==1 && min==48 && max==72)
  require(types.map{it.id}.distinct().size==types.size)
  types.forEach { require(it.semitones.first()==0 && it.semitones==it.semitones.distinct().sorted() && it.semitones.all{n->n>=0} && it.degrees.size==it.semitones.size) }
 }
 fun positions(type:ChordType) = if (max-type.semitones.last()<min) emptyList() else (min..max-type.semitones.last()).map { Position(type,it) }
 fun positions(ids:Set<String>) = types.filter { it.id in ids }.flatMap(::positions)
}
object Pitch {
 val naturals=listOf(0,2,4,5,7,9,11)
 private val canonicalLetters=listOf(0,0,1,2,2,3,3,4,5,5,6,6)
 private val letters=listOf("C","D","E","F","G","A","B")
 private val solfege=listOf("Do","Re","Mi","Fa","Sol","La","Si")
 fun frequency(midi:Int)=440.0*Math.pow(2.0,(midi-69)/12.0)
 fun name(midi:Int,latin:Boolean,octave:Boolean=true):String {
  val pc=Math.floorMod(midi,12); val l=canonicalLetters[pc]
  val diff=pc-naturals[l]
  return (if(latin) solfege else letters)[l]+accidental(diff)+(if(octave) "${midi/12-1}" else "")
 }
 private fun accidental(n:Int)=if(n>=0) "♯".repeat(n) else "♭".repeat(-n)
 fun spell(root:Int,semitones:Int,degree:Int,latin:Boolean):String {
  val rootLetter=canonicalLetters[root%12]
  val rootBase=(root/12)*7+rootLetter
  val targetBase=rootBase+degree-1
  val letter=targetBase%7
  val naturalMidi=(targetBase/7)*12+naturals[letter]
  val midi=root+semitones
  return (if(latin) solfege else letters)[letter]+accidental(midi-naturalMidi)+"${targetBase/7-1}"
 }
}
/** Shuffle bag, re-created only when the actual candidate universe changes. */
class Bag<T>(private val random:Random=Random.Default) {
 private var source:List<T> = emptyList(); private val remaining=mutableListOf<T>();private var last:T?=null
 fun next(items:List<T>):T? {
  if(items.isEmpty()) return null
  if(source!=items) { source=items.toList();remaining.clear() }
  if(remaining.isEmpty()) { remaining.addAll(items.shuffled(random)); if(remaining.size>1 && remaining.last()==last) java.util.Collections.swap(remaining,0,remaining.lastIndex) }
  return remaining.removeAt(remaining.lastIndex).also { last=it }
 }
}
object Answers {
 fun exact(selected:Set<Int>,position:Position)=selected==position.notes.toSet()
 fun options(target:Position,pool:List<Position>,byType:Boolean,random:Random=Random.Default):List<Position> {
  // Conservative: exclude pitch-class equivalent sets, even if bass/register differ.
  val equivalentTypes=pool.filter { it.sound==target.sound }.map { it.type.id }.toSet()
  val alternatives=pool.filter { it.sound!=target.sound && (!byType || it.type.id !in equivalentTypes) }
   .shuffled(random).distinctBy { if(byType) it.type.id else "${it.root%12}:${it.type.id}" }
   .distinctBy { it.sound }.take(3)
  return if(alternatives.isEmpty()) emptyList() else (alternatives+target).shuffled(random)
 }
}

object NoteFilter {
 fun notes(white:Boolean,twoOctaves:Boolean)=(48..if(twoOctaves)72 else 59).filter{!white || it%12 in Pitch.naturals}
}
