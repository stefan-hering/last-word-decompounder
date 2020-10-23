package org.hering

import org.junit.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile
import java.nio.file.Paths


class DecompounderIntegrationTest {

    companion object {
        lateinit var elastic:GenericContainer<*>;
        @BeforeClass
        @JvmStatic
        fun before() {
            elastic = GenericContainer<Nothing>(
                    ImageFromDockerfile()
                            .withFileFromPath("last-word-decompounder.zip",
                                    // TODO works on my machine
                                    Paths.get("c:\\workspace\\last-word-decompounder\\target\\last-word-decompounder.zip"))
                            .withDockerfileFromBuilder { builder ->
                                builder.from("elasticsearch:7.9.3")
                                        .copy("last-word-decompounder.zip", "/tmp")
                                        .run("bin/elasticsearch-plugin", "install", "file:///tmp/last-word-decompounder.zip")
                                        .env("discovery.type", "single-node")
                                        .build()
                            })
                    .apply { withExposedPorts(9200, 9300) }

            elastic.start()
        }
    }

    @Test
    fun testWithContainer() {
    }
}