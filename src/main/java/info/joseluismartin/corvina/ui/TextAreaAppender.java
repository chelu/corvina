package info.joseluismartin.corvina.ui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class TextAreaAppender extends AppenderSkeleton {
	private JTextArea textArea = null;
	private int maxSize = 100000;

	public TextAreaAppender(JTextArea textArea) {
		this.textArea = textArea;
		DefaultCaret caret = (DefaultCaret) this.textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	}

	@Override
	protected void append(LoggingEvent event) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					updateTextArea(event);

				}

			});
		} 
		catch (Exception e) {
		}

	}


	protected void updateTextArea(LoggingEvent event) {
		if (this.textArea != null)
			this.textArea.append(event.getMessage().toString() + "\n");

		String text = this.textArea.getText();
		if (text.length() > maxSize) {
			text = text.substring(maxSize - maxSize/10);
			this.textArea.setText(text);
		}

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
