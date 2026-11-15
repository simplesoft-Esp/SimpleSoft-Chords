"""Regenerate only the catalog table; guides and test evidence are maintained manually."""
from pathlib import Path
import json
root=Path(__file__).resolve().parents[1]
catalog=json.loads((root/'shared/catalog.json').read_text())
path=root/'docs/CATALOGO.md'
text=path.read_text()
header='| ID | Familia | Castellano / English | Símbolo | Semitonos | Grados diatónicos | Fundamental MIDI válida | Posiciones |'
rows=[header,'|---|---|---|---|---|---|---|---|']
for t in catalog['types']:
    high=72-max(t['semitones'])
    rows.append('| '+' | '.join([t['id'],t['family'],t['names']['es']+' / '+t['names']['en'],t['symbol'] or '(mayor)',', '.join(map(str,t['semitones'])),', '.join(map(str,t['degrees'])),f'48–{high}',str(max(0,high-47))])+' |')
start=text.index(header)
end=text.find('\n\n',start)
if end<0:end=len(text)
path.write_text(text[:start]+'\n'.join(rows)+text[end:])
print('Updated catalog table; guides and verification evidence preserved.')
