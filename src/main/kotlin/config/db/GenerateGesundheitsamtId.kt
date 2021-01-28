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

import com.acme.gesundheitsamt.entity.Gesundheitsamt
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.mapping.event.ReactiveBeforeConvertCallback
import reactor.kotlin.core.publisher.toMono
import java.util.UUID

/**
 * Spring-Konfiguration für die ID-Generierung beim Abspeichern in _MongoDB_.
 *
 * @author Eva Fiserova
 */
interface GenerateGesundheitsamtId {
    /**
     * Bean zur Generierung der gesundheitsamt-ID beim Anlegen einer neuen gesundheitsamt
     * @return gesundheitsamt-Objekt mit einer gesundheitsamt-ID
     */
    @Bean
    fun generateGesundheitsamtId() = ReactiveBeforeConvertCallback<Gesundheitsamt> { gesundheitsamt, _ ->
        if (gesundheitsamt.id == null) {
            gesundheitsamt.copy(id = UUID.randomUUID())
        } else {
            gesundheitsamt
        }.toMono()
    }
}
