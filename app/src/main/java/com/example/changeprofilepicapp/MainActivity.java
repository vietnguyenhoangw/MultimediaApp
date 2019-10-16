package com.example.changeprofilepicapp;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.michaelbel.bottomsheet.BottomSheet;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    CircleImageView profileImage;

    private int PICK_PHOTO_REQUEST_CODE = 200;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1001;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileImage = findViewById(R.id.profile_image);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet();
            }
        });
    }

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

    private void onLaunchCamera() {
// tạo Intent để truy cập Camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
// tạo file lưu hình ảnh và truy xuất trong tương lai
        String photoFileName = "IMG_" + System.currentTimeMillis();
        photoFile = getPhotoFileUri(photoFileName);
// bao bọc file bởi ContentProvider
        Uri fileProvider = FileProvider.getUriForFile(this, "com.example.changeprofilepicapp", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getPackageManager()) != null) {
// thực hiện Intent để chụp ảnh
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
// Dùng “getExternalFilesDir” để không cần request external read/write runtime permissions
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "MuiltimediaApp");
// Tạo thư mục chứa hình ảnh nếu chưa tồn tại
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d("MuiltimediaApp", "failed to create directory");
        }
// Trả về đối tượng file tương ứng với file name
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
// tại thời điểm này, đã có hình ảnh từ camera lưu trên file
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                /* chup anh vao bo nho */
                MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        takenImage,
                        "demo_image",
                        "demo_image"
                );
// RESIZE BITMAP, để tránh lỗi OutOfMemoryError khi hiển thị ảnh lên UI
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 200);
// Hiển thị hình ảnh vừa chụp lên image view
                profileImage.setImageBitmap(takenImage);
            } else { // Xử lý cho trường hợp không chụp ảnh (chọn Cancel ở Camera app)
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri photoUri = data.getData();
// Lấy uri của hình ảnh từ data
                    Bitmap selectedImage = null;
                    try { // Tạo đối tượng bitmap tương ứng với uri
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
// Hiển thị hình ảnh (bitmap) được chọn lên image view
                    if (selectedImage != null) {
                        profileImage.setImageBitmap(selectedImage);
                    }
                }
            }
        }
    }

    private void onPickPhoto() {
// Tạo Intent để chọn hình ảnh từ Gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
// thực hiện Intent để chọn ảnh
            startActivityForResult(intent, PICK_PHOTO_REQUEST_CODE);
        }
    }
}
