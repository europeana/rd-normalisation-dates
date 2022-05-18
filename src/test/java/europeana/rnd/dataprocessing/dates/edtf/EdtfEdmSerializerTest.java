package europeana.rnd.dataprocessing.dates.edtf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.junit.jupiter.api.Test;

import europeana.rnd.dataprocessing.dates.DatesNormaliser;
import europeana.rnd.dataprocessing.dates.EdmSerializer;
import europeana.rnd.dataprocessing.dates.Match;
import europeana.rnd.dataprocessing.dates.MatchId;
import europeana.rnd.dataprocessing.dates.EdmSerializer.Dcterms;
import europeana.rnd.dataprocessing.dates.EdmSerializer.Skos;

/**
 * @author nfrei
 *
 */
public class EdtfEdmSerializerTest {
	DatesNormaliser normaliser = new DatesNormaliser();
	
	@Test
	void serializeDcmiPeriod() throws Exception {
		Match match=normaliser.normalise("Byzantine Period; start=0395; end=0641");
		Resource edm = EdmSerializer.serialize(match.getExtracted());
		assertEquals(edm.getURI(), "#0395%2F0641");
		assertEquals(edm.listProperties(Dcterms.isPartOf).toList().size(), 4); 
		assertEquals("Byzantine Period", match.getExtracted().getLabel()); 
		assertEquals("Byzantine Period", edm.getProperty(Skos.prefLabel).getObject().asLiteral().getString()); 
		assertEquals("0395/0641", edm.getProperty(Skos.notation).getObject().asLiteral().getString()); 
	}

	@Test
	void serializeInterval() throws Exception {
		Match match=normaliser.normalise("1942-1943");
		Resource edm = EdmSerializer.serialize(match.getExtracted());
		assertEquals(edm.getURI(), "#1942%2F1943");
		List<Statement> partOf = edm.listProperties(Dcterms.isPartOf).toList();
		assertEquals(partOf.size(), 1); 
		assertEquals("1942/1943", edm.getProperty(Skos.prefLabel).getObject().asLiteral().getString()); 
		assertEquals("1942/1943", edm.getProperty(Skos.notation).getObject().asLiteral().getString()); 

		match=normaliser.normalise("[1842-1943]");
		edm = EdmSerializer.serialize(match.getExtracted());
		partOf = edm.listProperties(Dcterms.isPartOf).toList();
		assertEquals(partOf.size(), 2); 
		assertTrue(getUris(partOf).contains("http://data.europeana.eu/timespan/19")); 
		assertTrue(getUris(partOf).contains("http://data.europeana.eu/timespan/20")); 
		
		match=normaliser.normalise("[1801-1900]");
		edm = EdmSerializer.serialize(match.getExtracted());
		partOf = edm.listProperties(Dcterms.isPartOf).toList();
		assertEquals(partOf.size(), 1); 
		
		match=normaliser.normalise("-0005/0200");
		edm = EdmSerializer.serialize(match.getExtracted());
		partOf = edm.listProperties(Dcterms.isPartOf).toList();
		assertEquals(partOf.size(), 2); 
		assertTrue(getUris(partOf).contains("http://data.europeana.eu/timespan/1")); 
		assertTrue(getUris(partOf).contains("http://data.europeana.eu/timespan/2")); 

		match=normaliser.normalise("-0500/-0200");
		edm = EdmSerializer.serialize(match.getExtracted());
		partOf = edm.listProperties(Dcterms.isPartOf).toList();
		assertEquals(partOf.size(), 0); 
	}
	
	@Test
	void serializeInstant() throws Exception {
		Match match=normaliser.normalise("1942-03-03");
		Resource edm = EdmSerializer.serialize(match.getExtracted());
		assertEquals(edm.listProperties(Dcterms.isPartOf).toList().size(), 1); 

		match=normaliser.normalise("0001");
		edm = EdmSerializer.serialize(match.getExtracted());
		assertEquals(edm.listProperties(Dcterms.isPartOf).toList().size(), 1); 

		match=normaliser.normalise("-0001");
		edm = EdmSerializer.serialize(match.getExtracted());
		assertEquals(edm.listProperties(Dcterms.isPartOf).toList().size(), 0); 
	}	
	
	private HashSet<String> getUris(List<Statement> stms){
		HashSet<String> uris=new HashSet<String>();
		for(Statement st : stms) 
			uris.add(st.getObject().asResource().getURI());
		return uris;
	}
	
}