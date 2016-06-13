package br.com.jonathanzanella.myexpenses.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.models.Source;
import br.com.jonathanzanella.myexpenses.models.UnsyncModel;
import br.com.jonathanzanella.myexpenses.server.SourceApi;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class UnsyncModelAdapter extends RecyclerView.Adapter<UnsyncModelAdapter.ViewHolder> {
	protected List<UnsyncModel> models = new ArrayList<>();
    private SourceApi sourceApi = new SourceApi();

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
        @Bind(R.id.row_unsync_model_sync_progress)
        ProgressBar progressBar;

        View itemView;

		WeakReference<UnsyncModelAdapter> adapterWeakReference;

		public ViewHolder(View itemView, UnsyncModelAdapter adapter) {
			super(itemView);
            this.itemView = itemView;
			adapterWeakReference = new WeakReference<>(adapter);

			ButterKnife.bind(this, itemView);
		}

		@SuppressLint("DefaultLocale")
        public void setData(UnsyncModel unsyncModel) {
			serverId.setText(unsyncModel.getServerId());
            createdAt.setText(String.format("%d", unsyncModel.getCreatedAt()));
            updatedAt.setText(String.format("%d", unsyncModel.getUpdatedAt()));
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
            unsyncModel.setSync(true);
            unsyncModel.save();

            UnsyncModelAdapter adapter = adapterWeakReference.get();
            adapter.models.remove(unsyncModel);
            adapter.notifyDataSetChanged();
        }

        @OnClick(R.id.row_unsync_model_sync_btn)
        void onSync() {
            syncBtn.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);

            final Source source = (Source) itemView.getTag();
            adapterWeakReference.get().sourceApi.save(source, new Subscriber<List<Source>>() {

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
                        adapter.models.remove(source);
                        adapter.notifyDataSetChanged();

                        Source serverSource = sources.get(0);
                        source.setServerId(serverSource.getServerId());
                        source.setCreatedAt(serverSource.getCreatedAt());
                        source.setUpdatedAt(serverSource.getUpdatedAt());
                        source.setSync(true);
                        source.save();
                    }
                });
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
}
