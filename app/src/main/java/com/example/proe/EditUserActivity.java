package com.example.proe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditUserActivity extends AppCompatActivity implements LocationListener {

  private ImageButton btnback, btngps;
  private EditText etname, etphone, etcountry, etcity, etstate, etcompleteass, etmachanical;
  private Button btnupdata;
  private ImageView profileIv;
  private SwitchCompat fcmswitch;
  private TextView txnofistatus;

  private static final int LOCATION_REQUEST_CODE = 100;
  private static final int CAMERA_REQUEST_CODE = 200;
  private static final int STORAGE_REQUEST_CODE = 300;
  //image pick constant
  private static final int IMAGE_PICK_GALLERY_CODE = 400;
  private static final int IMAGE_PICK_CAMERA_CODE = 500;

  //permission array
  private String[] locationpermissions;
  private String[] camerapermissions;
  private String[] storagepermissions;

  private ProgressDialog progressDialog;
  private FirebaseAuth firebaseAuth;

  private LocationManager locationManager;
  private double latitude = 0.0, longitude = 0.0;

  private Uri image_uri;

  private SharedPreferences sp;
  private SharedPreferences.Editor spEdit;

  private boolean isChecked = false;

  private static final String enableMessage = "Notification are enable";
  private static final String disableMessage = "Notification are disable";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_user);

    btnback = findViewById(R.id.btnback);
    btngps = findViewById(R.id.btngps);
    btnupdata = findViewById(R.id.btnupdate);

    txnofistatus = findViewById(R.id.txnofistatus);

    profileIv = findViewById(R.id.profileIv);

    etname = findViewById(R.id.etname);
    etphone = findViewById(R.id.etphone);
    etcountry = findViewById(R.id.etcountry);
    etcity = findViewById(R.id.etcity);
    etstate = findViewById(R.id.etstate);
    etcompleteass = findViewById(R.id.etaddress);
    etmachanical = findViewById(R.id.etmachanical);

    fcmswitch = findViewById(R.id.fcmswitch);

    sp = getSharedPreferences("SETTINGS_SP", MODE_PRIVATE);
    isChecked = sp.getBoolean("FCM_ENABLED", false);
    fcmswitch.setChecked(isChecked);
    if (isChecked) {
      txnofistatus.setText(enableMessage);
    } else {
      txnofistatus.setText(disableMessage);
    }

    //ints permission array
    locationpermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    camerapermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    storagepermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //setup progressdialog
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Please wait");
    progressDialog.setCanceledOnTouchOutside(false);
    firebaseAuth = FirebaseAuth.getInstance();
    checkUser();

    btnback.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    btngps.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (checkLocationPermission()) {
          //already allow
          detectLocation();
        } else {
          //not
          requestLocationPermission();
        }
      }
    });

    btnupdata.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        inputData();
      }
    });

    profileIv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showImagePickDialog();
      }
    });

    fcmswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          subscribeToTopic();
        } else {
          unsubscribeToTopic();
        }
      }
    });

  }

  private String Name, Phone, Country, State, City, CompleteAddress, Machanical;

  private void inputData() {
    Name = etname.getText().toString().trim();
    Phone = etphone.getText().toString().trim();
    Country = etcountry.getText().toString().trim();
    State = etstate.getText().toString().trim();
    City = etcity.getText().toString().trim();
    CompleteAddress = etcompleteass.getText().toString().trim();
    Machanical = etmachanical.getText().toString().trim();

    updateProfile();
  }

  private void updateProfile() {
    progressDialog.setMessage("Updating profile...");
    progressDialog.show();

    if (image_uri == null) {
      //update without image
      com.example.proe.notification.SharedPreferences.saveDeviceConnect(getApplicationContext(), Machanical);
      //setup data to upload
      HashMap<String, Object> hashMap = new HashMap<>();
      hashMap.put("uid", "" + firebaseAuth.getUid());
      hashMap.put("Name", "" + Name);
      hashMap.put("Phone", "" + Phone);
      hashMap.put("CompleteAddress", "" + CompleteAddress);
      hashMap.put("Mechanical", "" + Machanical);
      hashMap.put("Country", "" + Country);
      hashMap.put("State", "" + State);
      hashMap.put("City", "" + City);
      hashMap.put("Latitude", "" + latitude);
      hashMap.put("Longitude", "" + longitude);


      DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
      reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                  progressDialog.dismiss();
                  Toast.makeText(EditUserActivity.this, "profile Updated...", Toast.LENGTH_SHORT).show();
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  progressDialog.dismiss();
                  Toast.makeText(EditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });

    } else {
      //update with image
      String filePathandName = "profile image/" + "" + firebaseAuth.getUid();
      //get storage reference
      StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);
      storageReference.putFile(image_uri)
              .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  //image uploaded, get url of uploaded image
                  Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                  while (!uriTask.isSuccessful()) ;
                  Uri downloadImageUri = uriTask.getResult();

                  if (uriTask.isSuccessful()) {
                    //image uri received, now upload in database

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("uid", "" + firebaseAuth.getUid());
                    hashMap.put("Name", "" + Name);
                    hashMap.put("Phone", "" + Phone);
                    hashMap.put("CompleteAddress", "" + CompleteAddress);
                     hashMap.put("Mechanical", "" + Machanical);
                    hashMap.put("Country", "" + Country);
                    hashMap.put("State", "" + State);
                    hashMap.put("City", "" + City);
                    hashMap.put("Latitude", "" + latitude);
                    hashMap.put("Longitude", "" + longitude);
                    hashMap.put("profileImage", "" + downloadImageUri);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
                    reference.child(firebaseAuth.getUid()).updateChildren(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(EditUserActivity.this, "profile Updated...", Toast.LENGTH_SHORT).show();
                              }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(EditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                              }
                            });
                  }

                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  progressDialog.dismiss();
                  Toast.makeText(EditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });
    }
  }

  private void checkUser() {
    FirebaseUser user = firebaseAuth.getCurrentUser();
    if (user == null) {
      startActivity(new Intent(getApplicationContext(), LoginActivity.class));
      finish();
    } else {
      LoadMyinfo();
    }
  }

  private void LoadMyinfo() {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    databaseReference.orderByChild("uid").equalTo(firebaseAuth.getUid())
            .addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                  String CompleteAddress = "" + dataSnapshot1.child("CompleteAddress").getValue();
                  String AccountType = "" + dataSnapshot1.child("AccountType").getValue();
                  String Country = "" + dataSnapshot1.child("Country").getValue();
                  String City = "" + dataSnapshot1.child("City").getValue();
                  String Machanical = "" + dataSnapshot1.child("Mechanical").getValue();
                  String State = "" + dataSnapshot1.child("State").getValue();
                  String Email = "" + dataSnapshot1.child("Email").getValue();
                  latitude = Double.parseDouble("" + dataSnapshot1.child("Latitude").getValue());
                  longitude = Double.parseDouble("" + dataSnapshot1.child("Longitude").getValue());
                  String Name = "" + dataSnapshot1.child("Name").getValue();
                  String online = "" + dataSnapshot1.child("online").getValue();
                  String Phone = "" + dataSnapshot1.child("Phone").getValue();
                  String timestamp = "" + dataSnapshot1.child("timestamp").getValue();
                  String profileImage = "" + dataSnapshot1.child("profileImage").getValue();
                  String uid = "" + dataSnapshot1.child("uid").getValue();

                  etname.setText(Name);
                  etphone.setText(Phone);
                  etcountry.setText(Country);
                  etcity.setText(City);
                  etstate.setText(State);
                  etcompleteass.setText(CompleteAddress);
                  etmachanical.setText(Machanical);

                  try {
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_account_pirple_24dp).into(profileIv);
                  } catch (Exception e) {
                    profileIv.setImageResource(R.drawable.ic_account_pirple_24dp);
                  }


                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
  }

  private void showImagePickDialog() {
    String[] option = {"Camera", "Gallery"};
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Pick image").setItems(option, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (which == 0) {
          //camera click
          if (checkCameraPermission()) {
            pickFromCAMERA();

          } else {
            requestCameraPermissions();

          }
        } else {
          //gallery click
          if (checkStoragePermission()) {
            pickFromGallery();

          } else {
            requestStoragePermissions();

          }
        }
      }
    }).show();
  }

  private boolean checkCameraPermission() {
    boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == (PackageManager.PERMISSION_GRANTED);
    boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == (PackageManager.PERMISSION_GRANTED);
    return result && result1;
  }

  private void pickFromCAMERA() {
    //intent to pick iamge from camera
    ContentValues contentValues = new ContentValues();
    contentValues.put(MediaStore.Images.Media.TITLE, "Temp image Title");
    contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp image description");

    image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
    startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
  }

  private void requestCameraPermissions() {
    ActivityCompat.requestPermissions(this, camerapermissions, CAMERA_REQUEST_CODE);
  }

  private boolean checkStoragePermission() {
    boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == (PackageManager.PERMISSION_GRANTED);
    return result;
  }

  private void pickFromGallery() {
    //intent to pick iamge from gallery
    Intent intent = new Intent(Intent.ACTION_PICK);
    intent.setType("image/*");
    startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
  }

  private void requestStoragePermissions() {
    ActivityCompat.requestPermissions(this, storagepermissions, STORAGE_REQUEST_CODE);
  }


  private boolean checkLocationPermission() {
    boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            (PackageManager.PERMISSION_GRANTED);
    return result;
  }

  private void detectLocation() {
    Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();

    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
  }

  private void requestLocationPermission() {
    ActivityCompat.requestPermissions(this, locationpermissions, LOCATION_REQUEST_CODE);
  }

  @Override
  public void onLocationChanged(Location location) {
    latitude = location.getLatitude();
    longitude = location.getLongitude();

    findAddress();
  }

  private void findAddress() {
    //find address country,state,city
    Geocoder geocoder;
    List<Address> addresses;
    geocoder = new Geocoder(this, Locale.getDefault());

    try {
      addresses = geocoder.getFromLocation(latitude, longitude, 1);

      String address = addresses.get(0).getAddressLine(0);
      String city = addresses.get(0).getLocality();
      String state = addresses.get(0).getAdminArea();
      String country = addresses.get(0).getCountryName();

      etcountry.setText(country);
      etstate.setText(state);
      etcity.setText(city);
      etcompleteass.setText(address);
    } catch (Exception e) {
      Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    Toast.makeText(this, "Please turn on location..", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case LOCATION_REQUEST_CODE: {
        if (grantResults.length > 0) {
          boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
          if (locationAccepted) {
            detectLocation();

          } else {
            Toast.makeText(this, "Location permission is necessary...", Toast.LENGTH_SHORT).show();
          }
        }
      }
      break;
      case CAMERA_REQUEST_CODE: {
        if (grantResults.length > 0) {
          boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
          boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

          if (cameraAccepted && storageAccepted) {
            pickFromCAMERA();

          } else {
            Toast.makeText(this, "Camera permission are necessary...", Toast.LENGTH_SHORT).show();
          }
        }
      }
      break;
      case STORAGE_REQUEST_CODE: {
        if (grantResults.length > 0) {

          boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

          if (storageAccepted) {
            pickFromGallery();

          } else {
            Toast.makeText(this, "Gallery permission is necessary...", Toast.LENGTH_SHORT).show();
          }
        }
      }
      break;
    }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == RESULT_OK) {

      if (requestCode == IMAGE_PICK_GALLERY_CODE) {

        image_uri = data.getData();
        profileIv.setImageURI(image_uri);
      } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
        profileIv.setImageURI(image_uri);

      }
    }
    super.onActivityResult(requestCode, resultCode, data);

  }

  private void subscribeToTopic() {

    FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {

            spEdit = sp.edit();
            spEdit.putBoolean("FCM_ENABLED", true);
            spEdit.apply();

            Toast.makeText(EditUserActivity.this, "" + enableMessage, Toast.LENGTH_SHORT).show();
            txnofistatus.setText(enableMessage);
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(EditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }

  private void unsubscribeToTopic() {

    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {

            spEdit = sp.edit();
            spEdit.putBoolean("FCM_ENABLED", false);
            spEdit.apply();

            Toast.makeText(EditUserActivity.this, "" + disableMessage, Toast.LENGTH_SHORT).show();
            txnofistatus.setText(disableMessage);
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Toast.makeText(EditUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });
  }
}
