package europeana.rnd.dataprocessing.dates.extraction;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.Date;
import europeana.rnd.dataprocessing.dates.edtf.Instant;
import europeana.rnd.dataprocessing.dates.edtf.Interval;

/**
 * A year with an indication of the era, for example ‘3000 BC’. Currently, the
 * normalisation process recognizes ‘BC/AD’ and ‘AC/DC’, but the abbreviations
 * used in other languages will be supported in the future. Or a date range
 * where the start/end years contain an indication of the era.
 *
 */
public class PatternBcAd implements DateExtractor {

	static final HashSet<String> bcAbbreviations = new HashSet<String>() {
		{
			add("B\\.?C".toLowerCase());
			add("A\\.?C".toLowerCase());
			add("v\\.?Chr".toLowerCase());
			add("vC".toLowerCase());
			add("avant J\\.?-C".toLowerCase());
			add("av[\\. ]J\\.?-C".toLowerCase());
//		add("eKr"); removed due to ambiguity
			add("f\\.?Kr".toLowerCase());
			add("π\\.*Χ".toLowerCase());
		}
	};
	static final HashSet<String> adAbbreviations = new HashSet<String>() {
		{
			add("A\\.?D".toLowerCase());
			add("D\\.?C".toLowerCase());
			add("n\\.?Chr".toLowerCase());
			add("nC".toLowerCase());
			add("après J-C".toLowerCase());
			add("apres J-C".toLowerCase());
			add("ap[\\. ]J-C".toLowerCase());
//		add("eKr"); removed due to ambiguity
			add("j\\.?Kr".toLowerCase());
			add("μ\\.?Χ".toLowerCase());
		}
	};

	static final HashSet<Pattern> bcAbbreviationsPatterns = new HashSet<Pattern>() {
		{
			for (String abrev : bcAbbreviations)
				add(Pattern.compile(abrev, Pattern.CASE_INSENSITIVE));
		}
	};

	Pattern patYyyy;
	Pattern patRange;

	public PatternBcAd() {
		String patYearBcAd = "(?<year>\\d{2,4})\\s*(?<era>";
		for (String abrev : bcAbbreviations)
			patYearBcAd += abrev + "|";
		for (String abrev : adAbbreviations)
			patYearBcAd += abrev + "|";
		patYearBcAd = patYearBcAd.substring(0, patYearBcAd.length() - 1) + ")\\.?";

		patYyyy = Pattern.compile(patYearBcAd, Pattern.CASE_INSENSITIVE);
		patRange = Pattern.compile(
				patYearBcAd + "\\s*[\\-\\/]\\s*" + patYearBcAd.replace("<year>", "<year2>").replace("<era>", "<era2>"),
				Pattern.CASE_INSENSITIVE);
	}

	public Match extract(String inputValue) {
		Matcher m = patYyyy.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			if (bcAbbreviations.contains(m.group("era").toLowerCase()))
				d.setYear(-Integer.parseInt(m.group("year")));
			else
				d.setYear(Integer.parseInt(m.group("year")));
			return new Match(MatchId.BcAd, inputValue, new Instant(d));
		}
		m = patRange.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			if (isBc(m.group("era")))
				d.setYear(-Integer.parseInt(m.group("year")));
			else
				d.setYear(Integer.parseInt(m.group("year")));
			Instant start = new Instant(d);

			d = new Date();
			if (isBc(m.group("era2")))
				d.setYear(-Integer.parseInt(m.group("year2")));
			else
				d.setYear(Integer.parseInt(m.group("year2")));
			Instant end = new Instant(d);

			return new Match(MatchId.BcAd, inputValue, new Interval(start, end));
		}
		return null;
	}

	private boolean isBc(String abbreviation) {
		for (Pattern pat : bcAbbreviationsPatterns) {
			if (pat.matcher(abbreviation).matches())
				return true;
		}
		return false;
	}

}
