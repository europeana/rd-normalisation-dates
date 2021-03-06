package europeana.rnd.dataprocessing.dates.extraction;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.Date;
import europeana.rnd.dataprocessing.dates.edtf.Date.YearPrecision;
import europeana.rnd.dataprocessing.dates.edtf.Instant;
import europeana.rnd.dataprocessing.dates.edtf.Interval;

/**
 * Patterns for numeric date ranges with variations in the separators of date
 * components, and supporting characters for unknown/unspecified date
 * components.
 */
public class PatternNumericDateRangeExtractorWithMissingPartsAndXx implements DateExtractor {
	// period separators "/" -> "[-\\.]"
	// period separators " - " -> "[-\\./]"
	// period separators "-" -> "[\\./]"
//	String dateYyyyMmDdPatStr="(\\d\\d\\d\\d(([-/.]\\d\\d?)?[-/.]\\d\\d?)?|\\?)";
//	String dateDdMmYyyyPatStr="(((\\d\\d?[-/.])?\\d\\d??[-/.])?\\d\\d\\d\\d?|\\?)";

	Pattern cleanSeparatorAndUnknown = Pattern.compile("[\\-\\./\\?Xu]", Pattern.CASE_INSENSITIVE);
	Pattern unknownChars = Pattern.compile("[\\-\\?Xu]+$", Pattern.CASE_INSENSITIVE);

	ArrayList<Pattern> patterns = new ArrayList<Pattern>();

