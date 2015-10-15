package info.joseluismartin.corvina;


import java.awt.EventQueue;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.ApplicationContextGuiFactory;
import org.numenta.nupic.network.Inference;
import org.numenta.nupic.network.Network;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import info.joseluismartin.corvina.config.CorvinaConfig;
import info.joseluismartin.corvina.htm.LogSubscriber;
import info.joseluismartin.corvina.sensor.ImageSensor;

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
		
		Network network = ctx.getBean(Network.class);
		network.observe().subscribe(new LogSubscriber<Inference>());
		ImageSensor sensor = ctx.getBean(ImageSensor.class);

		// Start swing application on event thread
//		EventQueue.invokeLater(new Runnable() {
//			
//			@Override
//			public void run() {
//				JFrame main = ctx.getBean("mainFrame", JFrame.class);
//				main.setVisible(true);
//			}
//		});
		
		int[] data = sensor.getSdr();
		
		log.debug("input data length: " + data.length);
		
		network.compute(data);
		
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
