package nana.android.hiraganaindonesian.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nana.android.hiraganaindonesian.dbhelper.DatabaseHelper;
import nana.android.hiraganaindonesian.hog.Hog;
import nana.android.hiraganaindonesian.entity.Huruf;
import nana.android.hiraganaindonesian.entity.Kamus;
import nana.android.hiraganaindonesian.preprocessing.Preprocessing;
import nana.android.hiraganaindonesian.R;
import nana.android.hiraganaindonesian.preprocessing.Segmentasi;

public class TranslateActivity extends AppCompatActivity {

    Segmentasi segment = new Segmentasi();
    int[][] count;
    String huruf;
    static String nama;
    ImageView citra;
    Bitmap bmres, bmdummy;

    List<Bitmap> BitmapList;

    ArrayList<Bitmap> bmpList = new ArrayList<Bitmap>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        final ImageView citra = (ImageView) findViewById(R.id.display);

        Bitmap bmp = CaptureActivity.bmpHasil;

        citra.setImageBitmap(bmp);
        Bitmap origbmp = Bitmap.createScaledBitmap(bmp, 480, 480, false);
        //Preprocessing
        bmres = Preprocessing.preproc(origbmp);

        //count = Preprocessing.segment(bmres);
        List<Bitmap> horizontal = null;
        int count = 0;
        try {
            int[] vertProj = segment.verticalProjection(bmres);
            Log.i("TA_debug", " " + vertProj.length);
            List<Bitmap> vertical = segment.verticalSegment(bmres, vertProj);

            if (!vertical.isEmpty()) {
                for (Bitmap bmp1 : vertical) {
                    int[] horProj = segment.horizontalProjection(bmp1);
                    horizontal = segment.horizontalSegment(bmp1, horProj);
                    Log.d("Count", String.valueOf(horizontal.size()));

                    if (!horizontal.isEmpty()) {
                        count = 1;
                        for (Bitmap bmp2 : horizontal) {
                            createDirectoryAndSaveFile(bmp2, "segment_" + count, ".jpg");
                            count++;
                        }
                    } else {
                        Log.i("Horizontal", "" + horProj.length);
                    }
                }
            } else {
                Log.i("Vertical", "" + vertProj.length);
            }
        } catch (Exception e) {

        }


        double[] d = new double[horizontal.size()];

        for (int i = 0; i < horizontal.size(); i++) {
            //Fitur HOG

            Bitmap c = Bitmap.createScaledBitmap(horizontal.get(i), 160, 160, false);
            Log.d("HOG Width", String.valueOf(c.getWidth()));
            Log.d("HOG Height", String.valueOf(c.getHeight()));
            Hog fitur = new Hog(c);
            try {
                d[i] = fitur.finaldes();
                Log.d("HOG", String.valueOf(d[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this, getFilesDir().getAbsolutePath());
        try {
            dbHelper.prepareDatabase();
        } catch (IOException e) {
            Log.e("Error Get Database", e.getMessage());
        }


        //KNN-------------------------------------------------------------------------------------

        List<Huruf> listKata = dbHelper.getHuruf();
        List<Kamus> resultList = dbHelper.getKata();
        int k = 1;
        String resultKata = "";
        for (int x = 0; x < horizontal.size(); x++) {
            double[] distArray = new double[listKata.size()];
            double[] distArray2 = new double[listKata.size()];
            double dist = 0.0;
            for (int j = 0; j < listKata.size(); j++) {
                /* 2) Menghitung nilai distance untuk data yang di uji dengan data training */
                Huruf itemkata = listKata.get(j);
                dist = Math.pow(itemkata.getValue() - d[x], 2);
                /* 3) Menghitung akar */
                double distance = Math.sqrt(dist);
                distArray[j] = distance;
                distArray2[j] = distance;
                //Log.d("Distance-"+String.valueOf(j),String.valueOf(distArray[j]));
            }
            Arrays.sort(distArray);
            int[] indexArray = new int[distArray.length];
            for (int i = 0; i < distArray.length; i++) {
                //Log.d("Sort-"+String.valueOf(i),String.valueOf(distArray[i]));
                for (int l = 0; l < distArray2.length; l++) {
                    if (distArray[i] == distArray2[l]) {
                        indexArray[i] = l;
                        //Log.d("Distance-"+String.valueOf(i),String.valueOf(distArray[i])+"|"+String.valueOf(distArray2[l]));
                    }
                }
            }
            for (int m = 0; m < k; m++) {
                Huruf itemkata = listKata.get(indexArray[m]);
                String kata = itemkata.getHuruf().toString();

                //Peleburan Kata
                if (kata.equals("AP")) {
                    resultKata = resultKata.substring(0, resultKata.length() - 1) + "A";
                    //Log.d("resKata",h);
                } else if (kata.equals("OP")) {
                    String h = resultKata.substring(resultKata.length() - 3);
                    if (h.equals("SHI")) {
                        resultKata = resultKata.substring(0, resultKata.length() - 1) + "O";
                    } else {
                        resultKata = resultKata.substring(0, resultKata.length() - 2) + "YO";
                    }
                } else if (kata.equals("YP")) {

                } else if (kata.equals("OP")) {

                } else {
                    resultKata = resultKata + kata;
                }


                Log.d("ResKata", String.valueOf(x) + "," + String.valueOf(k) + "=" + itemkata.getHuruf().toString());
            }
        }
//		String[] mn = new String[2];
//		String[] kn = new String[2];
//		mn[0]  = "ZO";
//		mn[1]  = "ZI";
//		kn[0]  = "A";
//		kn[1]  = "U";

        //0,0 + 1,0 , 0,0 + 1,1 , 0,1 + 0,0 + 1+1

        //ZOA - ZOU - ZIA - ZIU
//		String rangkainHuruf = "";
//
//		for (int i = 0; i < mn.length; i++) {
//			String t = "";
//			String rangkaianHuruf = "";
//			for (int j = 0; j < kn.length; j++) {
//				rangkainHuruf = mn[i]+kn[j];
//				Log.d("Kata",rangkaianHuruf);
//			}
//		}

        //Rangkai Kata

//		String result = "";
//		for (int i = 0; i < resultKata.length; i++) {
//			result = result + resultKata[i][k];
//		}

        //String resultKata = "YAGI";
        TextView knn = (TextView) findViewById(R.id.translation);
        knn.setText("Jepang : " + resultKata);
        TextView hog = (TextView) findViewById(R.id.translation2);
        for (int i = 0; i < resultList.size(); i++) {
            Kamus res = resultList.get(i);
            if (resultKata.equals(res.getJepang())) {
                Log.d("Indonesia", res.getIndonesia());
                hog.setText(String.valueOf("Indonesia : " + res.getIndonesia()));
            }
        }


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_translate, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.main_page:
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(intent);
                return true;

            case R.id.other_file:
                Intent intent2 = new Intent(this, BrowseActivity.class);
                this.startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /* Method untuk menyimpan file */
    static void createDirectoryAndSaveFile(Bitmap imageToSave, String part, String format) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/ProyekAkhir1");

        if (!direct.exists()) {
            File mainDir = new File(Environment.getExternalStorageDirectory() + "/ProyekAkhir1");
            mainDir.mkdirs();
        }

        File file = new File(new File(Environment.getExternalStorageDirectory() + "/ProyekAkhir1"),
                "Trans_" + nama + "_" + part + format);
        if (file.exists()) {
            file.delete();
            Log.i("judul", "isi");
        } else {
            Log.i("tidak", "eksis");
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}