	public PatternNumericDateRangeExtractorWithMissingPartsAndXx() {
		String dateSep = "\\s*[\\/\\|]\\s*";
		String componentSep = "[\\-]";
		String componentMissing = "[Xu]";

		String dateYmd = "\\s*((?<year>\\d\\d\\d\\d?|\\d\\d\\d" + componentMissing + "?|\\d\\d+" + componentMissing
				+ componentMissing + "?)(" + componentSep + "(?<month>\\d\\d?|\\d" + componentMissing + "?))?("
				+ componentSep + "(?<day>\\d\\d?|\\d" + componentMissing + "?))?|(?<unspecified>\\?))\\s*";
		String dateDmy = "\\s*(((?<day>\\d\\d?|\\d" + componentMissing + "?)" + componentSep + ")?((?<month>\\d\\d?|\\d"
				+ componentMissing + "?)" + componentSep + ")?(?<year>\\d\\d\\d\\d?|\\d\\d" + componentMissing
				+ componentMissing + "?)|(?<unspecified>\\?))\\s*";
		patterns.add(Pattern.compile(
				dateYmd + dateSep
						+ dateYmd.replace("year", "year2").replace("month", "month2").replace("day", "day2")
								.replace("unspecified", "unspecified2"),
				Pattern.CASE_INSENSITIVE));
		patterns.add(Pattern.compile(
				dateDmy + dateSep
						+ dateDmy.replace("year", "year2").replace("month", "month2").replace("day", "day2")
								.replace("unspecified", "unspecified2"),
				Pattern.CASE_INSENSITIVE));

		componentSep = "[\\.]";
		componentMissing = "[\\-Xu]";
		dateYmd = "\\s*((?<year>\\d\\d\\d\\d?|\\d\\d\\d" + componentMissing + "?|\\d\\d+" + componentMissing
				+ componentMissing + "?)(" + componentSep + "(?<month>\\d\\d?|\\d" + componentMissing + "?))?("
				+ componentSep + "(?<day>\\d\\d?|\\d" + componentMissing + "?))?|(?<unspecified>\\?))\\s*";
		dateDmy = "\\s*(((?<day>\\d\\d?|\\d" + componentMissing + "?)" + componentSep + ")?((?<month>\\d\\d?|\\d"
				+ componentMissing + "?)" + componentSep + ")?(?<year>\\d\\d\\d\\d?|\\d\\d" + componentMissing
				+ componentMissing + "?)|(?<unspecified>\\?))\\s*";
		patterns.add(Pattern.compile(
				dateYmd + dateSep
						+ dateYmd.replace("year", "year2").replace("month", "month2").replace("day", "day2")
								.replace("unspecified", "unspecified2"),
				Pattern.CASE_INSENSITIVE));
		patterns.add(Pattern.compile(
				dateDmy + dateSep
						+ dateDmy.replace("year", "year2").replace("month", "month2").replace("day", "day2")
								.replace("unspecified", "unspecified2"),
				Pattern.CASE_INSENSITIVE));

		dateSep = "\\s+[\\-\\|]\\s+";
		componentSep = "[\\./]";
		componentMissing = "[\\-Xu]";
		dateYmd = "\\s*((?<year>\\d\\d\\d\\d?|\\d\\d\\d" + componentMissing + "?|\\d\\d+" + componentMissing
				+ componentMissing + "?)(" + componentSep + "(?<month>\\d\\d?|\\d" + componentMissing + "?))?("
				+ componentSep + "(?<day>\\d\\d?|\\d" + componentMissing + "?))?|(?<unspecified>\\?))\\s*";
		dateDmy = "\\s*(((?<day>\\d\\d?|\\d" + componentMissing + "?)" + componentSep + ")?((?<month>\\d\\d?|\\d"
				+ componentMissing + "?)" + componentSep + ")?(?<year>\\d\\d\\d\\d?|\\d\\d" + componentMissing
				+ componentMissing + "?)|(?<unspecified>\\?))\\s*";
		patterns.add(Pattern.compile(
				dateYmd + dateSep
						+ dateYmd.replace("year", "year2").replace("month", "month2").replace("day", "day2")
								.replace("unspecified", "unspecified2"),
				Pattern.CASE_INSENSITIVE));
		patterns.add(Pattern.compile(
				dateDmy + dateSep
						+ dateDmy.replace("year", "year2").replace("month", "month2").replace("day", "day2")
								.replace("unspecified", "unspecified2"),
				Pattern.CASE_INSENSITIVE));

		dateSep = "\\s+-\\s+";
		componentSep = "[\\-]";
		componentMissing = "[Xu]";
		dateYmd = "\\s*((?<year>\\d\\d\\d\\d?|\\d\\d\\d" + componentMissing + "?|\\d\\d+" + componentMissing
				+ componentMissing + "?)(" + componentSep + "(?<month>\\d\\d?|\\d" + componentMissing + "?))?("
				+ componentSep + "(?<day>\\d\\d?|\\d" + componentMissing + "?))?|(?<unspecified>\\?))\\s*";
		dateDmy = "\\s*(((?<day>\\d\\d?|\\d" + componentMissing + "?)" + componentSep + ")?((?<month>\\d\\d?|\\d"
				+ componentMissing + "?)" + componentSep + ")?(?<year>\\d\\d\\d\\d?|\\d\\d" + componentMissing
				+ componentMissing + "?)|(?<unspecified>\\?))\\s*";
		patterns.add(
				Pattern.compile(
						dateYmd + dateSep + dateYmd.replace("year", "year2").replace("month", "month2")
								.replace("day", "day2").replace("unspecified", "unspecified2"),
						Pattern.CASE_INSENSITIVE));
		patterns.add(
				Pattern.compile(
						dateDmy + dateSep + dateDmy.replace("year", "year2").replace("month", "month2")
								.replace("day", "day2").replace("unspecified", "unspecified2"),
						Pattern.CASE_INSENSITIVE));

		dateSep = "-";
		componentSep = "[\\./]";
		componentMissing = "[Xu]";
		dateYmd = "\\s*((?<year>\\d\\d\\d\\d?|\\d\\d\\d" + componentMissing + "?|\\d\\d+" + componentMissing
				+ componentMissing + "?)(" + componentSep + "(?<month>\\d\\d?|\\d" + componentMissing + "?))?("
				+ componentSep + "(?<day>\\d\\d?|\\d" + componentMissing + "?))?|(?<unspecified>\\?))\\s*";
		dateDmy = "\\s*(((?<day>\\d\\d?|\\d" + componentMissing + "?)" + componentSep + ")?((?<month>\\d\\d?|\\d"
				+ componentMissing + "?)" + componentSep + ")?(?<year>\\d\\d\\d\\d?|\\d\\d" + componentMissing
				+ componentMissing + "?)|(?<unspecified>\\?))\\s*";
		patterns.add(
				Pattern.compile(
						dateYmd + dateSep + dateYmd.replace("year", "year2").replace("month", "month2")
								.replace("day", "day2").replace("unspecified", "unspecified2"),
						Pattern.CASE_INSENSITIVE));
		patterns.add(
				Pattern.compile(
						dateDmy + dateSep + dateDmy.replace("year", "year2").replace("month", "month2")
								.replace("day", "day2").replace("unspecified", "unspecified2"),
						Pattern.CASE_INSENSITIVE));
	}

