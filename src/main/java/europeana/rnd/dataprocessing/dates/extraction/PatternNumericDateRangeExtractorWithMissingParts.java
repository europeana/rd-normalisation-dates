package europeana.rnd.dataprocessing.dates.extraction;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.Date;
import europeana.rnd.dataprocessing.dates.edtf.Instant;
import europeana.rnd.dataprocessing.dates.edtf.Interval;

/**
 * Patterns for numeric date ranges with variations in the separators of date
 * components
 */
public class PatternNumericDateRangeExtractorWithMissingParts implements DateExtractor {
	// period separators "/" -> "[-\\.]"
	// period separators " - " -> "[-\\./]"
	// period separators "-" -> "[\\./]"
//	String dateYyyyMmDdPatStr="(\\d\\d\\d\\d(([-/.]\\d\\d?)?[-/.]\\d\\d?)?|\\?)";
//	String dateDdMmYyyyPatStr="(((\\d\\d?[-/.])?\\d\\d??[-/.])?\\d\\d\\d\\d?|\\?)";

	Pattern cleanSeparator = Pattern.compile("[\\-\\./]");

	ArrayList<Pattern> patterns = new ArrayList<Pattern>();

	public PatternNumericDateRangeExtractorWithMissingParts() {
		String dateSep = "/";
		String componentSep = "[\\-\\.]";
		String unsepecifiedVals = "\\?|\\-|\\.\\.";
		String dateYmd = "\\s*((?<year>\\d\\d\\d\\d?)(?<month>" + componentSep + "\\d\\d?)?(?<day>" + componentSep
				+ "\\d\\d?)?(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		String dateDmy = "\\s*((?<day>\\d\\d?" + componentSep + ")?(?<month>\\d\\d?" + componentSep
				+ ")?(?<year>\\d\\d\\d\\d?)(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		patterns.add(Pattern.compile(dateYmd + dateSep + dateYmd.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));
		patterns.add(Pattern.compile(dateDmy + dateSep + dateDmy.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));

		dateSep = " \\- ";
		componentSep = "[\\-\\./]";
		unsepecifiedVals = "\\?|\\-|\\.\\.";
		dateYmd = "\\s*((?<year>\\d\\d\\d\\d?)(?<month>" + componentSep + "\\d\\d?)?(?<day>" + componentSep
				+ "\\d\\d?)?(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		dateDmy = "\\s*((?<day>\\d\\d?" + componentSep + ")?(?<month>\\d\\d?" + componentSep
				+ ")?(?<year>\\d\\d\\d\\d?)(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		patterns.add(Pattern.compile(dateYmd + dateSep + dateYmd.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));
		patterns.add(Pattern.compile(dateDmy + dateSep + dateDmy.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));

		dateSep = "-";
		componentSep = "[\\./]";
		unsepecifiedVals = "\\?|\\.\\.";
		dateYmd = "\\s*((?<year>\\d\\d\\d\\d?)(?<month>" + componentSep + "\\d\\d?)?(?<day>" + componentSep
				+ "\\d\\d?)?(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		dateDmy = "\\s*((?<day>\\d\\d?" + componentSep + ")?(?<month>\\d\\d?" + componentSep
				+ ")?(?<year>\\d\\d\\d\\d?)(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		patterns.add(Pattern.compile(dateYmd + dateSep + dateYmd.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));
		patterns.add(Pattern.compile(dateDmy + dateSep + dateDmy.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));

		dateSep = " ";
		componentSep = "[\\./\\-]";
		unsepecifiedVals = "";
		dateYmd = "\\s*((?<year>\\d\\d\\d\\d?)(?<month>" + componentSep + "\\d\\d?)?(?<day>" + componentSep
				+ "\\d\\d?)?(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		dateDmy = "\\s*((?<day>\\d\\d?" + componentSep + ")?(?<month>\\d\\d?" + componentSep
				+ ")?(?<year>\\d\\d\\d\\d?)(?<uncertain>\\?)?|(?<unspecified>" + unsepecifiedVals + "))\\s*";
		patterns.add(Pattern.compile(dateYmd + dateSep + dateYmd.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));
		patterns.add(Pattern.compile(dateDmy + dateSep + dateDmy.replace("year", "year2").replace("month", "month2")
				.replace("day", "day2").replace("uncertain", "uncertain2").replace("unspecified", "unspecified2")));
	}

	public Match extract(String inputValue) {
		for (Pattern pat : patterns) {
			Matcher m = pat.matcher(inputValue.trim());
			if (m.matches()) {
				Date dStart = new Date();
				if (m.group("unspecified") != null) {
					dStart = Date.UNSPECIFIED;
				} else {
					dStart.setYear(Integer.parseInt(m.group("year")));
					if (m.group("month") != null && m.group("day") != null) {
						dStart.setMonth(Integer.parseInt(clean(m.group("month"))));
						dStart.setDay(Integer.parseInt(clean(m.group("day"))));
					} else if (m.group("month") != null) {
						dStart.setMonth(Integer.parseInt(clean(m.group("month"))));
					} else if (m.group("day") != null) {
						dStart.setMonth(Integer.parseInt(clean(m.group("day"))));
					}
					if (m.group("uncertain") != null)
						dStart.setUncertain(true);
				}
				Date dEnd = new Date();
				if (m.group("unspecified2") != null) {
					dEnd = Date.UNSPECIFIED;
				} else {
					dEnd.setYear(Integer.parseInt(m.group("year2")));
					if (m.group("month2") != null && m.group("day2") != null) {
						dEnd.setMonth(Integer.parseInt(clean(m.group("month2"))));
						dEnd.setDay(Integer.parseInt(clean(m.group("day2"))));
					} else if (m.group("month2") != null) {
						dEnd.setMonth(Integer.parseInt(clean(m.group("month2"))));
					} else if (m.group("day2") != null) {
						dEnd.setMonth(Integer.parseInt(clean(m.group("day2"))));
					}
					if (m.group("uncertain2") != null)
						dEnd.setUncertain(true);
				}

				if (dEnd.isUnspecified() && dStart.getYear() != null && dStart.getYear() < 1000)
					return null;// these cases are ambiguous. Example '187-?'
				return new Match(MatchId.Numeric_Range_AllVariants, inputValue,
						new Interval(new Instant(dStart), new Instant(dEnd)));
			}
		}
		return null;
	}

	private String clean(String group) {
		return cleanSeparator.matcher(group).replaceFirst("");
	}
}
