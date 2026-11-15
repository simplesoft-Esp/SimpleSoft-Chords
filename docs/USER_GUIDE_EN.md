# SimpleSoft-Chords 0.2.0 user guide

Android 8+, landscape, fully offline. The fixed C3–C5 piano supports multiple fingers.
Notes sustain while held and release when lifted. Slide to another key; two fingers
on one key share a voice. Audio stops when the app loses focus.

Choose a workspace:

- Recognize chords: play overlapping notes. The last gesture remains blue after
  release; a new gesture clears it. Analysis lists actual pitches, possible roots,
  qualities, bass and inversion (0 means root position). Octave doubles do not change
  identity. Ambiguous sets retain alternatives; the first match is not tonal certainty.
  A legend explains enharmonic sharp/flat names. Unknown sets are reported honestly.
- Chord builder: choose root, quality and inversion. Only complete voicings fitting
  the fixed two octaves appear. Stars show the proposed keys. Press Repeat to listen.
- Practice: choose Build, Identify, Complete or Ear. Levels progress from major/minor
  through major/minor/diminished/augmented triads to sevenths. Content offers custom filters.

Build: tap to toggle selected notes. Selection stays blue when released, but audio stops.
Identify: read the displayed pitches and choose the chord name.
Complete: keep the supplied fixed note and add the missing pitches.
Ear: listen and choose quality. All options share the root and register, so the task
never depends on distinguishing C major from D major by absolute pitch.

Press Validate to check any chord activity. An incorrect answer says Incorrect and
keeps blue keys without red. Retry or ask for hints. Correct answers show green keys
and remain until you manually press Next. Clear preserves the supplied completion note.
At beginner level, octave changes, inversions and valid doubles are accepted.
Advanced build/complete explicitly requests an inversion and bass pitch class;
only then must the bass match. No hidden exact voicing is required.
If filters cannot produce a valid question or distractor, broaden them using the
provided action or choose another activity.

Hint progresses through functions, intervals, note names and highlighted keys.
How it is built walks through individual notes and the full chord. Using help is
recorded in local activity/family statistics. Preferences and progress stay on device.
Spanish/English, note notation and labels are configurable; the theme follows the system.
White-key and octave filters affect single-note questions, never the fixed piano range.
Version 0.1.0 preferences remain readable. Workspace, builder and Advanced are session state.
See PRUEBAS.md for automated checks and the pending physical-device verification.
