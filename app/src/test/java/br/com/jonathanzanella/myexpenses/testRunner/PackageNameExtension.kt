package br.com.jonathanzanella.myexpenses.testRunner

/**
 * Copied from https://github.com/dpreussler/kotlin-testrunner/blob/master/kotlin-runner/src/main/kotlin/de/jodamob/kotlin/testrunner/PackageNameExtension.kt
 */
import kotlin.reflect.KClass

internal val KClass<*>.packageName: String
    get() = qualifiedName!!.removeSuffix(".$simpleName")

internal val Class<*>.packageName: String
    get() = `package`.name