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
package com.acme.gesundheitsamt.rest

import com.acme.gesundheitsamt.entity.gesundheitsamt
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.LinkRelation
import org.springframework.hateoas.server.reactive.SimpleReactiveRepresentationModelAssembler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange

/**
 * Mit der Klasse [gesundheitsamtModelAssembler] können Entity-Objekte der Klasse [com.acme.gesundheitsamt.entity.gesundheitsamt].
 * in eine HATEOAS-Repräsentation transformiert werden.
 *
 * @author [Jürgen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 *
 * @constructor Ein gesundheitsamtModelAssembler erzeugen.
 */
@Component
class GesundheitsamtModelAssembler : SimpleReactiveRepresentationModelAssembler<Gesundheitsamt> {
    /**
     * EntityModel eines gesundheitsamt-Objektes (gemäß Spring HATEOAS) um Atom-Links ergänzen.
     * @param gesundheitsamtModel Gefundenes gesundheitsamt-Objekt als EntityModel gemäß Spring HATEOAS
     * @return Model für die gesundheitsamten mit Atom-Links für HATEOAS
     */
    override fun addLinks(gesundheitsamtModel: EntityModel<Gesundheitsamt>, exchange: ServerWebExchange):
        EntityModel<Gesundheitsamt> {
            val id = gesundheitsamtModel.content?.id

            val request = exchange.request
            val baseUri = getBaseUri(request.headers, request.uri, id)
            val idUri = "$baseUri/$id"

            val selfLink = Link.of(idUri)
            val listLink = Link.of(baseUri, LinkRelation.of("list"))
            val addLink = Link.of(baseUri, LinkRelation.of("add"))
            val updateLink = Link.of(idUri, LinkRelation.of("update"))
            val removeLink = Link.of(idUri, LinkRelation.of("remove"))
            return gesundheitsamtModel.add(selfLink, listLink, addLink, updateLink, removeLink)
        }

    /**
     * Collection mit mehreren EntityModel-Instanzen eines gesundheitsamt-Objektes (gemäß Spring HATEOAS)
     * um Atom-Links ergänzen.
     * @param resources Liste oder Menge von gesundheitsamten mit _allen_ Atom-Links für HATEOAS
     * @param exchange Objekt für eine Request-Response-Interaktion aus dem Spring Framework
     * @return Model für die Liste oder Menge von gesundheitsamten mit jeweils einem self-Links für HATEOAS
     */
    override fun addLinks(
        resources: CollectionModel<EntityModel<Gesundheitsamt>>,
        exchange: ServerWebExchange,
    ): CollectionModel<EntityModel<Gesundheitsamt>> {
        // Entities nur mit dem self-Link: Liste der Links leeren und nur mit dem self-Link befuellen
        val entityModels = resources
            .content
            .map { model ->
                val selfLink = model
                    .links
                    .getRequiredLink("self")
                model.removeLinks()
                    .add(selfLink)
            }

        return CollectionModel.of(entityModels)
    }
}
