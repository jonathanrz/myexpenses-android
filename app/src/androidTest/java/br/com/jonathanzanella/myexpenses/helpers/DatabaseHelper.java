package br.com.jonathanzanella.myexpenses.helpers;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import br.com.jonathanzanella.myexpenses.database.MyDatabase;

/**
 * Created by jzanella on 7/24/16.
 */
public class DatabaseHelper {
	public static void reset(Context ctx) {
		FlowManager.getDatabase(MyDatabase.NAME).reset(ctx);
		FlowManager.destroy();
		FlowManager.init(new FlowConfig.Builder(ctx).build());
	}
}