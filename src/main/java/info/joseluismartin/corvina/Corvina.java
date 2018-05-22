package info.joseluismartin.corvina;


import java.awt.EventQueue;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.ApplicationContextGuiFactory;
import org.numenta.nupic.network.Inference;
import org.numenta.nupic.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import info.joseluismartin.corvina.config.CorvinaConfig;
import info.joseluismartin.corvina.htm.CorvinaClassifier;
import info.joseluismartin.corvina.sensor.ImageSensor;
import info.joseluismartin.corvina.ui.MainFrame;
import rx.Subscriber;
import rx.Subscription;

/**
 * Application launcher. Load Spring context and start the Network.
 * 
 * @author Jose Luis Martin
 * @since 1.0
 */
public class Corvina extends Subscriber<Inference> implements Runnable {

	private static final Object LOCK = new Object();
	private static Log log = LogFactory.getLog(Corvina.class);

	private Network network;
	@Autowired 
	private MainFrame mainFrame;
	@Autowired 
	private ImageSensor imageSensor;
	private CorvinaClassifier classifier = new CorvinaClassifier();
	private CorvinaClassifier sparseClassifier = new CorvinaClassifier();
	private volatile long step;
	private volatile boolean running;
	private Executor executor = Executors.newSingleThreadExecutor();
	private volatile boolean infer;
	private Subscription networkSubscription;
	private boolean usingSDR = true;

	public void start() {
		this.running = true;
		executor.execute(this);
	}
	
	public void stop() {
		this.running = false;
	}

	@Override
	public void run() {
		if (this.network == null)
			return;
		
		while (this.running) {
			int[] input = this.imageSensor.getAsDense();
			
			if (input == null) {
				this.running = false;
				return;
			}
			
			long millis = System.currentTimeMillis();
			try {

				if (input.length  == this.network.getTail().getTail().getConnections().getNumInputs()) {
					this.network.compute(input);
					log.info("Step [" + step +"] " + (double)(System.currentTimeMillis() - millis) / 1000 + " seconds");
					this.step++;

					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							mainFrame.refresh();
						}
					});
					onNext(this.network.getHead().getHead().getInference());
				}
				else {
					log.warn("Skipping invalid input of lenght [" + input.length + "].");
				}
			} 
			catch (Throwable e) {
				log.error(e);
			}
		}
	}

	@Override
	public void onCompleted() {
		log.info("On Completed");

	}

	@Override
	public void onError(Throwable e) {
		log.info("On Error");
		log.error(e);
		// this.networkSubscription = this.network.observe().subscribe(this);
	}

	public void onStart() {
		log.info("On Start");
	//	this.mainFrame.refresh();
		
	}

	@Override
	public void onNext(Inference t) {
		log.info("On Next");
		log.info("Sparse Actives: " + Arrays.toString(t.getFeedForwardSparseActives()));
		log.info("SDR: " + Arrays.toString(t.getSDR()));

		int[] toClassify = this.usingSDR  ? t.getSDR() : t.getFeedForwardActiveColumns();
		String infered = this.classifier.compute(toClassify, this.imageSensor.getClassifierName(), infer);
		
		if (infered != null)
			log.info("Seeing :" + infered);
		
//		String sparseInfered = this.sparseClassifier.compute(t.getSparseActives(),this.imageSensor.getImageName(), this.infer);
//		
//		if (sparseInfered != null)
//			log.info("Sparse Seeing :" + sparseInfered);
//		
		if (this.infer) {
			log.info("Stats: " + this.classifier.getStatsString());//
//			log.info("Sparse stats: " + this.sparseClassifier.getStatsString());
		}
		
		try {
			SwingUtilities.invokeLater(() -> this.mainFrame.setHit(infered));
		} 
		catch (Exception e) {
			log.error(e);
		}
	}

	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * @return the infer
	 */
	public boolean isInfer() {
		return infer;
	}

	/**
	 * @param infer the infer to set
	 */
	public void setInfer(boolean infer) {
		this.infer = infer;
	}

	public CorvinaClassifier getClassifier() {
		return this.classifier;
	}

	/**
	 * @return the network
	 */
	public Network getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(Network network) {
		if (this.network != null && this.network != network) {
			if (this.networkSubscription != null)
				this.networkSubscription.unsubscribe();
		}
		
		this.network = network;
		this.network.close();
		// this.networkSubscription = this.network.observe().subscribe(this);
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * @return the usingSDR
	 */
	public boolean isUsingSDR() {
		return usingSDR;
	}

	/**
	 * @param usingSDR the usingSDR to set
	 */
	public void setUsingSDR(boolean usingSDR) {
		this.usingSDR = usingSDR;
	}

	
	public static void main(String[] args) {
		log.info("Starting corvina...");
		
		ApplicationContextGuiFactory.setPlasticLookAndFeel();
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CorvinaConfig.class);

		MainFrame main = ctx.getBean("mainFrame", MainFrame.class);

		// Start swing application on event thread
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
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
