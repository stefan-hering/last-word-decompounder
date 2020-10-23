package org.hering

import org.elasticsearch.common.settings.Settings
import org.elasticsearch.env.Environment
import org.elasticsearch.index.IndexSettings
import org.elasticsearch.index.analysis.TokenFilterFactory
import org.elasticsearch.indices.analysis.AnalysisModule
import org.elasticsearch.plugins.AnalysisPlugin
import org.elasticsearch.plugins.Plugin
import org.hering.decompounder.LastWordDecompounderFactory


class LastWordDecompounderPlugin() : Plugin(), AnalysisPlugin {

    override fun getTokenFilters(): MutableMap<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> {
        return mutableMapOf("last_word_decompounder" to DecompounderFactoryFactory())
    }
}

class DecompounderFactoryFactory: AnalysisModule.AnalysisProvider<TokenFilterFactory> {
    override fun get(indexSettings: IndexSettings, environment: Environment, name: String, settings: Settings): TokenFilterFactory {
        return LastWordDecompounderFactory(indexSettings, environment, name, settings)
    }
}