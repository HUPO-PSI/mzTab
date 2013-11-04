package uk.ac.ebi.pride.tools.mztab_exporter.exporter;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.pride.tools.mztab_exporter.exporter.impl.PrideMzTabExporter;

public class ExporterFactory {
	/**
	 * Supported FileTypes by the exporter.
	 * @author jg
	 *
	 */
	public enum FileType {
		PRIDE_XML("pride");
		
		private String name;
		
		private FileType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public static List<String> getSupportedNames() {
			ArrayList<String> names = new ArrayList<String>();
			
			for (FileType type : values())
				names.add(type.getName());
			
			return names;
		}
		
		/**
		 * Returns the file type for the given name or
		 * null in case the file type doesn't exist.
		 * @param name
		 * @return
		 */
		public static FileType getFileType(String name) {
			for (FileType type : values()) {
				if (type.getName().equalsIgnoreCase(name))
					return type;
			}
			
			return null;
		}
	};
	
	/**
	 * There shouldn't be any instances
	 * of this class.
	 */
	private ExporterFactory() {
		
	}
	
	public static MzTabExporter getExporter(FileType filetype) throws Exception {
		switch (filetype) {
			case PRIDE_XML:
				return new PrideMzTabExporter();
			default:
				throw new Exception("Unknown file type '" + filetype.getName() + "'");
		}
	}
}
