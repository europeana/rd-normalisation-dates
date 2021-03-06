package europeana.rnd.dataprocessing.dates;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.DatatypeFormatException;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import europeana.rnd.dataprocessing.dates.edtf.Date;
import europeana.rnd.dataprocessing.dates.edtf.EdtfParser;
import europeana.rnd.dataprocessing.dates.edtf.EdtfSerializer;
import europeana.rnd.dataprocessing.dates.edtf.Instant;
import europeana.rnd.dataprocessing.dates.edtf.TemporalEntity;

/**
 * this class builds the RDF representation of normalised dates. It creates an
 * edm:TimeSpan from an EdmTemporalEntity instance.
 *
 */
public class EdmSerializer {
	public static class Rdf {
		public static final Property type = ResourceFactory
				.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	}

	public static class Edm {
		public static final Resource TimeSpan = ResourceFactory
				.createResource("http://www.europeana.eu/schemas/edm/TimeSpan");
		public static final Property begin = ResourceFactory
				.createProperty("http://www.europeana.eu/schemas/edm/begin");
		public static final Property end = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/end");
	}

	public static class Skos {
		public static final Property notation = ResourceFactory
				.createProperty("http://www.w3.org/2004/02/skos/core#notation");
		public static final Property note = ResourceFactory.createProperty("http://www.w3.org/2004/02/skos/core#note");
		public static final Property prefLabel = ResourceFactory
				.createProperty("http://www.w3.org/2004/02/skos/core#prefLabel");
	}

	public static class Dcterms {
		public static final Property isPartOf = ResourceFactory.createProperty("http://purl.org/dc/terms/isPartOf");
	}

	public static class EdtfLevel1Type extends BaseDatatype {
		public static final String uri = "http://id.loc.gov/datatypes/edtf/EDTF-level1";
		public static final RDFDatatype instance = new EdtfLevel1Type();

		static {
			TypeMapper.getInstance().registerDatatype(instance);
		}

		/** private constructor - single global instance */
		private EdtfLevel1Type() {
			super(uri);
		}

		/**
		 * Convert a value of this datatype out to lexical form.
		 */
		public String unparse(Object value) {
			TemporalEntity r = (TemporalEntity) value;
			return EdtfSerializer.serialize(r);
		}

		/**
		 * Parse a lexical form of this datatype to a value
		 * 
		 * @throws DatatypeFormatException if the lexical form is not legal
		 */
		public Object parse(String lexicalForm) throws DatatypeFormatException {
			try {
				TemporalEntity parsed = new EdtfParser().parse(lexicalForm);
				return parsed;
			} catch (ParseException e) {
				throw new DatatypeFormatException(lexicalForm, instance, e.getMessage());
			}
		}
	}

	public static Resource serialize(EdmTemporalEntity edmTemporalEntity) {
		TemporalEntity edtf = edmTemporalEntity.getEdtf();
		Model m = ModelFactory.createDefaultModel();
		String edtfString = EdtfSerializer.serialize(edtf);
		String uri;
		try {
			uri = "#" + URLEncoder.encode(edtfString, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		Resource r = m.createResource(uri);
		r.addProperty(Rdf.type, Edm.TimeSpan);
		r.addProperty(Skos.notation, edtfString, EdtfLevel1Type.instance);
		if (edtf.isApproximate())
			r.addProperty(Skos.note, "approximate", "en");
		if (edtf.isUncertain())
			r.addProperty(Skos.note, "uncertain", "en");

		Integer startCentury = null;
		Integer endCentury = null;
		Instant firstDay = edtf.getFirstDay();
		Instant lastDay = edtf.getLastDay();
		if (firstDay != null) {
			r.addProperty(Edm.begin, EdtfSerializer.serialize(firstDay));
			startCentury = firstDay.getCentury();
		}
		if (lastDay != null) {
			r.addProperty(Edm.end, EdtfSerializer.serialize(lastDay));
			endCentury = lastDay.getCentury();
		}

		if (startCentury == null)
			startCentury = endCentury;
		else if (endCentury == null)
			endCentury = startCentury;
		for (int c = Math.max(1, startCentury); c <= Math.max(0, endCentury); c++)
			r.addProperty(Dcterms.isPartOf, m.createResource("http://data.europeana.eu/timespan/" + c));

		if (!StringUtils.isEmpty(edmTemporalEntity.getLabel())) {
			r.addProperty(Skos.prefLabel, edmTemporalEntity.getLabel());
		} else
			r.addProperty(Skos.prefLabel, edtfString, "zxx");

		return r;
	}



}
