/*
 * Copyright (C) 2021 - present Juergen Zimmermann, Hochschule Karlsruhe
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
package com.acme.gesundheitsamt.service

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ViolationMessage
import com.acme.gesundheitsamt.entity.Gesundheitsamtlposition
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class GesundheitsamtpositionValidator {
    /**
     * `Validator` für _Yavi_
     */
    val validator = ValidatorBuilder.of<Gesundheitsamtposition>()
        .konstraint(Gesundheitsamtposition::einzelpreis) {
            greaterThanOrEqual(BigDecimal.ZERO).message(
                ViolationMessage.of(
                    "gesundheitsamtposition.einzelpreis.min",
                    "The unit price must be at least {1}.",
                )
            )
        }
        .konstraint(Gesundheitsamtposition::anzahl) {
            greaterThanOrEqual(1).message(
                ViolationMessage.of(
                    "gesundheitsamtposition.anzahl.min",
                    "The minimum quantity is {1}.",
                )
            )
        }
        .build()
}
