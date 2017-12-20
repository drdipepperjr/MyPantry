package com.example.karthikkribakaran.mypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
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
    private FragmentManager fragmentManager;
    public DBHelper db;


    SimpleDateFormat sdf = new SimpleDateFormat(GroceryItem.MY_FORMAT);

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View main;
        public TextView name;
        public TextView quantity;
        public TextView expiration;
        public com.pchmn.materialchips.ChipView tag;
        public LinearLayout ll;
        public ViewHolder(View v) {
            super(v);
            main = v;
            this.name = v.findViewById(R.id.name);
            this.quantity = v.findViewById(R.id.quantity);
            this.expiration = v.findViewById(R.id.expiration);
            this.tag = v.findViewById(R.id.tag);
            v.setBackgroundColor(Color.parseColor("#f0f0f0"));
            //Log.v("background:","" + v.getBackground() );
           this.ll = v.findViewById(R.id.linearlayout);
            //ll.setBackgroundColor(Color.parseColor("#567845"));
        }
    }

    public GroceriesAdapter(List<GroceryItem> myDataset, Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.mDataset = myDataset;
        this.fragmentManager = fragmentManager;
        db = new DBHelper(context);
    }

    @Override
    public GroceriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_view, parent, false);
        Log.v("background:","" + v.getDrawingCacheBackgroundColor() );
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final GroceryItem curr = mDataset.get(position);
        holder.name.setText(curr.title);



        DecimalFormat df = new DecimalFormat("###.#");
        holder.quantity.setText(String.format(" - " + df.format(curr.price) + " left"));

        Date exp = curr.date;
        Date currDate = new Date(System.currentTimeMillis());

        holder.tag.setLabel(curr.tag);

        int daysUntilExpiration = daysBetween(currDate, exp);

        String expirationText = "";
        if (daysUntilExpiration < 0) {
            expirationText = "Expired " + Integer.toString(-daysUntilExpiration) +" days ago";
            //holder.ll.setBackgroundColor(Color.parseColor("#567845"));
            holder.ll.setBackgroundColor(Color.RED);
        } else if (daysUntilExpiration == 0) {
            expirationText = "Expires today";
            holder.ll.setBackgroundColor(Color.RED);
        } else if (daysUntilExpiration <= 14) {
            expirationText = "Expires in " + Integer.toString(daysUntilExpiration) + " days";
        } else {
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
                                    // edit
                                    dialog.dismiss();
                                    editGrocery(item);
                                } else if (which == 1) {
                                    // delete
                                    dialog.dismiss();
                                    db.deleteItem(curr.title, sdf.format(curr.date));
                                    mDataset = db.getAllItems();
                                    notifyDataSetChanged();
                                } else {
                                    // consume/waste
                                    dialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle(item.title + " - Quantity Wasted");
                                    LinearLayout layout = (LinearLayout) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                                            .inflate(R.layout.waste_dialog, null);
                                    TextView text = layout.findViewById(R.id.total_quantity);
                                    DecimalFormat df = new DecimalFormat("###.#");
                                    text.setText(" / " + df.format(item.quantity));
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
                                                            wasteGrocery(item.title, item.date, Double.parseDouble(qty), curr.quantity, curr.tag);
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

    public void wasteGrocery(String title, Date expiration, double quantityWasted, double totalQuantity, String tag) {
        db.deleteItem(title, sdf.format(expiration));
        db.insertUsedItem(title, totalQuantity - quantityWasted, quantityWasted, tag);
        mDataset = db.getAllItems();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public int daysBetween(Date d1, Date d2){
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public void editGrocery(GroceryItem groceryItem) {
        Fragment addGrocery = AddGrocery.newInstance(groceryItem, true);
        fragmentManager.beginTransaction().replace(android.R.id.content, addGrocery).addToBackStack(null).commit();
    }
}
