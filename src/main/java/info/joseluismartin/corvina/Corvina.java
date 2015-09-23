package info.joseluismartin.corvina;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.ApplicationContextGuiFactory;
import org.jdal.swing.form.FormUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import info.joseluismartin.corvina.config.CorvinaConfig;

/**
 * Application launcher. Load Spring context and start the Network.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class Corvina {
	
	private static final Object LOCK = new Object();
	private static Log log = LogFactory.getLog(Corvina.class);
	
	public static void main(String[] args) {
		log.info("Starting corvina...");
		ApplicationContextGuiFactory.setPlasticLookAndFeel();
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CorvinaConfig.class);
		
		// Start swing application on event thread
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JFrame main = ctx.getBean("mainFrame", JFrame.class);
				main.setVisible(true);
			}
		});

		// wait for ever...
		synchronized (LOCK) {
			try {
				LOCK.wait();
			} catch (InterruptedException e) {
				log.info("Exiting.");
			}
		}
		
		ctx.close();
	}

}
