package com.simplesoft.chords

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.selection.toggleable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class MainActivity:ComponentActivity(){
 private val model:TrainingModel by viewModels()
 override fun onCreate(savedInstanceState:Bundle?){
  super.onCreate(savedInstanceState)
  enableEdgeToEdge(statusBarStyle=SystemBarStyle.light(android.graphics.Color.WHITE,android.graphics.Color.WHITE),navigationBarStyle=SystemBarStyle.light(android.graphics.Color.WHITE,android.graphics.Color.WHITE))
  setContent{SimpleSoftTheme{TrainingApp(model)}}
 }
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
 LaunchedEffect(Unit){if(!m.started)m.next()}
 LaunchedEffect(m.exerciseNumber,m.audio.ready,m.tool){if(m.audio.ready && q!=null && !q.build && q.kind!="identify" && m.tool=="practice")m.repeat()}
 Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).safeDrawingPadding()) {
  AppHeader(m,onDialog={dialog=it})
  BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
   val landscape=maxWidth>maxHeight
   val fontScale=androidx.compose.ui.platform.LocalDensity.current.fontScale
   // Reserve real control rows before allocating piano height, including feedback and
   // advanced/completion instructions. Narrow screens wrap the action row at 48 dp.
   val reserved=(136 + (if(q?.options?.isNotEmpty()==true)64 else 0) +
    (if(q?.given?.isNotEmpty()==true)24 else 0) + (if(q?.build==true && m.advanced)24 else 0))*fontScale +
    (if(maxWidth.value<480*fontScale)48 else 0)
   val keyboardHeight=if(m.tool=="practice")
    (maxHeight.value-reserved).coerceIn(minOf(80f,maxHeight.value*.25f),maxHeight.value*.55f).dp
    else (maxHeight.value*if(landscape).52f else .35f).dp
   Column(Modifier.fillMaxSize()) {
    if(m.tool=="practice") {
     PracticePanel(m,onSettings={dialog="settings"},onLesson={dialog="lesson"},modifier=Modifier.weight(1f).fillMaxWidth())
    } else {
     Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal=8.dp,vertical=2.dp),verticalArrangement=Arrangement.spacedBy(4.dp)){
      if(m.tool=="recognize")RecognitionPanel(m) else BuilderPanel(m)
      TextButton(onClick={m.clear()},modifier=Modifier.testTag("clear")){Text(m.t("clear"))}
     }
    }
    val visual=listOf(m.selected,m.pressed,m.keyboardGeneration,m.checked,m.marked,m.hint,m.reveal,m.guide,m.prefs,m.question,m.tool,m.builderPosition,m.feedback)
    AndroidView(factory={PianoView(it)},update={view->visual.hashCode();view.refresh(m)},modifier=Modifier.height(keyboardHeight).fillMaxWidth().testTag("piano"),onRelease={it.releasePointers()})
   }
  }
 }
 when(dialog){
  "settings"->SettingsDialog(m){dialog=""}
  "stats"->StatsDialog(m){dialog=""}
  "info"->RecognitionInfo(m){dialog=""}
  "lesson"->LessonDialog(m){m.closeGuide();dialog=""}
 }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun PracticePanel(m:TrainingModel,onSettings:()->Unit,onLesson:()->Unit,modifier:Modifier=Modifier){
 val q=m.question
 Column(modifier.padding(horizontal=8.dp,vertical=2.dp),verticalArrangement=Arrangement.SpaceBetween){
  if(q==null){
   Text(m.t("noExercises"),style=MaterialTheme.typography.titleMedium)
   Text(m.t(m.feedback.ifEmpty{"noContent"}))
   Button(onClick=onSettings,modifier=Modifier.testTag("editFilters")){Text(m.t("editFilters"))}
  } else {
   Column(verticalArrangement=Arrangement.spacedBy(2.dp)){
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
     Text(m.t(if(q.note!=null)"notes" else q.kind),style=MaterialTheme.typography.titleSmall,modifier=Modifier.weight(1f))
     Text("${m.t("exercise")} ${m.exerciseNumber} · ${m.t("resolved")}: ${m.sessionSolved}",style=MaterialTheme.typography.labelLarge,modifier=Modifier.testTag("progress"))
    }
    val instruction=when{
     q.build->"${m.t(if(q.kind=="complete")"complete" else "construct")} ${m.name(q.position!!)}"
     q.note!=null->m.t("listenNote")
     q.kind=="identify"->q.notes.joinToString(" · "){m.note(it)}
     else->m.t("listenChord")
    }
    Text(instruction,style=MaterialTheme.typography.titleMedium,modifier=Modifier.testTag("instruction"))
    if(q.given.isNotEmpty())Text("${m.t("givenNotes")}: ${q.given.joinToString{m.note(it)}}",style=MaterialTheme.typography.bodyMedium)
    if(q.build && m.advanced)Text("${m.t("requiredBass")}: ${m.note(q.notes.first())} · ${m.t("inversion")}: ${q.position?.inversion}",style=MaterialTheme.typography.bodyMedium)
    if(q.options.isNotEmpty()){
     Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(4.dp)){
      q.options.forEach{p->
       val chosen=m.pending?.key==p.key
       OutlinedButton(onClick={m.choose(p)},enabled=!m.solved,modifier=Modifier.weight(1f).heightIn(min=48.dp).testTag("answer-${p.key}"),contentPadding=PaddingValues(horizontal=4.dp,vertical=4.dp),colors=ButtonDefaults.outlinedButtonColors(containerColor=if(chosen)MaterialTheme.colorScheme.primaryContainer else Color.Transparent)){
        Text(if(q.kind=="ear")p.type.names.getValue(m.lang) else m.name(p),style=MaterialTheme.typography.labelLarge)
       }
      }
     }
    }
   }
   Column {
    if(m.feedback.isNotEmpty())Text(m.t(m.feedback),color=if(m.feedback=="correct")Color(0xFF246B45) else MaterialTheme.colorScheme.onSurface,style=MaterialTheme.typography.bodyMedium,modifier=Modifier.testTag("feedback"))
    if(m.audio.error || m.audio.focusDenied)Text(m.t(if(m.audio.error)"audioError" else "audioFocus"),style=MaterialTheme.typography.bodyMedium)
    FlowRow(horizontalArrangement=Arrangement.spacedBy(2.dp)){
     TextButton(onClick={m.repeat()},enabled=m.audio.ready,modifier=Modifier.testTag("repeat")){Text(m.t("repeat"))}
     if(q.position!=null)TextButton(onClick={m.help();onLesson()},modifier=Modifier.testTag("hint")){Text(m.t("hint"))}
     Button(onClick={m.check()},enabled=q.options.isEmpty() || m.pending!=null,modifier=Modifier.testTag("validate")){Text(m.t("check"))}
     TextButton(onClick={m.clear()},modifier=Modifier.testTag("clear")){Text(m.t("clear"))}
     TextButton(onClick={m.next()},modifier=Modifier.testTag("nextExercise")){Text(m.t("next"))}
     PracticeMore(m,onLesson)
    }
   }
  }
 }
}

