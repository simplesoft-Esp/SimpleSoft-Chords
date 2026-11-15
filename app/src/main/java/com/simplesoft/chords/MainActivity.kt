package com.simplesoft.chords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity:ComponentActivity(){
 private val model:TrainingModel by viewModels()
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{MaterialTheme(colorScheme=if(isSystemInDarkTheme())darkColorScheme() else lightColorScheme(primary=Color(0xFF285C80))){TrainingApp(model)}}}
 override fun onPause(){model.audio.suspendAudio();super.onPause()}
}
@Composable private fun Choice(label:String,options:List<Pair<String,String>>,value:String,onChange:(String)->Unit){
 var open by remember{mutableStateOf(false)}
 Box { OutlinedButton(onClick={open=true},contentPadding=PaddingValues(horizontal=10.dp,vertical=4.dp)){Text("$label: ${options.firstOrNull{it.first==value}?.second?:value}",fontSize=12.sp)}
 DropdownMenu(expanded=open,onDismissRequest={open=false}){options.forEach{(id,text)->DropdownMenuItem(text={Text(text)},onClick={open=false;onChange(id)})}} }
}
@Composable fun TrainingApp(m:TrainingModel){
 var dialog by remember{mutableStateOf("")}
 val q=m.question
 LaunchedEffect(Unit){m.next()}
 LaunchedEffect(q?.key,q?.kind,m.audio.ready,m.tool){if(m.audio.ready && q!=null && !q.build && q.kind!="identify" && m.tool=="practice")m.repeat()}
 Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding()) {
  // Only the upper half scrolls. The keyboard never moves with menus or explanations.
  Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal=10.dp),verticalArrangement=Arrangement.spacedBy(2.dp)){
   Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()){
    Image(painterResource(R.drawable.simplesoft_logo),"SimpleSoft",Modifier.size(32.dp))
    Text("SimpleSoft Chords",style=MaterialTheme.typography.titleMedium,modifier=Modifier.padding(horizontal=8.dp).weight(1f))
    TextButton(onClick={dialog="stats"}){Text(m.t("stats"))};TextButton(onClick={dialog="settings"}){Text(m.t("settings"))}
   }
   Choice(m.t("workspace"),listOf("practice","recognize","builder").map{it to m.t(it)},m.tool){m.changeTool(it)}
   if(m.tool=="recognize") {
    Text(m.t("playChord"))
    Text(m.played.sorted().joinToString(" · "){m.note(it)})
    Text(m.t("enharmonicNotice"),fontSize=12.sp)
    val matches=m.recognition
    if(matches.isEmpty())Text(m.t("unrecognized"))
    matches.forEach { match ->
     Text("${m.name(match.position)} · ${m.t("bass")}: ${m.note(match.bass)} · ${m.t("inversion")}: ${match.inversion?:"?"}")
    }
    if(matches.size>1)Text(m.t("ambiguous"))
   }
   if(m.tool=="builder")BuilderPanel(m)
   if(m.tool=="practice") {
   Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
    Choice(m.t("mode"),listOf("notes","chords","all").map{it to m.t(it)},m.prefs.mode){m.preferences(m.prefs.copy(mode=it))}
    if(m.prefs.mode!="notes")Choice(m.t("activity"),listOf("ear","build","identify","complete","mix").map{it to m.t(it)},m.prefs.activity){m.preferences(m.prefs.copy(activity=it))}
    OutlinedButton(onClick={dialog="content"},contentPadding=PaddingValues(10.dp)){Text("${m.t("content")}: ${m.prefs.ids.size}",fontSize=12.sp)}
   }
   Choice(m.t("level"),listOf("1" to m.t("level1"),"2" to m.t("level2"),"3" to m.t("level3")),when(m.prefs.ids){setOf("major","minor")->"1";setOf("major","minor","dim","aug")->"2";else->"3"}){level->
    m.preferences(m.prefs.copy(ids=when(level){"1"->setOf("major","minor");"2"->setOf("major","minor","dim","aug");else->setOf("seven","maj7","min7","half-dim","dim7")}))
   }
   Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=m.advanced,onCheckedChange={m.changeAdvanced(it)});Text(m.t("advanced"),fontSize=12.sp)}
   val instruction=when{q==null->m.t("noContent");q.build->"${m.t("construct")} ${m.name(q.position!!)}";q.note!=null->m.t("listenNote");q.kind=="identify"->m.t("identifyPrompt")+q.notes.joinToString{m.note(it)};else->m.t("listenChord")}
   if(q?.build==true && m.advanced)Text("${m.t("requiredBass")}: ${m.note(q.notes.first())} · ${m.t("inversion")}: ${q.position?.inversion}")
   if(q?.kind=="complete")Text(m.t("completePrompt"))
   Text(instruction,style=MaterialTheme.typography.titleMedium)
   if(!m.audio.ready)Text(m.t(if(m.audio.error)"audioError" else "loading"))
   if(m.audio.focusDenied)Text(m.t("audioFocus"))
   Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){
    TextButton(onClick={m.repeat()},enabled=m.audio.ready && q!=null){Text(m.t("repeat"))}
    TextButton(onClick={m.audio.chord(listOf(60))},enabled=m.audio.ready){Text(m.t("reference"))}
    TextButton(onClick={m.next()}){Text(m.t("next"))}
    if(q?.position!=null){TextButton(onClick={m.help()}){Text(m.t("hint"))};TextButton(onClick={m.openGuide()}){Text(m.t("how"))}}
   }
   if(q?.build==true){
    Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){
     Button(onClick={m.check()}){Text(m.t("check"))}
     TextButton(onClick={m.audio.chord(m.selected)},enabled=m.selected.isNotEmpty()){Text(m.t("listenSelection"))}
     TextButton(onClick={m.clear()}){Text(m.t("clear"))}
     TextButton(onClick={m.solution()}){Text(m.t("solution"))}
    }
    Text(m.t("pitchValidation"),fontSize=12.sp)
   }else if(q?.position!=null && q.options.isNotEmpty()){
    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){
     q.options.forEach{p->OutlinedButton(onClick={m.choose(p)},enabled=!m.solved,modifier=Modifier.weight(1f),contentPadding=PaddingValues(5.dp)){Text(if(q.kind=="ear")p.type.names.getValue(m.lang) else m.name(p),fontSize=12.sp)}}
    }
    Text(m.pending?.let{m.name(it)}?:m.t("chooseAnswer"))
    Button(onClick={m.validateChoice()},enabled=m.pending!=null && !m.solved){Text(m.t("check"))}
   }
   if(m.feedback in listOf("noContent","noChoices")){
    TextButton(onClick={m.preferences(m.prefs.copy(ids=setOf("major","minor"),mode="chords",activity="ear"))}){Text(m.t("broaden"))}
    TextButton(onClick={m.preferences(m.prefs.copy(activity="build"))}){Text(m.t("build"))}
   }
   if(m.feedback.isNotEmpty())Text(m.t(m.feedback),color=if(m.solved)Color(0xFF246B45) else MaterialTheme.colorScheme.onSurface)
   if(m.checked && q?.build==true){val target=q.notes.toSet()
    Text("${m.t("matched")}: ${m.selected.filter{it%12 in Harmony.classes(target)}.sorted().joinToString{m.note(it)}} · ${m.t("extra")}: ${m.selected.filter{it%12 !in Harmony.classes(target)}.sorted().joinToString{m.note(it)}}",fontSize=12.sp)
   }
   if(m.reveal)Text("${m.t("missing")}: ${q?.notes.orEmpty().filter{it%12 !in Harmony.classes(m.selected)}.sorted().joinToString{m.note(it)}}")
   if(m.hint>0 && q?.position!=null){val p=q.position
    Text(when(m.hint){1->"${m.t("structure")}: "+p.notes.indices.joinToString(" · "){m.function(p,it)};2->"${m.t("fromRoot")}: "+p.type.semitones.joinToString()+" "+m.t("semitones");else->"${m.t("noteNames")}: "+p.notes.indices.joinToString{m.spelling(p,it)}})
   }
   if(q?.note!=null)Text(m.t("referenceNotice"),fontSize=12.sp)
   if(m.helped)Text(m.t("helpNotice"),fontSize=12.sp)
   if(m.guide>=0 && q?.position!=null){val p=q.position;val i=m.guide
    Card{Column(Modifier.padding(8.dp)){
     if(i in p.notes.indices)Text("${m.t("step")} ${i+1}/${p.notes.size}: ${m.spelling(p,i)} · ${m.function(p,i)}\n${m.t("fromRoot")}: ${p.notes[i]-p.root} ${m.t("semitones")} · ${m.t("fromPrevious")}: ${if(i==0)0 else p.notes[i]-p.notes[i-1]}") else Text(m.t("playTogether"))
     Text(m.t("helpStep"),fontSize=12.sp)
     Row{TextButton(onClick={m.playStep()}){Text(m.t("repeat"))};TextButton(onClick={m.step()},enabled=i<p.notes.size){Text(m.t("next"))};TextButton(onClick={m.closeGuide()}){Text(m.t("close"))}}
    }}
   }
   }
  }
  // Reading observable state here invalidates the Android canvas on each training update.
  val visual=listOf(m.selected,m.checked,m.marked,m.hint,m.reveal,m.guide,m.prefs,m.question,m.played,m.tool,m.builderPosition,m.solved)
  AndroidView(factory={PianoView(it)},update={view->visual.hashCode();view.refresh(m)},modifier=Modifier.weight(1f).fillMaxWidth(),onRelease={it.releasePointers()})
 }
 when(dialog){
  "content"->ContentDialog(m){dialog=""}
  "settings"->SettingsDialog(m){dialog=""}
  "stats"->StatsDialog(m){dialog=""}
 }
}
@Composable private fun ContentDialog(m:TrainingModel,close:()->Unit){
 var ids by remember{mutableStateOf(m.prefs.ids)}
 AlertDialog(onDismissRequest=close,title={Text("${m.t("content")} · ${ids.size} ${m.t("selected")}")},text={Column(Modifier.heightIn(max=280.dp).verticalScroll(rememberScrollState())){
  Row{TextButton(onClick={ids=m.catalog.types.map{it.id}.toSet()}){Text(m.t("selectAll"))};TextButton(onClick={ids=emptySet()}){Text(m.t("selectNone"))}}
  m.catalog.types.groupBy{it.family}.forEach{(family,types)->
   Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=types.all{it.id in ids},onCheckedChange={yes->ids=if(yes)ids+types.map{it.id} else ids-types.map{it.id}.toSet()});Text(m.t(family))}
   types.forEach{type->Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.padding(start=16.dp)){Checkbox(checked=type.id in ids,onCheckedChange={yes->ids=if(yes)ids+type.id else ids-type.id});Text("${type.names.getValue(m.lang)} (${type.semitones.joinToString()})",fontSize=13.sp)}}
  }
 }},confirmButton={TextButton(onClick={m.preferences(m.prefs.copy(ids=ids));close()}){Text(m.t("apply"))}},dismissButton={TextButton(onClick=close){Text(m.t("cancel"))}})
}
@Composable private fun SettingsDialog(m:TrainingModel,close:()->Unit){
 var reset by remember{mutableStateOf(false)}
 AlertDialog(onDismissRequest=close,title={Text(m.t("settings"))},text={Column(Modifier.heightIn(max=260.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(4.dp)){
  Choice(m.t("language"),listOf("system" to m.t("system"),"es" to "Castellano","en" to "English"),m.prefs.language){m.preferences(m.prefs.copy(language=it))}
  Choice(m.t("notation"),listOf("latin" to "Do Re Mi","english" to "C D E"),if(m.prefs.latin)"latin" else "english"){m.preferences(m.prefs.copy(latin=it=="latin"))}
  Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=m.prefs.labels,onCheckedChange={m.preferences(m.prefs.copy(labels=it))});Text(m.t("labels"))}
  Choice(m.t("noteFilter"),listOf("white" to m.t("whiteOnly"),"all" to m.t("chromatic")),if(m.prefs.white)"white" else "all"){m.preferences(m.prefs.copy(white=it=="white"))}
  Choice(m.t("octaves"),listOf("one" to m.t("oneOctave"),"two" to m.t("twoOctaves")),if(m.prefs.twoOctaves)"two" else "one"){m.preferences(m.prefs.copy(twoOctaves=it=="two"))}
  Text(m.t("rangeNotice"),fontSize=12.sp);Text(m.t("about"),fontSize=12.sp)
  TextButton(onClick={reset=true}){Text(m.t("resetPrefs"))}
 }},confirmButton={TextButton(onClick=close){Text(m.t("close"))}})
 if(reset)AlertDialog(onDismissRequest={reset=false},text={Text(m.t("resetPrefsAsk"))},confirmButton={TextButton(onClick={m.preferences(Prefs());reset=false}){Text(m.t("resetPrefs"))}},dismissButton={TextButton(onClick={reset=false}){Text(m.t("cancel"))}})
}
@Composable private fun StatsDialog(m:TrainingModel,close:()->Unit){
 var reset by remember{mutableStateOf(false)}
 AlertDialog(onDismissRequest=close,title={Text(m.t("stats"))},text={Column(Modifier.heightIn(max=260.dp).verticalScroll(rememberScrollState())){
  if(m.stats.isEmpty())Text(m.t("noneYet"))
  m.stats.toSortedMap().forEach{(key,s)->val parts=key.split('/');Text("${m.t(parts[0])} · ${m.t(parts[1])}",style=MaterialTheme.typography.titleSmall);Text("${m.t("resolved")}: ${s.solved} · ${m.t("firstTry")}: ${s.first}\n${m.t("attempts")}: ${s.checks} · ${m.t("errors")}: ${s.errors} · ${m.t("helped")}: ${s.helped}",fontSize=12.sp)}
 }},confirmButton={TextButton(onClick=close){Text(m.t("close"))}},dismissButton={TextButton(onClick={reset=true}){Text(m.t("reset"))}})
 if(reset)AlertDialog(onDismissRequest={reset=false},text={Text(m.t("resetAsk"))},confirmButton={TextButton(onClick={m.resetStats();reset=false}){Text(m.t("reset"))}},dismissButton={TextButton(onClick={reset=false}){Text(m.t("cancel"))}})
}

