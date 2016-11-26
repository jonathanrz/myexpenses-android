package br.com.jonathanzanella.myexpenses.testRunner

/**
 * Copied from https://github.com/dpreussler/kotlin-testrunner/blob/master/kotlin-runner-junit4/src/main/kotlin/de/jodamob/kotlin/testrunner/KotlinTestRunner.kt
 */
import org.junit.runners.BlockJUnit4ClassRunner

class KotlinTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(configureClassOpeningClassLoader(klass))