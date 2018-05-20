package info.joseluismartin.corvina.ui;

import java.awt.BorderLayout;

import org.jdal.swing.SimpleDialog;
import org.numenta.nupic.network.Network;

/**
 * a JDialog to edit Networks.
 * 
 * @author Jose Luis Martin.
 * @since 1.1
 */
public class NetworkDialog extends SimpleDialog {

	private NetworkEditor editor;
	
	public NetworkDialog(String name, NetworkEditor editor) {
		super(null, name);
		this.editor = editor;
		setLayout(new BorderLayout());
		add(editor.getPanel(), BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}

	public Network createNetwork() {
		return this.editor.createNetwork();
	}

}
