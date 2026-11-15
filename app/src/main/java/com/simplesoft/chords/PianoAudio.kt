package com.simplesoft.chords

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** Bounded 32-voice offline PCM piano, no audio recording or network. */
class PianoAudio(context:Context) {
 private val handler=Handler(Looper.getMainLooper())
 private val manager=context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
 private val attrs=AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
 private val pool=SoundPool.Builder().setMaxStreams(32).setAudioAttributes(attrs).build()
 private val samples=mutableMapOf<Int,Int>();private val active=mutableSetOf<Int>()
 private var loaded=0;private var epoch=0;private var closed=false;private var focused=false
 var ready by mutableStateOf(false);private set
 var error by mutableStateOf(false);private set
 var focusDenied by mutableStateOf(false);private set
 private val focus=AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
  .setAudioAttributes(attrs).setOnAudioFocusChangeListener({ change ->
   if(change<=0) { focused=false;stopAll() } else focused=true
  },handler).build()
 init {
  pool.setOnLoadCompleteListener { _,_,status -> handler.post { if(status!=0) error=true else loaded++; ready=loaded==25 && !error } }
  for(midi in 48..72) context.assets.openFd("audio/piano_$midi.wav").use { samples[midi]=pool.load(it,1) }
 }
 private fun takeFocus():Boolean {
  if(closed || !ready)return false
  if(!focused) focused=manager.requestAudioFocus(focus)==AudioManager.AUDIOFOCUS_REQUEST_GRANTED
  focusDenied=!focused
  return focused
 }
 fun down(midi:Int):Int {
  if(!takeFocus()) return 0
  val stream=pool.play(samples[midi]?:return 0,.1f,.1f,1,0,1f)
  if(stream!=0) { active+=stream;val token=epoch;handler.postDelayed({ if(token==epoch) active-=stream },2400) }
  return stream
 }
 fun up(stream:Int) {
  if(stream==0 || stream !in active) return
  val token=epoch
  for(step in 1..6) handler.postDelayed({ if(!closed && token==epoch && stream in active) {
   val gain=.1f*(1-step/6f);pool.setVolume(stream,gain,gain)
   if(step==6) {pool.stop(stream);active-=stream}
  } },step*15L)
 }
 fun chord(notes:Collection<Int>) { active.toList().forEach(::up);notes.distinct().forEach(::down) }
 fun stopAll() { epoch++;active.forEach(pool::stop);active.clear() }
 fun suspendAudio() { stopAll();manager.abandonAudioFocusRequest(focus);focused=false }
 fun close() { suspendAudio();closed=true;pool.release() }
}
