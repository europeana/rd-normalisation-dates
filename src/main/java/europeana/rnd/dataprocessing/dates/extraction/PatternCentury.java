package europeana.rnd.dataprocessing.dates.extraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.Date;
import europeana.rnd.dataprocessing.dates.edtf.Date.YearPrecision;
import europeana.rnd.dataprocessing.dates.edtf.Instant;
import europeana.rnd.dataprocessing.dates.edtf.Interval;

/**
 * A century indicated as a Roman numeral, as for example ‘XVI’. The Roman
 * numerals may also be preceded by an abbreviation of century, as for example
 * ‘s. IXX’. Also supports ranges.
 */
public class PatternCentury implements DateExtractor {
	Pattern patYyyy = Pattern.compile("\\s*(?<uncertain>\\?)?(?<century>\\d{2})\\.{2}(?<uncertain2>\\?)?\\s*",
			Pattern.CASE_INSENSITIVE);
	Pattern patRoman = Pattern.compile(
			"\\s*(?<uncertain>\\?)?(s\\s|s\\.|sec\\.?|saec\\.?)\\s*(?<century>I{1,3}|IV|VI{0,3}|I?X|XI{1,3}|XIV|XVI{0,3}|I?XX|XXI)(?<uncertain2>\\?)?\\s*",
			Pattern.CASE_INSENSITIVE);
//	Pattern patYyyyX=Pattern.compile("\\s*([\\[\\?]{0,2})(\\d\\d)xx[\\]\\?]{0,2}\\s*",Pattern.CASE_INSENSITIVE);
	Pattern patRomanClean = Pattern.compile("\\s*(?<uncertain>\\?)?(?<century>I{1,3}|IV|VI{0,3}|I?X|XI{1,3}|XIV|XVI{0,3}|I?XX|XXI)(?<uncertain2>\\?)?\\s*",
			Pattern.CASE_INSENSITIVE);

	Pattern patEnglish = Pattern.compile("\\s*(?<uncertain>\\?)?(?<century>[12]?\\d)(st|nd|rd|th)\\s+century(?<uncertain2>\\?)?\\s*",
			Pattern.CASE_INSENSITIVE);
	
	Pattern patRomanRange = Pattern.compile("\\s*(?<uncertain>\\?)?(s\\.?|sec\\.?|saec\\.?)\\s*(?<century1>[XIV]{1,5})\\s*" + "\\-" + "\\s*(?<century2>[XIV]{1,5})(?<uncertain2>\\?)?\\s*",
			Pattern.CASE_INSENSITIVE);

	public Match extract(String inputValue) {
		Matcher m;
		m = patYyyy.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			d.setYearPrecision(YearPrecision.CENTURY);
			d.setYear(Integer.parseInt(m.group("century")) * 100);
			if (m.group("uncertain") != null || m.group("uncertain2") != null)
				d.setUncertain(true);
			return new Match(MatchId.Century_Numeric, inputValue, new Instant(d));
		}
		m = patEnglish.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			d.setYearPrecision(YearPrecision.CENTURY);
			d.setYear((Integer.parseInt(m.group("century")) - 1) * 100);
			if (m.group("uncertain") != null || m.group("uncertain2") != null)
				d.setUncertain(true);
			return new Match(MatchId.Century_Numeric, inputValue, new Instant(d));
		}
		m = patRoman.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			d.setYearPrecision(YearPrecision.CENTURY);
			d.setYear((RomanToNumber.romanToDecimal(m.group("century")) - 1) * 100);
			if (m.group("uncertain") != null || m.group("uncertain2") != null)
				d.setUncertain(true);
			return new Match(MatchId.Century_Roman, inputValue, new Instant(d));
		}
		m = patRomanClean.matcher(inputValue);
		if (m.matches()) {
			Date d = new Date();
			d.setYearPrecision(YearPrecision.CENTURY);
			d.setYear((RomanToNumber.romanToDecimal(m.group("century")) - 1) * 100);
			if (m.group("uncertain") != null || m.group("uncertain2") != null)
				d.setUncertain(true);
			return new Match(MatchId.Century_Roman, inputValue, new Instant(d));
		}
		m = patRomanRange.matcher(inputValue);
		if (m.matches()) {
			Date start = new Date();
			start.setYearPrecision(YearPrecision.CENTURY);
			start.setYear((RomanToNumber.romanToDecimal(m.group("century1")) - 1) * 100);
			Date end = new Date();
			end.setYearPrecision(YearPrecision.CENTURY);
			end.setYear((RomanToNumber.romanToDecimal(m.group("century2")) - 1) * 100);
			if (m.group("uncertain") != null || m.group("uncertain2") != null) {
				start.setUncertain(true);
				end.setUncertain(true);
			}
			return new Match(MatchId.Century_Range_Roman, inputValue,
					new Interval(new Instant(start), new Instant(end)));
		}
		return null;
	}

}
