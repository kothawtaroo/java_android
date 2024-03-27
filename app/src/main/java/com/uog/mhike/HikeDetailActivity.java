package com.uog.mhike;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.uog.mhike.database.DatabaseHelper;
import com.uog.mhike.database.Hike;

public class HikeDetailActivity extends AppCompatActivity {
    String name,location, date, parking,  description,difficulty,additional1,additional2;
    private int id=0;
    private double length;
    private Hike hike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hike_detail);

        TextView lblName = findViewById(R.id.lblName);
        TextView lblLocation = findViewById(R.id.lblLocation);
        TextView lblDate = findViewById(R.id.lblDate);
        TextView lblParking = findViewById(R.id.lblParking);
        TextView lblLength = findViewById(R.id.lblLength);
        TextView lblDifficulty = findViewById(R.id.lblDifficulty);
        TextView lblDescription = findViewById(R.id.lblDescription);
        TextView lblAdditional1 = findViewById(R.id.lblAdditional1);
        TextView lblAdditional2 = findViewById(R.id.lblAdditional2);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnSave = findViewById(R.id.btnSave);

        /*-----------------------Get Data From Entry------------------------------------------------*/
        Bundle bundle = getIntent().getExtras();
        if(bundle !=null ){
            id = bundle.getInt(Hike.ID,0);
            //String name,location, date, parking, length, description,,additional1,additional2;
             name = bundle.getString(Hike.NAME);
             location = bundle.getString(Hike.LOCATION);
             date = bundle.getString(Hike.DATE);
             parking = bundle.getString(Hike.PARKING);
             length = Double.parseDouble(bundle.getString(Hike.LENGTH,"0"));
             difficulty = bundle.getString(Hike.DIFFICULTY);
             description = bundle.getString(Hike.DESCRIPTION);
             additional1 = bundle.getString(Hike.ADDITIONAL1);
             additional2 = bundle.getString(Hike.ADDITIONAL2);

            lblName.setText(name);
            lblLocation.setText(location);
            lblDate.setText(date);
            lblParking.setText(parking);
            lblLength.setText(length+ "");
            lblDifficulty.setText(difficulty);
            lblDescription.setText(description);
            lblAdditional1.setText(additional1);
            lblAdditional2.setText(additional2);

            hike = new Hike(
                    id,
                    name,
                    location,
                    date,
                    parking,
                    length,
                    difficulty,
                    description,
                    additional1,
                    additional2,
                    null,
                    null
            );
        }//End of if

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper databaseHelper = new DatabaseHelper(getBaseContext());
                long result =0;
                if(id==0){
                    //this is new record
                    result = databaseHelper.saveHike(hike);
                }else {
                    result= databaseHelper.updateHike(hike);
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
                if(result > 0){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HikeDetailActivity.this) ;
                        alertDialogBuilder.setTitle("Success").setMessage("Hike Data Saved Successfully.").setIcon(R.drawable.baseline_info_24);
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent = new Intent(HikeDetailActivity.this,HikeListActivity.class);
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                }else {
                    new AlertDialog.Builder(HikeDetailActivity.this).setTitle("Failed")
                            .setMessage("Hike Data was not saved").setIcon(R.drawable.baseline_error_24).show();

                }

            }
        });

    }//End Of onCreate

}//End Of HikeDetailActivity