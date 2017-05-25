
import java.io.IOException;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;

/**
 * Expert: A <code>Scorer</code> for documents matching a <code>Term</code>.
 */
final class MultiScorer extends Scorer {

	private float[] idf;
	private final TermDocs[] termDocs;
	private float weightValue;
	private final byte[] norms;
	private int doc = -1;
	private int[] freq;
	private int termNum;
	private int curTerm;
	private Term[] term;

	private final int[] docs = new int[32]; // buffered doc numbers
	private final int[] freqs = new int[32]; // buffered term freqs
	private int pointer;
	private int pointerMax;

	private static final int SCORE_CACHE_SIZE = 32;
	private final float[] scoreCache = new float[SCORE_CACHE_SIZE];

	private float avgLength;
	private float avgLengthExtend;
	private float K1 = 2.0f;
	private float b = 0.75f;
	
	private IndexSearcher searcher;

	public void setBM25Params(float aveParam) {
		avgLength = aveParam;
	}

	public void setBM25Params(float aveParam, float kParam, float bParam) {
		avgLength = aveParam;
		K1 = kParam;
		b = bParam;
	}

	/**
	 * Construct a <code>SimpleScorer</code>.
	 * 
	 * @param weight
	 *            The weight of the <code>Term</code> in the query.
	 * @param td
	 *            An iterator over the documents matching the <code>Term</code>.
	 * @param similarity
	 *            The </code>Similarity</code> implementation to be used for
	 *            score computations.
	 * @param norms
	 *            The field norms of the document fields for the
	 *            <code>Term</code>.
	 */
	MultiScorer(Weight weight, TermDocs[] td, Similarity similarity, byte[] norms,
			float[] idfValue, float avg, IndexSearcher searcher, int tn, Term[] t, float avgExt) {
		super(similarity, weight);

		this.termDocs = td;
		this.norms = norms;
		this.weightValue = weight.getValue();
		this.idf = idfValue;
		this.avgLength = avg;
		this.searcher = searcher;
		this.termNum = tn;
		this.freq = new int[termNum];
		this.term = t;
		this.avgLengthExtend = avgExt;

		for (int i = 0; i < SCORE_CACHE_SIZE; i++)
			scoreCache[i] = getSimilarity().tf(i) * weightValue;
	}

	@Override
	public void score(Collector c) throws IOException {
		for(int i = 0; i < termNum; i ++) {
			curTerm = i;
			score(c, Integer.MAX_VALUE, nextDoc());
		}
	}

	// firstDocID is ignored since nextDoc() sets 'doc'
	@Override
	protected boolean score(Collector c, int end, int firstDocID)
			throws IOException {
		c.setScorer(this);
		while (doc < end) { // for docs in window
			c.collect(doc); // collect score

			if (++pointer >= pointerMax) {
				pointerMax = termDocs[curTerm].read(docs, freqs); // refill buffers
				if (pointerMax != 0) {
					pointer = 0;
				} else {
					termDocs[curTerm].close(); // close stream
					doc = Integer.MAX_VALUE; // set to sentinel value
					return false;
				}
			}
			doc = docs[pointer];
			for(int i = 0; i < termNum; i ++) {
				TermDocs tempTermDocs = searcher.getIndexReader().termDocs(term[i]);
				if(tempTermDocs.skipTo(doc) && tempTermDocs.doc() == doc) {
					freq[i] = tempTermDocs.freq();
				}
				else 
					freq[i] = 0;
			}
		}
		return true;
	}

	@Override
	public int docID() {
		return doc;
	}

	@Override
	public float freq() {
		return freq[curTerm];
	}

	/**
	 * Advances to the next document matching the query. <br>
	 * The iterator over the matching documents is buffered using
	 * {@link TermDocs#read(int[],int[])}.
	 * 
	 * @return the document matching the query or NO_MORE_DOCS if there are no
	 *         more documents.
	 */
	@Override
	public int nextDoc() throws IOException {
		pointer++;
		if (pointer >= pointerMax) {
			pointerMax = termDocs[curTerm].read(docs, freqs); // refill buffer
			if (pointerMax != 0) {
				pointer = 0;
			} else {
				termDocs[curTerm].close(); // close stream
				return doc = NO_MORE_DOCS;
			}
		}
		doc = docs[pointer];
		freq[curTerm] = freqs[pointer];
		return doc;
	}

	@Override
	public float score() {
		assert doc != -1;
		//TODO: implements BM25 ranking algorithm
		float docLength = 0, score = 0;
		float norm = Similarity.decodeNorm(norms[doc]);
		try {
			if(term[curTerm].field() == "title")
				docLength = searcher.doc(doc).get("title").length();
				//System.out.println(searcher.doc(doc).get("abstract") + " " + docLength + " " + freq[0]);
			else 
				docLength = searcher.doc(doc).get("content").length();
				//docLength = 1 / (norm * norm);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		float avg = (term[curTerm].field() == "title" ? avgLength : avgLengthExtend);
		//System.out.print(avg + " ");
		float K = K1 * (1 - b + b * docLength / avg);
		for(int i = 0; i < termNum; i ++)
			score += idf[i] * (K1 + 1) * freq[i] / (K + freq[i]);
		return score;
	}

	/**
	 * Advances to the first match beyond the current whose document number is
	 * greater than or equal to a given target. <br>
	 * The implementation uses {@link TermDocs#skipTo(int)}.
	 * 
	 * @param target
	 *            The target document number.
	 * @return the matching document or NO_MORE_DOCS if none exist.
	 */
	@Override
	public int advance(int target) throws IOException {
		// first scan in cache
		for (pointer++; pointer < pointerMax; pointer++) {
			if (docs[pointer] >= target) {
				freq[curTerm] = freqs[pointer];
				return doc = docs[pointer];
			}
		}

		// not found in cache, seek underlying stream
		boolean result = termDocs[curTerm].skipTo(target);
		if (result) {
			pointerMax = 1;
			pointer = 0;
			docs[pointer] = doc = termDocs[curTerm].doc();
			freqs[pointer] = freq[curTerm] = termDocs[curTerm].freq();
		} else {
			doc = NO_MORE_DOCS;
		}
		return doc;
	}

	/** Returns a string representation of this <code>SimpleScorer</code>. */
	@Override
	public String toString() {
		return "scorer(" + weight + ")";
	}

}
