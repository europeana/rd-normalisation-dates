package europeana.rnd.dataprocessing.dates.extraction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.ParseException;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import europeana.rnd.dataprocessing.dates.DatesNormaliser;
import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.edtf.EdtfSerializer;
import europeana.rnd.dataprocessing.dates.edtf.EdtfValidator;
import europeana.rnd.dataprocessing.dates.edtf.Instant;

public class DebugDateNormalizer {
	
	@Test
	void extractorsTest() throws Exception {
		DatesNormaliser normaliser = new DatesNormaliser();
		Match match=null;		
//		match = normaliser.normalise("168 B.C.-135 A.D.");
		match = normaliser.normalise("?/1807");
		System.out.println(match.getInput());
		System.out.println(match.getMatchId());
		System.out.println(match.getExtracted().getEdtf().serialize());
	}
	
	
}
