import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Explanation.*;
import org.apache.lucene.util.*;

/**
 * A Query that matches documents containing multiple terms. This may be combined with
 * other terms with a {@link BooleanQuery}.
 */
public class MultiQuery extends Query {
	private Term[] term;
	private int termNum;
	private float avgLength;
	private float avgLengthExtend;

	private class SimpleWeight extends Weight {
		private float avgLength;
		private Searcher search;
		private final Similarity similarity;
		private float value;
		private Term curTerm;
		private float idf[];
		private float queryNorm;
		private float queryWeight[];
		private IDFExplanation idfExp[];
		private Set<Integer> hash;

		public SimpleWeight(Searcher searcher, float avg)
				throws IOException {
			this.avgLength = avg;
			this.search = searcher;
			this.similarity = getSimilarity(searcher);
			idf = new float[termNum];
			idfExp = new IDFExplanation[termNum];
			queryWeight = new float[termNum];
			for(int i = 0; i < termNum; i ++) {
				curTerm = term[i];
				if (searcher instanceof IndexSearcher) {
					IndexReader ir = ((IndexSearcher) searcher).getIndexReader();
					hash = new HashSet<Integer>();
					final int dfSum[] = new int[1];
					new ReaderUtil.Gather(ir) {
						@Override
						protected void add(int base, IndexReader r)
								throws IOException {
							int df = r.docFreq(curTerm);
							dfSum[0] += df;
							if (df > 0) {
								hash.add(r.hashCode());
							}
						}
					}.run();
	
					idfExp[i] = similarity.idfExplain(curTerm, searcher, dfSum[0]);
				} else {
					idfExp[i] = similarity.idfExplain(curTerm, searcher);
					hash = null;
				}
	
				idf[i] = idfExp[i].getIdf();
			}
		}

		@Override
		public String toString() {
			return "weight(" + MultiQuery.this + ")";
		}

		@Override
		public Query getQuery() {
			return MultiQuery.this;
		}

		@Override
		public float getValue() {
			return value;
		}

		@Override
		public float sumOfSquaredWeights() {
			float result = 0;
			for(int i = 0; i < termNum; i ++) {
				queryWeight[i] += idf[i] * getBoost(); // compute query weight
				result += queryWeight[i] * queryWeight[i];
			}
			return result; // square it
		}

		@Override
		public void normalize(float queryNorm) {
			this.queryNorm = queryNorm;
			for(int i = 0; i < termNum; i ++) {
				queryWeight[i] *= queryNorm; // normalize query weight
				value += queryWeight[i] * idf[i]; // idf for document
			}
		}

		@Override
		public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder,
				boolean topScorer) throws IOException {
			// only use the early exit condition if we have an atomic reader,
			// because Lucene 3.x still supports non-atomic readers here:
			//if (hash != null && reader.getSequentialSubReaders() == null
			//		&& !hash.contains(reader.hashCode())) {
			//	return null;
			//}

			TermDocs[] termDocs = new TermDocs[termNum];
			for(int i = 0; i < termNum; i ++)
				termDocs[i] = reader.termDocs(term[i]);

			if (termDocs == null)
				return null;

			return new MultiScorer(this, termDocs, similarity, reader
					.norms(term[0].field()), idf,avgLength, ((IndexSearcher) search), termNum, term, avgLengthExtend);
		}

