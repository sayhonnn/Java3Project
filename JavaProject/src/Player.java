
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Player implements LineListener {
    
    private static final int SECONDS_IN_HOUR = 60 * 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private boolean playCompleted;
    private boolean isStopped;
    private boolean isPaused;
    private Clip audioClip;

    //method to load the audio file from the computer 
    public void load(String audioFilePath) throws Exception {
        //oen and read the audo file using jva builtin library AudioInputStream
        File audioFile = new File(audioFilePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = audioStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        audioClip = (Clip) AudioSystem.getLine(info);
        audioClip.addLineListener(this);
        audioClip.open(audioStream);
    }
    //get the timelength of the audio file
    public long getLength() {
        return audioClip.getMicrosecondLength() / 1_000_000;
    }
    //get string length to show in the GUI
    public String getLengthString() {
        String length = "";
        long hour = 0;
        long minute = 0;
        long seconds = audioClip.getMicrosecondLength() / 1_000_000;
        if (seconds >= SECONDS_IN_HOUR) {
            hour = seconds / SECONDS_IN_HOUR;
            length = String.format("%02d:", hour);
        } else {
            length += "00:";
        }
        minute = seconds - hour * SECONDS_IN_HOUR;
        if (minute >= SECONDS_IN_MINUTE) {
            minute = minute / SECONDS_IN_MINUTE;
            length += String.format("%02d:", minute);
        } else {
            minute = 0;
            length += "00:";
        }
        long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;
        length += String.format("%02d", second);
        return length;
    }
    
    //play teh audio file
    public void play() throws IOException {
        audioClip.start();  //playng the audio file
        playCompleted = false;
        isStopped = false;
        //dont stop untill the file is completely played
        while (!playCompleted) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                //handle other events, like stop button click and paused button clicks
                //stop if there buttons are clicke, otherwise NO
                if (isStopped) {
                    audioClip.stop();
                    break;
                }
                if (isPaused) {
                    audioClip.stop();
                } else {
                    audioClip.start();
                }
            }
        }
        audioClip.close();  //after it is all player, close it
    }
    //method to stop the audio file currently playing
    public void stop() {
        isStopped = true;
    }
    //method to pause the audio file currently playing
    public void pause() {
        isPaused = true;
    }
    //method to resume the audio file currently playing
    public void resume() {
        isPaused = false;
    }
    //this method updates the status of the audio file that is playing
    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        if (type == LineEvent.Type.STOP) {
            if (isStopped || !isPaused) {
                playCompleted = true;
            }
        }
    }
    //return the loaded file
    public Clip getAudioClip() {
        return audioClip;
    }
}