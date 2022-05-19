package europeana.rnd.dataprocessing.dates.extraction;

import org.junit.jupiter.api.Test;

import europeana.rnd.dataprocessing.dates.DatesNormaliser;
import europeana.rnd.dataprocessing.dates.Match;

public class DebugDateNormalizer {

	@Test
	void extractorsTest() throws Exception {
		DatesNormaliser normaliser = new DatesNormaliser();
		Match match = null;
//		match = normaliser.normalise("168 B.C.-135 A.D.");
		match = normaliser.normaliseDateProperty("?/1807");
		System.out.println(match.getInput());
		System.out.println(match.getMatchId());
		System.out.println(match.getExtracted().getEdtf().serialize());
	}

}
