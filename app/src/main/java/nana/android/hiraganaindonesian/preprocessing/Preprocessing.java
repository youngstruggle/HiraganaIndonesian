package nana.android.hiraganaindonesian.preprocessing;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import android.util.Log;

public class Preprocessing {

    static int BtsThreshold = 127;
    static int Hrzntl, Vrtcl;
    static int TmpThrsld;
    static int[][] GrayScl;
    static int[][] BlckWht;
    static int[][] pixel;
    static double r = 0.21, g = 0.71, b = 0.07;
    static String nama;

    public static Bitmap smooth(Bitmap src, double value) {
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        convMatrix.setAll(1);
        convMatrix.Matrix[1][1] = value;
        convMatrix.Factor = value + 8;
        convMatrix.Offset = 1;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

    public static Bitmap preproc(Bitmap bmp) {
        // TODO Auto-generated method stub
        Hrzntl = bmp.getHeight();
        Vrtcl = bmp.getWidth();
        GrayScl = new int[Hrzntl][Vrtcl];
        BlckWht = new int[Hrzntl][Vrtcl];
        pixel = new int[Hrzntl][Vrtcl];

        // Pixel's taken
        Bitmap FixThrsld = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
        Bitmap FixGray = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);

        for (int i = 0; i < bmp.getHeight(); i++) {
            for (int j = 0; j < bmp.getWidth(); j++) {
                pixel[i][j] = bmp.getPixel(j, i);
                // Image's turned to rgb
                GrayScl[i][j] = (int) ((Color.red(pixel[i][j]) * r) + (Color.green(pixel[i][j]) * g)
                        + (Color.blue(pixel[i][j]) * b));

                int TempGry = GrayScl[i][j];
                FixGray.setPixel(j, i, Color.rgb(TempGry, TempGry, TempGry));
            }
        }
        Log.i(null, "beres grayscale");
        createDirectoryAndSaveFile(FixGray, "grayscale", ".jpg");

        Bitmap bms = smooth(FixGray, 13);
        Log.i(null, "beres smoothing");
        createDirectoryAndSaveFile(bms, "smoothing", ".jpg");

        for (int i = 0; i < bms.getHeight(); i++) {
            for (int j = 0; j < bms.getWidth(); j++) {
                // Image's turned to binary
                if (GrayScl[i][j] >= BtsThreshold) {
                    BlckWht[i][j] = 0;
                } else {
                    BlckWht[i][j] = 255;
                }
                TmpThrsld = BlckWht[i][j];
                FixThrsld.setPixel(j, i, Color.rgb(TmpThrsld, TmpThrsld, TmpThrsld));
            }
        }
        Log.i(null, "beres thresholding");
        createDirectoryAndSaveFile(FixThrsld, "thresholding", ".jpg");


        return FixThrsld;
    } // Preproc


    public static int[][] segment(Bitmap bmcrop) {
        // TODO Auto-generated method stub
        Hrzntl = bmcrop.getHeight();
        Log.i(null, "" + Hrzntl);
        Vrtcl = bmcrop.getWidth();
        Log.i(null, "" + Vrtcl);
        GrayScl = new int[Hrzntl][Vrtcl];
        BlckWht = new int[Hrzntl][Vrtcl];
        pixel = new int[Hrzntl][Vrtcl];

        // Pixel's taken
        int[][] bres = new int[Hrzntl][Vrtcl];

        for (int i = 0; i < Hrzntl; i++) {
            for (int j = 0; j < Hrzntl; j++) {
                pixel[i][j] = bmcrop.getPixel(j, i);
                bres[i][j] = pixel[i][j];
                // Image's turned to rgb
                GrayScl[i][j] = (int) ((Color.red(pixel[i][j]) * r)
                        + (Color.green(pixel[i][j]) * g) + (Color
                        .blue(pixel[i][j]) * b));
                // Image's turned to binary
                if (GrayScl[i][j] >= BtsThreshold) {
                    bres[i][j] = 255;
                } else {
                    bres[i][j] = 0;
                }
            }
        }


        // Summing pixel of each column
        int totalh, totalv;
        int[][] tes = new int[bres.length][4];
        int k = 0;
        int a = 0;

        int[] extracth = new int[bres[0].length];
        int[][] extractv = new int[bres.length][1];
        int m = bres[0].length;

        for (int j = 0; j < bres[0].length; j++) {
            totalh = 0;
            for (int i = 0; i < bres.length; i++) {
                totalh = totalh + bres[i][j];
                extracth[j] = totalh;
            }

            // Taking image's segmentation coordinates
            if (extracth[j] != 0) {
                k++;
                if (k > 47 && extracth[j - 1] == 0) {
                    k = 1;
                } else if (j == m - 1) {
                    tes[a][0] = j - k;
                    tes[a][1] = j;
                }
            } else if (extracth[j] == 0) {
                k++;
                if (k > 47 && extracth[j - 1] != 0) {
                    tes[a][0] = j + 1 - k;
                    tes[a][1] = j;
                    a++;
                }
            }
        }

        int tg = 0;
        for (int i = 0; i < tes.length; i++) {
            if (tes[i][1] != 0) {
                tg++;
            }
        }

        int[][] tes2;
        for (int st = 0; st < tg; st++) {
            int left = tes[st][0];
            int right = tes[st][1];
            int width = right - left + 1;

            tes2 = new int[bres.length][width];
            for (int i = 0; i < tes2.length; i++) {
                int lf = left;
                totalv = 0;
                for (int j = 0; j < tes2[0].length; j++) {
                    tes2[i][j] = bres[i][lf];
                    totalv = totalv + bres[i][lf];
                    extractv[i][0] = totalv;
                    lf++;
                }
            }

            for (int i = 0; i < tes2.length; i++) {
                if (extractv[i][0] != 0) {
                    tes[st][2] = i;
                    break;
                }
            }

            for (int i = tes2.length - 1; i >= 0; i--) {
                if (extractv[i][0] != 0) {
                    tes[st][3] = i;
                    break;
                }
            }
        }

        return tes;
    } // segment

    static void createDirectoryAndSaveFile(Bitmap imageToSave, String part, String format) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/ProyekAkhir1");

        if (!direct.exists()) {
            File mainDir = new File(Environment.getExternalStorageDirectory() + "/ProyekAkhir1");
            mainDir.mkdirs();
        }

        File file = new File(new File(Environment.getExternalStorageDirectory() + "/ProyekAkhir1"),
                "Preproces_" + nama + "_" + part + format);
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