@Composable private fun BuilderPanel(m:TrainingModel){
 Choice(m.t("root"),(0..11).map{it.toString() to m.note(48+it,false)},m.builderRoot.toString()){m.builder(root=it.toInt(),inversion=0)}
 val executable=m.catalog.types.filter{type->type.semitones.indices.any{Harmony.voicings(type,m.builderRoot,it).isNotEmpty()}}
 Choice(m.t("type"),executable.map{it.id to it.names.getValue(m.lang)},m.builderType){m.builder(type=it,inversion=0)}
 val type=m.catalog.types.first{it.id==m.builderType}
 val inversions=type.semitones.indices.filter{Harmony.voicings(type,m.builderRoot,it).isNotEmpty()}
 Choice(m.t("inversion"),inversions.map{it.toString() to if(it==0)m.t("rootPosition") else it.toString()},m.builderInversion.toString()){m.builder(inversion=it.toInt())}
 val p=m.builderPosition
 if(p==null)Text(m.t("unplayable")) else {
  Text(m.name(p),style=MaterialTheme.typography.titleMedium)
  Text(p.notes.joinToString(" · "){m.note(it)})
  Text("${m.t("structure")}: ${type.semitones.joinToString()} ${m.t("semitones")}")
  Text(m.t("inversionLesson"),fontSize=12.sp)
  Button(onClick={m.audio.chord(p.notes)},enabled=m.audio.ready){Text(m.t("repeat"))}
 }
}
