package nana.android.hiraganaindonesian.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

import nana.android.hiraganaindonesian.R;

public class CaptureActivity extends AppCompatActivity {

    final static int CAMERA_RESULT = 0;
    public static Bitmap bmpHasil = null;
    ImageView imv;
    String imageFilePath;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hiragana.jpg";
        File imageFile = new File(imageFilePath);
        Uri imageFileUri = Uri.fromFile(imageFile);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, CAMERA_RESULT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK) {
            imv = (ImageView) findViewById(R.id.display);

            Display currentDisplay = getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight();

            BitmapFactory.Options bmpFO = new BitmapFactory.Options();
            bmpFO.inJustDecodeBounds = true;
            bmp = BitmapFactory.decodeFile(imageFilePath, bmpFO);

            int heightRatio = (int) Math.ceil(bmpFO.outHeight / (float) dh);
            int widthRatio = (int) Math.ceil(bmpFO.outWidth / (float) dw);

            Log.v("HEIGHTRATIO", "" + heightRatio);
            Log.v("WIDTHRATIO", "" + widthRatio);
            // If both of the ratios are greater than 1,
            // one of the sides of the image is greater than the screen
            if (heightRatio > 1 && widthRatio > 1) {
                if (heightRatio > widthRatio) {
                    // Height ratio is larger, scale according to it
                    bmpFO.inSampleSize = heightRatio;
                } else {
                    // Width ratio is larger, scale according to it
                    bmpFO.inSampleSize = widthRatio;
                }
            }
            // Decode it for real
            bmpFO.inJustDecodeBounds = false;
            bmp = BitmapFactory.decodeFile(imageFilePath, bmpFO);

            imv.setImageBitmap(bmp);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_capture, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.translate_fix:
                finish();
                Intent intent = new Intent(this, TranslateActivity.class);
                bmpHasil = bmp;
                // intent.putExtra("BitmapImage", bmp);
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
}
