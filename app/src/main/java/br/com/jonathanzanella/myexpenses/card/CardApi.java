package br.com.jonathanzanella.myexpenses.card;

import com.raizlabs.android.dbflow.StringUtils;

import java.io.IOException;
import java.util.List;

import br.com.jonathanzanella.myexpenses.log.Log;
import br.com.jonathanzanella.myexpenses.server.Server;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModel;
import br.com.jonathanzanella.myexpenses.sync.UnsyncModelApi;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jzanella on 6/12/16.
 */
public class CardApi implements UnsyncModelApi<Card> {
	private static final String LOG_TAG = CardApi.class.getSimpleName();
	CardInterface cardInterface;

	private CardInterface getInterface() {
		if(cardInterface == null)
			cardInterface = new Server().cardInterface();
		return cardInterface;
	}

	@Override
	public List<Card> index() {
		Call<List<Card>> caller = getInterface().index(Card.greaterUpdatedAt());

		try {
			Response<List<Card>> response = caller.execute();
			if(response.isSuccessful()) {
				return response.body();
			} else {
				Log.error(LOG_TAG, "Index request error: " + response.message());
			}
		} catch (IOException e) {
			Log.error(LOG_TAG, "Index request error: " + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void save(UnsyncModel model) {
		Card card = (Card) model;
		Call<Card> caller;
		if(StringUtils.isNotNullOrEmpty(card.getServerId()))
			caller = getInterface().update(card.getServerId(), card);
		else
			caller = getInterface().create(card);

		try {
			Response<Card> response = caller.execute();
			if(response.isSuccessful()) {
				model.syncAndSave(response.body());
				Log.info(LOG_TAG, "Updated: " + card.getData());
			} else {
				Log.error(LOG_TAG, "Save request error: " + response.message() + " uuid: " + card.getUuid());
			}
		} catch (IOException e) {
			Log.error(LOG_TAG, "Save request error: " + e.getMessage() + " uuid: " + card.getUuid());
			e.printStackTrace();
		}
	}

	@Override
	public List<Card> unsyncModels() {
		return Card.unsync();
	}

	@Override
	public long greaterUpdatedAt() {
		return Card.greaterUpdatedAt();
	}
}