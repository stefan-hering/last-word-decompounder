package org.hering

import com.carrotsearch.randomizedtesting.RandomizedRunner
import org.apache.lucene.analysis.BaseTokenStreamTestCase.assertTokenStreamContents
import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.MockTokenizer
import org.apache.lucene.analysis.compound.CompoundWordTokenFilterBase
import org.apache.lucene.analysis.compound.HyphenationCompoundWordTokenFilter
import org.apache.lucene.analysis.compound.hyphenation.HyphenationTree
import org.hering.decompounder.LastWordDecompounder
import org.junit.Test
import org.junit.runner.RunWith
import java.io.StringReader


@RunWith(RandomizedRunner::class)
class LastWordDecompounderTest {

    @Test
    fun testHyphenationCompoundWordsDA() {
        val mockTokenizer = MockTokenizer(MockTokenizer.WHITESPACE, true)
        mockTokenizer.setReader(StringReader("Wurstwasser Geflügelwurst Bratwurst"))

        val dict = CharArraySet(listOf("wurst"), true);
        val hyphenator: HyphenationTree = HyphenationCompoundWordTokenFilter
                .getHyphenationTree(this.javaClass.getResource("/de.xml").file)
        val tf = LastWordDecompounder(
                mockTokenizer,
                hyphenator,
                dict,
                3,
                CompoundWordTokenFilterBase.DEFAULT_MIN_SUBWORD_SIZE,
                CompoundWordTokenFilterBase.DEFAULT_MAX_SUBWORD_SIZE,
                false)

        assertTokenStreamContents(tf,
                arrayOf("wurstwasser", "geflügelwurst", "wurst", "bratwurst", "wurst"),
                intArrayOf(1, 1, 0, 1, 0))
    }
}