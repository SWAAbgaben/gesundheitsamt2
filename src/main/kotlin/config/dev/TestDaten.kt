/*
 * Copyright (C) 2019 - present Juergen Zimmermann, Hochschule Karlsruhe
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

import com.acme.gesundheitsamt.entity.Bestellposition
import com.acme.gesundheitsamt.entity.gesundheitsamt
import com.acme.gesundheitsamt.entity.gesundheitsamtId
import kotlinx.coroutines.flow.flowOf
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Testdaten für gesundheitsamten
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@SuppressWarnings("MagicNumber", "UnderscoresInNumericLiterals")
object TestDaten {
    private val kundeId1 = GesundheitsamtId.fromString("00000000-0000-0000-0000-000000000001")
    private val kundeId2 = GesundheitsamtId.fromString("00000000-0000-0000-0000-000000000002")
    private val kundeId4 = GesundheitsamtId.fromString("00000000-0000-0000-0000-000000000004")

    private val artikelId1 = GesundheitsamtId.fromString("20000000-0000-0000-0000-000000000001")
    private val artikelId2 = GesundheitsamtId.fromString("20000000-0000-0000-0000-000000000002")
    private val artikelId3 = GesundheitsamtId.fromString("20000000-0000-0000-0000-000000000003")
    private val artikelId4 = GesundheitsamtId.fromString("20000000-0000-0000-0000-000000000004")
    private val artikelId5 = GesundheitsamtId.fromString("20000000-0000-0000-0000-000000000005")
    private val artikelId6 = GesundheitsamtId.fromString("20000000-0000-0000-0000-000000000006")

    /**
     * Testdaten für gesundheitsamten
     */
    val gesundheitsamten = flowOf(
        Gesundheitsamt(
            id = GesundheitsamtId.fromString("10000000-0000-0000-0000-000000000001"),
            datum = LocalDate.of(2019, 1, 1),
            kundeId = kundeId1,
            gesundheitsamtpositionen = listOfNotNull(
                Gesundheitsamtposition(
                    artikelId = artikelId1,
                    einzelpreis = BigDecimal("10"),
                    anzahl = 1,
                ),
                Gesundheitsamtposition(
                    artikelId = artikelId2,
                    einzelpreis = BigDecimal("20"),
                    anzahl = 1,
                ),
            ),
        ),
        Gesundheitsamt(
            id = GesundheitsamtId.fromString("10000000-0000-0000-0000-000000000002"),
            datum = LocalDate.of(2019, 1, 2),
            kundeId = kundeId1,
            Gesundheitsamtpositionen = listOfNotNull(
                Gesundheitsamtposition(
                    artikelId = artikelId3,
                    einzelpreis = BigDecimal("30"),
                    anzahl = 3,
                ),
                Gesundheitsamtposition(
                    artikelId = artikelId4,
                    einzelpreis = BigDecimal("40"),
                    anzahl = 4,
                ),
            ),
        ),
        Gesundheitsamt(
            id = GesundheitsamtId.fromString("10000000-0000-0000-0000-000000000003"),
            datum = LocalDate.of(2019, 1, 3),
            kundeId = kundeId1,
            gesundheitsamtpositionen = listOfNotNull(
                Gesundheitsamtposition(
                    artikelId = artikelId5,
                    einzelpreis = BigDecimal("50"),
                    anzahl = 5,
                ),
                Gesundheitsamtposition(
                    artikelId = artikelId6,
                    einzelpreis = BigDecimal("60"),
                    anzahl = 6,
                ),
            ),
        ),
        Gesundheitsamt(
            id = GesundheitsamtId.fromString("10000000-0000-0000-0000-000000000004"),
            datum = LocalDate.of(2019, 1, 4),
            kundeId = kundeId2,
            gesundheitsamtpositionen = listOfNotNull(
                Gesundheitsamtposition(
                    artikelId = artikelId1,
                    einzelpreis = BigDecimal("10"),
                    anzahl = 1,
                ),
            ),
        ),
        Gesundheitsamt(
            id = GesundheitsamtId.fromString("10000000-0000-0000-0000-000000000005"),
            datum = LocalDate.of(2019, 1, 5),
            kundeId = kundeId4,
            gesundheitsamtpositionen = listOfNotNull(
                Gesundheitsamtposition(
                    artikelId = artikelId1,
                    einzelpreis = BigDecimal("10"),
                    anzahl = 1,
                ),
            ),
        ),
    )
}
