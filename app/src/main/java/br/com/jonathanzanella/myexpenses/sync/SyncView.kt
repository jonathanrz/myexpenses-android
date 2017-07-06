package br.com.jonathanzanella.myexpenses.sync

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import br.com.jonathanzanella.myexpenses.R
import kotlinx.android.synthetic.main.view_sync.view.*
import org.apache.commons.lang3.StringUtils

class SyncView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var serverData = ServerData(context)

    init {
        View.inflate(context, R.layout.view_sync, this)

        serverUrlView.setText(serverData.serverUrl)
        serverTokenView.setText(serverData.serverToken)
        syncBtn.setOnClickListener { sync() }
    }

    internal fun sync() {
        val serverUrl = serverUrlView.text.toString()
        val serverToken = serverTokenView.text.toString()

        if (StringUtils.isEmpty(serverUrl)) {
            serverTokenView.error = context.getString(R.string.error_message_server_url_not_informed)
            return
        }

        if (StringUtils.isEmpty(serverToken)) {
            serverTokenView.error = context.getString(R.string.error_message_server_token_not_informed)
            return
        }

        serverData.updateInfo(serverUrl, serverToken)
        val i = Intent(context, SyncService::class.java)
        i.putExtra(SyncService.KEY_EXECUTE_SYNC, true)
        context.startService(i)
    }
}
