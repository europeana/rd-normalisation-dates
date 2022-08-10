package europeana.rnd.dataprocessing.dates.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import europeana.rnd.dataprocessing.dates.DatesNormaliser;
import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.edtf.EdtfSerializer;

public class DateNormalizerGenericPropertyTest {
	HashMap<String, String> testCases = new HashMap<String, String>();

	public DateNormalizerGenericPropertyTest() {
		testCases.put("XIV", null);
		testCases.put("1905 09 01", "1905-09-01");
		testCases.put("1851-01-01  - 1851-12-31", "1851-01-01/1851-12-31");
		testCases.put("18..", null);
		testCases.put("2013-09-07 09:31:51 UTC", "2013-09-07");
		testCases.put("1918 / 1919", "1918/1919");
		testCases.put("1205/1215 [Herstellung]", null);
		testCases.put("1997-07", null);
		testCases.put("19??", null);
		testCases.put("1871 - 191-", null);
	}

	@Test
	void extractorsTest() throws Exception {
		DatesNormaliser normaliser = new DatesNormaliser();
		Match match = null;

		for (String testCase : testCases.keySet()) {
			match = normaliser.normaliseGenericProperty(testCase);
			if (match.getMatchId() == MatchId.NO_MATCH || match.getMatchId() == MatchId.INVALID) {
				assertNull(testCases.get(testCase), "Test case '" + testCase
						+ "' was a no-match but should be normalised to '" + testCases.get(testCase) + "'");
			} else {
				String edtfStr = EdtfSerializer.serialize(match.getExtracted().getEdtf());
				assertEquals(testCases.get(testCase), edtfStr, "Test case '" + testCase + "'");
				if (match.getMatchId() == MatchId.DCMIPeriod) {
					assertTrue(testCase.startsWith(match.getExtracted().getLabel()) || testCase.startsWith("name="+match.getExtracted().getLabel()),
							"Test case '" + testCase + "' period name not extracted");
				}
			}
		}

	}

}
