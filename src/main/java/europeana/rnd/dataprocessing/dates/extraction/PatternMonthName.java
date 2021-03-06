package europeana.rnd.dataprocessing.dates.extraction;

import java.time.Month;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.Date;
import europeana.rnd.dataprocessing.dates.edtf.Instant;

/**
 * A date where the month is specified by its name or an abbreviation. Supports
 * all the official languages of the European Union
 */
public class PatternMonthName implements DateExtractor {

	HashMap<Month, Pattern> patternDayMonthYear = new HashMap<Month, Pattern>(12);
	HashMap<Month, Pattern> patternMonthDayYear = new HashMap<Month, Pattern>(12);
	HashMap<Month, Pattern> patternMonthYear = new HashMap<Month, Pattern>(12);

	public PatternMonthName() {
		MonthMultilingual months = new MonthMultilingual();
		for (Month month : Month.values()) {
			String monthNamesPattern = null;
			for (String m : months.getMonthStrings(month)) {
				if (monthNamesPattern == null)
					monthNamesPattern = "(?<month>";
				else
					monthNamesPattern += "|";
				monthNamesPattern += m.replaceAll("\\.", "\\.");
			}
			monthNamesPattern += ")";

			patternDayMonthYear
					.put(month,
							Pattern.compile(
									"\\s*(?<day>\\d\\d?)[ \\.,]([a-zA-Z]{0,2}[ \\.,])?" + monthNamesPattern
											+ "[ \\.,]([a-zA-Z]{0,2}[ \\.,])?(?<year>\\d{4})\\s*",
									Pattern.CASE_INSENSITIVE));
			patternMonthDayYear.put(month, Pattern.compile("\\s*" + monthNamesPattern
					+ "[ \\.,]([a-zA-Z]{0,2}[ \\.,])?(?<day>\\d\\d?)[ \\.,][a-zA-Z]{0,2}[ \\.,](?<year>\\d{4})\\s*",
					Pattern.CASE_INSENSITIVE));
			patternMonthYear.put(month,
					Pattern.compile("\\s*" + monthNamesPattern + "[ \\.,]([a-zA-Z]{0,2}[ \\.,])?(?<year>\\d{4})\\s*",
							Pattern.CASE_INSENSITIVE));
		}
	}

	@Override
	public Match extract(String inputValue) {
		for (Month month : Month.values()) {
			Matcher m = patternDayMonthYear.get(month).matcher(inputValue);
			if (m.matches()) {
				Date d = new Date();
				d.setYear(Integer.parseInt(m.group("year")));
				d.setMonth(month.getValue());
				d.setDay(Integer.parseInt(m.group("day")));
				return new Match(MatchId.MONTH_NAME, inputValue, new Instant(d));
			}
			m = patternMonthDayYear.get(month).matcher(inputValue);
			if (m.matches()) {
				Date d = new Date();
				d.setYear(Integer.parseInt(m.group("year")));
				d.setMonth(month.getValue());
				d.setDay(Integer.parseInt(m.group("day")));
				return new Match(MatchId.MONTH_NAME, inputValue, new Instant(d));
			}
			m = patternMonthYear.get(month).matcher(inputValue);
			if (m.matches()) {
				Date d = new Date();
				d.setYear(Integer.parseInt(m.group("year")));
				d.setMonth(month.getValue());
				return new Match(MatchId.MONTH_NAME, inputValue, new Instant(d));
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		new PatternMonthName();
	}
}
