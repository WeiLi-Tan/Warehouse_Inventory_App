package com.example.warehouseinventoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.warehouseinventoryapp.provider.Item;
import com.example.warehouseinventoryapp.provider.ItemViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.StringTokenizer;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "LIFE_CYCLE_TRACING";
    public static final String STOCKITEMTAG = "STOCK ITEM DATA";
    private static final String DEBUG_TAG = "WEEK10_TAG";

    private EditText mEditName;
    private EditText mEditQuantity;
    private EditText mEditCost;
    private EditText mEditDesc;
    private ToggleButton mFrozenItem;

    private Boolean clearItem;
    private String name;
    private String quantity;
    private String cost;
    private String description;
    private Boolean frozenItem;

    private DrawerLayout drawerlayout;
    private NavigationView navigationView;
    Toolbar toolbar;

    private RecyclerAdapter adapter;
    ItemViewModel mItemViewModel;

    private int down_x;
    private int down_y;
    private View constraintLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.drawer_activity_main);

        mEditName = (EditText) findViewById(R.id.txt_item_name);
        mEditQuantity = (EditText) findViewById(R.id.txt_quantity);
        mEditCost = (EditText) findViewById(R.id.txt_cost);
        mEditDesc = (EditText) findViewById(R.id.txt_description);
        mFrozenItem = (ToggleButton) findViewById(R.id.togglebtn_frozen_item);

        // request permission to access SMS
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);
        /* Create and instantiate the local broadcast receiver
        This class listens to messages come from class SMSReceiver
         */
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();

        /*
         * Register the broadcast handler with the intent filter that is declared in
         * class SMSReceiver @line 11
         * */
        registerReceiver(myBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER));

        //Week 5
        drawerlayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerlayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new MyNavigationListener());

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Item saved!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        adapter = new RecyclerAdapter();

        mItemViewModel=new ViewModelProvider(this).get(ItemViewModel.class);
        mItemViewModel.getAllItems().observe(this, newData -> {
            adapter.setData(newData);
            adapter.notifyDataSetChanged();
        });

        constraintLayoutView = findViewById(R.id.constraint_id);
        constraintLayoutView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getActionMasked();
                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        down_x = (int) event.getX();
                        down_y = (int) event.getY();
                        Log.d(DEBUG_TAG,"Action was DOWN");
                        return true;
                    case (MotionEvent.ACTION_MOVE):
                        Log.d(DEBUG_TAG,"Action was MOVE");
                        return true;
                    case (MotionEvent.ACTION_UP):
                        if (Math.abs(down_y - event.getY()) < 40){
                            if (down_x - event.getX() < 0){
                                addNewItem(mEditName);
                            } else {
                                clearAllFields(mEditName);
                            }
                        }
                        Log.d(DEBUG_TAG,"Action was UP");
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        /*
         * This method 'onReceive' will get executed every time class SMSReceive sends a broadcast
         * */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "receive message");
            /*
             * Retrieve the message from the intent
             * */
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            /*
             * String Tokenizer is used to parse the incoming message
             * The protocol is to have the account holder name and account number separate by a semicolon
             * */
            StringTokenizer sT = new StringTokenizer(msg, ";");
            name = sT.nextToken();
            quantity = sT.nextToken();
            // change the number format of cost shown in UI to one decimal place
            float costFloat = Float.parseFloat(sT.nextToken());
            cost = String.format("%.01f", costFloat);

            description = sT.nextToken();
            if (sT.nextToken().equals("true")) {
                frozenItem = true;
            } else {
                frozenItem = false;
            }

            // update UI with the value obtained from string tokenizer
            mEditName.setText(name);
            mEditQuantity.setText(quantity);
            mEditCost.setText(cost);
            mEditDesc.setText(description);
            mFrozenItem.setChecked(frozenItem);
        }
    }

    class MyNavigationListener implements NavigationView.OnNavigationItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // get the id of the selected item
            int id = item.getItemId();

            if (id == R.id.item_id_1) {
                addNewItem(mEditName);
            } else if (id == R.id.item_id_2) {
                clearAllItem();
            } else if (id == R.id.item_id_3) {
                showStock(mEditName);
            }

            // close the drawer
            drawerlayout.closeDrawers();
            // tell the OS
            return true;
        }
    }

    // week 5
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.optionItem_id_1) {
            addNewItem(mEditName);
        } else if (id == R.id.optionItem_id_2) {
            clearAllItem();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        clearItem = false;

        // restore persistent data from shared preference
        SharedPreferences myData = getSharedPreferences("f1",0);

        name = myData.getString("name","");
        quantity = myData.getString("quantity","0");
        cost = myData.getString("cost","0.0");
        description = myData.getString("description","");
        frozenItem = myData.getBoolean("frozenItem",false);

        // show the saved data on each corresponding view
        mEditName.setText(name);
        mEditQuantity.setText(quantity);
        mEditCost.setText(cost);
        mEditDesc.setText(description);
        mFrozenItem.setChecked(frozenItem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");

        // remove the key-value pairs from SharedPreferences if use clicked the button to clear all item
        if (clearItem) {
            SharedPreferences myData = getSharedPreferences("f1",0);
            SharedPreferences.Editor myEditor = myData.edit();
            myEditor.remove("name").remove("quantity").remove("cost")
                    .remove("description").remove("frozenItem");
            myEditor.apply();

        } else {
            // else save the latest value of all the view into SharedPreferences
            name = mEditName.getText().toString();
            quantity = mEditQuantity.getText().toString();
            cost = mEditCost.getText().toString();
            description = mEditDesc.getText().toString();
            frozenItem = mFrozenItem.isChecked();

            SharedPreferences myData = getSharedPreferences("f1",0);
            SharedPreferences.Editor myEditor = myData.edit();
            myEditor.putString("name", name).putString("quantity", quantity)
                    .putString("cost", cost).putString("description",description)
                    .putBoolean("frozenItem",frozenItem);
            myEditor.apply();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void showToast(View view){
        String name = mEditName.getText().toString();
        Toast toast = Toast.makeText(this, "New item (" + name + ") has been added", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void clearAllFields(View view) {
        mEditName.setText("");
        mEditQuantity.setText("");
        mEditCost.setText("");
        mEditDesc.setText("");
        mFrozenItem.setChecked(false);
        // set clearItem to true if the user click the clear all item button
        clearItem = true;
    }

    /*
    retrieve values in the Text view and stored as stockitem in an arraylist and display toast message
    upon completion
     */
    public void addNewItem(View view) {
        name = mEditName.getText().toString();
        quantity = mEditQuantity.getText().toString();
        cost = mEditCost.getText().toString();
        description = mEditDesc.getText().toString();
        frozenItem = mFrozenItem.isChecked();
        Item newItem = new Item(name, quantity, cost, description, Boolean.toString(frozenItem));
        // insert item to database via ViewModel
        mItemViewModel.insert(newItem);

        // display toast message after the item is being stored
        showToast(mEditName);
    }

    /*
    called when the list all items in navigation drawer is clicked to create and run ShowStock activity
     */
    public void showStock(View view) {
        Intent intent = new Intent(view.getContext(), ShowStock.class);
        startActivity(intent);
    }

    // add a new method to clear all items in database when the options to clear all items is being chosen
    public void clearAllItem(){
        mItemViewModel.deleteAll();
    }
}
