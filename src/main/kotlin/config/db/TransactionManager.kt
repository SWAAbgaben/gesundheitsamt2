/*
 * Copyright (C) 2016 - present Juergen Zimmermann, Hochschule Karlsruhe
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
package com.acme.gesundheitsamt.config.db

import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager

/**
 * Spring-Konfiguration für den transaktionalen Zugriff auf _MongoDB_.
 *
 * @author Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
interface TransactionManager {
    /**
     * Transaktionsmanager für den transaktionalen Zugriff auf _MongoDB_  bei "Reactive Programming" bereitstellen.
     * Ein Bean vom Typ `ReactiveMongoDatabaseFactory` wird dafür benötigt.
     * @return Transaktionsmanager für _MongoDB_.
     */
    @Bean
    fun transactionManager(dbFactory: ReactiveMongoDatabaseFactory) = ReactiveMongoTransactionManager(dbFactory)
}
