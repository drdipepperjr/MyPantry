package com.example.karthikkribakaran.mypantry;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddGrocery.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddGrocery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGrocery extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddGrocery() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddGrocery.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGrocery newInstance(String param1, String param2) {
        AddGrocery fragment = new AddGrocery();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add_grocery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        /*
        FloatingActionButton addButton = getView().findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment addGrocery = new AddGrocery();
                getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, addGrocery).addToBackStack(null).commit();
            }
        });
        */
        Button addGrocery = (Button) getView().findViewById(R.id.add);
        addGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText title = getView().findViewById(R.id.title);
                EditText price = getView().findViewById(R.id.price);
                EditText quantity = getView().findViewById(R.id.quantity);
                EditText expiration = getView().findViewById(R.id.expiration);
                try {
                    GroceryItem groceryItem = new GroceryItem(title.getText().toString(), Double.parseDouble(price.getText().toString()), Integer.parseInt(quantity.getText().toString()), expiration.getText().toString());
                    Log.v("Add Another","" + title.getText() + " " + price.getText()
                            + " " + quantity.getText()
                            + " " + expiration.getText());
                    Log.v("Add Another","" + groceryItem.Title + " " + groceryItem.Price
                            + " " + groceryItem.Quantity
                            + " " + groceryItem.Date);
                }
                catch (Exception e){};


            }
        });


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
}
