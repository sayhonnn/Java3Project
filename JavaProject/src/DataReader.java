
import java.io.FileReader;
import com.opencsv.CSVReader;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DataReader {

    public static JTable table = null;
    
    //method to show the dtable populated with data
    public void show() {
        //frame to add data and table in to it
        JFrame f = new JFrame();
        String data[][] = new String[100][3];
        CSVReader reader = null;
        int counter = 0;
        //read the csv file using third party library
        try {
            reader = new CSVReader(new FileReader("input.csv"));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                for (int a = 0; a < nextLine.length; a++) {
                    data[counter][a] = nextLine[a]; //addall the data to the data string 2D array 
                }
                counter++;
            }
            //put the table into a panel
            JPanel p = new JPanel();
            p.setBounds(0, 0, 500, 400);
            p.setLayout(null);
            //add columns to the table
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("First Name"); 
            model.addColumn("Last Name"); 
            model.addColumn("Email");
            table = new JTable(model);
            table.setBounds(0, 0, 500, 300);
            p.add(table);   //add the table to the panel
            //add the file data to the table rows
            for (int a = 0; a < counter; a++) {
                model.addRow(new Object[]{data[a][0], data[a][1], data[a][2]});
            }
            //another panel for the sort and search buttons
            JPanel p2 = new JPanel(new FlowLayout());
            p2.setBounds(0, 300, 500, 100);
            //sort button and its action listener
            JButton sort = new JButton("Bubble Sort");
            p2.add(sort);
            sort.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //get the number of rows in the data
                    int n = 0;
                    for (int i = 0; i < data.length; i++) {
                        if(data[i][0] != null){
                            n++;
                        }
                    }
                    //sort the array using bubble sort
                    for (int i = 0; i < n; i++) {
                        for (int j = 1; j < (n - i); j++) {
                            if (data[j - 1][0].compareTo(data[j][0]) > 0) { //comparing the first names to sort
                                //swapping data rows
                                String[] temp = data[j - 1];
                                data[j - 1] = data[j];
                                data[j] = temp;
                            }
                        }
                    }
                    //remove the old unsorted data from the table
                    for (int a = 0; a < model.getRowCount(); a++) {
                        model.removeRow(a);
                        a--;
                    }
                    //add new data to the table
                    for (int a = 0; a < n; a++) {
                        model.addRow(new Object[]{data[a][0], data[a][1], data[a][2]});
                    }
                }
            });
            //text input field and button for the seach functionality
            JTextField tf = new JTextField(15);
            p2.add(tf);
            JButton search = new JButton("Search");
            p2.add(search);
            search.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!tf.getText().equals("")) {
                        //get the  keyword from the user
                        String result = "";
                        String keyword = tf.getText();
                        //search the keyword in all the data 
                        for (int a = 0; a < data.length; a++) {
                            if (data[a][0] != null) {
                                if (data[a][0].contains(keyword) || data[a][1].contains(keyword)
                                        || data[a][2].contains(keyword)) {
                                    //if found the keyword, add those rows to a string result
                                    result += "FirstName: " + data[a][0] + ", Last Name: " + data[a][1] + ", Email: " + data[a][2] + "\n";
                                }
                            }
                        }
                        //if result doesnt have any dta, print the error msg
                        if (result.equals("")) {
                            JOptionPane.showMessageDialog(null, "Not Found!");
                        }   //if result string has something found, show it 
                        else {
                            JOptionPane.showMessageDialog(null, result);
                        }
                    }
                }
            });
            //add both the panels to the frame and display the frame to the user on screen
            p.add(p2);
            f.add(p);
            f.setSize(500, 450);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "File Not Found!");
        }

    }
}
