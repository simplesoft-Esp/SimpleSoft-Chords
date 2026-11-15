package com.simplesoft.chords

import org.json.JSONArray
import org.json.JSONObject

// Keep the v1 preference format readable, including an explicitly empty chord selection.
data class Prefs(val language:String="system",val latin:Boolean=true,val labels:Boolean=true,val mode:String="chords",val activity:String="mix",val byType:Boolean=true,val white:Boolean=true,val twoOctaves:Boolean=true,val ids:Set<String> = setOf("major","minor"),val advanced:Boolean=false)
internal object PrefsCodec {
 fun read(text:String):Prefs=runCatching {
  val j=JSONObject(text)
  Prefs(j.optString("language","system"),j.optBoolean("latin",true),j.optBoolean("labels",true),j.optString("mode","chords"),j.optString("activity","mix"),j.optBoolean("byType",true),j.optBoolean("white",true),j.optBoolean("twoOctaves",true),if(j.has("ids"))j.getJSONArray("ids").let{a->(0 until a.length()).map{a.getString(it)}.toSet()} else setOf("major","minor"),j.optBoolean("advanced",false))
 }.getOrDefault(Prefs())
 fun write(p:Prefs)=JSONObject().put("language",p.language).put("latin",p.latin).put("labels",p.labels).put("mode",p.mode).put("activity",p.activity).put("byType",p.byType).put("white",p.white).put("twoOctaves",p.twoOctaves).put("ids",JSONArray(p.ids.sorted())).put("advanced",p.advanced).toString()
}
internal object ChordSelection {
 val basics=setOf("major","minor","dim","aug")
 fun toggle(ids:Set<String>,id:String)=if(id in ids)ids-id else ids+id
 fun family(ids:Set<String>,types:List<ChordType>,enabled:Boolean)=if(enabled)ids+types.map{it.id} else ids-types.map{it.id}.toSet()
 fun all(catalog:Catalog)=catalog.types.map{it.id}.toSet()
}
