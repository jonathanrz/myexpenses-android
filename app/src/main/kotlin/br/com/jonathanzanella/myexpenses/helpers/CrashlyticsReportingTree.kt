package br.com.jonathanzanella.myexpenses.helpers

import com.crashlytics.android.Crashlytics
import timber.log.Timber

class CrashlyticsReportingTree: Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            Crashlytics.logException(t)
        }

        Crashlytics.log(priority, tag, message)
    }
}
