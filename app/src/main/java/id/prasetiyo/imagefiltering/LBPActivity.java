package id.prasetiyo.imagefiltering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import id.prasetiyo.imagefiltering.adapter.ResultAdapter;
import id.prasetiyo.imagefiltering.helper.FilePicker;
import id.prasetiyo.imagefiltering.model.ResultModel;

public class LBPActivity extends AppCompatActivity {
    private final String lokasi= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/LBP/";
    private final String lokasi2= Environment.getExternalStorageDirectory().getAbsolutePath();
    private final int INTENT_CODE=1101;
    private static final int REQUEST_LOAD_IMAGE = 10011;
    private static final int REQUEST_TAKE_IMAGE = 10012;
    private static final int PICKFILE_REQUEST_CODE = 10013;
    private Uri fileUri;
    private File imageFile;
    private Bitmap bmp_asli;
    private ArrayList<String> datasheet;
    private ArrayList<ResultModel> results;
    private Button btn_query;
    private ImageView asli;
    private ListView listView;
    private String patokan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lbp);
        listView = (ListView) findViewById(R.id.listView_resurlt);
        btn_query = (Button) findViewById(R.id.btn_query);
        asli = (ImageView) findViewById(R.id.img_1);
        datasheet = new ArrayList<String>();
        results = new ArrayList<ResultModel>();

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_valid_dataset()) {return;}

                /*FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
                Mat m = Imgcodecs.imread(datasheet.get(0));
                MatOfKeyPoint o = new MatOfKeyPoint();
                detector.detect(m,o);
                writeToFile(o.dump());*/

                act_query();

                //Toast.makeText(LBPActivity.this, ""+Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Toast.LENGTH_SHORT).show();
                //Glide.with(getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").asBitmap().into(asli);

                /*Mat m = Imgcodecs.imread(datasheet.get(0));
                Bitmap b= Bitmap.createBitmap(m.rows(),m.cols(),Bitmap.Config.ARGB_8888);
                Imgproc.cvtColor(m,m,Imgproc.COLOR_BGR2RGB);
                Utils.matToBitmap(m,b);
                asli.setImageBitmap(b);*/
            }
        });


    }

    private void writeToFile(String data) {
        try {
            File file = new File(lokasi+"hasilIMG.txt");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
            //outputStreamWriter.write(data);
            //outputStreamWriter.close();
            outputStreamWriter.append(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private boolean is_valid_dataset(){
        if (datasheet.isEmpty()){
            Toast.makeText(LBPActivity.this, "Silakan load dataset terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }else if (asli.getDrawable()==null){
            Toast.makeText(LBPActivity.this, "Silakan load gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void act_query(){
        new lbp().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        try {
            menu.findItem(R.id.act_load_datasheet).setVisible(true);
            menu.findItem(R.id.act_show_datasheet).setVisible(true);
            menu.findItem(R.id.act_capture_image).setVisible(false);
        } catch (Exception e){
            e.printStackTrace();
        }

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
        /*if (id == R.id.act_capture_image) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            imageFile=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"testsdgdh.jpg");
            fileUri= Uri.fromFile(imageFile);
            i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(i, REQUEST_TAKE_IMAGE);
            return true;
        }*/
        if (id==R.id.act_load_datasheet){
            Intent i = new Intent(getApplicationContext(), FilePicker.class);
            i.putExtra(FilePicker.EXTRA_ALLOW_MULTIPLE, false);
            i.putExtra(FilePicker.EXTRA_ALLOW_CREATE_DIR, false);
            i.putExtra(FilePicker.EXTRA_MODE, FilePickerActivity.MODE_FILE);
            i.putExtra(FilePicker.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

            startActivityForResult(i, PICKFILE_REQUEST_CODE);
        }
        if (id==R.id.act_show_datasheet){
            show_datasheet();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startCrop(Uri uri){
        Intent i = new Intent(getApplicationContext(),CropImage.class);
        i.setData(uri);
        startActivityForResult(i,INTENT_CODE);
    }

    private void getRealPath(Uri data){
        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(data);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            patokan = cursor.getString(columnIndex);
        }

        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_LOAD_IMAGE && resultCode== Activity.RESULT_OK){
            getRealPath(data.getData());
            startCrop(data.getData());
        }
        /*if(requestCode==REQUEST_TAKE_IMAGE && resultCode== Activity.RESULT_OK){
            startCrop(fileUri);
        }*/
        if(requestCode==INTENT_CODE && resultCode== Activity.RESULT_OK){
            Uri imageUri = data.getData();
            try {
                bmp_asli = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile!=null && imageFile.exists()){
                imageFile.delete();
            }
            asli.setImageBitmap(bmp_asli);
        }
        if(requestCode==PICKFILE_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            Uri uri = data.getData();
            Toast.makeText(LBPActivity.this, "Datasheet : "+uri.getPath(), Toast.LENGTH_SHORT).show();
            FileInputStream is;
            BufferedReader reader;
            final File file = new File(uri.getPath());
            try {
                datasheet.clear();
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                while(line != null){
                    datasheet.add(lokasi+line);
                    line = reader.readLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!datasheet.isEmpty()){
                for (String s : datasheet) {
                    results.add(new ResultModel(s,0.0));
                }
            }
        }
    }

    private void show_datasheet(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_show_datasheet, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Datasheet");
        ListView lv = (ListView) convertView.findViewById(R.id.listView_datasheet);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datasheet);
        lv.setAdapter(adapter);
        alertDialog.show();
    }

    private double compare(String l_img2){
        //Mat img1 = new Mat(asli.getHeight(),asli.getWidth(), CvType.CV_8U);
        //Utils.bitmapToMat(((BitmapDrawable)asli.getDrawable()).getBitmap(),img1);

        Mat img1 = Imgcodecs.imread(patokan);
        Mat img2 = Imgcodecs.imread(l_img2);

        double hasil;
        Mat HSV_img1=new Mat(img1.rows(),img1.cols(), CvType.CV_8U);
        Mat HSV_img2=new Mat(img1.rows(),img1.cols(), CvType.CV_8U);
        Imgproc.cvtColor(img1,HSV_img1,Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(img2,HSV_img2,Imgproc.COLOR_BGR2HSV);

        /// Using 50 bins for hue and 60 for saturation
        int hBins = 50;
        int sBins = 60;
        MatOfInt histSize = new MatOfInt( hBins,  sBins);

        // hue varies from 0 to 179, saturation from 0 to 255
        MatOfFloat ranges =  new MatOfFloat( 0f,180f,0f,256f );

        // we compute the histogram from the 0-th and 1-st channels
        MatOfInt channels = new MatOfInt(0, 1);

        Mat hist1= new Mat();
        Mat hist2= new Mat();

        ArrayList<Mat> histImages=new ArrayList<Mat>();
        histImages.add(HSV_img1);

        Imgproc.calcHist(histImages,channels,new Mat(),hist1,histSize,ranges,false);
        Core.normalize(hist1,hist1,0,1,Core.NORM_MINMAX,-1,new Mat());

        histImages=new ArrayList<Mat>();
        histImages.add(HSV_img2);
        Imgproc.calcHist(histImages,channels,new Mat(),hist2,histSize,ranges,false);
        Core.normalize(hist2,hist2,0,1,Core.NORM_MINMAX,-1,new Mat());

        hasil = Imgproc.compareHist(hist1,hist2,Imgproc.CV_COMP_BHATTACHARYYA);

        return hasil;
    }
    private class lbp extends AsyncTask<Void,Void,Void>{

        ProgressDialog progress;

        @Override
        protected Void doInBackground(Void... params) {
            //Thread.sleep(3000);
            for (ResultModel result : results) {
                result.setJarak(compare(result.getImg()));
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = ProgressDialog.show(LBPActivity.this, "Processing","Silakan tunggu sebentar", true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            Collections.sort(results);
            ResultAdapter adapter = new ResultAdapter(LBPActivity.this,results);
            listView.setAdapter(adapter);
        }
    }
}