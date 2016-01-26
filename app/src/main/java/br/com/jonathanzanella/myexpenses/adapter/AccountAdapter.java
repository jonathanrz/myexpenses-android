package br.com.jonathanzanella.myexpenses.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import br.com.jonathanzanella.myexpenses.R;
import br.com.jonathanzanella.myexpenses.model.Account;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jonathan Zanella on 26/01/16.
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    protected List<Account> accounts;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.row_account_name)
        TextView name;
        @Bind(R.id.row_account_balance)
        TextView balance;
        @Bind(R.id.row_account_balance_date)
        TextView balanceDate;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setData(Account acc) {
            name.setText(acc.getName());
            balance.setText(NumberFormat.getCurrencyInstance().format(acc.getBalance() / 100));
            balanceDate.setText(AccountAdapter.sdf.format(acc.getBalanceDate().toDate()));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_account, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        return accounts != null ? accounts.size() : 0;
    }

    public void loadData() {
        accounts = Account.all();
    }

    public void addAccount(@NonNull Account acc) {
        accounts.add(acc);
        notifyItemInserted(accounts.size() - 1);
    }
}
