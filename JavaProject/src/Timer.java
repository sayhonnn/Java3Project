
import java.text.*;
import java.util.*;
import javax.sound.sampled.Clip;
import javax.swing.*;
public class Timer extends Thread {
    //class fields
    private DateFormat df = new SimpleDateFormat("HH:mm:ss");
    private boolean running = false, paused = false, reset = false;
    private long startTime;
    private long pauseTime;
    private JLabel timeLbl;
    private JSlider slider;
    private Clip clip;

    //set the audio file for the timer
    public void setClip(Clip clip) {
        this.clip = clip;
    }
    //constructor method to set the time in the slider
    Timer(JLabel time, JSlider slider) {
        this.timeLbl = time;
        this.slider = slider;
    }

    //method to keep record of the time using thread
    @Override
    public void run() {
        running = true;
        startTime = System.currentTimeMillis(); //get the now time 
        while (running) {
            try {
                Thread.sleep(100);
                //if the song is not paused
                if (!paused) {
                    //and the clip is not running 
                    if (clip != null && clip.isRunning()) {
                        //set time in the label, 
                        timeLbl.setText(toTimeString());
                        //continously update the value of slider in the loop
                        int currentSecond = (int) clip.getMicrosecondPosition() / 1_000_000;
                        slider.setValue(currentSecond);
                    }
                } else {
                    pauseTime += 100;
                }
            } catch (InterruptedException ex) {
                //in the reset case, set everything to zero
                if (reset) {
                    slider.setValue(0);
                    timeLbl.setText("00:00:00");
                    running = false;
                    break;
                }
            }
        }
    }
    
    //method to reset the timer
    void reset() {
        reset = true;
        running = false;
    }
    
    //method to pause the timer
    void pauseTimer() {
        paused = true;
    }
    
    //method to resume the timer
    void resumeTimer() {
        paused = false;
    }
    
    //metbod to convert the time into a string and return 
    private String toTimeString() {
        long now = System.currentTimeMillis();
        Date c = new Date(now - startTime - pauseTime);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String timeCounter = df.format(c);
        return timeCounter;
    }
}
