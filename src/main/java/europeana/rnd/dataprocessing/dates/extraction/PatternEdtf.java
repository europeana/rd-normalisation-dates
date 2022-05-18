package europeana.rnd.dataprocessing.dates.extraction;

import java.text.ParseException;

import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.EdtfParser;
import europeana.rnd.dataprocessing.dates.edtf.TemporalEntity;

/**
 * The pattern for EDTF dates. Also compatible with ISO 8601 dates.
 */
public class PatternEdtf implements DateExtractor {
	EdtfParser parser = new EdtfParser();

	public Match extract(String inputValue) {
		try {
			TemporalEntity parsed = parser.parse(inputValue);
			parsed.removeTime();
			return new Match(MatchId.Edtf, inputValue, parsed);
		} catch (ParseException | NumberFormatException e) {
			return null;
		}
	}

}
