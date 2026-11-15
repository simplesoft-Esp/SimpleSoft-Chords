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
data class Question(val position:Position?=null,val note:Int?=null,val build:Boolean=false,val options:List<Position> = emptyList()) {
 val notes get()=position?.notes?:listOfNotNull(note)
 val family get()=position?.type?.family?:"notes"
 val key get()=position?.key?:"note:$note"
 val activity get()=if(build)"build" else "ear"
}
data class Score(val checks:Int=0,val errors:Int=0,val solved:Int=0,val first:Int=0,val helped:Int=0)
class TrainingModel(app:Application):AndroidViewModel(app) {
 private val disk=app.getSharedPreferences("chords.v1",0)
 val catalog=Catalog(app.assets.open("catalog.json").bufferedReader().use{it.readText()})
 private val strings=JSONObject(app.assets.open("i18n.json").bufferedReader().use{it.readText()})
 val audio=PianoAudio(app)
 var prefs by mutableStateOf(loadPrefs());private set
 var question by mutableStateOf<Question?>(null);private set
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
 fun name(p:Position,octave:Boolean=false)=note(p.root,octave)+" "+p.type.names.getValue(lang)
 fun function(p:Position,index:Int)=t("function.${p.type.degrees[index]}.${p.type.semitones[index]}")
 fun spelling(p:Position,index:Int)=Pitch.spell(p.root,p.type.semitones[index],p.type.degrees[index],prefs.latin)
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
  audio.stopAll();selected=emptySet();checked=false;solved=false;errors=0;helped=false;hint=0;guide=-1;reveal=false;feedback="";marked=null
  val positions=catalog.positions(prefs.ids)
  val notePool=NoteFilter.notes(prefs.white,prefs.twoOctaves)
  val universe=buildList { if(prefs.mode!="chords")addAll(notePool.map{"n:$it"});if(prefs.mode!="notes") addAll(positions.map{it.key}) }
  val id=bag.next(universe)
  if(id==null){question=null;feedback="noContent";return}
  if(id.startsWith("n:"))question=Question(note=id.substringAfter(':').toInt())
  else {
   val p=positions.first{it.key==id};val build=prefs.activity=="build" || (prefs.activity=="mix" && random.nextBoolean())
   val opts=if(build)emptyList() else Answers.options(p,positions,prefs.byType)
   question=Question(position=p,build=build,options=opts)
   if(!build && opts.isEmpty())feedback="noChoices"
  }
 }
 fun repeat(){question?.let{audio.chord(it.notes)}}
 fun touch(midi:Int) {
  val q=question?:return
  marked=midi
  if(q.build){selected=if(midi in selected)selected-midi else selected+midi;checked=false;feedback=""}
  else if(q.note!=null && !solved){val ok=midi==q.note;checked=true;feedback=if(ok)"correct" else "retry";record(ok)}
 }
 fun check(){val q=question?:return;val p=q.position?:return;if(!q.build)return;val ok=Answers.exact(selected,p);checked=true;feedback=if(ok)"correct" else "retry";record(ok)}
 fun answer(p:Position){if(solved)return;val q=question?:return;val ok=if(prefs.byType)p.type.id==q.position?.type?.id else p.key==q.position?.key;feedback=if(ok)"correct" else "retry";record(ok)}
 fun clear(){selected=emptySet();checked=false;feedback="";marked=null}
 fun help(){helped=true;hint=(hint+1).coerceAtMost(4)}
 fun solution(){helped=true;reveal=true;feedback="solutionShown"}
 fun openGuide(){helped=true;guide=0;playStep()}
 fun closeGuide(){guide=-1}
 fun step(){val p=question?.position?:return;guide=(guide+1).coerceAtMost(p.notes.size);playStep()}
 fun playStep(){val p=question?.position?:return;audio.chord(if(guide in p.notes.indices)listOf(p.notes[guide]) else p.notes)}
 override fun onCleared(){audio.close()}
}
