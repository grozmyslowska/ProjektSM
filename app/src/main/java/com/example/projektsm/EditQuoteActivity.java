package com.example.projektsm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class EditQuoteActivity extends AppCompatActivity {

    public static final String EXTRA_EDIT_CONTENT = "CONTENT";
    public static final String EXTRA_EDIT_BOOK_TITLE = "BOOK_TITLE";
    public static final String EXTRA_EDIT_BOOK_AUTHOR = "BOOK_AUTHOR";

    private EditText editContentEditText;
    private EditText editTitleEditText;
    private EditText editAuthorEditText;

    SensorManager sensorManager;
    Sensor sensor;
    float x2 = 0;
    float y2 = 0;
    float z2 = 10;
    long lastUpdate = 0;

    Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quote);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        editContentEditText = findViewById(R.id.edit_content);
        editTitleEditText = findViewById(R.id.edit_book_title);
        editAuthorEditText = findViewById(R.id.edit_book_author);

        final Button buttonPhoto = findViewById(R.id.button_take_photo);
        final Button button = findViewById(R.id.button_save);
        final Button buttonWeb = findViewById(R.id.web_page_button);


        String content = getIntent().getStringExtra("CONTENT");
        String title = getIntent().getStringExtra("BOOK_TITLE");
        String author = getIntent().getStringExtra("BOOK_AUTHOR");

        if(title != null && author != null && content != null){
            editContentEditText.setText(content);
            editTitleEditText.setText(title);
            editAuthorEditText.setText(author);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent replyIntent = new Intent();
                    if(TextUtils.isEmpty(editTitleEditText.getText()) || TextUtils.isEmpty(editAuthorEditText.getText()) || TextUtils.isEmpty(editContentEditText.getText())){
                        setResult(RESULT_CANCELED, replyIntent);
                    }
                    else {
                        String content = editContentEditText.getText().toString();
                        replyIntent.putExtra(EXTRA_EDIT_CONTENT, content);

                        String title = editTitleEditText.getText().toString();
                        replyIntent.putExtra(EXTRA_EDIT_BOOK_TITLE, title);

                        String author = editAuthorEditText.getText().toString();
                        replyIntent.putExtra(EXTRA_EDIT_BOOK_AUTHOR, author);

                        setResult(RESULT_OK, replyIntent);
                    }
                    finish();
                }
            });
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();
                if(TextUtils.isEmpty(editTitleEditText.getText()) || TextUtils.isEmpty(editAuthorEditText.getText()) || TextUtils.isEmpty(editContentEditText.getText())){
                    setResult(RESULT_CANCELED, replyIntent);
                }
                else {
                    String content = editContentEditText.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_CONTENT, content);

                    String title = editTitleEditText.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_BOOK_TITLE, title);

                    String author = editAuthorEditText.getText().toString();
                    replyIntent.putExtra(EXTRA_EDIT_BOOK_AUTHOR, author);

                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });

        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        buttonWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = "https://lubimyczytac.pl/szukaj/ksiazki?phrase=";
                address += getIntent().getStringExtra("BOOK_TITLE");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(address));
                startActivity(intent);
            }
        });

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            detectText();

        }
    }

    private void detectText(){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                editContentEditText.setText(firebaseVisionText.getText());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DetectedText","nie znaleziono tekstu");
            }
        });
    }
}
