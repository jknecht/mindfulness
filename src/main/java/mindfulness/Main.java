package mindfulness;

import java.awt.AWTException;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class Main {
	private static final long SECONDS = 1000;
	private static final long MINUTES = 60 * SECONDS;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws AWTException 
	 */
	public static void main(String[] args) throws IOException, AWTException {
		final Preferences userPrefs = Preferences.userRoot().node("mindfulness");
		final long delay = userPrefs.getLong("delay", 15);
		final Reminder reminder = new Reminder(delay * MINUTES);
		
		if (SystemTray.isSupported()) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
			
			SystemTray systemTray = SystemTray.getSystemTray();
			BufferedImage image = ImageIO.read(Main.class.getResourceAsStream("/singing-bowl-icon.png"));
			String tooltip = "Be mindful";
			
			PopupMenu popup = new PopupMenu();
			
			TrayIcon icon = new TrayIcon(image, tooltip, popup);
			icon.setImageAutoSize(true);

			MenuItem configureMenu = new MenuItem("Configure");
			configureMenu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					final JFrame frame = new JFrame("Mindfulness Configuration");
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					
					
					final JTextField delayField = new JTextField(String.valueOf(userPrefs.getLong("delay", 15)));
					
					JButton okButton = new JButton(new AbstractAction("OK") {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								final long value = Long.parseLong(delayField.getText());
								SwingUtilities.invokeLater(new Runnable() {
									
									@Override
									public void run() {
										userPrefs.putLong("delay", value);
										reminder.stop();
										reminder.setDelay(value * MINUTES);
										Thread t = new Thread(reminder);
										t.start();
									}
								});
								
							} catch (NumberFormatException nfe) {
								nfe.printStackTrace();
							}
							frame.dispose();
						}
					});

					JButton closeButton = new JButton(new AbstractAction("Cancel") {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							frame.dispose();
						}
					});

					JLabel label = new JLabel("Delay (minutes): ");
					
					JPanel panel = new JPanel();
					SpringLayout layout = new SpringLayout();
					panel.setLayout(layout);

					panel.add(label);
					panel.add(delayField);
					panel.add(okButton);
					panel.add(closeButton);
					

					layout.putConstraint(SpringLayout.WEST, label, 5, SpringLayout.WEST, panel);
					layout.putConstraint(SpringLayout.NORTH, label, 5, SpringLayout.NORTH, panel);

					layout.putConstraint(SpringLayout.WEST, delayField, 5, SpringLayout.EAST, label);
					layout.putConstraint(SpringLayout.EAST, delayField, -5, SpringLayout.WEST, okButton);
					layout.putConstraint(SpringLayout.BASELINE, delayField, 0, SpringLayout.BASELINE, label);

					layout.putConstraint(SpringLayout.EAST, okButton, -5, SpringLayout.WEST, closeButton);
					layout.putConstraint(SpringLayout.BASELINE, okButton, 0, SpringLayout.BASELINE, label);

					layout.putConstraint(SpringLayout.EAST, closeButton, -5, SpringLayout.EAST, panel);
					layout.putConstraint(SpringLayout.BASELINE, closeButton, 0, SpringLayout.BASELINE, label);


					panel.setPreferredSize(new Dimension(300, 30));
					frame.getContentPane().add(panel);
					frame.pack();
					
					frame.setVisible(true);
				}
			});
			popup.add(configureMenu);
			
			
			
			MenuItem quitMenu = new MenuItem("Quit");
			quitMenu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					reminder.stop();
					System.exit(0);
				}
			});
			popup.add(quitMenu);
			
			

			
			
			systemTray.add(icon);
		}
		
		Thread t = new Thread(reminder);
		t.start();
	}

}
