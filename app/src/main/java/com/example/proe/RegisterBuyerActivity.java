package com.example.proe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterBuyerActivity extends AppCompatActivity implements LocationListener {

    private ImageButton btnback, btngps;
    private ImageView profileIv;
    private EditText etname, etemail, etpassword, etcfpassword, etshopname, etphone, etcountry, etcity, etstate, etaddress;
    private Button btnregiser;

    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE= 400;
    private static final int IMAGE_PICK_CAMERA_CODE= 500;




    private String[] locationpermissions;
    private String[] camerapermissions;
    private String[] storagepermissions;

    private Uri image_uri;

    private double latitude = 0.0,longitude = 0.0;
    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_buyer);

        btnback = findViewById(R.id.btnback);
        btngps = findViewById(R.id.btngps);
        btnregiser = findViewById(R.id.btnregister);

        profileIv = findViewById(R.id.profileIv);

        etname = findViewById(R.id.etname);
        etshopname = findViewById(R.id.etshopname);
        etphone = findViewById(R.id.etphone);
        etemail = findViewById(R.id.etemail);
        etpassword = findViewById(R.id.etpassword);
        etcfpassword = findViewById(R.id.etcfpassword);
        etcountry = findViewById(R.id.etcountry);
        etcity = findViewById(R.id.etcity);
        etstate = findViewById(R.id.etstate);
        etaddress = findViewById(R.id.etaddress);

        locationpermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        camerapermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btngps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLocationPermission()){
                    detectLocation();
                }
                else{
                    requestLocationPermissions();
                }

            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        btnregiser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

   private String Name,ShopName,Phone,Country,State,City,CompleteAddress,Email,Password,CFpassword;
    private void inputData() {
        Name = etname.getText().toString().trim();
        ShopName = etshopname.getText().toString().trim();
        Phone = etphone.getText().toString().trim();
        Country = etcountry.getText().toString().trim();
        State = etstate.getText().toString().trim();
        City = etcity.getText().toString().trim();
        CompleteAddress = etaddress.getText().toString().trim();
        Email = etemail.getText().toString().trim();
        Password = etpassword.getText().toString().trim();
        CFpassword = etcfpassword.getText().toString().trim();

        if (TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(ShopName)){
            Toast.makeText(this, "Enter Shopname", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(Phone)){
            Toast.makeText(this, "Enter Phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latitude == 0.0 || longitude == 0.0){
            Toast.makeText(this, "Please click GPS button to detect location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(this, "Invaild email pattern", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Password.length()<6){
            Toast.makeText(this, "Password must be morethan 6 ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Password.equals(CFpassword)){
            Toast.makeText(this, "Password don't matches...", Toast.LENGTH_SHORT).show();
            return;
        }
        createAccount();
    }

    private void createAccount() {
        progressDialog.setMessage("Create Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(Email,Password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        saverFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterBuyerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverFirebaseData(){
        progressDialog.setMessage("Saving Account Info...");

        final String timestamp = ""+System.currentTimeMillis();

        if ((image_uri == null)){

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("Email",""+Email);
            hashMap.put("Name",""+Name);
            hashMap.put("ShopName",""+ShopName);
            hashMap.put("Password",""+Password);
            hashMap.put("CFpassword",""+CFpassword);
            hashMap.put("Phone",""+Phone);
            hashMap.put("CompleteAddress",""+CompleteAddress);
            hashMap.put("Country",""+Country);
            hashMap.put("State",""+State);
            hashMap.put("City",""+City);
            hashMap.put("Latitude",""+latitude);
            hashMap.put("Longitude",""+longitude);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("AccountType","Buyer");
            hashMap.put("online","true");
            hashMap.put("ShopOpen","true");
            hashMap.put("profileImage","");



            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
            reference.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterBuyerActivity.this,MainBuyerActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            startActivity(new Intent(RegisterBuyerActivity.this,MainBuyerActivity.class));
                            finish();
                        }
                    });

        }
        else{
            String filePathandName = "profile_image/"+""+firebaseAuth.getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("Email",""+Email);
                                hashMap.put("Name",""+Name);
                                hashMap.put("ShopName",""+ShopName);
                                hashMap.put("Password",""+Password);
                                hashMap.put("CFpassword",""+CFpassword);
                                hashMap.put("Phone",""+Phone);
                                hashMap.put("CompleteAddress",""+CompleteAddress);
                                hashMap.put("Country",""+Country);
                                hashMap.put("State",""+State);
                                hashMap.put("City",""+City);
                                hashMap.put("Latitude",""+latitude);
                                hashMap.put("Longitude",""+longitude);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("AccountType","Buyer");
                                hashMap.put("online","true");
                                hashMap.put("ShopOpen","true");
                                hashMap.put("profileImage",""+downloadImageUri);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
                                reference.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterBuyerActivity.this,MainBuyerActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterBuyerActivity.this,MainBuyerActivity.class));
                                                finish();
                                            }
                                        });

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterBuyerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }



    private void showImagePickDialog() {
        String[] option = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick image").setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0){
                    //camera click
                    if (checkCameraPermission()){
                        pickFromCAMERA();

                    }
                    else{
                        requestCameraPermissions();

                    }
                }
                else {
                    //gallery click
                    if (checkStoragePermission()){
                        pickFromGallery();

                    }
                    else{
                        requestStoragePermissions();

                    }
                }
            }
        }).show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCAMERA(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp image description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this, storagepermissions, STORAGE_REQUEST_CODE);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermissions() {
        ActivityCompat.requestPermissions(this, camerapermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMAGE_PICK_GALLERY_CODE){

            image_uri = data.getData();
            profileIv.setImageURI(image_uri);
        }
        else if (requestCode == IMAGE_PICK_CAMERA_CODE){
            profileIv.setImageURI(image_uri);


        }

        super.onActivityResult(requestCode, resultCode, data);
    }



    private void detectLocation(){
        Toast.makeText(this,"Please Wait...",Toast.LENGTH_LONG).show();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private boolean checkLocationPermission() {

        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, locationpermissions, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        findAddress();

    }

    private void findAddress() {
        Geocoder geocoder;
        List<Address>addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();

            etcountry.setText(country);
            etstate.setText(state);
            etcity.setText(city);
            etaddress.setText(address);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this,"Please turn on location..",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        detectLocation();

                    }
                    else {
                        Toast.makeText(this,"Location permission is necessary...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted){
                        pickFromCAMERA();

                    }
                    else {
                        Toast.makeText(this,"Camera permission are necessary...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){

                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted){
                        pickFromGallery();

                    }
                    else {
                        Toast.makeText(this,"Gallery permission is necessary...",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
