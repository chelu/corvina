package info.joseluismartin.corvina.sensor;

import java.io.File;

public class FileNameGenerator  implements NameGenerator {

	@Override
	public String createName(File file) {
		return file.getName();
	}

	@Override
	public String toString() {
		return "File Name";
	}
}
