import javax.sound.sampled.*;
import java.io.File;

public class Music {
    private Clip clip;
    private float loopVolume = -10.0f;   // Volume de la musique de fond
    private float sfxVolume = 0.0f;      // Volume des sons d'effet

    public void playLoop(String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            setVolume(clip, loopVolume); // Appliquer le volume ici
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playOnce(String filePath) {
        try {
            File file = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip oneShot = AudioSystem.getClip();
            oneShot.open(audioStream);
            setVolume(oneShot, sfxVolume);
            oneShot.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    // Méthode pour changer le volume
    private void setVolume(Clip clip, float volumeDb) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volumeDb);
        } else {
            System.out.println("Le volume n'est pas supporté pour ce clip.");
        }
    }



    public void setLoopVolume(float volumeDb) {
        this.loopVolume = volumeDb;
    }

    public void setSfxVolume(float volumeDb) {
        this.sfxVolume = volumeDb;
    }
}

