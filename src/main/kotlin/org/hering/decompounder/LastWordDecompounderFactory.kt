package org.hering.decompounder

import org.apache.lucene.analysis.TokenStream
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.env.Environment
import org.elasticsearch.index.IndexSettings
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory

class LastWordDecompounderFactory(
        indexSettings: IndexSettings?,
        environment: Environment?,
        name: String?,
        settings: Settings?,
) : AbstractTokenFilterFactory(
        indexSettings,
        name,
        settings) {

    override fun create(tokenStream: TokenStream): TokenStream {
        return LastWordDecompounder(tokenStream, null!!)
    }
}