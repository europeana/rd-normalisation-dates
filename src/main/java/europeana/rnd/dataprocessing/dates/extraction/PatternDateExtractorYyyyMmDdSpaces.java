package europeana.rnd.dataprocessing.dates.extraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.Date;
import europeana.rnd.dataprocessing.dates.edtf.Instant;

/**
 * A complete date using whitespace to separate the date components.
 */
public class PatternDateExtractorYyyyMmDdSpaces implements DateExtractor {
	Pattern patYyyyMmDd = Pattern.compile("\\s*(\\d{4}) (\\d{1,2}) (\\d{1,2})\\s*");
	Pattern patDdMmYyyy = Pattern.compile("\\s*(\\d{1,2}) (\\d{1,2}) (\\d{4})\\s*");

	public Match extract(String inputValue) {
		Matcher m = patYyyyMmDd.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			d.setYear(Integer.parseInt(m.group(1)));
			d.setMonth(Integer.parseInt(m.group(2)));
			d.setDay(Integer.parseInt(m.group(3)));
			return new Match(MatchId.YYYY_MM_DD_Spaces, inputValue, new Instant(d));
		}
		m = patDdMmYyyy.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			d.setYear(Integer.parseInt(m.group(3)));
			d.setMonth(Integer.parseInt(m.group(2)));
			d.setDay(Integer.parseInt(m.group(1)));
			return new Match(MatchId.YYYY_MM_DD_Spaces, inputValue, new Instant(d));
		}
		return null;
	}

}
