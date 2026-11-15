package com.simplesoft.chords

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject
import java.util.Locale

data class Score(val checks:Int=0,val errors:Int=0,val solved:Int=0,val first:Int=0,val helped:Int=0)
class TrainingModel(app:Application):AndroidViewModel(app) {
 private val disk=app.getSharedPreferences("chords.v1",0)
 val catalog=Catalog(app.assets.open("catalog.json").bufferedReader().use{it.readText()})
 private val strings=JSONObject(app.assets.open("i18n.json").bufferedReader().use{it.readText()})
 val audio=PianoAudio(app)
 var prefs by mutableStateOf(loadPrefs());private set
 var question by mutableStateOf<Question?>(null);private set
 private val piano=PianoState()
 private val generator=ExerciseGenerator(catalog)
 val selected get()=piano.selected
 val pressed get()=piano.pressed
 val keyboardGeneration get()=piano.generation
 var started by mutableStateOf(false);private set
 var exerciseNumber by mutableStateOf(0);private set
 var sessionSolved by mutableStateOf(0);private set
 var tool by mutableStateOf("recognize");private set
 var builderRoot by mutableStateOf(0);private set
 var builderType by mutableStateOf("major");private set
 var builderInversion by mutableStateOf(0);private set
 val advanced get()=prefs.advanced
 var pending by mutableStateOf<Position?>(null);private set
 val builderPosition get()=catalog.types.firstOrNull{it.id==builderType}?.let{Harmony.voicings(it,builderRoot,builderInversion).firstOrNull()}
 val recognition get()=Harmony.recognize(selected,catalog)
 fun changeTool(value:String){clear();tool=value}
 fun builder(root:Int=builderRoot,type:String=builderType,inversion:Int=builderInversion){
  clear();builderRoot=root;builderType=type
  val t=catalog.types.first{it.id==type}
  val valid=t.semitones.indices.filter{Harmony.voicings(t,root,it).isNotEmpty()}
  builderInversion=if(inversion in valid)inversion else valid.firstOrNull()?:0
 }
 fun press(pointer:Int,note:Int,token:Int):Boolean {
  val first=piano.down(pointer,note,token)
  if(first)selectionChanged(note)
  return first
 }
 fun release(pointer:Int,token:Int)=piano.up(pointer,token)
 fun touch(note:Int){piano.toggle(note);selectionChanged(note)}
 private fun selectionChanged(note:Int){marked=note;checked=false;feedback=""}
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
 private fun loadPrefs()=PrefsCodec.read(disk.getString("prefs","{}")?:"{}")
 fun preferences(p:Prefs) {
  prefs=p
  disk.edit().putString("prefs",PrefsCodec.write(p)).apply();next()
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
  if(correct){solved=true;sessionSolved++} else errors++
 }
 fun resetStats(){ stats=emptyMap();disk.edit().remove("stats").apply() }
 fun next() {
  audio.stopAll();piano.startExercise();started=true
  checked=false;solved=false;errors=0;helped=false;hint=0;guide=-1;reveal=false;feedback="";marked=null;pending=null
  val generated=generator.next(prefs)
  question=generated.question;feedback=generated.error
  if(question!=null)exerciseNumber++
 }
 fun repeat(){question?.let{audio.chord(it.notes)}}
 fun check(){
  val q=question?:return
  if(q.position!=null && q.build){
   val ok=Harmony.accepts(selected+q.given,q.position,requireBass=advanced)
   checked=true;feedback=if(ok)"correct" else "retry";record(ok)
  } else if(q.note!=null){
   val ok=selected==setOf(q.note)
   checked=true;feedback=if(ok)"correct" else "retry";record(ok)
  } else validateChoice()
 }
 fun choose(p:Position){if(!solved){pending=p;feedback=""}}
 fun validateChoice(){if(solved)return;val p=pending?:return;val q=question?:return
  val ok=if(q.kind=="ear")p.type.id==q.position?.type?.id else p.key==q.position?.key
  checked=true;feedback=if(ok)"correct" else "retry";record(ok)
 }
 fun clear(){
  audio.stopAll();piano.clear();checked=false;feedback="";marked=null;pending=null
  hint=0;reveal=false;guide=-1
 }
 fun help(){helped=true;hint=(hint+1).coerceAtMost(4)}
 fun solution(){helped=true;reveal=true;feedback="solutionShown"}
 fun openGuide(){helped=true;guide=0;playStep()}
 fun closeGuide(){guide=-1}
 fun step(){val p=question?.position?:return;guide=(guide+1).coerceAtMost(p.notes.size);playStep()}
 fun playStep(){val p=question?.position?:return;audio.chord(if(guide in p.notes.indices)listOf(p.notes[guide]) else p.notes)}
 override fun onCleared(){audio.close()}
}
