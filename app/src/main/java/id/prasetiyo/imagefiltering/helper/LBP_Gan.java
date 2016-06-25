package id.prasetiyo.imagefiltering.helper;

import android.graphics.Bitmap;

import java.text.DecimalFormat;

/**
 * Created by aoktox on 16/06/16.
 */
public class LBP_Gan {

    public static double[][] doNormalisasi(double[][] matrix, int numfile) {
        for (int i = 0; i < numfile; i++) {
            double temp = 0.0;
            for (int j = 0; j < numfile; j++) {
                temp += matrix[i][j];
            }
            for (int j = 0; j < numfile; j++) {
                matrix[i][j] = matrix[i][j] / temp;
                DecimalFormat df = new DecimalFormat("#.00000");
                matrix[i][j] = Double.parseDouble(df.format(matrix[i][j]));
            }

        }
        return matrix;
    }

    public static String[] doSaveFile(int[][][][] binLBP1a,int q,int dim,int t,int l){
        String[] lines = new String[t*l*dim];
        int num=0;
        for (int y = 0; y < t; y++) {
            for (int x = 0; x < l; x++) {
                for (int i = 0; i < dim; i++) {
                    num=((y*t)*x)*dim+i;
                    lines[num]=""+binLBP1a[q][y][x][i];
                }
            }
        }
        return lines;
    }

    public static int[][][] getLBP(Bitmap bmp,int[][][] binLBP,int p,int l, int t){
        int pixelColor;
        Bitmap out = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(),bmp.getConfig());
        int[] typeLBP=new int[2];
        for (int j = 0; j < t; j++) {
            for (int i = 0; i < l; i++) {
                int batas_x1 = i*bmp.getWidth()/l+1;
                int batas_x2 = (i+1)*bmp.getWidth()/l-1;
                int batas_y1 = i*bmp.getHeight()/t+1;
                int batas_y2 = (i+1)*bmp.getHeight()/t-1;

                for (int y = batas_y1; y < batas_y2; y++) {
                    for (int x = batas_x1; x < batas_x2; x++) {
                        pixelColor=bmp.getPixel(x,y);
                        //int value = getValueLBP(out,bmp,x,y);

                    }
                }
            }
        }
        return binLBP;
    }

    public static String[] doSaveFile2(double[][] matrix,int numfile){
        String[] lines = new String[numfile];
        for (int i = 0; i < numfile; i++) {
            for (int j = 0; j < numfile; j++) {
                lines[i]+=matrix[i][j]+" ";
            }
        }
        return lines;
    }

}