@Composable private fun PracticeMore(m:TrainingModel,onLesson:()->Unit){
 var open by remember{mutableStateOf(false)}
 Box {
  TextButton(onClick={open=true}){Text(m.t("more"))}
  DropdownMenu(expanded=open,onDismissRequest={open=false}){
   DropdownMenuItem(text={Text(m.t("listenSelection"))},enabled=m.selected.isNotEmpty() && m.audio.ready,onClick={open=false;m.audio.chord(m.selected)})
   DropdownMenuItem(text={Text(m.t("reference"))},enabled=m.audio.ready,onClick={open=false;m.audio.chord(listOf(60))})
   if(m.question?.position!=null){
    DropdownMenuItem(text={Text(m.t("how"))},onClick={open=false;m.openGuide();onLesson()})
    DropdownMenuItem(text={Text(m.t("solution"))},onClick={open=false;m.solution();onLesson()})
   }
  }
 }
}

@Composable private fun LessonDialog(m:TrainingModel,close:()->Unit){
 val p=m.question?.position?:return
 AlertDialog(onDismissRequest=close,title={Text(m.t("info"))},text={
  Column(Modifier.heightIn(max=280.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(8.dp)){
   if(m.reveal)Text(p.notes.indices.joinToString(" · "){m.spelling(p,it)})
   if(m.hint>0)Text(when(m.hint){1->"${m.t("structure")}: "+p.notes.indices.joinToString(" · "){m.function(p,it)};2->"${m.t("fromRoot")}: "+p.type.semitones.joinToString()+" "+m.t("semitones");else->"${m.t("noteNames")}: "+p.notes.indices.joinToString{m.spelling(p,it)}})
   Text(m.t("pitchValidation"));Text(m.t("inversionLesson"))
   if(m.guide>=0){
    val i=m.guide
    if(i in p.notes.indices)Text("${m.t("step")} ${i+1}/${p.notes.size}: ${m.spelling(p,i)} · ${m.function(p,i)}\n${m.t("fromRoot")}: ${p.notes[i]-p.root} ${m.t("semitones")} · ${m.t("fromPrevious")}: ${if(i==0)0 else p.notes[i]-p.notes[i-1]}") else Text(m.t("playTogether"))
    Row{TextButton(onClick={m.playStep()}){Text(m.t("repeat"))};TextButton(onClick={m.step()},enabled=i<p.notes.size){Text(m.t("next"))}}
   }
  }
 },confirmButton={TextButton(onClick=close){Text(m.t("close"))}},dismissButton={TextButton(onClick={m.help()}){Text(m.t("hint"))}})
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun SettingsDialog(m:TrainingModel,close:()->Unit){
 var draft by remember{mutableStateOf(m.prefs)}
 var section by remember{mutableStateOf("learning")}
 androidx.compose.ui.window.Dialog(onDismissRequest=close,properties=androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth=false)){
  Surface(shape=MaterialTheme.shapes.large,modifier=Modifier.fillMaxWidth(.96f).widthIn(max=840.dp).fillMaxHeight(.95f)){
   Column(Modifier.padding(12.dp)){
    Text(m.t("settings"),style=MaterialTheme.typography.titleLarge)
    Row{TextButton(onClick={section="learning"}){Text(m.t("learningSettings"))};TextButton(onClick={section="display"}){Text(m.t("displaySettings"))}}
    Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(4.dp)){
     if(section=="learning"){
      FlowRow(horizontalArrangement=Arrangement.spacedBy(4.dp)){
       Choice(m.t("mode"),listOf("notes","chords","all").map{it to m.t(it)},draft.mode){draft=draft.copy(mode=it)}
       Choice(m.t("activity"),listOf("ear","build","identify","complete","mix").map{it to m.t(it)},draft.activity){draft=draft.copy(activity=it)}
      }
      Text(m.t("chordsIncluded"),style=MaterialTheme.typography.titleMedium)
      FlowRow {
       TextButton(onClick={draft=draft.copy(ids=ChordSelection.all(m.catalog))},modifier=Modifier.testTag("selectAll")){Text(m.t("selectAll"))}
       TextButton(onClick={draft=draft.copy(ids=ChordSelection.basics)},modifier=Modifier.testTag("selectBasics")){Text(m.t("selectBasics"))}
       TextButton(onClick={draft=draft.copy(ids=emptySet())},modifier=Modifier.testTag("selectNone")){Text(m.t("selectNone"))}
      }
      m.catalog.types.groupBy{it.family}.forEach{(family,types)->
       val all=types.all{it.id in draft.ids}
       val any=types.any{it.id in draft.ids}
       Row(verticalAlignment=Alignment.CenterVertically){
        TriStateCheckbox(state=if(all)androidx.compose.ui.state.ToggleableState.On else if(any)androidx.compose.ui.state.ToggleableState.Indeterminate else androidx.compose.ui.state.ToggleableState.Off,onClick={draft=draft.copy(ids=ChordSelection.family(draft.ids,types,!all))})
        Text(m.t(family),style=MaterialTheme.typography.titleSmall)
       }
       types.forEach{type->
        Row(Modifier.fillMaxWidth().testTag("chordFilter-${type.id}").toggleable(value=type.id in draft.ids,role=androidx.compose.ui.semantics.Role.Checkbox,onValueChange={draft=draft.copy(ids=ChordSelection.toggle(draft.ids,type.id))}).padding(start=12.dp),verticalAlignment=Alignment.CenterVertically){
         Checkbox(checked=type.id in draft.ids,onCheckedChange=null)
         Text(type.names.getValue(m.lang),modifier=Modifier.padding(vertical=12.dp))
        }
       }
      }
      HorizontalDivider()
      Text(m.t("otherFilters"),style=MaterialTheme.typography.titleMedium)
      Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=draft.advanced,onCheckedChange={draft=draft.copy(advanced=it)});Text(m.t("advanced"))}
      Choice(m.t("noteFilter"),listOf("white" to m.t("whiteOnly"),"all" to m.t("chromatic")),if(draft.white)"white" else "all"){draft=draft.copy(white=it=="white")}
      Choice(m.t("octaves"),listOf("one" to m.t("oneOctave"),"two" to m.t("twoOctaves")),if(draft.twoOctaves)"two" else "one"){draft=draft.copy(twoOctaves=it=="two")}
      Text(m.t("rangeNotice"),style=MaterialTheme.typography.bodySmall)
     } else {
      Choice(m.t("language"),listOf("system" to m.t("system"),"es" to "Castellano","en" to "English"),draft.language){draft=draft.copy(language=it)}
      Choice(m.t("notation"),listOf("latin" to "Do Re Mi","english" to "C D E"),if(draft.latin)"latin" else "english"){draft=draft.copy(latin=it=="latin")}
      Row(verticalAlignment=Alignment.CenterVertically){Checkbox(checked=draft.labels,onCheckedChange={draft=draft.copy(labels=it)});Text(m.t("labels"))}
      Text(m.t("about"),style=MaterialTheme.typography.bodyMedium)
      TextButton(onClick={draft=Prefs()}){Text(m.t("resetPrefs"))}
     }
    }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End){
     TextButton(onClick=close){Text(m.t("cancel"))}
     Button(onClick={m.preferences(draft);close()},modifier=Modifier.testTag("applySettings")){Text(m.t("apply"))}
    }
   }
  }
 }
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

