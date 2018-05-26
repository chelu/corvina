package info.joseluismartin.corvina;


import java.awt.EventQueue;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdal.swing.ApplicationContextGuiFactory;
import org.numenta.nupic.algorithms.CLAClassifier;
import org.numenta.nupic.algorithms.Classification;
import org.numenta.nupic.network.Inference;
import org.numenta.nupic.network.Network;
import org.numenta.nupic.util.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import info.joseluismartin.corvina.config.CorvinaConfig;
import info.joseluismartin.corvina.htm.ClassifierResult;
import info.joseluismartin.corvina.sensor.ImageSensor;
import info.joseluismartin.corvina.ui.MainFrame;
import rx.Subscriber;
import rx.Subscription;

/**
 * Application launcher. Load Spring context and start and control the Network.
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
	private CLAClassifier classifier = new CLAClassifier();
	private volatile int step;
	private volatile boolean running;
	private Executor executor = Executors.newSingleThreadExecutor();
	private volatile boolean infer;
	private Subscription networkSubscription;
	private boolean usingSDR = true;
	private Map<Object, ClassifierResult> stats = new HashMap<>();
	private int[] lastInput;

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
			
//			if (Arrays.equals(input, this.lastInput))
//				log.warn("SAME INPUT");
//			
			this.lastInput = input;

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
		log.error(e);
	}

	public void onStart() {
		log.info("On Start");

	}

	@Override
	public void onNext(Inference t) {
		log.info("On Next");
		// log.info("Sparse Actives: " + Arrays.toString(t.getFeedForwardSparseActives()));
		// log.info("SDR: " + Arrays.toString(t.getSDR()));
		

		Map<String, Object> classification = new HashedMap<>();
		classification.put("bucketIdx", this.imageSensor.getBucketIdx());
		classification.put("actValue", this.imageSensor.getClassifierName());
		int[] toClassify = this.usingSDR  ? t.getSDR() : t.getFeedForwardActiveColumns();
		// Ensure that array is sparse, note that inference don't update sparses well.
		if (!ArrayUtils.isSparse(toClassify))
			toClassify = ArrayUtils.where(toClassify, ArrayUtils.WHERE_1);
		log.info("Sparse Actives:" + Arrays.toString(toClassify));
		// Do clasification
		Classification<String> infered = 
				this.classifier.compute(this.step, classification, toClassify, network.isLearn(), this.infer);

		if (infered != null)
			log.info("Seeing :" + infered.getMostProbableValue(1));

		if (this.infer) {
			Object real = this.imageSensor.getClassifierName();
			Object predicted = infered.getMostProbableValue(1);

			if (real.equals(predicted)) { 
				addHit(real);
			}
			else {
				addWrong(real);
			}
			log.info("Stats: " + stats.get(real).toString());
		}

		try {
			SwingUtilities.invokeLater(() -> this.mainFrame.setHit(infered.getMostProbableValue(1)));
		} 
		catch (Exception e) {
			log.error(e);
		}
	}

	private void addWrong(Object real) {
		if (!this.stats.containsKey(real))
			this.stats.put(real, new ClassifierResult(real.toString()));

		ClassifierResult result = this.stats.get(real);
		result.addWrong();
		result.addStep();

	}

	private void addHit(Object real) {
		if (!this.stats.containsKey(real))
			this.stats.put(real, new ClassifierResult(real.toString()));

		ClassifierResult result = this.stats.get(real);
		result.addHit();
		result.addStep();

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

	public CLAClassifier getClassifier() {
		return this.classifier;
	}
	
	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(CLAClassifier classifier) {
		this.classifier = classifier;
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

	public String getReport() {
		StringBuffer sb = new StringBuffer();

		for (ClassifierResult r : this.stats.values()) {
			sb.append(r.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	public Map<Object, ClassifierResult> getStats() {
		return this.stats;
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
