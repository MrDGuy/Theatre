package run.mycode.theater.support;

import java.awt.Graphics2D;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import run.mycode.theater.Stage;

public class PlayNoteAction implements SceneAction {
    private final int instrument; // MIDI instrument number (0–127)
    private final int note;       // MIDI note number (0–127)
    private final int duration;   // in milliseconds
    private static Synthesizer synthesizer;
    private static MidiChannel[] channels;

    static {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channels = synthesizer.getChannels();
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("MIDI system not available", e);
        }
    }

    public PlayNoteAction(int instrument, int note, double seconds) {
        this.instrument = instrument;
        this.note = note;
        this.duration = (int) (seconds * 1000);
    }

    @Override
    public void go(Graphics2D context, Stage stage) {
        if (channels == null) return;

        MidiChannel channel = channels[0]; // Use channel 0 by default
        channel.programChange(instrument); // Set the instrument
        channel.noteOn(note, 100);         // Velocity (volume)

        // Stop note after duration using a separate thread to not block the UI
        new Thread(() -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ignored) {}
            channel.noteOff(note);
        }).start();
    }
}