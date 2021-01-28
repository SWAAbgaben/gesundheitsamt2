/*
 * Copyright (C) 2016 - 2018 Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.gesundheitsamt.config.dev

import com.acme.gesundheitsamt.Router.Companion.ID_PATTERN
import com.acme.gesundheitsamt.config.Settings.DEV
import com.acme.gesundheitsamt.config.dev.TestDaten.gesundheitsamten
import com.acme.gesundheitsamt.entity.gesundheitsamt
import com.mongodb.reactivestreams.client.MongoCollection
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bson.Document
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Description
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.CollectionOptions
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.createCollection
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.indexOps
import org.springframework.data.mongodb.core.insert
import org.springframework.data.mongodb.core.oneAndAwait
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty.array
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty.date
import org.springframework.data.mongodb.core.schema.JsonSchemaProperty.string
import org.springframework.data.mongodb.core.schema.MongoJsonSchema

/**
 * Interface, um im Profil _dev_ die (Test-) DB neu zu laden.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
interface DbPopulate {
    /**
     * Bean-Definition, um einen CommandLineRunner für das Profil "dev" bereitzustellen, damit die (Test-) DB neu
     * geladen wird.
     * @param mongo Template für MongoDB
     * @return CommandLineRunner
     */
    @Bean
    @Description("DB neu laden")
    @Profile(DEV)
    fun dbPopulate(mongo: ReactiveMongoOperations) = CommandLineRunner {
        val logger: Logger = LogManager.getLogger(DbPopulate::class.java)
        logger.warn("Neuladen der Collection 'gesundheitsamt'")

        runBlocking {
            mongo.dropCollection<Gesundheitsamt>().awaitFirstOrNull()
            createSchema(mongo, logger)
            createIndex(mongo, logger)
            gesundheitsamten.onEach { gesundheitsamt -> mongo.insert<Gesundheitsamt>().oneAndAwait(gesundheitsamt) }
                .collect { gesundheitsamt -> logger.warn("{}", gesundheitsamt) }
        }
    }

    private suspend fun createSchema(mongoOps: ReactiveMongoOperations, logger: Logger): MongoCollection<Document> {
        // https://docs.mongodb.com/manual/core/schema-validation/
        // https://www.mongodb.com/blog/post/mongodb-36-json-schema-validation-expressive-query-syntax
        val schema = MongoJsonSchema.builder()
            .required("datum", "kundeId", "gesundheitsamtpositionen")
            .properties(
                date("datum"),
                string("kundeId").matching(ID_PATTERN),
                array("gesundheitsamtpositionen").uniqueItems(true),
            )
            .build()

        logger.info("JSON Schema fuer gesundheitsamt: {}", { schema.toDocument().toJson() })
        return mongoOps.createCollection<Gesundheitsamt>(CollectionOptions.empty().schema(schema)).awaitFirst()
    }

    private suspend fun createIndex(mongoOps: ReactiveMongoOperations, logger: Logger): String {
        logger.warn("Index fuer 'kundeId'")
        val idx = Index("kundeId", Sort.Direction.ASC).named("kundeId")
        return mongoOps.indexOps<Gesundheitsamt>().ensureIndex(idx).awaitFirst()
    }
}
