package info.joseluismartin.corvina.htm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rx.Subscriber;

public class LogSubscriber<T> extends Subscriber<T> {
	
	private static final Log log = LogFactory.getLog(LogSubscriber.class);
	
	@Override
	public void onCompleted() {
		log.info("COMPLETED");
		
	}

	@Override
	public void onError(Throwable e) {
		log.error(e);
	}

	@Override
	public void onNext(T t) {
		log.info("Next: " + t.toString());
	}

}
