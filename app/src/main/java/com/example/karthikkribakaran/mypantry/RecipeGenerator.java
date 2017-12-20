package com.example.karthikkribakaran.mypantry;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeGenerator.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeGenerator#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeGenerator extends Fragment {
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Recipe> recipes;
    private static List<GroceryItem> checkedGroceries;


    private OnFragmentInteractionListener mListener;

    public RecipeGenerator() {
        recipes = new ArrayList<>();
    }

    public static RecipeGenerator newInstance(List<GroceryItem> checked) {
        RecipeGenerator fragment = new RecipeGenerator();
        checkedGroceries = checked;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_generator, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = getView().findViewById(R.id.recipesRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecipeAdapter(recipes, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        getRecipes();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class GetRecipeTask extends AsyncTask<String , Void ,String> {
        String server_response;

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            processResponse(server_response);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.recipes_menu, menu);
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private void processResponse(String s) {
        try {
            JSONObject json = new JSONObject(s);
            JSONArray recipeArr = json.getJSONArray("hits");
            for (int i = 0, size = recipeArr.length(); i < size; i++)
            {
                JSONObject recipe = recipeArr.getJSONObject(i).getJSONObject("recipe");
                String label = recipe.getString("label");
                int calories = recipe.getInt("calories");
                String url = recipe.getString("url");
                recipes.add(new Recipe(label, calories, url));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        mAdapter.notifyDataSetChanged();
    }

    private void getRecipes() {
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.edamam.com/search?q=");
        String delim = "";
        for (GroceryItem gi : checkedGroceries) {
            sb.append(delim).append(gi.title);
            delim = ",";
        }
        sb.append("&app_id=4e8afd3f&app_key=65c5e629aa80f4320a5e8d88b300a8d4");
        GetRecipeTask task = new GetRecipeTask();
        task.execute(sb.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.back:
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
