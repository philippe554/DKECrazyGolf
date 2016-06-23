package CrazyGolf.Menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.*;

public class SplashScreen extends JWindow {

    JLabel imageLabel = new JLabel();
    JPanel southPanel = new JPanel();
    //    JProgressBar progressBar = new JProgressBar();
    ImageIcon imageIcon;
    Color bluepic = new Color(77, 151,154);
    Color darkpic = new Color(63, 123,126);

    public SplashScreen(ImageIcon imageIcon) {
        this.imageIcon = imageIcon;
        try {
            jbInit();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    // note - this class created with JBuilder
    void jbInit() throws Exception {
        imageLabel.setIcon(imageIcon);
        this.getContentPane().setLayout(new BorderLayout());
        southPanel.setLayout(new FlowLayout());
        southPanel.setBackground(Color.WHITE);
        this.getContentPane().add(imageLabel, BorderLayout.CENTER);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);
//        southPanel.add(progressBar);
        this.pack();
    }

//    public void setProgressMax(int maxProgress)
//    {
//        progressBar.setMaximum(maxProgress);
//    }

//    public void setProgress(int progress)
//    {
//        final int theProgress = progress;
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                progressBar.setValue(theProgress);
//            }
//        });
//    }
//
//    public void setProgress(String message, int progress)
//    {
//        final int theProgress = progress;
//        final String theMessage = message;
//        setProgress(progress);
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                progressBar.setValue(theProgress);
//                setMessage(theMessage);
//            }
//        });
//    }

    public void setScreenVisible(boolean b)
    {
        final boolean boo = b;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(boo);
            }
        });
    }

//    private void setMessage(String message)
//    {
//        if (message==null)
//        {
//            message = "";
//            progressBar.setStringPainted(true);
//            progressBar.setForeground(Color.BLACK);
//        }
//        else
//        {
//            progressBar.setStringPainted(true);
//            progressBar.setForeground(bluepic);
//            progressBar.setSize(200,5);
//            progressBar.setFont(new Font("Calibri",Font.PLAIN, 11));
//        }
//        progressBar.setString(message);
//    }
}
