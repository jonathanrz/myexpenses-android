package br.com.jonathanzanella

import android.content.Context
import br.com.jonathanzanella.myexpenses.App
import br.com.jonathanzanella.myexpenses.injection.AppModule
import br.com.jonathanzanella.myexpenses.injection.DaggerTestComponent
import br.com.jonathanzanella.myexpenses.injection.TestComponent
import java.lang.ref.WeakReference

class TestApp: App() {
    private lateinit var testComponent: TestComponent

    override fun onCreate() {
        super.onCreate()
        TestApp.app = WeakReference(this)
    }

    override fun buildComponent() {
        super.buildComponent()
        testComponent = DaggerTestComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    companion object {
        private var app: WeakReference<TestApp>? = null

        fun getApp(): TestApp {
            return app!!.get()!!
        }

        fun getContext(): Context {
            return app!!.get()!!
        }

        fun getTestComponent(): TestComponent {
            return app!!.get()!!.testComponent
        }
    }
}