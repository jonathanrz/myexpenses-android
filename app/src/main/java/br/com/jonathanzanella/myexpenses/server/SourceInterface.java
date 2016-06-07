package br.com.jonathanzanella.myexpenses.server;

import java.util.List;

import br.com.jonathanzanella.myexpenses.models.Source;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jzanella on 6/5/16.
 */
public interface SourceInterface {
    @GET("sources")
    Observable<List<Source>> index(@Query("last-updated-at") long lastUpdatedAt);
}
