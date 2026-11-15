# SimpleSoft-Chords 0.2.0 user guide

Android 8+, offline, no accounts, fixed light theme. Layout adapts to portrait and
landscape. Landscape gives wider keys on phones. The full C3–C5 piano always uses
the available width; there is no zoom or horizontal scrolling. Menu opens recognition,
builder, practice, settings, information and statistics.

Tap a key to select it in blue; tap again to deselect. Key-up stops audio but keeps
selection. Separate and simultaneous taps use the same behavior in every workspace.
Two fingers on one key share one voice and toggle only once. Physical key state is
independent: a held but deselected key uses an outline. Clear removes every selected
note, including after success and in Complete, clears feedback/hints and stops audio.
Lift any held fingers before playing again; old movement events are ignored. Each
new exercise starts with no selected notes.

Recognition updates on both adding and removing notes and stays after release.
Use Clear to replace the entire chord; a new gesture no longer clears automatically.
The large name, pitches and inversion/bass remain primary. Enharmonic explanations
and full alternatives are in Information. Octave doubles do not change identity.
Builder preserves root, quality, inversion, playable voicings and playback. Gold
reference keys are instructions, separate from blue user selection.

Menu → Settings → Learning contains mode, activity and chord filters by family,
with individual selection and All / Basics / None shortcuts. Basics selects major,
minor, diminished and augmented. Apply saves the draft locally; Cancel discards it.
An empty selection remains empty. Other filters contains Advanced, white/chromatic
single-note questions and their octave range; these never shrink the piano. Display
contains language, notation and labels. Advanced is now also persisted. Legacy prefs
remain readable. Start ear practice with major/minor, then expand by family.

Practice keeps the activity and exercise/session progress, instruction, answer choices,
Repeat, Hint, Validate, Clear, Next and the piano visible, without a vertical scroll
container or configuration controls. Invalid filters show an explanation and Edit filters.

Build selects chord notes. Identify chooses a name for written pitches. Complete shows
a supplied pitch as a gold reference, not a protected blue selection; validation combines
that reference with the notes you select. Ear compares qualities sharing root/register,
without requiring absolute pitch. Single-note questions also use selection and Validate.
Errors stay blue without red; success is green and Next remains manual. Octave changes
and valid doubles are accepted; Advanced requires the bass only when explicitly stated.
The piano remains editable after success without counting the same exercise twice.

Hint opens progressive explanations in a dialog. More keeps Play selection, C4 reference,
How it is built (step-by-step guide) and Show solution. Answer choices lock after solving
until Next; Clear always empties piano selection. Statistics and preferences stay on device.
See PRUEBAS.md for automated checks and pending physical-device/layout/audio verification.
