package com.example.karthikkribakaran.mypantry;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


public class MetricsMainMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DBHelper db;
    private OnFragmentInteractionListener mListener;
    ArrayList<Integer> colors = new ArrayList<>();

    public MetricsMainMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MetricsMainMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static MetricsMainMenu newInstance(String param1, String param2) {
        MetricsMainMenu fragment = new MetricsMainMenu();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        colors.add(getResources().getColor(R.color.p1));
        colors.add(getResources().getColor(R.color.p2));
        colors.add(getResources().getColor(R.color.p3));
        colors.add(getResources().getColor(R.color.p4));
        colors.add(getResources().getColor(R.color.p5));
        colors.add(getResources().getColor(R.color.p6));
        colors.add(getResources().getColor(R.color.p7));
        colors.add(getResources().getColor(R.color.p8));
        colors.add(getResources().getColor(R.color.p9));
        colors.add(getResources().getColor(R.color.p10));
        colors.add(getResources().getColor(R.color.p11));
        colors.add(getResources().getColor(R.color.p12));
        colors.add(getResources().getColor(R.color.p13));
        colors.add(getResources().getColor(R.color.p14));
        colors.add(getResources().getColor(R.color.p15));

        db=new DBHelper(this.getContext());


        getTotalWasted();
        getPieChart();
        getLineChart();
        getTopWastedItem();
        getMostExpensiveItem();
        Button finished = getView().findViewById(R.id.backButton);
        finished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metrics_main_menu, container, false);
    }
    DecimalFormat df = new DecimalFormat("#.##");

    //changes textview of monthly wasted
    private  void getTotalWasted(){
        TextView monthTotalTv = (TextView) getView().findViewById(R.id.monthTotalText);
        double amount=db.getMoneyWastedThisMonth();
        if (db.getMostWasted()==null){
            monthTotalTv.setText("No Data is Available");
            return;
        }

        monthTotalTv.setText("$"+String.valueOf(df.format(amount)));
    }

    //changes textVeiw of top wasted Item Stat
    private void getTopWastedItem(){
        TextView topWastedTv = (TextView) getView().findViewById(R.id.topWastedItem);

        Cursor item= db.getMostWasted();
        if (db.getMostWasted().getCount()==0){
            topWastedTv.setText("No Data is Available");
            return;
        }

        String itemName=null;
        double wastedValue=0;
        if(item.getCount()!=0) {
            item.moveToFirst();
            itemName = item.getString(0);
            wastedValue = item.getDouble(1);
        }
        topWastedTv.setText("This month's most wasted item: "+itemName+"\n"+"Total wasted on this item: $"+String.valueOf(df.format(wastedValue)));
    }

    //we changed least wasted to most expensive and didn't bother changing most of the names
    private void getMostExpensiveItem(){
        TextView mostExpensiveTv = (TextView) getView().findViewById(R.id.mostSpentItem);

        Cursor item= db.getMostTotalSpent();
        if (item.getCount()==0){
            mostExpensiveTv.setText("No Data is Available");
            return;
        }

        String itemName=null;
        double spentValue=0;
        if(item.getCount()!=0) {
            item.moveToFirst();
            itemName = item.getString(0);
            spentValue = item.getDouble(1);
        }
        mostExpensiveTv.setText("This month's item with highest expenditure: "+itemName+"\n"+"Total spent on this item: $"+String.valueOf(df.format(spentValue)));
    }

    //code that sets up pie chart
    private void getPieChart(){
        PieChart monthlypieChart = (PieChart) getView().findViewById(R.id.monthyPieChart);

        Description lineChartDescript= new Description();
        lineChartDescript.setText("Monthly spending waste by tag");
        monthlypieChart.setDescription(lineChartDescript);

        List<PieEntry> pieSlices= new ArrayList<>();
        //get items
        HashMap<String,Double> tagMp= db.getMoneyWastedByTag();
        if (tagMp.isEmpty()== true){
            monthlypieChart.setNoDataText("No Data is Available");
            return;
        }

        for (Map.Entry<String, Double> entry : tagMp.entrySet()) {
            String tag = entry.getKey();
            double d = entry.getValue();
            float value = (float)d;
            pieSlices.add(new PieEntry(value, tag));
        }

        PieDataSet pDataSet =new PieDataSet(pieSlices,"Tags");
        PieData pData = new PieData(pDataSet);

        pDataSet.setColors(colors);
        monthlypieChart.setData(pData);
        monthlypieChart.invalidate();
    }

    //just test function will delete later
    private  void testPie(){
        PieChart monthlypieChart = (PieChart) getView().findViewById(R.id.monthyPieChart);


        List<PieEntry> pieSlices= new ArrayList<>();

        HashMap<String,Double> tagMp= db.getMoneyWastedByTag();
        if (tagMp.isEmpty()== true){
            monthlypieChart.setNoDataText("No Data is Available");
            return;
        }

        pieSlices.add(new PieEntry(10.5f, "icecream"));
        pieSlices.add(new PieEntry(7, "soda"));
        pieSlices.add(new PieEntry(7, "vegges"));
        pieSlices.add(new PieEntry(20, "seafood"));

        PieDataSet pDataSet =new PieDataSet(pieSlices, "Monthly Wasted Percentages By Tags");
        PieData pData = new PieData(pDataSet);

        pDataSet.setColors(colors);
        monthlypieChart.setData(pData);
        monthlypieChart.invalidate();

    }

    private int getIntMonth(String monthS){
        Date date = null;
        try {
            date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(monthS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return month;
    }

    private void getLineChart(){
        LineChart yearlyLineChart= (LineChart) getView().findViewById(R.id.yearlyChart);

        //set description
        Description lineChartDescript= new Description();
        lineChartDescript.setText("Yearly spending by Month");
        yearlyLineChart.setDescription(lineChartDescript);

        //line for wasted
        List<Entry> lineEntry= new ArrayList<>();
        //line for total spent
        List<Entry> lineEntry2= new ArrayList<>();
        //get items
        Cursor months= db.getAllMonths();
        if (months.getCount()==0){
            yearlyLineChart.setNoDataText("No Data is Available");
            return;
        }
        int i=0;
        for (boolean hasItem = months.moveToFirst(); hasItem; hasItem = months.moveToNext()) {
            String monthName= months.getString(0);
            double dWasted=months.getDouble(2);
            float wastedValue = (float)dWasted;
            double dUsed=months.getDouble(1);
            float spent= wastedValue+ (float)dUsed;

            i=getIntMonth(monthName);
            lineEntry.add(new Entry(i,wastedValue));
            lineEntry2.add(new Entry(i, spent));


        }

        LineDataSet lDataSet= new LineDataSet(lineEntry, "Money Wasted");
        LineDataSet lDataSet2= new LineDataSet(lineEntry2, "Total Spent");

        //set line color
        lDataSet.setColor(Color.RED);
        lDataSet2.setColor(Color.BLUE);

        //combine data
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lDataSet);
        dataSets.add(lDataSet2);
        LineData lData = new LineData(dataSets);

        final String[] monthsList = new String[] { "Dec", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec", "Jan"};
        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return monthsList[(int) value];
            }

        };


        XAxis xAxis = yearlyLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);


        yearlyLineChart.setData(lData);
        yearlyLineChart.invalidate();


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

}
