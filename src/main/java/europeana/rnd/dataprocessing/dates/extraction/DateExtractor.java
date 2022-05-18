package europeana.rnd.dataprocessing.dates.extraction;

import europeana.rnd.dataprocessing.dates.Match;

/**
 * The interface for all the implementation of date patterns
 *
 */
public interface DateExtractor {
	
	public Match extract(String inputValue);
	
}
