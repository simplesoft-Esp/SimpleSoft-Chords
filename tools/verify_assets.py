"""Independent data/PCM checks; no Android or external Python dependencies."""
from pathlib import Path
import json, math, wave, struct, unittest, xml.etree.ElementTree as ET
R=Path(__file__).resolve().parents[1]
CAT=json.loads((R/'shared/catalog.json').read_text());TYPES=CAT['types']
class Assets(unittest.TestCase):
 def test_formulas_and_all_positions(self):
  self.assertEqual((CAT['midiMin'],CAT['midiMax']),(48,72));total=0
  for t in TYPES:
   f=t['semitones'];self.assertEqual(f,sorted(set(f)));self.assertEqual(f[0],0);self.assertEqual(len(f),len(t['degrees']))
   for root in range(48,73-max(f)):
    notes=[root+n for n in f];self.assertEqual(min(notes),root);self.assertTrue(all(48<=n<=72 for n in notes));total+=1
  self.assertEqual(total,472)
 def test_translation_coverage(self):
  i=json.loads((R/'shared/i18n.json').read_text());self.assertEqual(set(i['es']),set(i['en']))
  for t in TYPES:
   for d,s in zip(t['degrees'],t['semitones']):self.assertIn(f'function.{d}.{s}',i['en'])
   self.assertTrue(t['names']['es'] and t['names']['en'])
 def test_audio_files_tuning_and_headroom(self):
  for midi in range(48,73):
   with wave.open(str(R/f'app/src/main/assets/audio/piano_{midi}.wav')) as w:
    self.assertEqual((w.getnchannels(),w.getsampwidth(),w.getframerate()),(1,2,22050));rate=w.getframerate();a=struct.unpack('<'+'h'*w.getnframes(),w.readframes(w.getnframes()))
   self.assertLess(max(map(abs,a)),32767);self.assertEqual(a[0],0);self.assertLess(abs(a[-1]),30)
   self.assertLess(max(map(abs,a))/32768*32*.1,1)
   # Scan around A440-derived frequency; the fundamental must be strongest locally.
   target=440*2**((midi-69)/12);part=a[2205:13230]
   def energy(f):
    real=sum(v*math.cos(2*math.pi*f*j/rate) for j,v in enumerate(part));imag=sum(v*math.sin(2*math.pi*f*j/rate) for j,v in enumerate(part));return real*real+imag*imag
   candidates=[target*(1+s/2000) for s in range(-12,13)]
   measured=max(candidates,key=energy);self.assertLess(abs(1200*math.log2(measured/target)),3)
 def test_no_permissions_and_landscape(self):
  root=ET.parse(R/'app/src/main/AndroidManifest.xml').getroot();self.assertEqual(root.findall('uses-permission'),[])
  a=root.find('application/activity');self.assertEqual(a.attrib['{http://schemas.android.com/apk/res/android}screenOrientation'],'sensorLandscape')
 def test_reference_logo_is_exact(self):
  import hashlib
  self.assertEqual(hashlib.sha256((R/'app/src/main/res/drawable/simplesoft_logo.png').read_bytes()).hexdigest(),'fb4f6a6f55e89f192aa1a0a1147340d0ecaf68745ef6300927f8e139f423cffc')
if __name__=='__main__':unittest.main(verbosity=2)
