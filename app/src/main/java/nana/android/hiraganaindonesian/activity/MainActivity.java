package nana.android.hiraganaindonesian.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import nana.android.hiraganaindonesian.dbhelper.DatabaseHelper;
import nana.android.hiraganaindonesian.entity.Huruf;
import nana.android.hiraganaindonesian.R;

public class MainActivity extends AppCompatActivity {

    private Button btnCapture, btnGaleri, btnInfo, btnExit;
    private final static String TAG = "MainActivity";
    DatabaseHelper dbHelper = null;
//    DBHelper db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        db = new DBHelper(this);

        dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        showData();

        btnCapture = (Button) findViewById(R.id.capture);
        btnGaleri = (Button) findViewById(R.id.galeri);
        btnInfo = (Button) findViewById(R.id.about);
        btnExit = (Button) findViewById(R.id.exit);

        // btnCapture
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });

        // btnGaleri
        btnGaleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BrowseActivity.class);
                startActivity(intent);
            }
        });

        // btnInfo
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//				Intent intent = new Intent(MainActivity.this, InfoActivity.class);
//				startActivity(intent);
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        // btnExit
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAlert();
            }
        });
    }

    // exit alert
    public void exitAlert() {
        new AlertDialog.Builder(MainActivity.this).setTitle("Exiting Final Project")
                .setMessage("Do you really want to exit this application?").setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Cancel", null).show();
    }

    @Override
    public void onBackPressed() {
        exitAlert();
    }

    private void showData() {
        List<Huruf> list = dbHelper.getHuruf();
        StringBuffer data = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            Huruf emp = list.get(i);
            data.append(emp.getId()).append(",").append(emp.getHuruf())
                    .append(",").append(emp.getValue()).append("||");
        }
        TextView textView = (TextView) findViewById(R.id.bodytext);
        textView.setText(Html.fromHtml(data.toString()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
