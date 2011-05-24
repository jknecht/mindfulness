package mindfulness;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Reminder implements Runnable {
	private long delay;
	private boolean running;
	private Thread myThread;
	
	public Reminder(long delay) {
		this.delay = delay;
	}

	@Override
	public void run() {
		myThread = Thread.currentThread();
		this.running = true;
		while (running) {
			try {
				myThread.sleep(delay);
			} catch (InterruptedException e) {
				break;
			}
			
			try {
				final AudioInputStream in = AudioSystem.getAudioInputStream(Reminder.class.getResourceAsStream("/singing_bowl.wav"));
				final Clip clip = AudioSystem.getClip();
				clip.addLineListener(new LineListener() {
					
					@Override
					public void update(LineEvent event) {
						if (event.getType().equals(Type.STOP)) {
							clip.close();
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
					}
				});
				
				clip.open(in);
				clip.start();
				
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void stop() {
		this.running = false;
		myThread.interrupt();
	}
	
}
