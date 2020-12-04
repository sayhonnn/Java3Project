
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Scanner;
import javax.swing.*;

public class Server extends Thread {
    //class fields to be used later on in the class
    private static Socket socket = null;
    private static ServerSocket server = null;
    private static DataInputStream in = null;
    private static DataOutputStream out = null;
    private static JButton startServer= null;
    
    public static String user = "";
    public static String pass = "";
    public static JTextArea area = null;
    public static int port = 5000;
    public static Scanner scanner = new Scanner(System.in);
    
    //method for the thread to run and accept client's requests
    @Override
    public void run() {
        try {
            //start the server
            server = new ServerSocket(port);
            JOptionPane.showMessageDialog(null, "Server Started!");
            socket = server.accept();
            //initialise values to communicate with client
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            //loop to communicate continously
            while(true){
                String line = "";
                try {
                    //read user's msg
                    line = in.readUTF();
                    //disconnect, if user says to do so
                    if(line.equals("disonnect")){
                        startServer.setEnabled(true); 
                        break;  //break the loop and close the server
                    }
                    System.out.println(line);
                    //check if user id and pass are valid
                    if (authenticate(line)) {   //if valid, send success message to cliend
                        out.writeUTF("Access Granted!");
                    } else {    //send the error otherwise
                        out.writeUTF("Invalid Credentials!");
                    }
                } catch (IOException i) {
                    System.out.println(i);
                }
            }
            //close the server
            System.out.println("Closing connection");
            socket.close();
            in.close();
            out.close();
        } catch (IOException i) {
            System.out.println(i);
            scanner.nextLine();
        }
    }
    
    //main method to setup the gui
    public static void main(String s[]) {
        //create frame and add other components to it
        JFrame frame = new JFrame("Server");
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
        
        JButton signup = new JButton("Signup");
        signup.setBounds(50, 120, 100, 30);
        panel.add(signup);
        signup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!tf.getText().equals("") && !tf2.getText().equals("")){
                    //get the new user id and pass for signup
                    user = tf.getText();
                    pass = tf2.getText();
                    //display message in the area box after creating the new user
                    area.setText("User Created!\n");
                    area.append("User name: "+user+"\n");
                    area.append("Password: "+pass+"\n");
                    area.append("Hash: "+hash(pass)+"\n");
                    tf.setText("");
                    tf2.setText("");
                }
                else{
                    JOptionPane.showMessageDialog(null, "Username or Password cannot be empty!");
                }
            }
        });
        
        JLabel lbl4 = new JLabel("Path");
        lbl4.setBounds(310, 50, 100, 25);
        panel.add(lbl4);
        JTextField tf3 = new JTextField("127.0.0.1/5000");
        tf3.setEditable(false);
        tf3.setBounds(350, 50, 250, 25);
        panel.add(tf3);
        //button and action listener to start the server
        startServer = new JButton("Start Server");
        startServer.setBounds(350, 80, 120, 30);
        panel.add(startServer);
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //start the server when the button is clicked.
                Server server = new Server();
                server.start();
                startServer.setEnabled(false);
            }
        });
        //are box to handle the messages from server
        area = new JTextArea();
        area.setBounds(350, 140, 330, 200);
        panel.add(area);
        //display the panel to user on screen
        frame.add(panel);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    //method to generate hash of a string password usign MD5
    private static String hash(String pass) {
        try {
            MessageDigest dig = MessageDigest.getInstance("MD5");
            dig.update(pass.getBytes());
            byte[] bytes = dig.digest();
            String s = "";
            for (int i = 0; i < bytes.length; i++) {
                s += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            }
            return s;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    //mthod to check if user entered valid username and password or not
    private boolean authenticate(String msg) {
        String[] creds = msg.split(",");
        if (creds[0].equals(user) && creds[1].equals(hash(pass))) {
            return true;
        }
        return false;
    }
}