	public Match extract(String inputValue) {
		for (Pattern pat : patterns) {
			Matcher m = pat.matcher(inputValue);
			if (m.matches()) {
				Date dStart = new Date();
				if (m.group("unspecified") != null) {
					dStart = Date.UNSPECIFIED;
				} else {
					String year = m.group("year");
					Matcher mtc = unknownChars.matcher(year);
					if (!mtc.find())
						dStart.setYear(Integer.parseInt(year));
					else {
						if (mtc.group(0).length() == 2) {
							dStart.setYear(
									Integer.parseInt(year.substring(0, year.length() - mtc.group(0).length())) * 100);
							dStart.setYearPrecision(YearPrecision.CENTURY);
						} else {
							dStart.setYear(
									Integer.parseInt(year.substring(0, year.length() - mtc.group(0).length())) * 10);
							dStart.setYearPrecision(YearPrecision.DECADE);
						}
					}

					String month = m.group("month");
					if (month != null)
						month = clean(month);
					String day = m.group("day");
					if (day != null)
						day = clean(day);

					if (!StringUtils.isEmpty(month) && !StringUtils.isEmpty(day)) {
						dStart.setMonth(Integer.parseInt(month));
						dStart.setDay(Integer.parseInt(day));
					} else if (!StringUtils.isEmpty(month)) {
						dStart.setMonth(Integer.parseInt(month));
					} else if (!StringUtils.isEmpty(day))
						dStart.setMonth(Integer.parseInt(day));
					if (m.group("unspecified") != null)
						dStart.setUnspecified(true);
				}

				Date dEnd = new Date();
				if (m.group("unspecified2") != null) {
					dEnd = Date.UNSPECIFIED;
				} else {
					String year = m.group("year2");
					Matcher mtc = unknownChars.matcher(year);
					if (!mtc.find())
						dEnd.setYear(Integer.parseInt(year));
					else {
						if (mtc.group(0).length() == 2) {
							dEnd.setYear(
									Integer.parseInt(year.substring(0, year.length() - mtc.group(0).length())) * 100);
							dEnd.setYearPrecision(YearPrecision.CENTURY);
						} else {
							dEnd.setYear(
									Integer.parseInt(year.substring(0, year.length() - mtc.group(0).length())) * 10);
							dEnd.setYearPrecision(YearPrecision.DECADE);
						}
					}

					String month = m.group("month2");
					if (month != null)
						month = clean(month);
					String day = m.group("day2");
					if (day != null)
						day = clean(day);

					if (!StringUtils.isEmpty(month) && !StringUtils.isEmpty(day)) {
						dEnd.setMonth(Integer.parseInt(month));
						dEnd.setDay(Integer.parseInt(day));
					} else if (!StringUtils.isEmpty(month)) {
						dEnd.setMonth(Integer.parseInt(month));
					} else if (!StringUtils.isEmpty(day))
						dEnd.setMonth(Integer.parseInt(day));
					if (m.group("unspecified2") != null)
						dEnd.setUnspecified(true);
				}
				if (dEnd.isUnspecified() && dStart.getYear() != null && dStart.getYear() < 1000)
					return null;// these cases are ambiguous. Example '187-?'
				return new Match(MatchId.Numeric_Range_AllVariants_Xx, inputValue,
						new Interval(new Instant(dStart), new Instant(dEnd)));
			}
		}
		return null;
	}

	private String clean(String group) {
		return cleanSeparatorAndUnknown.matcher(group).replaceAll("");
	}
}
