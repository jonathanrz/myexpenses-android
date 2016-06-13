package br.com.jonathanzanella.myexpenses.server;

import java.util.List;

import br.com.jonathanzanella.myexpenses.models.UnsyncModel;
import rx.Subscriber;

/**
 * Created by jzanella on 6/12/16.
 */
public interface UnsyncModelApi {
    void index(Subscriber<List<? extends UnsyncModel>> subscriber);
    void save(UnsyncModel model, Subscriber<List<? extends UnsyncModel>> subscriber);
}
