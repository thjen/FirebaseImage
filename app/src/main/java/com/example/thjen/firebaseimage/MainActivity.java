package com.example.thjen.firebaseimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button bt;
    EditText et;
    ListView lv;
    ArrayList<HinhAnh> list;

    int REQUEST_CODE_IMAGE = 1;

    DatabaseReference dataRef;
    adapter ada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = (ImageView) findViewById(R.id.imageV);
        bt = (Button) findViewById(R.id.bt);
        et = (EditText) findViewById(R.id.et);
        lv = (ListView) findViewById(R.id.lv);

        dataRef = FirebaseDatabase.getInstance().getReference();

        list = new ArrayList<>();
        ada = new adapter(this, list, R.layout.row);
        lv.setAdapter(ada);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                byte[] imageArray = ImageConvertByteArray(image);
                String stringImage = Base64.encodeToString(imageArray, Base64.DEFAULT);

                HinhAnh hinhAnh = new HinhAnh(et.getText().toString(), stringImage);

                dataRef.child("Hinh Anh").push().setValue(hinhAnh);

            }
        });

        GetData();

    }

    public byte[] ImageConvertByteArray(ImageView imageView) {

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ( requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null ) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void GetData() {

        dataRef.child("Hinh Anh").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                HinhAnh hinhAnh = dataSnapshot.getValue(HinhAnh.class);
                list.add(new HinhAnh(hinhAnh.getTen(), hinhAnh.getImage()));
                ada.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
