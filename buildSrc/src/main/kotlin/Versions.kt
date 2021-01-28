@file:Suppress("unused", "KDocMissingDocumentation")
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

// https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources

@Suppress("MemberVisibilityCanBePrivate", "Reformat")
object Versions {
    const val kotlin = "1.4.30-RC"
    const val springBoot = "2.4.2"

    object Plugins {
        const val kotlin = Versions.kotlin
        const val allOpen = kotlin
        const val noArg = kotlin
        const val kapt = kotlin

        const val springBoot = Versions.springBoot
        const val testLogger = "2.1.1"
        const val allure = "2.8.1"

        const val jib = "2.7.1"
        const val vplugin = "3.0.5"
        const val versions = "0.36.0"
        const val detekt = "1.15.0"
        const val dokka = "1.4.20"
        const val sweeney = "4.2.0"
        const val owaspDependencyCheck = "6.0.5"
        const val snyk = "0.4"
        const val asciidoctorConvert = "3.3.0"
        const val asciidoctorPdf = asciidoctorConvert
        const val asciidoctorDiagram = asciidoctorConvert
        const val jig = "2021.1.3"
        const val jk1DependencyLicenseReport = "1.16"
    }

    const val javaMin = "15.0.2"
    // FIXME IntelliJ IDEA 2020.3.x unterstuetzt nicht Java 16
    // https://www.jetbrains.com/help/idea/supported-java-versions.html
    //const val javaMax = "16"
    const val javaMax = javaMin

    // fuer Cloud Native Buildpack innerhalb der Task "bootBuildImage"
    //val javaSourceCompatibility = org.gradle.api.JavaVersion.VERSION_15
    val javaSourceCompatibility = org.gradle.api.JavaVersion.VERSION_11

    //const val kotlinLanguageVersion = "1.5"
    const val kotlinLanguageVersion = "1.4"
    const val kotlinApiVersion = kotlinLanguageVersion
    val kotlinJvmTarget = org.gradle.api.JavaVersion.VERSION_11.majorVersion

    val jibJava = org.gradle.api.JavaVersion.VERSION_11.majorVersion

    const val yavi = "0.5.0"
    const val annotations = "20.1.0"
    const val jansi = "2.1.1"
    const val springSecurityRsa = "1.0.9.RELEASE"

    // -------------------------------------------------------------------------------------------
    // Versionsnummern aus BOM-Dateien ueberschreiben
    // siehe org.springframework.boot:spring-boot-dependencies
    //    https://github.com/spring-projects/spring-boot/blob/master/spring-boot-dependencies/pom.xml
    // -------------------------------------------------------------------------------------------

    const val jackson = "2.12.1"
    //const val junitJupiterBom = "5.7.0"
    //const val kotlinCoroutines = "1.4.2"
    const val log4j2 = "2.14.0"
    const val mongodb = "4.2.0-beta1"
    const val mongoDriverReactivestreams = mongodb
    const val nettyCodecHttp = "4.1.58.Final"
    //const val reactiveStreams = "1.0.3"
    //const val reactorBom = "2020.0.3"
    // fuer kapt mit spring-context-indexer
    const val springBom = "5.3.3"
    const val springDataBom = "2021.0.0-M2"
    //const val springDataMongoDB = "3.2.0-M1"
    //const val springGraalvmNative = "0.8.2"
    const val springHateoas = "1.3.0-M1"
    const val springSecurityBom = "5.5.0-M1"
    //const val thymeleaf = "3.0.12.RELEASE"
    // org.apache.tomcat.embed:tomcat-embed-core   javax/servlet/Servlet -> jakarta/servlet/Servlet
    //const val tomcat = "10.0.0-M7"
    //const val tomcat = "9.0.41"

    const val kotest = "4.4.0.RC2"
    const val mockk = "1.10.5"

    const val ktlint = "0.40.0"
    const val ktlintKotlin = kotlin
    const val httpClientKtlint = "4.5.13"
    //const val jacocoVersion = "0.8.7-SNAPSHOT"
    const val jacocoVersion = "0.8.6"
    const val allure = "2.13.8"
    const val allureCommandline = allure
    const val allureJunit = allure
    // https://repo.spring.io/libs-milestone
    const val aspectjweaver = "1.9.7.M1"
    const val asciidoctorj = "2.4.2"
    const val asciidoctorjDiagram = "2.1.0"
    const val asciidoctorjPdf = "1.5.4"
}
