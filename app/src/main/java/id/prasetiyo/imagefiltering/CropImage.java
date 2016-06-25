package id.prasetiyo.imagefiltering;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.IOException;

public class CropImage extends AppCompatActivity {
    private CropImageView mCropView;
    private ImageButton buttonDone, buttonRotateLeft, buttonRotateRight;
    private ProgressDialog progress;

    private void initComponents(){
        buttonDone = (ImageButton) findViewById(R.id.buttonDone);
        buttonRotateLeft = (ImageButton) findViewById(R.id.buttonRotateLeft);
        buttonRotateRight = (ImageButton) findViewById(R.id.buttonRotateRight);
        buttonDone.setOnClickListener(btnListener);
        buttonRotateLeft.setOnClickListener(btnListener);
        buttonRotateRight.setOnClickListener(btnListener);
        mCropView= (CropImageView) findViewById(R.id.cropImageView);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        initComponents();

        loadImage();
        if(mCropView.getImageBitmap() == null){
            mCropView.setImageResource(R.drawable.sample5);
        }
    }

    private void loadImage(){
        Uri imageUri = getIntent().getData();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCropView.setImageBitmap(bitmap);
        mCropView.setCropMode(CropImageView.CropMode.SQUARE);
        mCropView.setOutputMaxSize(500,500);
    }

    public Uri createSaveUri() {
        return Uri.fromFile(new File(this.getCacheDir(), "cropped"));
    }

    private void showLoading(){
        if (progress == null) {
            progress = new ProgressDialog(this);
            progress.setMessage("Tunggu sebentar");
        }
        progress.show();
    }
    private void dismissLoadingDialog() {

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonDone:
                    showLoading();
                    mCropView.startCrop(createSaveUri(), mCropCallback, mSaveCallback);
                    break;
                case R.id.buttonRotateLeft:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.buttonRotateRight:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
            }
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped){
        }

        @Override
        public void onError() {
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {
            dismissLoadingDialog();
            Intent intent=getIntent();
            intent.setData(outputUri);
            setResult(RESULT_OK,intent);
            finish();//finishing activity
        }

        @Override
        public void onError() {
        }
    };
}
