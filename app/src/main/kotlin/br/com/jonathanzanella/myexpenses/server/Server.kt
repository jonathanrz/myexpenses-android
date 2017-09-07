package br.com.jonathanzanella.myexpenses.server

import android.content.Context
import br.com.jonathanzanella.myexpenses.account.AccountInterface
import br.com.jonathanzanella.myexpenses.bill.BillInterface
import br.com.jonathanzanella.myexpenses.card.CardInterface
import br.com.jonathanzanella.myexpenses.expense.ExpenseInterface
import br.com.jonathanzanella.myexpenses.receipt.ReceiptInterface
import br.com.jonathanzanella.myexpenses.source.SourceInterface
import br.com.jonathanzanella.myexpenses.sync.ServerData
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class Server(context: Context) {
    private val retrofit: Retrofit
    private val serverData: ServerData = ServerData(context)

    private inner class HeaderInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            request = request.newBuilder()
                    .addHeader("Auth-token", serverData.serverToken)
                    .build()
            return chain.proceed(request)
        }
    }

    init {
        val dateTimeJsonSerializer = JsonSerializer<DateTime> { src, _, _ -> if (src == null) null else JsonPrimitive(src.millis) }
        val dateTimeJsonDeserializer = JsonDeserializer<DateTime> { json, _, _ -> if (json == null) null else DateTime(json.asLong) }
        val gson = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(DateTime::class.java, dateTimeJsonSerializer)
                .registerTypeAdapter(DateTime::class.java, dateTimeJsonDeserializer)
                .create()

        val client = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor())
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
                .build()

        retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(serverData.serverUrl)
                .client(client)
                .build()
    }

    internal fun serverInterface(): ServerInterface {
        return retrofit.create(ServerInterface::class.java)
    }

    fun sourceInterface(): SourceInterface {
        return retrofit.create(SourceInterface::class.java)
    }

    fun accountInterface(): AccountInterface {
        return retrofit.create(AccountInterface::class.java)
    }

    fun billInterface(): BillInterface {
        return retrofit.create(BillInterface::class.java)
    }

    fun cardInterface(): CardInterface {
        return retrofit.create(CardInterface::class.java)
    }

    fun expenseInterface(): ExpenseInterface {
        return retrofit.create(ExpenseInterface::class.java)
    }

    fun receiptInterface(): ReceiptInterface {
        return retrofit.create(ReceiptInterface::class.java)
    }

    companion object {
        private val TIMEOUT = 120
    }
}
