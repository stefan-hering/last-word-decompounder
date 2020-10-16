package org.hering.decompounder

import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase
import org.apache.lucene.analysis.compound.HyphenationCompoundWordTokenFilter
import org.apache.lucene.analysis.compound.hyphenation.Hyphenation
import org.apache.lucene.analysis.compound.hyphenation.HyphenationTree


class LastWordDecompounder : CompoundWordTokenFilterBase {
    private val hyphenator: HyphenationTree


    /**
     * Creates a new [HyphenationCompoundWordTokenFilter] instance.
     *
     * @param input
     * the [org.apache.lucene.analysis.TokenStream] to process
     * @param hyphenator
     * the hyphenation pattern tree to use for hyphenation
     * @param dictionary
     * the word dictionary to match against.
     */
    constructor(input: TokenStream, hyphenator: HyphenationTree, dictionary: CharArraySet?)
            : this(input, hyphenator, dictionary, DEFAULT_MIN_WORD_SIZE, DEFAULT_MIN_SUBWORD_SIZE, DEFAULT_MAX_SUBWORD_SIZE, false)

    /**
     * Creates a new [HyphenationCompoundWordTokenFilter] instance.
     *
     * @param input
     * the [org.apache.lucene.analysis.TokenStream] to process
     * @param hyphenator
     * the hyphenation pattern tree to use for hyphenation
     * @param dictionary
     * the word dictionary to match against.
     * @param minWordSize
     * only words longer than this get processed
     * @param minSubwordSize
     * only subwords longer than this get to the output stream
     * @param maxSubwordSize
     * only subwords shorter than this get to the output stream
     * @param onlyLongestMatch
     * Add only the longest matching subword to the stream
     */
    constructor(input: TokenStream,
                hyphenator: HyphenationTree, dictionary: CharArraySet?, minWordSize: Int,
                minSubwordSize: Int, maxSubwordSize: Int, onlyLongestMatch: Boolean) : super(input, dictionary, minWordSize, minSubwordSize, maxSubwordSize,
            onlyLongestMatch) {
        this.hyphenator = hyphenator
    }

    /**
     * Create a HyphenationCompoundWordTokenFilter with no dictionary.
     *
     *
     * Calls [ HyphenationCompoundWordTokenFilter(matchVersion, input, hyphenator,][.HyphenationCompoundWordTokenFilter]
     */
    constructor(input: TokenStream,
                hyphenator: HyphenationTree, minWordSize: Int, minSubwordSize: Int,
                maxSubwordSize: Int) : this(input, hyphenator, null, minWordSize, minSubwordSize,
            maxSubwordSize, false)

    /**
     * Create a HyphenationCompoundWordTokenFilter with no dictionary.
     *
     *
     * Calls [ HyphenationCompoundWordTokenFilter(matchVersion, input, hyphenator,][.HyphenationCompoundWordTokenFilter]
     */
    constructor(input: TokenStream, hyphenator: HyphenationTree)
            : this(input, hyphenator, DEFAULT_MIN_WORD_SIZE, DEFAULT_MIN_SUBWORD_SIZE, DEFAULT_MAX_SUBWORD_SIZE)

    override fun decompose() {
        // get the hyphenation points
        val hyphens: Hyphenation = hyphenator.hyphenate(termAtt.buffer(), 0, termAtt.length, 1, 1) ?: return
        // No hyphen points found -> exit
        val hyp = hyphens.hyphenationPoints
        for (i in hyp.indices) {
            val remaining = hyp.size - i
            val start = hyp[i]
            var lastMatch: CompoundToken? = null
            var wasEnd = false
            for (j in 1 until remaining) {
                val partLength = hyp[i + j] - start

                // if the part is longer than maxSubwordSize we
                // are done with this round
                if (partLength > maxSubwordSize) {
                    break
                }

                // we only put subwords to the token stream
                // that are longer than minPartSize
                if (partLength < minSubwordSize) {
                    // BOGUS/BROKEN/FUNKY/WACKO: somehow we have negative 'parts' according to the
                    // calculation above, and we rely upon minSubwordSize being >=0 to filter them out...
                    continue
                }

                // check the dictionary
                if (dictionary == null || dictionary.contains(termAtt.buffer(), start, partLength)) {
                    lastMatch = CompoundToken(start, partLength)
                    wasEnd = j == remaining - 1
                } else if (dictionary.contains(termAtt.buffer(), start, partLength - 1)) {
                    CompoundToken(start, partLength)
                }
            }

            if (lastMatch != null && wasEnd) {
                tokens.add(lastMatch)
            }
        }
    }

}