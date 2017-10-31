package br.com.jonathanzanella

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

class TestRunner: AndroidJUnitRunner() {
    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application =
            super.newApplication(cl, TestApp::class.java.name, context)
}