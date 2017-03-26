/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rpi_armit;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javax.swing.JTextField;

/**
 *
 * @author Thomas Coolidge
 */
public class RPI_ARMIT extends JApplet {

    private static final int JFXPANEL_WIDTH_INT = 800;
    private static final int JFXPANEL_HEIGHT_INT = 250;
    private static JFXPanel fxContainer;
    private static JFXPanel fxContainer2;
    private List<String> SiteList = new ArrayList<String>();
    private List<Boolean> SiteStatus = new ArrayList<Boolean>();
    private int position = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception e) {
                }

                JFrame frame = new JFrame("RPI Site Check Program");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                JApplet applet = new RPI_ARMIT();
                applet.init();

                frame.setContentPane(applet.getContentPane());

                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                applet.start();
            }
        });
    }

    @Override
    public void init() {

        fxContainer = new JFXPanel();
        fxContainer2 = new JFXPanel();
        fxContainer.setPreferredSize(new Dimension(JFXPANEL_WIDTH_INT, JFXPANEL_HEIGHT_INT));
        fxContainer2.setPreferredSize(new Dimension(JFXPANEL_WIDTH_INT, 30));
        add(fxContainer, BorderLayout.NORTH);
        add(fxContainer2, BorderLayout.SOUTH);
        
        GetSites();

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                TopRow();
                BottomRow();
            }
        });
    }

    private void TopRow() {
        HBox root = new HBox();
        root.setSpacing(10);

        Button btn1 = new Button();
        btn1.setText("Check Website");
        btn1.setPrefSize(150, 20);
        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                int size = SiteList.size();
                SiteStatus.clear();

                for (int i = 0; i < size; i++) {
                    SiteStatus.add(SiteCheck(SiteList.get(i)));
                    String status;
                    if (SiteStatus.get(i) == false) {
                        status = "status false";
                    } else {
                        status = "status true";
                    }
                    System.out.println(status);
                }
            }
        });
        Button btn2 = new Button();
        btn2.setText("Get Websites");
        btn2.setPrefSize(150, 20);
        btn2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                GetSites();
            }
        });
        Button btn3 = new Button();
        btn3.setText("Put Websites");
        btn3.setPrefSize(150, 20);
        btn3.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                PutSites();
            }
        });

        root.getChildren().addAll(btn1, btn2, btn3);
        fxContainer.setScene(new Scene(root));
    }
    
    private void BottomRow() {
        HBox root = new HBox();
        root.setSpacing(10);
        TextField userText = new TextField();

        Button btn1 = new Button();
        btn1.setText("Add Website");
        btn1.setPrefSize(150, 20);
        btn1.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                SiteList.add(userText.getText());
                userText.clear();
            }
        });
        Button btn2 = new Button();
        btn2.setText("Back");
        btn2.setPrefSize(150, 20);
        btn2.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (position == 0)
                {
                    SiteList.get(position);
                }
                else
                {
                    position -= 1;
                    userText.setText(SiteList.get(position));
                }
                
            }
        });
        Button btn3 = new Button();
        btn3.setText("Forward");
        btn3.setPrefSize(150, 20);
        btn3.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if(position == SiteList.size() - 1)
                {
                    userText.setText(SiteList.get(position));
                }
                else
                {
                    position += 1;
                    userText.setText(SiteList.get(position));
                }
            }
        });
        
        Button btn4 = new Button();
        btn4.setText("Delete Website");
        btn4.setPrefSize(150, 20);
        btn4.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                SiteList.remove(position);
                userText.clear();
            }
        });
        
        root.getChildren().addAll(btn1, btn2, btn3, userText, btn4);
        fxContainer2.setScene(new Scene(root));
    }
    

    public static boolean SiteCheck(String host) {
        int port = 8089;
        
    try (Socket socket = new Socket()) {
        socket.connect(new InetSocketAddress(host, port));
        return true;
    } catch (IOException e) {
        return false; // Either timeout or unreachable or failed DNS lookup.
    }
}

    public void GetSites() {
        FileReader in = null;
        String site;
        SiteList.clear();

        try {
            File file = new File("SiteList.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            while ((site = bufferedReader.readLine()) != null) {
                stringBuffer.append(site);
                SiteList.add(site);
                System.out.println(site);
            }
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void PutSites() {
        FileWriter out = null;

        try {
            out = new FileWriter("SiteList.txt");
        } catch (IOException ex) {
            Logger.getLogger(RPI_ARMIT.class.getName()).log(Level.SEVERE, null, ex);

            System.out.println("Unable to write file");
            return;
        }

        int size = SiteList.size();

        for (int i = 0; i < size; i++) {
            try {
                out.write(SiteList.get(i));
                out.write("\n");
            } catch (IOException ex) {
                Logger.getLogger(RPI_ARMIT.class.getName()).log(Level.SEVERE, null, ex);

                System.out.println("Unable to write site to file");

                return;
            }
        }

        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(RPI_ARMIT.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
