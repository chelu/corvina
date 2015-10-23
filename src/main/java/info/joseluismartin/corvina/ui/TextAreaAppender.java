package info.joseluismartin.corvina.ui;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class TextAreaAppender extends AppenderSkeleton {
	private JTextArea textArea = null;

	public TextAreaAppender(JTextArea textArea) {
		this.textArea = textArea;
		DefaultCaret caret = (DefaultCaret) this.textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}
	
	@Override
	protected void append(LoggingEvent event) {
		if (this.textArea != null)
			this.textArea.append(event.getMessage().toString() + "\n");
	}


	@Override
	public void close() {
		
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}
	
	 public void setTextArea(JTextArea textArea) {
	        this.textArea = textArea;
	    }
}
