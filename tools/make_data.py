from pathlib import Path
import json, math, wave, struct
ROOT=Path(__file__).resolve().parents[1]
types=[]
def add(id,family,es,en,symbol,formula,degrees,aliases=[]):
 types.append(dict(id=id,family=family,names={'es':es,'en':en},symbol=symbol,semitones=formula,degrees=degrees,aliases=aliases,explanationKey='formula'))
add('fifth','dyads','quinta justa','perfect fifth','5',[0,7],[1,5],['power chord'])
add('third-major','dyads','tercera mayor','major third','(3)',[0,4],[1,3])
add('third-minor','dyads','tercera menor','minor third','(b3)',[0,3],[1,3])
for args in [
('major','triads','mayor','major','',[0,4,7],[1,3,5]),
('minor','triads','menor','minor','m',[0,3,7],[1,3,5]),
('dim','triads','disminuido','diminished','dim',[0,3,6],[1,3,5]),
('aug','triads','aumentado','augmented','+',[0,4,8],[1,3,5]),
('sus2','suspended','suspendido de segunda','suspended second','sus2',[0,2,7],[1,2,5]),
('sus4','suspended','suspendido de cuarta','suspended fourth','sus4',[0,5,7],[1,4,5]),
('six','sixths','sexta mayor','major sixth','6',[0,4,7,9],[1,3,5,6]),
('minor-six','sixths','menor con sexta','minor sixth','m6',[0,3,7,9],[1,3,5,6]),
('seven','sevenths','séptima dominante','dominant seventh','7',[0,4,7,10],[1,3,5,7]),
('maj7','sevenths','séptima mayor','major seventh','maj7',[0,4,7,11],[1,3,5,7]),
('min7','sevenths','menor séptima','minor seventh','m7',[0,3,7,10],[1,3,5,7]),
('minmaj7','sevenths','menor con séptima mayor','minor major seventh','m(maj7)',[0,3,7,11],[1,3,5,7]),
('half-dim','sevenths','semidisminuido','half-diminished','m7b5',[0,3,6,10],[1,3,5,7]),
('dim7','sevenths','séptima disminuida','diminished seventh','dim7',[0,3,6,9],[1,3,5,7]),
('add9','added','mayor con novena añadida','major add ninth','add9',[0,4,7,14],[1,3,5,9]),
('madd9','added','menor con novena añadida','minor add ninth','madd9',[0,3,7,14],[1,3,5,9]),
('add11','added','mayor con oncena añadida','major add eleventh','add11',[0,4,7,17],[1,3,5,11]),
('madd11','added','menor con oncena añadida','minor add eleventh','madd11',[0,3,7,17],[1,3,5,11]),
('six-nine','added','sexta con novena','six nine','6/9',[0,4,7,9,14],[1,3,5,6,9]),
('nine','extensions','novena dominante','dominant ninth','9',[0,4,7,10,14],[1,3,5,7,9]),
('maj9','extensions','novena mayor','major ninth','maj9',[0,4,7,11,14],[1,3,5,7,9]),
('min9','extensions','menor novena','minor ninth','m9',[0,3,7,10,14],[1,3,5,7,9]),
('eleven','extensions','oncena dominante','dominant eleventh','11',[0,4,7,10,14,17],[1,3,5,7,9,11]),
('maj11','extensions','oncena mayor','major eleventh','maj11',[0,4,7,11,14,17],[1,3,5,7,9,11]),
('min11','extensions','menor oncena','minor eleventh','m11',[0,3,7,10,14,17],[1,3,5,7,9,11]),
('thirteen','extensions','trecena dominante','dominant thirteenth','13',[0,4,7,10,14,17,21],[1,3,5,7,9,11,13]),
('maj13','extensions','trecena mayor','major thirteenth','maj13',[0,4,7,11,14,17,21],[1,3,5,7,9,11,13]),
('min13','extensions','menor trecena','minor thirteenth','m13',[0,3,7,10,14,17,21],[1,3,5,7,9,11,13]),
('seven-flat5','altered','séptima con quinta bemol','seventh flat fifth','7b5',[0,4,6,10],[1,3,5,7]),
('seven-sharp5','altered','séptima con quinta aumentada','seventh sharp fifth','7#5',[0,4,8,10],[1,3,5,7]),
('seven-flat9','altered','séptima con novena bemol','seventh flat ninth','7b9',[0,4,7,10,13],[1,3,5,7,9]),
('seven-sharp9','altered','séptima con novena aumentada','seventh sharp ninth','7#9',[0,4,7,10,15],[1,3,5,7,9]),
('nine-sharp11','altered','novena con oncena aumentada','ninth sharp eleventh','9#11',[0,4,7,10,14,18],[1,3,5,7,9,11]),
('seven-flat13','altered','séptima con trecena bemol','seventh flat thirteenth','7b13',[0,4,7,10,20],[1,3,5,7,13])]: add(*args)
shared=ROOT/'shared';shared.mkdir(exist_ok=True)
catalog=dict(schemaVersion=1,catalogVersion='0.1.0',midiMin=48,midiMax=72,tuningA4=440,types=types)
(shared/'catalog.json').write_text(json.dumps(catalog,ensure_ascii=False,indent=2)+'\n')
# Original additive synthesis, deterministic PCM. No external sound bank.
for midi in range(48,73):
 f=440*2**((midi-69)/12);rate=22050;duration=2.3
 values=[]
 for i in range(round(rate*duration)):
  t=i/rate;attack=min(1,t/.006);end=min(1,(duration-t)/.08)
  v=sum(a*math.exp(-t*(1.5+h*.5))*math.sin(2*math.pi*f*h*t) for h,a in [(1,1),(2,.42),(3,.23),(4,.12),(5,.065),(6,.035)] if f*h<rate/2)
  values.append(round(15000*attack*end*v/1.9))
 with wave.open(str(ROOT/f'app/src/main/assets/audio/piano_{midi}.wav'),'wb') as w:
  w.setparams((1,2,rate,0,'NONE','not compressed'));w.writeframes(struct.pack('<'+'h'*len(values),*values))
print(f'{len(types)} types; {sum(25-max(t["semitones"]) for t in types)} positions; 25 original PCM samples')
