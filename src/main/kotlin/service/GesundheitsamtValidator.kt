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
import am.ik.yavi.builder.forEach
import am.ik.yavi.builder.konstraint
import am.ik.yavi.core.ViolationMessage
import com.acme.gesundheitsamt.entity.Gesundheitsamt
import org.springframework.stereotype.Service

@Service
class GesundheitsamtValidator(gesundheitsamtpositionValidator: GesundheitsamtpositionValidator) {
    private val validator = ValidatorBuilder.of<Gesundheitsamt>()
        .konstraint(Gesundheitsamt::gesundheitsamtpositionen) {
            notEmpty().message(
                ViolationMessage.of(
                    "gesundheitsamt.gesundheitsamtpositionen.notEmpty",
                    "At least one order item is required.",
                )
            )
        }
        .forEach(Gesundheitsamt::gesundheitsamtpositionen,gesundheitsamtpositionValidator.validator)
        .build()

    /**
     * Validierung eines Entity-Objekts der Klasse [Gesundheitsamt]
     *
     * @param gesundheitsamt Das zu validierende gesundheitsamt-Objekt
     * @return Eine Liste mit den Verletzungen der Constraints oder eine leere Liste
     */
    fun validate(gesundheitsamt: Gesundheitsamt) = validator.validate(gesundheitsamtg)
}
