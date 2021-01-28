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
package com.acme.gesundheitsamt.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Transient
import org.springframework.data.annotation.Version
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.util.UUID

/**
 * Unveränderliche Daten einer gesundheitsamt. In DDD ist gesundheitsamt ist ein _Aggregate Root_.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 *
 * @property id ID einer gesundheitsamt als UUID.
 * @property version Versionsnummber in der DB.
 * @property datum Bestelldatum.
 * @property kundeId ID des zugehörigen Kunden.
 * @property gesundheitsamtpositionen Liste von [gesundheitsamtposition]
 * @property kundeNachname Nachname des Kunden. Der Nachname wird nicht in der DB gespeichert.
 */
@JsonPropertyOrder("datum", "kundeId", "kundeNachname", "bestellpositionen")
@Suppress("UnusedPrivateMember")
data class Gesundheitsamt(
    @JsonIgnore
    val id: GesundheitsamtId? = null,

    @Version
    @JsonIgnore
    val version: Int? = null,

    val datum: LocalDate = now(),

    val kundeId: KundeId,

    val gesundheitsamtpositionen: List<Gesundheitsamtposition> = emptyList(),

    @CreatedDate
    @JsonIgnore
    private val erzeugt: LocalDateTime? = null,

    @LastModifiedDate
    @JsonIgnore
    private val aktualisiert: LocalDateTime? = null,
) {
    @Transient
    @Suppress("DataClassShouldBeImmutable", "UndocumentedPublicProperty")
    var kundeNachname: String? = null

    /**
     * Vergleich mit einem anderen Objekt oder null.
     * @param other Das zu vergleichende Objekt oder null
     * @return True, falls das zu vergleichende (Kunde-) Objekt die gleiche
     *      ID und die gleiche Kunde-ID hat.
     */
    @Suppress("ReturnCount")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as gesundheitsamt
        return id == other.id && kundeId == other.kundeId
    }

    /**
     * Hashwert aufgrund der Emailadresse.
     * @return Der Hashwert.
     */
    override fun hashCode(): Int {
        val result = id?.hashCode() ?: 0
        @Suppress("MagicNumber")
        return 31 * result + kundeId.hashCode()
    }
}

typealias GesundheitsamtId = UUID
typealias KundeId = UUID