@Composable private fun AppHeader(m:TrainingModel,onDialog:(String)->Unit) {
 var open by remember{mutableStateOf(false)}
 Row(Modifier.fillMaxWidth().padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically){
  Image(painterResource(R.drawable.simplesoft_logo),"SimpleSoft",Modifier.size(32.dp))
  Text("SimpleSoft Chords",style=MaterialTheme.typography.titleMedium,modifier=Modifier.padding(horizontal=8.dp).weight(1f))
  Box {
   TextButton(onClick={open=true}){Text(m.t("menu"))}
   DropdownMenu(expanded=open,onDismissRequest={open=false}){
    listOf("recognize","builder","practice").forEach{tool->
     DropdownMenuItem(text={Text(m.t(tool))},onClick={open=false;m.changeTool(tool)})
    }
    HorizontalDivider()
    listOf("info","stats","settings").forEach{id->
     DropdownMenuItem(text={Text(m.t(id))},onClick={open=false;onDialog(id)})
    }
   }
  }
 }
}

@Composable internal fun RecognitionPanel(m:TrainingModel) {
 val matches=m.recognition
 val primary=matches.firstOrNull()
 // Read observable MIDI state directly in this composable, not only in the canvas.
 Text(
  text=primary?.let{m.name(it.position)}?:m.t(if(m.selected.isEmpty())"playChordShort" else "unrecognized"),
  style=MaterialTheme.typography.headlineLarge,
  color=MaterialTheme.colorScheme.onSurface,
  modifier=Modifier.testTag("chordName").semantics{liveRegion=LiveRegionMode.Polite}
 )
 if(m.selected.isNotEmpty())Text(m.selected.sorted().joinToString(" · "){m.note(it)},style=MaterialTheme.typography.bodyLarge,modifier=Modifier.testTag("chordNotes"))
 if(primary?.inversion!=null && primary.inversion>0) {
  Text("${m.t("inversion")}: ${primary.inversion} · ${m.t("bass")}: ${m.note(primary.bass)}",color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.testTag("chordInversion"))
 }
 if(matches.size>1)Text(m.t("alternativesInInfo"),style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable private fun RecognitionInfo(m:TrainingModel,close:()->Unit) {
 AlertDialog(onDismissRequest=close,title={Text(m.t("info"))},text={
  Column(Modifier.heightIn(max=280.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(12.dp)){
   Text(m.t("playChord"))
   Text(m.t("enharmonicNotice"))
   Text(m.t("inversionLesson"))
   if(m.recognition.size>1){
    Text(m.t("ambiguous"))
    m.recognition.forEach{match->Text("${m.name(match.position)} · ${m.t("bass")}: ${m.note(match.bass)} · ${m.t("inversion")}: ${match.inversion?:"?"}")}
   }
  }
 },confirmButton={TextButton(onClick=close){Text(m.t("close"))}})
}
