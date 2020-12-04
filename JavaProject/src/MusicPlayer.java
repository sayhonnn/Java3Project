
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class MusicPlayer extends JFrame implements ActionListener {
    //buttons and sliders to add to GUI 
    private Player player = new Player();
    private Thread playbackThread;
    private Timer timer;
    private boolean isPlaying = false, isPause = false;
    private JButton play = new JButton("Play");
    private JButton pause = new JButton("Pause");
    private String audioFilePath, lastOpenPath;
    private JLabel nameLbl = new JLabel("Playing File:");
    private JLabel durLbl = new JLabel("00:00:00");
    private JButton open = new JButton("Open");
    private JLabel timeLbl = new JLabel("00:00:00");
    private JSlider slider = new JSlider();
    private DefaultListModel<String> model = null;
    private JList<String> list = null;
    private int currentSong = 0;
    //create LinkedList as dynamic structure.
    private LinkedList<String> ll = new LinkedList<>();
    //class constructor to set up the GUI and handle button events 
    public MusicPlayer() {
        JPanel upper = new JPanel();
        upper.setBounds(0, 0, 400, 150);
        
        setTitle("Audio Player");
        //add all the image buttons, slider to the layout and set their locations on the GUI
        upper.setLayout(new GridBagLayout());
        GridBagConstraints bag = new GridBagConstraints();
        bag.insets = new Insets(5, 5, 5, 5);
        bag.anchor = GridBagConstraints.WEST;
        open.setFont(new Font("Sans", Font.BOLD, 14));
        //adding images to the buttons 
        open.setIcon(new ImageIcon(getClass().getResource("images/Open.png")));
        play.setFont(new Font("Sans", Font.BOLD, 14));
        play.setIcon(new ImageIcon(getClass().getResource("images/Play.gif")));
        pause.setFont(new Font("Sans", Font.BOLD, 14));
        pause.setIcon(new ImageIcon(getClass().getResource("images/Pause.png")));
        slider.setPreferredSize(new Dimension(400, 20));
        slider.setEnabled(false);
        slider.setValue(0);
        bag.gridx = 0;
        bag.gridy = 0;
        bag.gridwidth = 3;
        upper.add(nameLbl, bag);
        bag.anchor = GridBagConstraints.CENTER;
        bag.gridy = 1;
        bag.gridwidth = 1;
        upper.add(timeLbl, bag);
        bag.gridx = 1;
        upper.add(slider, bag);
        bag.gridx = 2;
        upper.add(durLbl, bag);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btns.add(open);
        btns.add(play);
        btns.add(pause);
        bag.gridwidth = 3;
        bag.gridx = 0;
        bag.gridy = 2;
        upper.add(btns, bag);
        //setting action listener for buttons
        open.addActionListener(this);
        play.addActionListener(this);
        pause.addActionListener(this);
        JPanel p = new JPanel();
        p.setLayout(null);
        
        JButton add = new JButton("Add");
        add.setBounds(50, 150, 300, 25);
        p.add(add);
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        
        JButton prev = new JButton("Previous");
        prev.setBounds(50, 180, 150, 25);
        p.add(prev);
        prev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentSong > 0){
                    currentSong--;
                    list.setSelectedIndex(currentSong);
                    playBack();
                }
                else
                    JOptionPane.showMessageDialog(null, "First Song Selected!");
            }
        });
        JButton next = new JButton("Next");
        next.setBounds(200, 180, 150, 25);
        p.add(next);
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(currentSong < list.getModel().getSize()-1){
                    currentSong++;
                    list.setSelectedIndex(currentSong);
                    playBack();
                }
                else
                    JOptionPane.showMessageDialog(null, "End of Playlist!");
            }
        });
        
        JButton first = new JButton("First");
        first.setBounds(50, 210, 150, 25);
        p.add(first);
        first.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(list.getModel().getSize() > 0){
                    currentSong = 0;
                    list.setSelectedIndex(currentSong);
                    playBack();
                }
                else
                    JOptionPane.showMessageDialog(null, "No Songs in the List!");
            }
        });
        JButton last = new JButton("Last");
        last.setBounds(200, 210, 150, 25);
        p.add(last);
        last.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(list.getModel().getSize() > 0){
                    currentSong = list.getModel().getSize()-1;
                    list.setSelectedIndex(currentSong);
                    playBack();
                }
                else
                    JOptionPane.showMessageDialog(null, "No Songs in the List!");
            }
        });
        
        model = new DefaultListModel<>();
        list = new JList<>(model);
        
        list.setBounds(50, 240, 300, 200);  
        p.add(list);
        
        
        
        
        add(upper);
        add(p);
        //pack and dispaly the frame to the screen
        pack();
        setSize(400, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    //a separte method to handle the button clicks
    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        //if else conditions to get which button was clicked and then accordingly
        if (source instanceof JButton) {
            JButton button = (JButton) source;
            //call the openFile method if open button is clicked
            if (button == open) {
                openFile();
            }
            //play the msuic if play button is clicked
            else if (button == play) {
                if (!isPlaying) {
                    playBack();
                } else {
                    stopPlaying();
                }
            }
            //pause the music if pause button is clicked
            else if (button == pause) {
                if (!isPause) {
                    pausePlaying();
                } else {
                    resumePlaying();
                }
            }
        }
    }

    //method to read and load the audio file to the application
    private void openFile() {
        //fie choose so the use can select a file from the computer
        JFileChooser fc = null;
        if (lastOpenPath != null && !lastOpenPath.equals("")) {
            fc = new JFileChooser(lastOpenPath);
        } else {
            fc = new JFileChooser();
        }
        //get the WAV type audio file from the computer
        FileFilter wf = new FileFilter() {
            @Override
            public String getDescription() {
                return "Sound file (*.WAV)";
            }

            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    return file.getName().toLowerCase().endsWith(".wav");
                }
            }
        };
        fc.setFileFilter(wf);
        fc.setDialogTitle("Open Audio File");
        fc.setAcceptAllFileFilterUsed(false);

        //if the file was selecte,d
        int userChoice = fc.showOpenDialog(this);
        if (userChoice == JFileChooser.APPROVE_OPTION) {
            //get the file path as a string for future use
            audioFilePath = fc.getSelectedFile().getAbsolutePath();
            ll.addLast(audioFilePath);
            model.addElement(audioFilePath);
            list.setModel(model);
            //lastOpenPath = fc.getSelectedFile().getParent();
            //if the music is running, stop it and start this new file instead
            if (isPlaying || isPause) {
                stopPlaying();
                while (player.getAudioClip().isRunning()) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        
                    }
                }
            }
            //playBack();
        }
    }
    //method to keep playing the music
    private void playBack() {
        if (isPlaying || isPause) {
            stopPlaying();
            while (player.getAudioClip().isRunning()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {

                }
            }
        }
        //timer to show the music file time
        timer = new Timer(timeLbl, slider);
        timer.start();
        isPlaying = true;
        playbackThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    list.setSelectedIndex(currentSong);
                    //change the center button to play if the song is not playing 
                    //change the center button to pause if the song is playing currently
                    play.setText("Stop");
                    play.setIcon(new ImageIcon(getClass().getResource("images/Stop.gif")));
                    play.setEnabled(true);
                    pause.setText("Pause");
                    pause.setEnabled(true);
                    audioFilePath = list.getModel().getElementAt(currentSong);
                    player.load(audioFilePath);
                    timer.setClip(player.getAudioClip());
                    nameLbl.setText("Playing File: " + audioFilePath);
                    slider.setMaximum((int) player.getLength());
                    durLbl.setText(player.getLengthString());
                    player.play();
                    resetControls();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error. Cant play the file!");
                    resetControls();
                }
            }
        });

        playbackThread.start();
    }
    //method to stop the file from playing
    private void stopPlaying() {
        isPause = false;
        pause.setText("Pause");
        pause.setEnabled(false);
        timer.reset();
        timer.interrupt();
        player.stop();
        playbackThread.interrupt();
    }
    //method to pause the file from playing
    private void pausePlaying() {pause.setText("Resume");
        isPause = true;
        player.pause();
        timer.pauseTimer();
        playbackThread.interrupt();
    }
    //method to resume the file from playing
    private void resumePlaying() {
        pause.setText("Pause");
        isPause = false;
        player.resume();
        timer.resumeTimer();
        playbackThread.interrupt();
    }
    //method to reset all the media player the file from playing
    private void resetControls() {
        timer.reset();
        timer.interrupt();
        play.setText("Play");
        play.setIcon(new ImageIcon(getClass().getResource("images/Stop.gif")));
        pause.setEnabled(false);
        isPlaying = false;
    }
    
}
