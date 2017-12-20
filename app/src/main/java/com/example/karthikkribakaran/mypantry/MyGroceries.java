package com.example.karthikkribakaran.mypantry;

import android.content.Context;
import android.database.Cursor;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DividerItemDecoration;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyGroceries.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyGroceries#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyGroceries extends Fragment {
    private RecyclerView mRecyclerView;
    private GroceriesAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    public DBHelper db;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyGroceries() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyGroceries.
     */
    // TODO: Rename and change types and number of parameters
    public static MyGroceries newInstance(String param1, String param2) {
        MyGroceries fragment = new MyGroceries();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = new DBHelper(this.getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_groceries, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FloatingActionButton addButton = getView().findViewById(R.id.addGroceries);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment addGrocery = AddGrocery.newInstance(new GroceryItem(), false);
                getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, addGrocery).addToBackStack(null).commit();
            }
        });

        FloatingActionButton sortButton = getView().findViewById(R.id.sort);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Sort")
                        .setItems(R.array.sort, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    // date
                                    dialog.dismiss();
                                    mAdapter = new GroceriesAdapter(sortByDate(), getActivity(), getActivity().getSupportFragmentManager());
                                    mRecyclerView.setAdapter(mAdapter);

                                } else if (which == 1) {
                                    // tag
                                    dialog.dismiss();
                                    mAdapter = new GroceriesAdapter(sortByTag(), getActivity(), getActivity().getSupportFragmentManager());
                                    mRecyclerView.setAdapter(mAdapter);

                                } else if (which == 2){
                                    // title
                                    dialog.dismiss();
                                    mAdapter = new GroceriesAdapter(sortByName(), getActivity(), getActivity().getSupportFragmentManager());
                                    mRecyclerView.setAdapter(mAdapter);
                                } else if (which == 3){
                                    // quantity
                                    dialog.dismiss();
                                    mAdapter = new GroceriesAdapter(sortByQuantity(), getActivity(), getActivity().getSupportFragmentManager());
                                    mRecyclerView.setAdapter(mAdapter);
                                } else if (which == 4){
                                    // price
                                    dialog.dismiss();
                                    mAdapter = new GroceriesAdapter(sortByPrice(), getActivity(), getActivity().getSupportFragmentManager());
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                            }
                        });
                builder.create().show();
            }
        });

        mRecyclerView = getView().findViewById(R.id.groceriesRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        //mAdapter = new GroceriesAdapter(getGroceries(), getActivity(), getActivity().getSupportFragmentManager());
        mAdapter = new GroceriesAdapter(sortByDate(), getActivity(), getActivity().getSupportFragmentManager());
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private List<GroceryItem> getGroceries() {
        ArrayList<GroceryItem> db2 = db.getAllItems();
        Collections.sort(db2, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem groceryItem, GroceryItem t1) {
                return groceryItem.date.compareTo(t1.date);
            }
        });
        return db2;
    }

    public List<GroceryItem> sortByName(){
        ArrayList<GroceryItem> db2 = db.getAllItems();
        Collections.sort(db2, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem groceryItem, GroceryItem t1) {
                return groceryItem.title.compareTo(t1.title);
            }
        });
        //mAdapter = new GroceriesAdapter(db2, getActivity(), getActivity().getSupportFragmentManager());
        //mRecyclerView.setAdapter(mAdapter);
        return db2;

    }

    public List<GroceryItem> sortByDate(){
        ArrayList<GroceryItem> db2 = db.getAllItems();
        Collections.sort(db2, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem groceryItem, GroceryItem t1) {
                return groceryItem.date.compareTo(t1.date);
            }
        });
        //mAdapter = new GroceriesAdapter(db2, getActivity(), getActivity().getSupportFragmentManager());
        //mRecyclerView.setAdapter(mAdapter);
        return db2;
    }

    public List<GroceryItem> sortByPrice(){
        ArrayList<GroceryItem> db2 = db.getAllItems();
        Collections.sort(db2, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem groceryItem, GroceryItem t1) {
                return groceryItem.price > t1.price? -1: (groceryItem.price < t1.price) ? 1 : 0;
            }
        });
        //mAdapter = new GroceriesAdapter(db2, getActivity(), getActivity().getSupportFragmentManager());
        //mRecyclerView.setAdapter(mAdapter);
        return db2;
    }

    public List<GroceryItem> sortByTag(){
        ArrayList<GroceryItem> db2 = db.getAllItems();
        Collections.sort(db2, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem groceryItem, GroceryItem t1) {
                return groceryItem.tag.compareTo(t1.tag);
            }
        });
        //mAdapter = new GroceriesAdapter(db2, getActivity(), getActivity().getSupportFragmentManager());
        //mRecyclerView.setAdapter(mAdapter);
        return db2;
    }

    public List<GroceryItem> sortByQuantity(){
        ArrayList<GroceryItem> db2 = db.getAllItems();
        Collections.sort(db2, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem groceryItem, GroceryItem t1) {
                return groceryItem.quantity > t1.quantity? -1: (groceryItem.quantity < t1.quantity) ? 1 : 0;
            }
        });
        //mAdapter = new GroceriesAdapter(db2, getActivity(), getActivity().getSupportFragmentManager());
        //mRecyclerView.setAdapter(mAdapter);
        return db2;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.gen_recipes:
                List<GroceryItem> selected = mAdapter.getSelected();
                if (selected.size() < 1) {
                    Toast.makeText(getActivity(), "No items selected", Toast.LENGTH_LONG).show();
                    return true;
                }
                Fragment recipeGen = RecipeGenerator.newInstance(selected);
                getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, recipeGen).addToBackStack(null).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
