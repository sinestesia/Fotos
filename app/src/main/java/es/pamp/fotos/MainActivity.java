package es.pamp.fotos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE =1;
    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    ImageView imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageview = (ImageView) findViewById(R.id.imageView);

        Button fotoBoton =(Button) findViewById(R.id.fotoBoton);
        fotoBoton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (i.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(i,REQUEST_IMAGE_CAPTURE);

                };
            };
        });
        Button guardarBoton = (Button) findViewById(R.id.guardarBoton);
        guardarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i.resolveActivity(getPackageManager()) != null) {
                    File foto =null;
                    try {
                        foto = createImageFile();
                    }
                    catch(IOException ex){}
                    if (foto != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "es.pamp.fotos.fileprovider",
                                foto);
                        i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
                    }
                };
            }

            ;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast toast = Toast.makeText(getApplicationContext(),"Imagen guardada",Toast.LENGTH_LONG);
        BitmapFactory bitmapFactory = new BitmapFactory();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            if (data != null){
                Bundle extras = data.getExtras();
                Bitmap imageBitMap =(Bitmap) extras.get("data");

                imageview.setImageBitmap(imageBitMap);

            }
            else
                
                setPic();
                toast.show();

        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );


        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageview.getWidth();
        int targetH = imageview.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageview.setImageBitmap(bitmap);
    }
}