		@Override
		public Explanation explain(IndexReader reader, int doc)
				throws IOException {

			ComplexExplanation result = new ComplexExplanation();
			result.setDescription("weight(" + getQuery() + " in " + doc
					+ "), product of:");
			
			for(int i = 0; i < termNum; i ++) {
				Explanation expl = new Explanation(idf[i], idfExp[i].explain());
	
				// explain query weight
				Explanation queryExpl = new Explanation();
				queryExpl.setDescription("queryWeight(" + getQuery()
						+ "), product of:");
	
				Explanation boostExpl = new Explanation(getBoost(), "boost");
				if (getBoost() != 1.0f)
					queryExpl.addDetail(boostExpl);
				queryExpl.addDetail(expl);
	
				Explanation queryNormExpl = new Explanation(queryNorm, "queryNorm");
				queryExpl.addDetail(queryNormExpl);
	
				queryExpl.setValue(boostExpl.getValue() * expl.getValue()
						* queryNormExpl.getValue());
	
				result.addDetail(queryExpl);
	
				// explain field weight
				String field = term[i].field();
				ComplexExplanation fieldExpl = new ComplexExplanation();
				fieldExpl.setDescription("fieldWeight(" + term[i] + " in " + doc
						+ "), product of:");
	
				Explanation tfExplanation = new Explanation();
				int tf = 0;
				TermDocs termDocs = reader.termDocs(term[i]);
				if (termDocs != null) {
					try {
						if (termDocs.skipTo(doc) && termDocs.doc() == doc) {
							tf = termDocs.freq();
						}
					} finally {
						termDocs.close();
					}
					tfExplanation.setValue(similarity.tf(tf));
					tfExplanation.setDescription("tf(termFreq(" + term[i] + ")=" + tf
							+ ")");
				} else {
					tfExplanation.setValue(0.0f);
					tfExplanation.setDescription("no matching term");
				}
				fieldExpl.addDetail(tfExplanation);
				fieldExpl.addDetail(expl);
	
				Explanation fieldNormExpl = new Explanation();
				byte[] fieldNorms = reader.norms(field);
				float fieldNorm = fieldNorms != null ? similarity
						.decodeNormValue(fieldNorms[doc]) : 1.0f;
				fieldNormExpl.setValue(fieldNorm);
				fieldNormExpl.setDescription("fieldNorm(field=" + field + ", doc="
						+ doc + ")");
				fieldExpl.addDetail(fieldNormExpl);
	
				fieldExpl.setMatch(Boolean.valueOf(tfExplanation.isMatch()));
				fieldExpl.setValue(tfExplanation.getValue() * expl.getValue()
						* fieldNormExpl.getValue());
	
				result.addDetail(fieldExpl);
				result.setMatch(fieldExpl.getMatch());
	
				// combine them
				result.setValue(queryExpl.getValue() * fieldExpl.getValue());
	
				if (queryExpl.getValue() == 1.0f)
					return fieldExpl;
			}

			return result;
		}
	}

	/** Constructs a query for the term <code>t</code>. */
	public MultiQuery(Term[] t, float avg, int num, float avgExt) {
		term = t;
		avgLength=avg;
		avgLengthExtend=avgExt;
		termNum = num;
	}

	/** Returns the term of this query. */
	public Term getTerm(int k) {
		if(k < termNum)
			return term[k];
		else
			return null;
	}

	@Override
	public Weight createWeight(Searcher searcher) throws IOException {
		return new SimpleWeight(searcher,avgLength);
	}

	@Override
	public void extractTerms(Set<Term> terms) {
		for(int i = 0; i < termNum; i++)
			terms.add(getTerm(i));
	}

	/** Prints a user-readable version of this query. */
	@Override
	public String toString(String field) {
		StringBuilder buffer = new StringBuilder();
		for(int i = 0; i < termNum; i ++) {
			if (!term[i].field().equals(field)) {
				buffer.append(term[i].field());
				buffer.append(":");
			}
			buffer.append(term[i].text());
			buffer.append(ToStringUtils.boost(getBoost()));
		}
		return buffer.toString();
	}

	/** Returns true iff <code>o</code> is equal to this. */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof SimpleQuery))
			return false;
		MultiQuery other = (MultiQuery) o;
		return (this.getBoost() == other.getBoost())
				&& this.term.equals(other.term);
	}

	/** Returns a hash code value for this object. */
	@Override
	public int hashCode() {
		return Float.floatToIntBits(getBoost()) ^ term.hashCode();
	}

}
