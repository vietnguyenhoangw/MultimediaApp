package com.example.changeprofilepicapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.suke.widget.SwitchButton;

import org.michaelbel.bottomsheet.BottomSheet;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    LinearLayout ln_item;

    CircleImageView profileImage;

    private int PICK_PHOTO_REQUEST_CODE = 200;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1001;
    private static final int REQUEST = 1000;
    private File photoFile;

    MediaPlayer mediaPlayer;
    com.suke.widget.SwitchButton bgMusicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bgMusicSwitch = findViewById(R.id.switch_button);
        profileImage = findViewById(R.id.profile_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet();
            }
        });

        ln_item = findViewById(R.id.fvr_songs);
        ln_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRawAudio();

                Intent intent = new Intent(MainActivity.this, FavoriteSongsActivity.class);
                startActivity(intent);
            }
        });


        bgMusicSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (bgMusicSwitch.isChecked()) {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.solo_music);

                    playRawAudio();
                }
                else {
                    stopRawAudio();
                }
            }
        });
    }

    /* Create bottom sheet (UI, listener...) */
    public void bottomSheet() {
        String[] item = new String[] {
                "Camera",
                "Photos",
        };

        int[] icon = new int[] {
                R.drawable.ic_camera_24dp,
                R.drawable.ic_photo_library_24dp,
        };

        BottomSheet.Builder builder = new BottomSheet.Builder(this);
        builder.setTitle("Change your profile image.")
                .setCellHeight(200)
                .setWindowDimming(125)
                .setItems(item, icon, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                onLaunchCamera();
                                break;
                            case 1:
                                onPickPhoto();
                                break;
                        }
                    }
                })
                .show();
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }


    /* request permissions */
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                },
                REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST && grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onLaunchCamera();
            onPickPhoto();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /* launch camera */
    private void onLaunchCamera() {
        if (checkPermissions()) {
            requestPermissions();
        }
        else {
            /* Create Intent to access the camera */
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            /* ceate save images file and access in future */
            String photoFileName = "IMG_" + System.currentTimeMillis();
            photoFile = getPhotoFileUri(photoFileName);

            // bao bọc file bởi ContentProvider
            /* cover file by content provider */
            Uri fileProvider = FileProvider.getUriForFile(this, "com.example.changeprofilepicapp", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
            if (intent.resolveActivity(getPackageManager()) != null) {

                // start intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    /* getPhotoFile*/
    public File getPhotoFileUri(String fileName) {
        /* using "getExternalFileDir" to not need to use request external read/write runtime permissions */
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MuiltimediaApp");

        /* Create save images file if file is not exist */
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("MuiltimediaApp", "failed to create directory");
        }

        /* return the file object corresponding to file name */
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    /* pic image */
    private void onPickPhoto() {
        if (checkPermissions()) {
            requestPermissions();
        }
        else {
            /* Create intent to pick images from Gallery */
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            if (intent.resolveActivity(getPackageManager()) != null) {
            /* start activity to get images */
                startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
            }
        }
    }

    /*
    *  take launch camera action or picimages action
    * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*
         Requestcode equal take photo
         */
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                /* at this time, images is avaiable from camera to save on file*/
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                /* take photo to Storage */
                MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        takenImage,
                        "demo_image",
                        "demo_image"
                );
                /* RESIZE BITMAP, to  avoidOutOfMemoryError when show image on UI */
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 200);

                /* setimage was taken on imageview */
                profileImage.setImageBitmap(takenImage);

            } else { /* handle in picture was not taken case. */
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        /*
         Requestcode equal pick a photo
         */
        if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri photoUri = data.getData();
                    /* get image's uri from data */
                    Bitmap selectedImage = null;
                    try { // create bitmap image corresponding to uri
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /* set image (bitmap) on imageview */
                    if (selectedImage != null) {
                        profileImage.setImageBitmap(selectedImage);
                    }
                }
            }
        }
    }

    /*
       play and stop background music
     */
    private void playRawAudio() {
        MediaPlayer.create(this, R.raw.solo_music);

        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopRawAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // check switch button music is checked

    private void mediaPlayCheck() {
        if (mediaPlayer == null) {
            return;
        }
        else {
            if (mediaPlayer.isPlaying()) {
                return;
            }
            else {
                if (bgMusicSwitch.isChecked()) {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.solo_music);

                    playRawAudio();
                }
                else {
                    stopRawAudio();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mediaPlayCheck();
    }
}
