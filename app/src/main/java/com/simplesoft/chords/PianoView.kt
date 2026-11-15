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
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeProvider

/** Full-width two-octave keyboard. Pointers own audio; only the shared selection paints blue. */
class PianoView(context:Context):View(context) {
 var model:TrainingModel?=null
 private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
 private data class Voice(val note:Int,val stream:Int,val generation:Int)
 private val held=mutableMapOf<Int,Voice>()
 private val admitted=mutableMapOf<Int,Pair<Float,Float>>()
 private var observedGeneration=-1
 private val slop=ViewConfiguration.get(context).scaledTouchSlop.toFloat()
 init {isFocusable=true;importantForAccessibility=IMPORTANT_FOR_ACCESSIBILITY_YES}
 fun refresh(m:TrainingModel){
  if(model!==m)releasePointers()
  model=m;syncGeneration();invalidate()
 }
 private fun syncGeneration(){
  val token=model?.keyboardGeneration?:return
  if(token!=observedGeneration){releasePointers();observedGeneration=token}
 }
 private fun bounds(note:Int)=PianoGeometry.bounds(note,width.toFloat(),height.toFloat()).let{RectF(it.left,it.top,it.right,it.bottom)}
 private fun keyAt(x:Float,y:Float)=PianoGeometry.keyAt(x,y,width.toFloat(),height.toFloat())
 override fun onDraw(canvas:Canvas){
  val m=model?:return
  val q=if(m.tool=="practice")m.question else null
  val target=q?.notes.orEmpty().toSet()
  val guideNote=if(m.guide>=0)q?.notes?.getOrNull(m.guide) else null
  val correctAnswer=m.checked && m.feedback=="correct"
  val density=resources.displayMetrics.density
  for(n in PianoGeometry.whites+PianoGeometry.blacks){
   val black=n in PianoGeometry.blacks;val r=bounds(n)
   val selected=n in m.selected
   val correct=correctAnswer && (selected || n in q?.given.orEmpty() || (q?.build==false && n in target))
   val shown=(m.tool=="builder" && n in m.builderPosition?.notes.orEmpty()) || n in q?.given.orEmpty() ||
    ((m.reveal || m.hint>=4) && n in target) || guideNote==n || (m.guide>=0 && m.guide==q?.notes?.size && n in target)
   paint.color=when {
    correct->PianoColors.correct
    selected->if(black)PianoColors.blackSelected else PianoColors.whiteSelected
    shown->PianoColors.hint
    black->PianoColors.blackKey
    else->PianoColors.whiteKey
   }
   paint.style=Paint.Style.FILL;canvas.drawRect(r,paint)
   // A held but deselected key uses an outline, never the persistent blue fill.
   paint.style=Paint.Style.STROKE
   paint.strokeWidth=if(n in m.pressed.values)3*density else density
   paint.color=if(black)Color.rgb(110,115,120) else Color.rgb(75,80,85)
   canvas.drawRect(r,paint);paint.style=Paint.Style.FILL
   canvas.save();canvas.clipRect(r)
   paint.color=if(black || selected || correct || shown)PianoColors.lightLabel else PianoColors.darkLabel
   paint.textAlign=Paint.Align.CENTER
   paint.textSize=(width/15f*.25f).coerceIn(8*density,16*density)
   val label=m.note(n)
   if(paint.measureText(label)>r.width()-2*density)paint.textSize*=((r.width()-2*density)/paint.measureText(label)).coerceAtLeast(.5f)
   val mark=when{correct->"✓";selected->"●";shown->"★";else->""}
   if(mark.isNotEmpty())canvas.drawText(mark,r.centerX(),r.bottom-paint.textSize*2,paint)
   if(m.prefs.labels || n%12==0)canvas.drawText(label,r.centerX(),r.bottom-paint.textSize*.55f,paint)
   canvas.restore()
  }
 }
 private fun down(id:Int,note:Int){
  val m=model?:return
  val first=held.values.none{it.note==note}
  m.press(id,note,observedGeneration)
  val stream=if(first)m.audio.down(note) else held.values.first{it.note==note}.stream
  held[id]=Voice(note,stream,observedGeneration)
 }
 private fun up(id:Int){
  val m=model?:return
  held.remove(id)?.let{voice->
   m.release(id,voice.generation)
   if(held.values.none{it.note==voice.note})m.audio.up(voice.stream)
  }
 }
 override fun onTouchEvent(event:MotionEvent):Boolean {
  if(model==null)return false
  // Check here as well as during Compose updates: a rapid Next may precede recomposition.
  syncGeneration()
  when(event.actionMasked){
   MotionEvent.ACTION_DOWN,MotionEvent.ACTION_POINTER_DOWN->{
    if(event.actionMasked==MotionEvent.ACTION_DOWN)releasePointers()
    parent?.requestDisallowInterceptTouchEvent(true)
    val i=event.actionIndex;val id=event.getPointerId(i)
    admitted[id]=event.getX(i) to event.getY(i)
    keyAt(event.getX(i),event.getY(i))?.let{down(id,it)}
   }
   MotionEvent.ACTION_MOVE->{
    for(i in 0 until event.pointerCount){
     val id=event.getPointerId(i);val origin=admitted[id]?:continue
     val x=event.getX(i);val y=event.getY(i);val note=keyAt(x,y);val old=held[id]?.note
     val dx=x-origin.first;val dy=y-origin.second
     if(note!=old && (old==null || note==null || dx*dx+dy*dy>=slop*slop)){
      up(id);if(note!=null)down(id,note);admitted[id]=x to y
     }
    }
   }
   MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP->{
    val id=event.getPointerId(event.actionIndex);up(id);admitted.remove(id)
    if(event.actionMasked==MotionEvent.ACTION_UP)performClick()
   }
   MotionEvent.ACTION_CANCEL->releasePointers()
  }
  invalidate();return true
 }
 fun releasePointers(){held.keys.toList().forEach(::up);admitted.clear();invalidate()}
 override fun performClick():Boolean{super.performClick();return true}
 override fun onWindowFocusChanged(hasWindowFocus:Boolean){super.onWindowFocusChanged(hasWindowFocus);if(!hasWindowFocus)releasePointers()}
 override fun onSizeChanged(w:Int,h:Int,oldw:Int,oldh:Int){super.onSizeChanged(w,h,oldw,oldh);releasePointers()}
 override fun onDetachedFromWindow(){releasePointers();super.onDetachedFromWindow()}
 override fun getAccessibilityNodeProvider():AccessibilityNodeProvider=object:AccessibilityNodeProvider(){
  override fun createAccessibilityNodeInfo(id:Int):AccessibilityNodeInfo {
   if(id==View.NO_ID)return AccessibilityNodeInfo.obtain(this@PianoView).apply{onInitializeAccessibilityNodeInfo(this);(48..72).forEach{addChild(this@PianoView,it)}}
   return AccessibilityNodeInfo.obtain().apply{
    setSource(this@PianoView,id);setParent(this@PianoView);packageName=context.packageName;className="android.widget.Button"
    contentDescription=model?.note(id);isSelected=id in model?.selected.orEmpty();isEnabled=true;isClickable=true;isVisibleToUser=isShown
    val r=bounds(id);val rect=Rect();r.roundOut(rect);setBoundsInParent(rect)
    val xy=IntArray(2);getLocationOnScreen(xy);rect.offset(xy[0],xy[1]);setBoundsInScreen(rect)
    addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
   }
  }
  override fun performAction(id:Int,action:Int,arguments:Bundle?):Boolean {
   if(id in 48..72 && action==AccessibilityNodeInfo.ACTION_CLICK){model?.let{it.audio.chord(listOf(id));it.touch(id)};invalidate();return true}
   return false
  }
 }
}
