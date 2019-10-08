package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.eclipse.jetty.server.Server;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

public class HackweekApplet {
  JFrame frame = new JFrame("Hackweek Applet");
  HackweekPanel topPanel = new HackweekPanel();
  LowerPanel lowerPanel = new LowerPanel();
  JPanel outerPanel;


  public HackweekApplet() {
    FlowLayout layout = new FlowLayout();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(layout);
    frame.setSize(300, 100);
    outerPanel = new JPanel(new BorderLayout());
    outerPanel.add(topPanel, BorderLayout.NORTH);
    outerPanel.add(lowerPanel, BorderLayout.CENTER);
    frame.add(outerPanel);
    frame.setVisible(true);
    frame.getContentPane().setBackground(Color.BLACK);
    frame.getContentPane().setPreferredSize(new Dimension(300, 150));
  }

  public void addLowerPanel(String text, ImageIcon icon) {
    lowerPanel.setTextAndIcon(text, icon);
    frame.pack();
    frame.revalidate();
    frame.repaint();
  }

  public class LowerPanel extends JPanel {
    JLabel nameLabel = new JLabel();
    JLabel appLabel = new JLabel();

    public LowerPanel() {
      nameLabel.setForeground(Color.WHITE);
      nameLabel.setVisible(false);
      this.add(nameLabel);

      appLabel.setVisible(false);
      this.add(appLabel);

      setOpaque(true);
      setBackground(Color.BLACK);
    }

    public void setTextAndIcon(String text, ImageIcon icon) {
      nameLabel.setText(text);
      appLabel.setIcon(icon);
      nameLabel.setVisible(true);
      appLabel.setVisible(true);
      frame.revalidate();
      frame.repaint();
    }
  }

  public class HackweekPanel extends JPanel {
    private BufferedImage intelliJImage;
    String imageFilePath = "src/main/resources/IntelliJ_IDEA_Logo.png";
    String[] NAMES = {"Samir", "Tami", "Ryan", "Jared", "Jack"};
    HashSet<String> namesSet = new HashSet<>(Arrays.asList(NAMES));
    JTextField textField = new JTextField(10);

    public HackweekPanel() {
      try {
        File file = new File(imageFilePath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        intelliJImage = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No intelliJImage");
      }

      JList namesList = new JList(NAMES);
      AutoCompleteDecorator.decorate(namesList, textField, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);
      textField.getDocument().addDocumentListener(new DocumentListener() {
        public void changedUpdate(DocumentEvent e) {
          checkNameAndRedrawIfPresent();
        }
        public void removeUpdate(DocumentEvent e) {
          checkNameAndRedrawIfPresent();
        }
        public void insertUpdate(DocumentEvent e) {
          checkNameAndRedrawIfPresent();
        }

        public void checkNameAndRedrawIfPresent() {
          String s = textField.getText();
          if(namesSet.contains(textField.getText())) {
            addLowerPanel(s, new ImageIcon(intelliJImage.getScaledInstance(50, 50, Image.SCALE_FAST)));
          }
        }
      });
      this.add(textField);

      JLabel picLabel = new JLabel(new ImageIcon(intelliJImage.getScaledInstance(50, 50, Image.SCALE_FAST)));
      this.add(picLabel);

      setOpaque(true);
      setBackground(Color.BLACK);
    }
  }

  public static void main(String[] args) {
    new HackweekApplet();
    Server server = new Server(8080);
    server.setHandler(new CommentListener());
    try {
      server.start();
      server.join();
    } catch(Exception e) {
      System.out.println(e);
    }
  }
}