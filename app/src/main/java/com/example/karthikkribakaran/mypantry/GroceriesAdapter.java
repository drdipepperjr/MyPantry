package com.example.karthikkribakaran.mypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import org.w3c.dom.Text;

/**
 * Created by karthikkribakaran on 12/8/17.
 */

public class GroceriesAdapter extends RecyclerView.Adapter<GroceriesAdapter.ViewHolder> {
    private List<GroceryItem> mDataset;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View main;
        public TextView name;
        public TextView quantity;
        public TextView expiration;
        public ViewHolder(View v) {
            super(v);
            main = v;
            this.name = v.findViewById(R.id.name);
            this.quantity = v.findViewById(R.id.quantity);
            this.expiration = v.findViewById(R.id.expiration);
        }
    }

    public GroceriesAdapter(List<GroceryItem> myDataset, Context context) {
        this.context = context;
        this.mDataset = myDataset;
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
        holder.name.setText(curr.title);

        holder.quantity.setText(String.format(" - %f", curr.quantity));

        Date exp = curr.date;
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
            expirationText = "Expires "+ sdf.format(curr.date);
        }

        holder.expiration.setText(expirationText);

        final GroceryItem item = mDataset.get(position);

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(item.title)
                        .setItems(R.array.groceries, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    dialog.dismiss();
                                    consumeGrocery(item.id);
                                } else {
                                    dialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(item.title + " - Quantity Wasted");
                                    LinearLayout layout = (LinearLayout) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                            .inflate(R.layout.waste_dialog, null);
                                    TextView text = layout.findViewById(R.id.total_quantity);
                                    text.setText(" / " + Double.toString(item.quantity));
                                    final EditText input = layout.findViewById(R.id.waste_edit);
                                    builder.setView(layout)
                                            .setPositiveButton("Done",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                            int id) {
                                                            String qty = input.getText().toString();
                                                            if (qty == null || qty.isEmpty()) {
                                                                return;
                                                            }
                                                            wasteGrocery(item.id, Integer.parseInt(qty));
                                                        }

                                                    });
                                    builder.create().show();
                                }
                            }
                        });
                builder.create().show();
            }
        });
    }

    public void consumeGrocery(int groceryId) {
        // make call to DB and mark grocery as consumed
    }

    public void wasteGrocery(int groceryId, int quantityWasted) {
        // make call to DB and mark grocery with quantity wasted
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public int daysBetween(Date d1, Date d2){
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
