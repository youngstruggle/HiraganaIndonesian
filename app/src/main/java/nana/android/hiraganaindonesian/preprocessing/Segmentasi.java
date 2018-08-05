package nana.android.hiraganaindonesian.preprocessing;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Segmentasi {

// class Segmentation berisikan fungsi-fungsi
// yang berkaitan dengan proses Segmentasi

    // constructor
    public Segmentasi() {

    }

    // untuk menentukan seberapa banyak baris pada suatu image
    // asumsi image tidak dalam keadaan "skew"
    // asumsi image sudah dalam BW
    public int[] verticalProjection(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int count = 0;
        int[] result = new int[height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // ambil nilai aRGB
                int p = bitmap.getPixel(x, y);
                int r = (p >> 16) & 0xff;

                count += (r != 0) ? 1 : 0;
            }
            result[y] = count;
            count = 0;
        }

        return result;

    }

    // pemecahan gambar menjadi sekumpulan array of Bitmap
    // pemecahan gambar untuk bagian vertical
    public List<Bitmap> verticalSegment(Bitmap input, int[] vertProj) {

        List<Bitmap> result = new ArrayList<Bitmap>();
        int batasAtas, batasBawah = 0;

        for (int i = 0; i < vertProj.length; i++) {
            if (vertProj[i] != 0) {
                batasAtas = i;
                for (int j = i; j < vertProj.length; j++) {
                    if (vertProj[j] == 0) {
                        batasBawah = j;
                        Bitmap temp = Bitmap.createBitmap(input, 0, batasAtas, input.getWidth(), batasBawah - batasAtas + 1);
                        result.add(temp);
                        break;
                    }
                }
                i = batasBawah;
            }
        }

        return result;

    }

    // untuk menentukan banyaknya karakter dalam suatu image
    // image sudah dipecah berdasarkan vertical projection
    // image dalam BW
    public int[] horizontalProjection(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int count = 0;
        int[] result = new int[width];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // ambil nilai aRGB
                int p = bitmap.getPixel(x, y);
                int r = (p >> 16) & 0xff;

                count += (r != 0) ? 1 : 0;
            }
            result[x] = count;
            count = 0;
        }

        return result;

    }

    // pemecahan gambar menjadi sekumpulan array of Bitmap
    // pemecahan gambar untuk bagian vertical
    public List<Bitmap> horizontalSegment(Bitmap input, int[] horiProj) {

        List<Bitmap> result = new ArrayList<Bitmap>();
        int batasAtas, batasBawah = 0;

        for (int i = 0; i < horiProj.length; i++) {
            if (horiProj[i] != 0) {
                batasAtas = i;
                for (int j = i; j < horiProj.length; j++) {
                    if (horiProj[j] == 0) {
                        batasBawah = j;
                        Bitmap temp = Bitmap.createBitmap(input, batasAtas, 0, batasBawah - batasAtas + 1, input.getHeight());
                        result.add(temp);
                        break;
                    }
                }
                i = batasBawah;
            }
        }

        return result;

    }
}

