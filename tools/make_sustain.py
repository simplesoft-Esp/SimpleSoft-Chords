"""Original periodic additive PCM for held notes; preserves v0.1 piano one-shots."""
from pathlib import Path
import math, struct, wave
root=Path(__file__).resolve().parents[1]
for midi in range(48,73):
    frequency=440*2**((midi-69)/12)
    rate=22050
    frames=round(rate*40/frequency)
    samples=[round(6500*sum(a*math.sin(2*math.pi*40*h*i/frames) for h,a in [(1,1),(2,.30),(3,.12),(4,.05)])) for i in range(frames)]
    with wave.open(str(root/f'app/src/main/assets/audio/sustain_{midi}.wav'),'wb') as out:
        out.setparams((1,2,rate,0,'NONE','not compressed'))
        out.writeframes(struct.pack('<'+'h'*frames,*samples))
