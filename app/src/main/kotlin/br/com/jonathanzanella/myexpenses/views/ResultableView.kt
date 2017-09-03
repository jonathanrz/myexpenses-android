package br.com.jonathanzanella.myexpenses.views

import android.content.Intent

interface ResultableView {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}
