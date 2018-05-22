package info.joseluismartin.corvina.sensor;

import java.io.File;

public class DirectoryNameGenerator implements NameGenerator {

	@Override
	public String createName(File file) {
		return file.getParentFile().getName();
	}

	@Override
	public String toString() {
		return "Directory Name";
	}
}
