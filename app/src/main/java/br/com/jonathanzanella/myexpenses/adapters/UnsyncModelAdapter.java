package br.com.jonathanzanella.myexpenses.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.models.Source;
import br.com.jonathanzanella.myexpenses.models.UnsyncModel;
import br.com.jonathanzanella.myexpenses.server.Server;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class UnsyncModelAdapter extends RecyclerView.Adapter<UnsyncModelAdapter.ViewHolder> {
	protected List<UnsyncModel> models = new ArrayList<>();

	public static class ViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.row_unsync_model_id)
		TextView serverId;
		@Bind(R.id.row_unsync_model_created_at)
		TextView createdAt;
		@Bind(R.id.row_unsync_model_updated_at)
		TextView updatedAt;
		@Bind(R.id.row_unsync_model_data)
		TextView data;
		@Bind(R.id.row_unsync_model_sync_btn)
		TextView syncBtn;
		@Bind(R.id.row_unsync_model_save_btn)
		TextView saveBtn;

        View itemView;

		WeakReference<UnsyncModelAdapter> adapterWeakReference;

		public ViewHolder(View itemView, UnsyncModelAdapter adapter) {
			super(itemView);
            this.itemView = itemView;
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);
		}

		public void setData(UnsyncModel unsyncModel) {
			serverId.setText(unsyncModel.getServerId());
            createdAt.setText("" + unsyncModel.getCreatedAt());
            updatedAt.setText("" + unsyncModel.getUpdatedAt());
            data.setText(unsyncModel.getData());

            itemView.setTag(unsyncModel);

			if(unsyncModel.getId() != 0) {
                saveBtn.setVisibility(View.GONE);
                syncBtn.setVisibility(View.VISIBLE);
			} else {
                saveBtn.setVisibility(View.VISIBLE);
                syncBtn.setVisibility(View.GONE);
			}
		}

        @OnClick(R.id.row_unsync_model_save_btn)
        void onSave() {
            UnsyncModel unsyncModel = (UnsyncModel) itemView.getTag();
            unsyncModel.save();

            UnsyncModelAdapter adapter = adapterWeakReference.get();
            adapter.models.remove(unsyncModel);
            adapter.notifyDataSetChanged();
        }

        @OnClick(R.id.row_unsync_model_sync_btn)
        void onSync() {
            final UnsyncModel unsyncModel = (UnsyncModel) itemView.getTag();
            if(StringUtils.isNotNullOrEmpty(unsyncModel.getServerId())) {
                Observable<List<Source>> source = new Server().sourceInterface().update(unsyncModel.getServerId(), (Source) unsyncModel);
                source.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Subscriber<List<Source>>() {

                            @Override
                            public void onCompleted() {
                                Log.i("UnsyncModelAdapter", "update synced");
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<Source> sources) {
                                Log.i("UnsyncModelAdapter", "update onNext");

                                UnsyncModelAdapter adapter = adapterWeakReference.get();
                                adapter.models.remove(unsyncModel);
                                adapter.notifyDataSetChanged();

                                Source source = sources.get(0);
                                Source src = ((Source) unsyncModel);
                                src.setServerId(source.getServerId());
                                src.setCreatedAt(source.getCreatedAt());
                                src.setUpdatedAt(source.getUpdatedAt());
                                src.setSync(true);
                                unsyncModel.save();
                            }
                        });
            } else {
                Observable<List<Source>> source = new Server().sourceInterface().create((Source) unsyncModel);
                source.observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Subscriber<List<Source>>() {

                            @Override
                            public void onCompleted() {
                                Log.i("UnsyncModelAdapter", "create synced");
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<Source> sources) {
                                Log.i("UnsyncModelAdapter", "create onNext");

                                UnsyncModelAdapter adapter = adapterWeakReference.get();
                                adapter.models.remove(unsyncModel);
                                adapter.notifyDataSetChanged();

                                Source source = sources.get(0);
                                Source src = ((Source) unsyncModel);
                                src.setServerId(source.getServerId());
                                src.setCreatedAt(source.getCreatedAt());
                                src.setUpdatedAt(source.getUpdatedAt());
                                src.setSync(true);
                                unsyncModel.save();
                            }
                        });
            }
        }
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_unsync_model, parent, false);
		return new ViewHolder(v, this);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.setData(models.get(position));
	}

	@Override
	public int getItemCount() {
		return models != null ? models.size() : 0;
	}

	public void addData(List<? extends UnsyncModel> models) {
        this.models.addAll(models);
	}

	public @Nullable UnsyncModel getModel(int position) {
		return models != null ? models.get(position) : null;
	}
}
