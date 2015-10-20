package info.joseluismartin.corvina.htm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.numenta.nupic.network.Inference;
import org.numenta.nupic.network.Network;

import info.joseluismartin.corvina.sensor.ImageSensor;
import info.joseluismartin.corvina.ui.MainFrame;
import rx.Subscriber;

public class ImageSensorSubscriber extends Subscriber<Inference> {
	
	private static final Log log = LogFactory.getLog(ImageSensorSubscriber.class);
	
	private ImageSensor imageSensor;
	private Network network;
	private MainFrame mainFrame;
	
	public ImageSensorSubscriber(ImageSensor imageSensor, Network network, 
			MainFrame mainFrame) {
		this.imageSensor = imageSensor;
		this.network = network;
		this.mainFrame = mainFrame;
	}
	
	@Override
	public void onCompleted() {
		log.info("On completed");
	}

	@Override
	public void onError(Throwable e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNext(Inference inference) {
		log.info("OnNext");
		log.info(inference.getSDR());
		this.mainFrame.refresh();
		this.network.compute(this.imageSensor.getAsDense());
	}

}
