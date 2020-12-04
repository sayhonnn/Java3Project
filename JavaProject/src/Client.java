
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Scanner;
import javax.swing.*;

public class Client {

    //class fields to be used in the code later on
    private static Socket socket = null;
    private static Scanner sc = null;
    private static DataInputStream in = null;
    private static DataOutputStream out = null;
    private static JTextArea area3 = null;
    private static JButton reader = null, player = null, help = null;

    //construtor method to set up a client
    public Client(String address, int port) {
        try {
            //send a requet to connect to the server
            socket = new Socket(address, port);
            sc = new Scanner(System.in);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            area3.setText("Connected!\n");
        } catch (Exception ex) {
            //show error message if server is closed
            System.out.println(ex.getMessage());
            area3.setText("Server is not Running!");
        }
    }

    //main method to setup the GUI and other functionality
    public static void main(String s[]) {
        //create the GUI with its lable,s text areas and buttons
        JFrame frame = new JFrame("Client");
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        JLabel lbl = new JLabel("Username");
        lbl.setBounds(50, 50, 100, 25);
        panel.add(lbl);
        JTextField tf = new JTextField();
        tf.setBounds(120, 50, 150, 25);
        panel.add(tf);
        
        JLabel lbl2 = new JLabel("Password");
        lbl2.setBounds(50, 80, 100, 25);
        panel.add(lbl2);
        JTextField tf2 = new JTextField();
        tf2.setBounds(120, 80, 150, 25);
        panel.add(tf2);
        //login button and its action listener to handle button clicks
        JButton login = new JButton("Login");
        login.setBounds(50, 120, 100, 30);
        panel.add(login);
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if the login button is cliked, check the values entered by user
                if (!tf.getText().equals("") && !tf2.getText().equals("")) {
                    //read the username and pass
                    String user = tf.getText();
                    String pass = tf2.getText();
                    String msg = "";
                    try {
                        //pass the username and pass to the server
                        msg = user + "," + hash(pass);
                        out.writeUTF(msg);
                        msg = in.readUTF(); //read the server's response
                        area3.setText(msg); //and show the response in the area box on the client window
                    } catch (IOException i) {
                        System.out.println(i);
                    }
                    //set the reader and music player button enabled
                    tf.setText("");
                    tf2.setText("");
                    reader.setEnabled(true);
                    player.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Username or Password cannot be empty!");
                }
            }
        });
        //other frame components on the GUI
        JLabel lbl3 = new JLabel("Server's Feedback");
        lbl3.setBounds(50, 170, 150, 30);
        panel.add(lbl3);

        area3 = new JTextArea();
        area3.setEditable(false);
        area3.setBounds(50, 200, 250, 150);
        panel.add(area3);

        JLabel lbl4 = new JLabel("Path");
        lbl4.setBounds(370, 50, 100, 25);
        panel.add(lbl4);
        JTextField tf3 = new JTextField("127.0.0.1:5000");  //localhost and the port number. FIXED
        tf3.setEditable(false);
        tf3.setBounds(410, 50, 150, 25);
        panel.add(tf3);
        //buttons to connect and disconnect to the server
        JButton connect = new JButton("Connect");
        connect.setBounds(410, 80, 150, 50);
        panel.add(connect);
        JButton disconnect = new JButton("Disconnect");
        disconnect.setBounds(410, 140, 150, 50);
        panel.add(disconnect);
        //action listener for the connect button
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ///connect to the server on the port 5000
                Client client = new Client("127.0.0.1", 5000);
                connect.setEnabled(false);
                disconnect.setEnabled(true);
            }
        });
        //action listener for the disconnect button
        disconnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //send a request to server to disconnect
                    out.writeUTF("disconnect");
                    out.close();
                    socket.close(); //close the connection
                    //print the message in the area box
                    area3.setText("Disconnected!\n");
                    area3.append("Server Closed!\n");
                    connect.setEnabled(true);
                    disconnect.setEnabled(false);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        });
        //Other buttons toread the CSV file and play the music
        JLabel lbl5 = new JLabel("Login to access");
        lbl5.setBounds(370, 200, 150, 25);
        panel.add(lbl5);
        
        reader = new JButton("CSV Reader");
        reader.setEnabled(false);
        reader.setBounds(410, 230, 150, 50);
        panel.add(reader);
        //action listner for the csv reader button
        reader.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //call and open the SCVReader screen to read and display the data
                DataReader read = new DataReader();
                read.show();
            }
        });
        
        player = new JButton("Music Player");
        player.setEnabled(false);
        player.setBounds(410, 290, 150, 50);
        panel.add(player);
        //action listner for the music player button
        player.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //call and open the msuic playerReader screen to play the wav audio files
                new MusicPlayer().setVisible(true);
            }
        });
        help = new JButton("Help");
        help.setBounds(410, 350, 150, 50);
        panel.add(help);
        //action listner for the helpbutton
        help.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = "HOW TO RUN?\n";
                str += "1. Start the Server First of All\n";
                str += "2. Create a User with Username and PAssword\n";
                str += "3. Start the Client\n";
                str += "4. Click the Connect button to Connect to Server\n";
                str += "5. Fill in the Username and Password\n";
                str += "6. Click Login to Login to the Server\n";
                str += "7. Click CSVReader button to read and display the data from .csv file\n";
                str += "8. Click Music Player to play the songs\n";
                str += "9. Click Disconnect to disconnect from the Server and close the connection\n";
                JOptionPane.showMessageDialog(null, str);
            }
        });
        //add everything to the frame and show the frame to the user on screen
        frame.add(panel);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    //method to generat hashcode for a password using MD5 algorithm
    private static String hash(String pass) {
        try {
            //convert the string password to hash code using MD5
            MessageDigest dig = MessageDigest.getInstance("MD5");
            dig.update(pass.getBytes());
            byte[] bytes = dig.digest();
            String s = "";
            for (int i = 0; i < bytes.length; i++) {
                s += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            }
            return s;   //return the generateed hash
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
}
