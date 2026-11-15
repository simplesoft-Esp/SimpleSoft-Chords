package com.simplesoft.chords

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeProvider

/** 15 whites + 10 blacks. Pointer IDs own independent sound streams. */
class PianoView(context:Context):View(context) {
 var model:TrainingModel?=null
 private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
 private var observedQuestion:Question?=null
 private var observedTool=""
 fun refresh(m:TrainingModel){
  if(observedQuestion!==m.question || observedTool!=m.tool){releasePointers();observedQuestion=m.question;observedTool=m.tool}
  model=m;invalidate()
 }
 private val held=mutableMapOf<Int,Pair<Int,Int>>()
 private val whites=(48..72).filter{it%12 in Pitch.naturals}
 private val blacks=(48..72).filter{it%12 !in Pitch.naturals}
 init { isFocusable=true;importantForAccessibility=IMPORTANT_FOR_ACCESSIBILITY_YES }
 private fun bounds(midi:Int):RectF {
  val w=width/15f
  val index=whites.indexOf(midi)
  return if(index>=0)RectF(index*w,0f,(index+1)*w,height.toFloat())
  else { val left=whites.count{it<midi}*w;RectF(left-w*.31f,0f,left+w*.31f,height*.61f) }
 }
 private fun keyAt(x:Float,y:Float)=(blacks+whites).firstOrNull{bounds(it).contains(x,y)}
 override fun onDraw(canvas:Canvas) {
  val m=model?:return;val q=if(m.tool=="practice")m.question else null;val target=q?.notes?.toSet()?:emptySet()
  val guideNote=if(m.guide>=0) q?.position?.notes?.getOrNull(m.guide) else null
  for(n in whites+blacks) {
   val black=n in blacks;val r=bounds(n)
   val correct=m.solved && ((m.tool=="practice" && n in m.selected) || (q?.build==false && n in target))
   val noteFeedback=q?.note!=null && m.checked && m.marked==n
   val shown=(m.tool=="builder" && n in m.builderPosition?.notes.orEmpty()) || (m.reveal || m.hint>=4) && n in target || guideNote==n || (m.guide==q?.notes?.size && m.guide>=0 && n in target)
   paint.color=when {
    correct || (noteFeedback && n==q?.note)->PianoColors.correct
    (m.tool=="practice" && n in m.selected) || n in m.played ->if(black)PianoColors.blackSelected else PianoColors.whiteSelected
    shown->PianoColors.hint
    held.values.any{it.first==n}->PianoColors.whiteSelected
    black->PianoColors.blackKey
    else->PianoColors.whiteKey
   }
   paint.style=Paint.Style.FILL;canvas.drawRect(r,paint)
   paint.style=Paint.Style.STROKE;paint.strokeWidth=2f;paint.color=Color.rgb(75,80,85);canvas.drawRect(r,paint);paint.style=Paint.Style.FILL
   paint.color=if(black || correct || (m.tool=="practice" && n in m.selected) || n in m.played || shown)PianoColors.lightLabel else PianoColors.darkLabel
   paint.textAlign=Paint.Align.CENTER;paint.textSize=(width/15f*.24f).coerceIn(13f,30f)
   val mark=when {correct->"✓";shown->"★";(m.tool=="practice" && n in m.selected)->"●";else->""}
   if(mark.isNotEmpty())canvas.drawText(mark,r.centerX(),r.bottom-paint.textSize*2,paint)
   if(m.prefs.labels || n%12==0)canvas.drawText(m.note(n),r.centerX(),r.bottom-paint.textSize*.55f,paint)
  }
 }
 override fun onTouchEvent(event:MotionEvent):Boolean {
  val m=model?:return false
  when(event.actionMasked) {
   MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN -> {
    parent.requestDisallowInterceptTouchEvent(true)
    val i=event.actionIndex;val n=keyAt(event.getX(i),event.getY(i))
    if(n!=null) { val first=held.values.none{it.first==n};val stream=if(first)m.audio.down(n) else held.values.first{it.first==n}.second;held[event.getPointerId(i)]=n to stream;m.press(n);if(first && m.tool=="practice")m.touch(n);invalidate() }
   }
   MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP -> { held.remove(event.getPointerId(event.actionIndex))?.let{m.release(it.first);if(held.values.none{other->other.first==it.first})m.audio.up(it.second)};invalidate();if(event.actionMasked==MotionEvent.ACTION_UP)performClick() }
   MotionEvent.ACTION_MOVE -> {
    for(i in 0 until event.pointerCount){
     val id=event.getPointerId(i);val old=held[id];val n=keyAt(event.getX(i),event.getY(i))
     if(n!=old?.first){
      held.remove(id)?.let{m.release(it.first);if(held.values.none{other->other.first==it.first})m.audio.up(it.second)}
      if(n!=null){val first=held.values.none{it.first==n};val stream=held.values.firstOrNull{it.first==n}?.second?:m.audio.down(n);held[id]=n to stream;m.press(n);if(first && m.tool=="practice")m.touch(n)}
     }
    };invalidate()
   }
   MotionEvent.ACTION_CANCEL -> releasePointers()
  }
  return true
 }
 fun releasePointers(){val m=model;held.values.forEach{m?.audio?.up(it.second);m?.release(it.first)};held.clear();invalidate()}
 override fun performClick():Boolean {super.performClick();return true}
 override fun onWindowFocusChanged(hasWindowFocus:Boolean){super.onWindowFocusChanged(hasWindowFocus);if(!hasWindowFocus)releasePointers()}
 override fun onDetachedFromWindow(){releasePointers();super.onDetachedFromWindow()}
 // Virtual keys expose the same pitch actions to accessibility services.
 override fun getAccessibilityNodeProvider():AccessibilityNodeProvider = object:AccessibilityNodeProvider(){
  override fun createAccessibilityNodeInfo(id:Int):AccessibilityNodeInfo {
   if(id==View.NO_ID)return AccessibilityNodeInfo.obtain(this@PianoView).apply{onInitializeAccessibilityNodeInfo(this);(48..72).forEach{addChild(this@PianoView,it)}}
   return AccessibilityNodeInfo.obtain().apply {
    setSource(this@PianoView,id);setParent(this@PianoView);packageName=context.packageName;className="android.widget.Button"
    contentDescription=model?.note(id);isEnabled=true;isClickable=true;isVisibleToUser=isShown
    val r=bounds(id);val rect=Rect();r.roundOut(rect);setBoundsInParent(rect)
    val xy=IntArray(2);getLocationOnScreen(xy);rect.offset(xy[0],xy[1]);setBoundsInScreen(rect)
    addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
   }
  }
  override fun performAction(id:Int,action:Int,arguments:Bundle?):Boolean {
   if(id in 48..72 && action==AccessibilityNodeInfo.ACTION_CLICK){model?.let{it.audio.chord(listOf(id));it.press(id);it.release(id);if(it.tool=="practice")it.touch(id)};invalidate();return true}
   return false
  }
 }
}
