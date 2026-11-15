package com.simplesoft.chords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{MaterialTheme(colorScheme=lightColorScheme(primary=Color(0xFF285C80))){TrainingApp(model)}}}
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
 LaunchedEffect(q?.key,q?.build,m.audio.ready){if(m.audio.ready && q!=null && !q.build)m.repeat()}
 Column(Modifier.fillMaxSize().background(Color(0xFFF1F3F4)).safeDrawingPadding()) {
  // Only the upper half scrolls. The keyboard never moves with menus or explanations.
  Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal=10.dp),verticalArrangement=Arrangement.spacedBy(2.dp)){
   Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth()){
    Image(painterResource(R.drawable.simplesoft_logo),"SimpleSoft",Modifier.size(32.dp))
    Text("SimpleSoft Chords",style=MaterialTheme.typography.titleMedium,modifier=Modifier.padding(horizontal=8.dp).weight(1f))
    TextButton(onClick={dialog="stats"}){Text(m.t("stats"))};TextButton(onClick={dialog="settings"}){Text(m.t("settings"))}
   }
   Row(horizontalArrangement=Arrangement.spacedBy(6.dp)){
    Choice(m.t("mode"),listOf("notes","chords","all").map{it to m.t(it)},m.prefs.mode){m.preferences(m.prefs.copy(mode=it))}
    if(m.prefs.mode!="notes")Choice(m.t("activity"),listOf("ear","build","mix").map{it to m.t(it)},m.prefs.activity){m.preferences(m.prefs.copy(activity=it))}
    OutlinedButton(onClick={dialog="content"},contentPadding=PaddingValues(10.dp)){Text("${m.t("content")}: ${m.prefs.ids.size}",fontSize=12.sp)}
   }
   val instruction=when{q==null->m.t("noContent");q.build->"${m.t("construct")} ${m.name(q.position!!)} · ${m.t("rootOctave")}: ${m.note(q.position.root)}";q.note!=null->m.t("listenNote");else->m.t("listenChord")}
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
    Text(m.t("exact"),fontSize=12.sp)
   }else if(q?.position!=null && q.options.isNotEmpty()){
    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){
     q.options.forEach{p->OutlinedButton(onClick={m.answer(p)},enabled=!m.solved,modifier=Modifier.weight(1f),contentPadding=PaddingValues(5.dp)){Text(if(m.prefs.byType)p.type.names.getValue(m.lang) else m.name(p),fontSize=12.sp)}}
    }
   }
   if(m.feedback.isNotEmpty())Text(m.t(m.feedback),color=if(m.solved)Color(0xFF246B45) else MaterialTheme.colorScheme.onSurface)
   if(m.checked && q?.build==true){val target=q.notes.toSet()
    Text("${m.t("matched")}: ${(m.selected intersect target).sorted().joinToString{m.note(it)}} · ${m.t("extra")}: ${(m.selected-target).sorted().joinToString{m.note(it)}}",fontSize=12.sp)
   }
   if(m.reveal)Text("${m.t("missing")}: ${(q?.notes.orEmpty().toSet()-m.selected).sorted().joinToString{m.note(it)}}")
   if(m.hint>0 && q?.position!=null){val p=q.position
    Text(when(m.hint){1->"${m.t("structure")}: "+p.notes.indices.joinToString(" · "){m.function(p,it)};2->"${m.t("fromRoot")}: "+p.type.semitones.joinToString()+" "+m.t("semitones");else->"${m.t("noteNames")}: "+p.notes.indices.joinToString{m.spelling(p,it)}})
   }
   if(q?.note!=null)Text(m.t("referenceNotice"),fontSize=12.sp)
   if(m.helped)Text(m.t("helpNotice"),fontSize=12.sp)
   if(m.guide>=0 && q?.position!=null){val p=q.position;val i=m.guide
    Card{Column(Modifier.padding(8.dp)){
     if(i in p.notes.indices)Text("${m.t("step")} ${i+1}/${p.notes.size}: ${m.spelling(p,i)} · ${m.function(p,i)}\n${m.t("fromRoot")}: ${p.type.semitones[i]} ${m.t("semitones")} · ${m.t("fromPrevious")}: ${if(i==0)0 else p.type.semitones[i]-p.type.semitones[i-1]}") else Text(m.t("playTogether"))
     Text(m.t("helpStep"),fontSize=12.sp)
     Row{TextButton(onClick={m.playStep()}){Text(m.t("repeat"))};TextButton(onClick={m.step()},enabled=i<p.notes.size){Text(m.t("next"))};TextButton(onClick={m.closeGuide()}){Text(m.t("close"))}}
    }}
   }
  }
  // Reading observable state here invalidates the Android canvas on each training update.
  val visual=listOf(m.selected,m.checked,m.marked,m.hint,m.reveal,m.guide,m.prefs,m.question)
  AndroidView(factory={PianoView(it)},update={view->visual.hashCode();view.model=m;view.invalidate()},modifier=Modifier.weight(1f).fillMaxWidth(),onRelease={it.releasePointers()})
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
  Choice(m.t("variant"),listOf("type" to m.t("type"),"full" to m.t("fullName")),if(m.prefs.byType)"type" else "full"){m.preferences(m.prefs.copy(byType=it=="type"))}
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
