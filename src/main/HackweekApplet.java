package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;
import org.eclipse.jetty.server.Server;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class HackweekApplet {
  JFrame frame = new JFrame("");
  LowerHackweekPanel lowerPanel;
  UpperHackweekPanel upperPanel;
  JPanel outerPanel;
  String mainUser;
  public boolean isDarkMode;

  public HackweekApplet(String mainUser, boolean isDarkMode) {
    Color background = isDarkMode ? Color.BLACK : Color.WHITE;
    this.mainUser = mainUser;
    this.isDarkMode = isDarkMode;
    lowerPanel = new LowerHackweekPanel(this);
    upperPanel = new UpperHackweekPanel(this);
    FlowLayout layout = new FlowLayout();
    frame.setLocationRelativeTo(null);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(layout);
    frame.setSize(300, 100);
    outerPanel = new JPanel(new BorderLayout());
    outerPanel.add(upperPanel, BorderLayout.NORTH);
    outerPanel.add(new JSeparator(SwingConstants.VERTICAL));
    outerPanel.add(lowerPanel, BorderLayout.CENTER);
    frame.add(outerPanel);
    frame.setVisible(true);
    frame.getContentPane().setBackground(background);
    frame.pack();
  }

  public void redrawNameAndFace(String name) {
    lowerPanel.redrawNameAndFace(name);
  }

  public void notificationReceived(String filePath) {
    upperPanel.setNotificationReceived(filePath);
  }

  public class UpperHackweekPanel extends JPanel {
    private HackweekApplet applet;
    String[] NAMES; // add samir and remove current user if changing users
    JComboBox comboBox;
    private BufferedImage currentUserIcon;
    String currentUserIconPath; // Change this to use a different user
    private BufferedImage currentUserNotificationIcon;
    String currentUserIconNotificationPath; // Change this too for new user
    JLabel userIconLabel;
    MouseListener ml;
    String fileToOpen = "src/main/HackweekApplet.java";

    public UpperHackweekPanel(HackweekApplet applet) {
      this.applet = applet;
      Color backgroundColor = applet.isDarkMode ? Color.BLACK : Color.WHITE;

      if(applet.mainUser.equals("Samir")) {
        NAMES = new String[]{ "Ryan", "Tami", "Jared", "Jack"};
        currentUserIconPath = "src/main/resources/samir.png";
        currentUserIconNotificationPath = "src/main/resources/samir-notification.png";
      } else {
        NAMES = new String[]{ "Samir", "Tami", "Jared", "Jack"};
        currentUserIconPath = "src/main/resources/ryan.png";
        currentUserIconNotificationPath = "src/main/resources/ryan-notification.png";
      }

       comboBox = new JComboBox(NAMES);

      try {
        File file = new File(currentUserIconPath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        currentUserIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No appIcon");
      }

      try {
        File file = new File(currentUserIconNotificationPath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        currentUserNotificationIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No appIcon");
      }

      AutoCompleteDecorator.decorate(comboBox);
      comboBox.setPrototypeDisplayValue("Please search here.       ");
      for (Component component : comboBox.getComponents()) {
        if (component instanceof JButton) {
          comboBox.remove(component);
        }
      }
      comboBox.setRenderer(new ComboBoxRenderer());
      comboBox.setEditable(true);
      comboBox.addActionListener (e -> {
        String selectedValue = comboBox.getSelectedItem().toString();
        applet.redrawNameAndFace(selectedValue);
      });
      comboBox.setSelectedIndex(0);
      add(comboBox);

      userIconLabel = new JLabel(new ImageIcon(currentUserIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      this.add(userIconLabel);

      setOpaque(true);
      setBackground(backgroundColor);

      ml = new MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
          userIconLabel.setIcon(new ImageIcon(currentUserIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
          File file = new File(fileToOpen);
          try {
            Desktop.getDesktop().open(file);
          } catch (IOException e) {
            e.printStackTrace();
          }
          userIconLabel.removeMouseListener(ml);
        }
      };
      //setNotificationReceived("");
      setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.DARK_GRAY));
    }

    public void setNotificationReceived(String filePath) {
      userIconLabel.setIcon(new ImageIcon(currentUserNotificationIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      fileToOpen = filePath;
      userIconLabel.addMouseListener(ml);
    }

    public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
      private JLabel label = new JLabel();
      private JPanel panel = new JPanel();

      public ComboBoxRenderer() {

        label.setOpaque(false);
        label.setFont(new Font("Helvetica Neue", Font.PLAIN, 12));
        label.setForeground(Color.BLACK);

        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.add(label);
      }

      @Override
      public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.toString());
        return this;
      }
    }
  }

  public static class LowerHackweekPanel extends JPanel {
    private BufferedImage appIcon;
    private BufferedImage userIcon;
    private BufferedImage phoneIcon;
    private BufferedImage messageIcon;
    String messageIconPath;
    String phoneImagePath;
    String imageFilePath = "src/main/resources/IntelliJ_IDEA_Logo.png";
    String userIconPath = "src/main/resources/jared.png";
    HashMap<String, String> namesToImagePaths = new HashMap<>();
    HashMap<String, String> namesToAppPaths = new HashMap<>();
    JLabel userIconLabel;
    JLabel usernameLabel;
    JLabel appIconLabel;
    HackweekApplet applet;

    public LowerHackweekPanel(HackweekApplet app) {
      applet = app;

      messageIconPath = applet.isDarkMode ? "src/main/resources/message_dark_mode.png" :
              "src/main/resources/message_icon.png";
      phoneImagePath = applet.isDarkMode ? "src/main/resources/phone-icon-dark-mode.png" :
              "src/main/resources/phone-icon.png";
      Color background_color = applet.isDarkMode ? Color.BLACK : Color.WHITE;

      initializeNamesToImagesMap();
      initializeNamesToAppsMap();

      try {
        File file = new File(imageFilePath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        appIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No appIcon");
      }

      try {
        File file = new File(userIconPath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        userIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No user icon");
      }

      try {
        File file = new File(phoneImagePath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        phoneIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No user icon");
      }

      try {
        File file = new File(messageIconPath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        messageIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No user icon");
      }

      userIconLabel = new JLabel(new ImageIcon(userIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      this.add(userIconLabel);

      usernameLabel = new JLabel("Jared");
      usernameLabel.setFont(new Font("Helvetica Neue", Font.PLAIN, 16));
      Color userNameColor = applet.isDarkMode ? Color.WHITE : Color.BLACK;
      usernameLabel.setForeground(userNameColor);
      //usernameLabel.setBorder(new MatteBorder(0, 0, 1, 0, Color.BLACK));
      usernameLabel.setMinimumSize(new Dimension(75, 20));
      usernameLabel.setPreferredSize(new Dimension(75, 20));
      usernameLabel.setMaximumSize(new Dimension(75, 20));
      add(usernameLabel);

      JLabel messageLabel = new JLabel(new ImageIcon(messageIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      add(messageLabel);

      JLabel phoneLabel = new JLabel(new ImageIcon(phoneIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      add(phoneLabel);

      appIconLabel = new JLabel(new ImageIcon(appIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      add(appIconLabel);
      //add(new JSeparator(SwingConstants.VERTICAL));

      setOpaque(true);
      setBackground(background_color);
    }

    public void initializeNamesToImagesMap() {
      namesToImagePaths.put("Samir", "src/main/resources/samir.png");
      namesToImagePaths.put("Jared", "src/main/resources/jared.png");
      namesToImagePaths.put("Tami", "src/main/resources/tami.png");
      namesToImagePaths.put("Ryan", "src/main/resources/ryan.png");
      namesToImagePaths.put("Jack", "src/main/resources/jack.png");
    }

    public void initializeNamesToAppsMap() {
      namesToAppPaths.put("Samir", "src/main/resources/IntelliJ_IDEA_Logo.png");
      namesToAppPaths.put("Jared", "src/main/resources/IntelliJ_IDEA_Logo.png");
      namesToAppPaths.put("Tami", "src/main/resources/rubymine-icon.png");
      namesToAppPaths.put("Ryan", "src/main/resources/IntelliJ_IDEA_Logo.png");
      namesToAppPaths.put("Jack", "src/main/resources/sublime_icon.png");
    }

    public void redrawNameAndFace(String name) {
      String imagePath = namesToImagePaths.get(name);
      try {
        File file = new File(imagePath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        userIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No image");
      }

      String appPath = namesToAppPaths.get(name);
      try {
        File file = new File(appPath);
        if(!file.exists()) {
          System.err.println("my file is not there, I was looking at " + file.getAbsolutePath());
        }
        appIcon = ImageIO.read(file);
      } catch (IOException ex) {
        System.out.println("No image");
      }

      userIconLabel.setIcon(new ImageIcon(userIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      appIconLabel.setIcon(new ImageIcon(appIcon.getScaledInstance(30, 30, Image.SCALE_FAST)));
      usernameLabel.setText(name);
    }
  }

  public static void main(String[] args) {
    HackweekApplet applet;
    Server server;
    String mainUser;
    boolean isDarkMode = false;

    if(args.length == 0 || args[0].equals("8080")) {
      server = new Server(8080);
      mainUser = "Samir";
    } else {
      server = new Server(8081);
      mainUser = "Ryan";
    }

    if (args.length == 2 && args[1].equalsIgnoreCase("darkmode")) {
      isDarkMode = true;
    }

    applet = new HackweekApplet(mainUser, isDarkMode);

    server.setHandler(new CommentListener(applet));
    try {
      server.start();
      server.join();
    } catch(Exception e) {
      System.out.println(e);
    }
  }
}
