package info.joseluismartin.corvina;


import java.awt.EventQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.ApplicationContextGuiFactory;
import org.numenta.nupic.network.Inference;
import org.numenta.nupic.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import info.joseluismartin.corvina.config.CorvinaConfig;
import info.joseluismartin.corvina.sensor.ImageSensor;
import info.joseluismartin.corvina.ui.MainFrame;
import rx.Subscriber;

/**
 * Application launcher. Load Spring context and start the Network.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class Corvina extends Subscriber<Inference> {
	
	private static final Object LOCK = new Object();
	private static Log log = LogFactory.getLog(Corvina.class);
	
	@Autowired
	private Network network;
	@Autowired 
	private MainFrame mainFrame;
	@Autowired ImageSensor imageSensor;
	
	private volatile boolean running = true;
	
	public static void main(String[] args) {
		log.info("Starting corvina...");
		ApplicationContextGuiFactory.setPlasticLookAndFeel();
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CorvinaConfig.class);
		
		Network network = ctx.getBean(Network.class);
		MainFrame main = ctx.getBean("mainFrame", MainFrame.class);
		Corvina corvina = ctx.getBean(Corvina.class);
		network.observe().subscribe(corvina);
		ImageSensor sensor = ctx.getBean(ImageSensor.class);
		
		// Start swing application on event thread
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				main.setVisible(true);
			}
		});
		
		network.compute(sensor.getSdr());
		
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

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Throwable e) {
		// TODO Auto-generated method stub
		
	}
	
	public void onStart() {
		log.info("On Start");
	}

	@Override
	public void onNext(Inference t) {
		log.info("On Next");
		this.mainFrame.refresh();
		if (running) {
			this.network.compute(this.imageSensor.getSdr());
		}
	}

}
