package com.example.karthikkribakaran.mypantry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthikkribakaran on 12/19/17.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private Context context;

    public RecipeAdapter(List<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_view, parent, false);
        Log.v("background:","" + v.getDrawingCacheBackgroundColor() );
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View main;
        public TextView label;
        public TextView calories;
        public ViewHolder(View v) {
            super(v);
            main = v;
            this.label = v.findViewById(R.id.label);
            this.calories = v.findViewById(R.id.calories);
        }
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.ViewHolder holder, int position) {
        final Recipe curr = recipes.get(position);
        holder.label.setText(curr.label);
        holder.calories.setText(Integer.toString(curr.calories) + " calories");

        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(curr.recipeURL));
                context.startActivity(i);
            }
        });
    }



    @Override
    public int getItemCount() {
        return recipes.size();
    }
}
