package br.com.jonathanzanella.myexpenses.testRunner

/**
 * Copied from https://github.com/dpreussler/kotlin-testrunner/blob/master/kotlin-runner/src/main/kotlin/de/jodamob/kotlin/testrunner/OpenedClasses.kt
 */

import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Inherited annotation class OpenedClasses(vararg val value: KClass<*>)