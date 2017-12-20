package com.example.karthikkribakaran.mypantry;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


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
    private String title;
    private Double quantity;
    private Double price;
    private Date date;
    private String tag;

    private boolean isEdit;

    private EditText titleView;
    private EditText priceView;
    private EditText quantityView;
    private EditText expirationView;
    private EditText tagView;


    DBHelper db;

    Calendar myCalendar = Calendar.getInstance();

    private OnFragmentInteractionListener mListener;

    public AddGrocery() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddGrocery.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGrocery newInstance(GroceryItem groceryItem, boolean isEdit) {
        AddGrocery fragment = new AddGrocery();
        Bundle args = new Bundle();
        args.putString("title", groceryItem.title);
        args.putDouble("quantity", groceryItem.quantity);
        args.putDouble("price", groceryItem.price);
        fragment.setDate(groceryItem.date);
        args.putString("tag", groceryItem.tag);
        fragment.setArguments(args);
        fragment.isEdit = isEdit;
        return fragment;
    }

    private void setDate(Date date) {
        this.date = date;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
            quantity = getArguments().getDouble("quantity");
            price = getArguments().getDouble("price");
            tag = getArguments().getString("tag");
        }

        db = new DBHelper(this.getContext());
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

        EditText edittext= getView().findViewById(R.id.expiration);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                return true;
            }
        });

        Button addGrocery = getView().findViewById(R.id.add);

        titleView = getView().findViewById(R.id.title);
        priceView = getView().findViewById(R.id.price);
        quantityView = getView().findViewById(R.id.quantity);
        expirationView = getView().findViewById(R.id.expiration);
        tagView = getView().findViewById(R.id.tag);

        if (isEdit) {
            populateFields();
        }

        addGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToDatabase(titleView, priceView, quantityView, expirationView, tagView);
//                try {
//                    // TODO: add grocery item directly to DB instead of creating GroceryItem object
//                    // since GroceryItem object requires id. Temporarily putting in id of 1.
//
//                    GroceryItem groceryItem = new GroceryItem(title.getText().toString(), Double.parseDouble(price.getText().toString()), Integer.parseInt(quantity.getText().toString()), getDate(expiration.getText().toString()), 1, "tag");
//                    Log.v("Add Another","" + title.getText() + " " + price.getText()
//                            + " " + quantity.getText()
//                            + " " + expiration.getText());
//                    Log.v("Add Another","" + groceryItem.title + " " + groceryItem.Price
//                            + " " + groceryItem.Quantity
//                            + " " + groceryItem.Date);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
                if (isEdit) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    titleView.getText().clear();
                    priceView.getText().clear();
                    quantityView.getText().clear();
                    expirationView.getText().clear();
                    tagView.getText().clear();
                }
            }
        });

        Button finished = getView().findViewById(R.id.button3);
        finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });


    }

    public void addToDatabase(EditText titleView, EditText priceView, EditText quantityView, EditText expirationView, EditText tagView) {
        String newTitle;
        Double newPrice;
        Double newQuantity;
        String newExpiration;
        String newTag;
        try {
            newTitle = titleView.getText().toString();
            newPrice = Double.parseDouble(priceView.getText().toString());
            newQuantity = Double.parseDouble(quantityView.getText().toString());
            newTag = tagView.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat(GroceryItem.MY_FORMAT);
            newExpiration = sdf.format(getDate(expirationView.getText().toString()));
            if (isEdit) {
                db.updateItem(title, sdf.format(date), newTitle, newQuantity, newExpiration, newPrice, newTag);
            } else {
                db.insertItem(newTitle, newQuantity, newExpiration, newPrice, newTag);
            }
        } catch (Exception e) {
            Log.v( "add grocery attempt", e.toString());
            Toast.makeText(getActivity(), R.string.invalid_input, Toast.LENGTH_LONG).show();
            return;
        }
        System.out.println("ADD: " + title + ", " + Double.toString(price) + ", " + Double.toString(quantity) + ", " + newExpiration.toString());
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

    public void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(GroceryItem.MY_FORMAT, Locale.US);

        EditText expiration = getView().findViewById(R.id.expiration);
        expiration.setText(sdf.format(myCalendar.getTime()));
    }

    public Date getDate(String date) {
        try {
            DateFormat formatter = new SimpleDateFormat(GroceryItem.MY_FORMAT);
            return formatter.parse(date);
        } catch (ParseException exp) {
            exp.printStackTrace();
        }
        return null;
    }

    private void populateFields() {
        titleView.setText(title);
        priceView.setText(Double.toString(quantity));
        quantityView.setText(Double.toString(price));

        SimpleDateFormat sdf = new SimpleDateFormat(GroceryItem.MY_FORMAT, Locale.US);
        expirationView.setText(sdf.format(date));

        tagView.setText(tag);
    }
}
