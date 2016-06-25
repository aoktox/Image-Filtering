package id.prasetiyo.imagefiltering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final int INTENT_CODE=1101;
    private static final int REQUEST_LOAD_IMAGE = 10011;
    private static final int REQUEST_TAKE_IMAGE = 10012;
    private Uri fileUri;
    private File imageFile;
    private Bitmap bmp_asli,bmp_hasil;
    private ImageView img_asli,img_hasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        new CVLoader().execute();

        img_asli = (ImageView) findViewById(R.id.img_origin);
        img_hasil = (ImageView) findViewById(R.id.img_hasil);
        getBMP();
    }

    private void getBMP(){
        bmp_asli = ((BitmapDrawable)img_asli.getDrawable()).getBitmap();
        bmp_hasil = Bitmap.createBitmap(bmp_asli.getWidth(),bmp_asli.getHeight(),bmp_asli.getConfig());
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.act_load_image) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_LOAD_IMAGE);
            return true;
        }
        if (id == R.id.act_capture_image) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"testsdgdh.jpg");
            fileUri=Uri.fromFile(imageFile);
            i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(i, REQUEST_TAKE_IMAGE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id !=R.id.nav_lbp){
            new runFilter().execute(id);
        } else {
            startActivity(new Intent(this,LBPActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void startCrop(Uri uri){
        Intent i = new Intent(getApplicationContext(),CropImage.class);
        i.setData(uri);
        startActivityForResult(i,INTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_LOAD_IMAGE && resultCode== Activity.RESULT_OK){
            startCrop(data.getData());
        }
        if(requestCode==REQUEST_TAKE_IMAGE && resultCode== Activity.RESULT_OK){
            startCrop(fileUri);
        }
        if(requestCode==INTENT_CODE && resultCode== Activity.RESULT_OK){
            Uri imageUri = data.getData();
            try {
                bmp_asli = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            }catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile!=null && imageFile.exists()){
                if (imageFile.delete()){
                    Log.d("Sukses", "");
                }
            }
            if (findViewById(R.id.img_origin) != null) {
                img_asli.setImageBitmap(bmp_asli);
                img_hasil.setImageBitmap(bmp_asli);
            }
            getBMP();
        }
    }

    private Bitmap toGRAY(){
        bmp_hasil = Bitmap.createBitmap(bmp_asli.getWidth(),bmp_asli.getHeight(),bmp_asli.getConfig());
        Mat mat = new Mat(img_asli.getHeight(),img_asli.getWidth(), CvType.CV_8U);
        Utils.bitmapToMat(bmp_asli,mat);
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(mat,bmp_hasil);
        //img_hasil.setImageBitmap(bmp_hasil);
        return bmp_hasil;
    }

    private Bitmap sobel(){
        Mat grad_x = new Mat(bmp_asli.getWidth(),bmp_asli.getHeight(),CvType.CV_8U,new Scalar(4));
        Mat grad_y = new Mat(bmp_asli.getWidth(),bmp_asli.getHeight(),CvType.CV_8U,new Scalar(4));

        Mat mat = getMat();

        Utils.bitmapToMat(bmp_asli,mat);

        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);

        Imgproc.Sobel(mat,grad_x,CvType.CV_16S,1,0,3,1,0);
        Imgproc.Sobel(mat,grad_y,CvType.CV_16S,0,1,3,1,0);

        Core.convertScaleAbs(grad_x,grad_x);
        Core.convertScaleAbs(grad_y,grad_y);

        Core.addWeighted(grad_x,0.5,grad_y,0.5,0,mat);

        Utils.matToBitmap(mat,bmp_hasil);
        //img_hasil.setImageBitmap(bmp_hasil);
        return bmp_hasil;
    }

    private Mat getMat(){
        return new Mat(img_asli.getHeight(),img_asli.getWidth(), CvType.CV_8U);
    }

    private Bitmap laplace(){
        Mat mat = getMat();
        Mat tmp = getMat();
        Utils.bitmapToMat(bmp_asli,mat);
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_RGB2GRAY);

        Imgproc.Laplacian(mat,tmp,CvType.CV_16S);
        Core.convertScaleAbs(tmp,mat);
        Utils.matToBitmap(mat,bmp_hasil);
        //img_hasil.setImageBitmap(bmp_hasil);
        return bmp_hasil;
    }

    private Bitmap canny(){
        Mat src=getMat(), src_gray=getMat();
        Mat dst = getMat(), detected_edges=getMat();

        //int edgeThresh = 1;
        int lowThreshold=50;
        //int max_lowThreshold = 100;
        int ratio = 3;
        //int kernel_size = 3;

        Utils.bitmapToMat(bmp_asli,src);
        dst.create(src.size(),src.type());

        Imgproc.cvtColor(src,src_gray,Imgproc.COLOR_RGB2GRAY);

        Imgproc.blur( src_gray, detected_edges, new Size(3,3));
        //Imgproc.Canny(detected_edges, detected_edges, lowThreshold, lowThreshold*ratio, kernel_size);
        Imgproc.Canny(detected_edges, detected_edges,lowThreshold,lowThreshold*ratio);
        dst = new Mat(img_asli.getHeight(),img_asli.getWidth(), CvType.CV_8U,new Scalar(0));
        src.copyTo(dst,detected_edges);

        Utils.matToBitmap(detected_edges,bmp_hasil);
        //img_hasil.setImageBitmap(bmp_hasil);
        return bmp_hasil;
    }

    private Bitmap hough(){
        Mat src = getMat();
        Utils.bitmapToMat(bmp_asli,src);
        Mat gray = getMat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, gray, new Size(3,3), 2, 2);
        //Imgproc.blur(gray,gray,new Size(9,9));

        Mat circles = new Mat();
        //Imgproc.HoughCircles( gray, circles, Imgproc.HOUGH_GRADIENT, 1, 30);
        Imgproc.HoughCircles( gray, circles, Imgproc.CV_HOUGH_GRADIENT,1,30);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2BGR);
        for (int x = 0; x < circles.cols(); x++){
            double vCircle[]=circles.get(0,x);

            Point center=new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int)Math.round(vCircle[2]);
            // draw the circle center
            Imgproc.circle(src, center, 3,new Scalar(0,255,0), -1, 8, 0 );
            // draw the circle outline
            Imgproc.circle( src, center, radius, new Scalar(0,0,255), 3, 8, 0 );
        }

        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2RGB);
        Utils.matToBitmap(src,bmp_hasil);
        //img_hasil.setImageBitmap(bmp_hasil);
        return bmp_hasil;
    }

    private class CVLoader extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void...params) {
            OpenCVLoader.initDebug();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "OpenCV loaded", Toast.LENGTH_SHORT).show();
        }
    }

    private class runFilter extends AsyncTask<Integer,Void,Void>{
        ProgressDialog progress;
        Bitmap hasil;
        @Override
        protected Void doInBackground(Integer... params) {
            int id = params[0];
            if (id == R.id.filter_sobel) {
                hasil = sobel();
            } else if (id == R.id.filter_laplace) {
                hasil = laplace();
            } else if (id == R.id.filter_canny) {
                hasil = canny();
            } else if (id == R.id.filter_hough) {
                hasil = hough();
            } else if (id == R.id.filter_gray){
                hasil = toGRAY();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(MainActivity.this, "Processing","Silakan tunggu sebentar", true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    img_hasil.setImageBitmap(bmp_hasil);
                }
            });
        }
    }
}
