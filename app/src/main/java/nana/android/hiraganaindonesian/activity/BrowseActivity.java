package nana.android.hiraganaindonesian.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.FileNotFoundException;

import nana.android.hiraganaindonesian.R;

public class BrowseActivity extends AppCompatActivity {

    ImageView citra;
    String uri;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        Intent choosePicture = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePicture, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        final ImageView citra = (ImageView) findViewById(R.id.display);

        if (resultCode == RESULT_OK) {
            Uri imageFileUri = intent.getData();

            Display currentDisplay = getWindowManager().getDefaultDisplay();
            int dw = currentDisplay.getWidth();
            int dh = currentDisplay.getHeight() / 2 - 100;
            try {
                // Load up the image's dimensions not the image itself
                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                bmpFactoryOptions.inJustDecodeBounds = true;

                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null,
                        bmpFactoryOptions);

                int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) dh);
                int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) dw);

                if (heightRatio > 1 && widthRatio > 1) {
                    if (heightRatio > widthRatio) {
                        bmpFactoryOptions.inSampleSize = heightRatio;
                    } else {
                        bmpFactoryOptions.inSampleSize = widthRatio;
                    }
                }
                bmpFactoryOptions.inJustDecodeBounds = false;
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null,
                        bmpFactoryOptions);

                citra.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                Log.v("ERROR", e.toString());
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_file, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.translate_fix:
                finish();
                finishActivity(0);
                Intent intent = new Intent(this, TranslateActivity.class);
                CaptureActivity.bmpHasil = bmp;
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
