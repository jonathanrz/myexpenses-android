package br.com.jonathanzanella.myexpenses.testRunner

/**
 * Copied from https://github.com/dpreussler/kotlin-testrunner/blob/master/kotlin-runner/src/main/kotlin/de/jodamob/kotlin/testrunner/OpenedPackages.kt
 */
import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Inherited annotation class OpenedPackages(vararg val value: String)