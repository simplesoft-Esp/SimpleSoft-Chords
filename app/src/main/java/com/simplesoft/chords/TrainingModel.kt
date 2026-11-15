package com.simplesoft.chords

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject
import java.util.Locale
import kotlin.random.Random

data class Prefs(val language:String="system",val latin:Boolean=true,val labels:Boolean=true,val mode:String="chords",val activity:String="mix",val byType:Boolean=true,val white:Boolean=true,val twoOctaves:Boolean=true,val ids:Set<String> = setOf("major","minor"))
data class Question(val position:Position?=null,val note:Int?=null,val build:Boolean=false,val options:List<Position> = emptyList(), val kind:String=if(build)"build" else "ear") {
 val notes get()=position?.notes?:listOfNotNull(note)
 val family get()=position?.type?.family?:"notes"
 val key get()=position?.key?:"note:$note"
 val activity get()=kind
}
data class Score(val checks:Int=0,val errors:Int=0,val solved:Int=0,val first:Int=0,val helped:Int=0)
class TrainingModel(app:Application):AndroidViewModel(app) {
 private val disk=app.getSharedPreferences("chords.v1",0)
 val catalog=Catalog(app.assets.open("catalog.json").bufferedReader().use{it.readText()})
 private val strings=JSONObject(app.assets.open("i18n.json").bufferedReader().use{it.readText()})
 val audio=PianoAudio(app)
 var prefs by mutableStateOf(loadPrefs());private set
 var question by mutableStateOf<Question?>(null);private set
 private val recognizer=RecognitionState(catalog)
 val played get()=recognizer.played
 var tool by mutableStateOf("recognize");private set
 var builderRoot by mutableStateOf(0);private set
 var builderType by mutableStateOf("major");private set
 var builderInversion by mutableStateOf(0);private set
 var advanced by mutableStateOf(false);private set
 var pending by mutableStateOf<Position?>(null);private set
 val builderPosition get()=catalog.types.firstOrNull{it.id==builderType}?.let{Harmony.voicings(it,builderRoot,builderInversion).firstOrNull()}
 val recognition get()=recognizer.matches
 fun changeTool(value:String){audio.stopAll();recognizer.reset();tool=value}
 fun builder(root:Int=builderRoot,type:String=builderType,inversion:Int=builderInversion){
  builderRoot=root;builderType=type
  val t=catalog.types.first{it.id==type}
  val valid=t.semitones.indices.filter{Harmony.voicings(t,root,it).isNotEmpty()}
  builderInversion=if(inversion in valid)inversion else valid.firstOrNull()?:0
 }
 fun changeAdvanced(value:Boolean){advanced=value;next()}
 fun press(n:Int)=recognizer.press(n)
 fun release(n:Int)=recognizer.release(n)
 var selected by mutableStateOf(setOf<Int>());private set
 var checked by mutableStateOf(false);private set
 var solved by mutableStateOf(false);private set
 var errors by mutableStateOf(0);private set
 var helped by mutableStateOf(false);private set
 var hint by mutableStateOf(0);private set
 var guide by mutableStateOf(-1);private set
 var reveal by mutableStateOf(false);private set
 var feedback by mutableStateOf("");private set
 var marked by mutableStateOf<Int?>(null);private set
 var stats by mutableStateOf(loadStats());private set
 private val bag=Bag<String>();private val random=Random.Default
 val lang get()=if(prefs.language=="system") if(Locale.getDefault().language=="es")"es" else "en" else prefs.language
 fun t(key:String)=strings.getJSONObject(lang).optString(key,key)
 fun note(midi:Int,octave:Boolean=true)=Pitch.name(midi,prefs.latin,octave)
 fun name(p:Position,octave:Boolean=false)=p.displayName(lang,prefs.latin,octave)
 private fun toneIndex(p:Position,index:Int)=p.type.semitones.indexOfFirst{(p.root+it)%12==p.notes[index]%12}
 fun function(p:Position,index:Int):String {val i=toneIndex(p,index);return t("function.${p.type.degrees[i]}.${p.type.semitones[i]}")}
 fun spelling(p:Position,index:Int):String {
  val i=toneIndex(p,index);val semitone=p.notes[index]-p.root
  val degree=p.type.degrees[i]+7*((semitone-p.type.semitones[i])/12)
  return Pitch.spell(p.root,semitone,degree,prefs.latin)
 }
 private fun loadPrefs():Prefs = runCatching {
  val j=JSONObject(disk.getString("prefs","{}")!!)
  Prefs(j.optString("language","system"),j.optBoolean("latin",true),j.optBoolean("labels",true),j.optString("mode","chords"),j.optString("activity","mix"),j.optBoolean("byType",true),j.optBoolean("white",true),j.optBoolean("twoOctaves",true),if(j.has("ids"))j.getJSONArray("ids").let{a->(0 until a.length()).map{a.getString(it)}.toSet()} else setOf("major","minor"))
 }.getOrDefault(Prefs())
 fun preferences(p:Prefs) {
  prefs=p
  val j=JSONObject().put("language",p.language).put("latin",p.latin).put("labels",p.labels).put("mode",p.mode).put("activity",p.activity).put("byType",p.byType).put("white",p.white).put("twoOctaves",p.twoOctaves).put("ids",org.json.JSONArray(p.ids.toList()))
  disk.edit().putString("prefs",j.toString()).apply();next()
 }
 private fun loadStats():Map<String,Score> = runCatching {
  val j=JSONObject(disk.getString("stats","{}")!!)
  j.keys().asSequence().associateWith { k->val v=j.getJSONObject(k);Score(v.optInt("checks"),v.optInt("errors"),v.optInt("solved"),v.optInt("first"),v.optInt("helped")) }
 }.getOrDefault(emptyMap())
 private fun record(correct:Boolean) {
  if(solved)return
  val q=question?:return;val key="${q.activity}/${q.family}"
  val old=stats[key]?:Score();stats=stats+(key to old.copy(checks=old.checks+1,errors=old.errors+if(correct)0 else 1,solved=old.solved+if(correct)1 else 0,first=old.first+if(correct && errors==0 && !helped)1 else 0,helped=old.helped+if(correct && helped)1 else 0))
  val j=JSONObject();stats.forEach{(k,v)->j.put(k,JSONObject().put("checks",v.checks).put("errors",v.errors).put("solved",v.solved).put("first",v.first).put("helped",v.helped))}
  disk.edit().putString("stats",j.toString()).apply()
  if(correct) solved=true else errors++
 }
 fun resetStats(){ stats=emptyMap();disk.edit().remove("stats").apply() }
 fun next() {
  audio.stopAll();recognizer.reset();selected=emptySet();checked=false;solved=false;errors=0;helped=false;hint=0;guide=-1;reveal=false;feedback="";marked=null;pending=null
  val rawPositions=catalog.positions(prefs.ids)
  val positions=if(prefs.activity=="ear")rawPositions.filter{Harmony.earOptions(it,rawPositions).isNotEmpty()} else rawPositions
  val notePool=NoteFilter.notes(prefs.white,prefs.twoOctaves)
  val universe=buildList { if(prefs.mode!="chords")addAll(notePool.map{"n:$it"});if(prefs.mode!="notes") addAll(positions.map{it.key}) }
  val id=bag.next(universe)
  if(id==null){question=null;feedback=if(rawPositions.isNotEmpty() && prefs.mode!="notes")"noChoices" else "noContent";return}
  if(id.startsWith("n:"))question=Question(note=id.substringAfter(':').toInt())
  else {
   val p=positions.first{it.key==id};val build=prefs.activity=="build" || (prefs.activity=="mix" && random.nextBoolean())
   val kind=if(build)"build" else if(prefs.activity=="complete")"complete" else if(prefs.activity=="identify")"identify" else "ear"
   val target=if(advanced && kind in listOf("build","complete")) {
    val choices=p.type.semitones.indices.flatMap{Harmony.voicings(p.type,p.root%12,it)}
    choices.randomOrNull()?:p
   } else p
   val keyboard=kind in listOf("build","complete")
   val opts=if(keyboard)emptyList() else if(kind=="ear")Harmony.earOptions(target,positions) else Answers.options(target,positions,false)
   question=Question(position=target,build=keyboard,options=opts,kind=kind)
   if(kind=="complete")selected=setOf(target.notes.first())
   if(!keyboard && opts.isEmpty())feedback="noChoices"
  }
 }
 fun repeat(){question?.let{audio.chord(it.notes)}}
 fun touch(midi:Int) {
  val q=question?:return
  marked=midi
  if(q.kind=="complete" && midi==q.notes.first())return
  if(q.build && !solved){selected=if(midi in selected)selected-midi else selected+midi;checked=false;feedback=""}
  else if(q.note!=null && !solved){val ok=midi==q.note;checked=true;feedback=if(ok)"correct" else "retry";record(ok)}
 }
 fun check(){val q=question?:return;val p=q.position?:return;if(!q.build)return;if(solved)return;val ok=Harmony.accepts(selected,p,requireBass=advanced);checked=true;feedback=if(ok)"correct" else "retry";record(ok)}
 fun choose(p:Position){if(!solved){pending=p;feedback=""}}
 fun validateChoice(){if(solved)return;val p=pending?:return;val q=question?:return
  val ok=if(q.kind=="ear")p.type.id==q.position?.type?.id else p.key==q.position?.key
  checked=true;feedback=if(ok)"correct" else "retry";record(ok)
 }
 fun clear(){if(solved)return;selected=if(question?.kind=="complete")setOf(question!!.notes.first()) else emptySet();checked=false;feedback="";marked=null}
 fun help(){helped=true;hint=(hint+1).coerceAtMost(4)}
 fun solution(){helped=true;reveal=true;feedback="solutionShown"}
 fun openGuide(){helped=true;guide=0;playStep()}
 fun closeGuide(){guide=-1}
 fun step(){val p=question?.position?:return;guide=(guide+1).coerceAtMost(p.notes.size);playStep()}
 fun playStep(){val p=question?.position?:return;audio.chord(if(guide in p.notes.indices)listOf(p.notes[guide]) else p.notes)}
 override fun onCleared(){audio.close()}
}
