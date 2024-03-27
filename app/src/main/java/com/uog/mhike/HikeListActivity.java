package com.uog.mhike;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.uog.mhike.adapter.HikeAdapter;
import com.uog.mhike.database.DatabaseHelper;
import com.uog.mhike.database.Hike;

import java.util.ArrayList;
import java.util.List;

public class HikeListActivity extends AppCompatActivity {

    private List<Hike> hikeList = new ArrayList<>();
    private DatabaseHelper databaseHelper;
    private HikeAdapter hikeAdapter;
    private RecyclerView recyclerView;

    public static final int UPDATE_REQUEST=1;
    public static final int SEARCH_REQUEST=2;

    /* --------------------- Start onCreate  ------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseHelper = new DatabaseHelper(getBaseContext());
        hikeAdapter = new HikeAdapter(hikeList);
        hikeAdapter.setListener(new HikeAdapter.ClickListener() {
            @Override
            public void onButtonClick(int position, View v, long id) {
                Hike hike = hikeList.get(position);
                if(id == R.id.btnDetail){
                    //TODO goto hike detail and observation
                    gotoDetailView(hike);
                }else if(id == R.id.btnEdit){
                    //TODO pass data detail and observation
                    gotoEntry(hike);

                }else if(id == R.id.btnDelete){
                    // Delete the record
                   long  result = databaseHelper.deleteHike(hike.getId());
                   if(result != 1){
                       //data deleting error
                       new AlertDialog.Builder(getBaseContext())
                               .setTitle("Error").setMessage("Can not Delete").show();
                   }else {
                       // extract the data again
                       search("");
                   }
                }
            }
        });

        recyclerView.setAdapter(hikeAdapter);
        FloatingActionButton fab = findViewById(R.id.fabAddHike);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), HikeEntryActivity.class);
                startActivityForResult(intent,UPDATE_REQUEST);
            }
        });

        EditText txtSearch = findViewById(R.id.txtSearch);
        Button btnSearch = findViewById(R.id.btnSearch);
        Button btnAdvanceSearch = findViewById(R.id.btnAdvanceSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(txtSearch.getText().toString());
            }
        });
        btnAdvanceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),HikeAdvanceSearchActivity.class);
                startActivityForResult(intent,SEARCH_REQUEST);
            }
        });

    }//End Of onCreated

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        search("");
    }

    /* ---------------------Search the data from Database     ------------------------------*/
    private void search(String keyword){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    hikeList = databaseHelper.searchHike(keyword);
                    hikeAdapter.setHikeList(hikeList);
                    hikeAdapter.notifyDataSetChanged();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }//End Of Search Data
    /* ---------------------Pass the data to HikeEntryActivity     ------------------------------*/
    private void gotoEntry(Hike hike){
        Intent intent = new Intent(this, HikeEntryActivity.class);
        fillIntentData(intent,hike);
        startActivityForResult(intent,UPDATE_REQUEST);
    }//End Of GotoEntry

    /* ---------------------Pass the data to DetailView     ------------------------------*/
    private void gotoDetailView(Hike hike){
        Intent intent = new Intent(this, HikeDetailViewActivity.class);
        fillIntentData(intent,hike);
        startActivity(intent);
    }//End Of DetailView


    private void fillIntentData(Intent intent,Hike hike){
        intent.putExtra(Hike.ID,hike.getId());
        intent.putExtra(Hike.NAME, hike.getName());
        intent.putExtra(Hike.LOCATION, hike.getLocation());
        intent.putExtra(Hike.DATE, hike.getDate());
        intent.putExtra(Hike.PARKING, hike.getParking());
        intent.putExtra(Hike.LENGTH, hike.getLength() +"");
        intent.putExtra(Hike.DIFFICULTY, hike.getDifficulty());
        intent.putExtra(Hike.DESCRIPTION, hike.getDescription());
        intent.putExtra(Hike.ADDITIONAL1, hike.getAdditional1());
        intent.putExtra(Hike.ADDITIONAL2, hike.getAdditional2());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == UPDATE_REQUEST && resultCode == RESULT_OK){
            search("");
        } else if( requestCode==SEARCH_REQUEST && resultCode==RESULT_OK){
            String name = data.getStringExtra(Hike.NAME);
            String location = data.getStringExtra(Hike.LOCATION);
            String date = data.getStringExtra(Hike.DATE);
            String length = data.getStringExtra(Hike.LENGTH);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        hikeList = databaseHelper.searchHike(name,location,date,(length != null && !length.isEmpty()? Double.parseDouble(length) : null ));
                        hikeAdapter.setHikeList(hikeList);
                        hikeAdapter.notifyDataSetChanged();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


        }
        super.onActivityResult(requestCode, resultCode, data);
    }//End Of onActivityResult

}//End Of Class