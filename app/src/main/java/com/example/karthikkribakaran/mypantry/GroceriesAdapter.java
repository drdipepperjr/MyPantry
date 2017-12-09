package com.example.karthikkribakaran.mypantry;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import org.w3c.dom.Text;

/**
 * Created by karthikkribakaran on 12/8/17.
 */

public class GroceriesAdapter extends RecyclerView.Adapter<GroceriesAdapter.ViewHolder> {
    private List<GroceryItem> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView quantity;
        public TextView expiration;
        public ViewHolder(View v) {
            super(v);
            this.name = v.findViewById(R.id.name);
            this.quantity = v.findViewById(R.id.quantity);
            this.expiration = v.findViewById(R.id.expiration);
        }
    }

    public GroceriesAdapter(List<GroceryItem> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public GroceriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_view, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GroceryItem curr = mDataset.get(position);
        holder.name.setText(curr.Title);

        DecimalFormat df = new DecimalFormat("0.00");

        holder.quantity.setText(String.format(" - %d", curr.Quantity));

        Date exp = curr.Date;
        Date currDate = new Date(System.currentTimeMillis());

        int daysUntilExpiration = daysBetween(currDate, exp);

        String expirationText = "";
        if (daysUntilExpiration < 0) {
            expirationText = "Expired";
        } else if (daysUntilExpiration == 0) {
            expirationText = "Expires today";
        } else if (daysUntilExpiration <= 14) {
            expirationText = "Expires in " + Integer.toString(daysUntilExpiration) + " days";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(GroceryItem.MY_FORMAT);
            expirationText = "Expires "+ sdf.format(curr.Date);
        }

        holder.expiration.setText(expirationText);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public int daysBetween(Date d1, Date d2){
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
