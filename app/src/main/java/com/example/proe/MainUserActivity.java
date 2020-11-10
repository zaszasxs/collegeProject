package com.example.proe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proe.Adapter.AdapterBuyer;
import com.example.proe.Adapter.AdapterInfoBuyer;
import com.example.proe.Adapter.AdapterOrderUser;
import com.example.proe.Model.ModelBuyerUI;
import com.example.proe.Model.ModelInfoBuyer;
import com.example.proe.Model.ModelOrderUser;
import com.example.proe.Model.ModelPMG;

import com.example.proe.Model.Mechanical;
import com.example.proe.notification.NotificationHelper;
import com.example.proe.notification.SharedPreferences;
import com.example.proe.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainUserActivity extends AppCompatActivity {

  private TextView NameIv, EmailIv, tabpmg, tabbuyer, taborder, tabinfo, txplastic, txmetal, txglass, txVolumnP, txVolumnM, txVolumnG, txmechanic;
  private EditText search_bar;
  private ImageButton btnlogout;
  private ImageView profileIv;
  private RelativeLayout relativepmg, relativebuyer, relativeorder, relativeinfo;
  private RecyclerView buyerRv, orderRv, infoRv;

  private FirebaseAuth firebaseAuth;
  private ProgressDialog progressDialog;

  private ArrayList<ModelBuyerUI> modelBuyerUIS;
  private AdapterBuyer adapterBuyer;

  private ArrayList<ModelOrderUser> OrderUsersliat;
  private AdapterOrderUser adapterOrderUser;

  private String BuyerUid;

  private ArrayList<ModelPMG> modelPMGArrayList;

  private DatabaseReference storageBinReference;

  private TextView tvDeviceName, tvDeviceName2, tvDeviceName3;
  private String[] listDevice = {"Plastic", "Metal", "Glass"};
  private FloatingActionButton fbSettings;
  String deviceConnect;

  private ArrayList<ModelInfoBuyer> infoBuyerArrayList = new ArrayList<>();
  private AdapterInfoBuyer adapterInfoBuyer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_user);

    NameIv = findViewById(R.id.NameIv);
    EmailIv = findViewById(R.id.EmailIv);
    tabpmg = findViewById(R.id.tabpmg);
    tabbuyer = findViewById(R.id.tabbuyer);
    taborder = findViewById(R.id.taborder);
    tabinfo = findViewById(R.id.tabinfo);
    relativepmg = findViewById(R.id.relativepmg);
    relativeorder = findViewById(R.id.relativeorder);
    relativebuyer = findViewById(R.id.relativebuyer);
    relativeinfo = findViewById(R.id.relativeinfo);
    buyerRv = findViewById(R.id.buyerRv);
    orderRv = findViewById(R.id.orderRv);
    txplastic = findViewById(R.id.txplastic);
    txmetal = findViewById(R.id.txmetal);
    txglass = findViewById(R.id.txglass);
    txVolumnP = findViewById(R.id.txVolumnP);
    txVolumnM = findViewById(R.id.txVolumnM);
    txVolumnG = findViewById(R.id.txVolumnG);
    tvDeviceName = findViewById(R.id.tvDeviceName);
    tvDeviceName2 = findViewById(R.id.tvDeviceName2);
    tvDeviceName3 = findViewById(R.id.tvDeviceName3);
    txmechanic = findViewById(R.id.txmechanic);
    infoRv = findViewById(R.id.infoRv);
    profileIv = findViewById(R.id.profileIv);
    search_bar = findViewById(R.id.search_bar);
    fbSettings = findViewById(R.id.fbSettings);


    btnlogout = findViewById(R.id.btnlogout);
    progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Please wait");
    progressDialog.setCanceledOnTouchOutside(false);
    firebaseAuth = FirebaseAuth.getInstance();
    clearNotificationAll();
    checkDeviceConnect();
    checkUser();
    loadInfoBuyer();

    showPMGUI();


    btnlogout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MakeOffline();
      }
    });


    profileIv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(MainUserActivity.this, EditUserActivity.class));
      }
    });

    tabpmg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showPMGUI();
      }
    });

    tabbuyer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showBuyerUI();
      }
    });

    taborder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showOrderUI();
      }
    });

    tabinfo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showInfoUI();
      }
    });

    fbSettings.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        openSettingsDialog();
      }
    });

    initSetNameDeviceConnect();
  }

  private void checkDeviceConnect() {
    if (SharedPreferences.getDeviceConnect(getApplicationContext()).isEmpty()) {
      deviceConnect = "1";
    } else {
      deviceConnect = SharedPreferences.getDeviceConnect(getApplicationContext());
    }
  }

  public void openSettingsDialog() {
    final Dialog dialog = new Dialog(this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_settings);
    dialog.setCancelable(true);

    // Setting dialogview
    final Window window = dialog.getWindow();
    window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    WindowManager.LayoutParams wlp = window.getAttributes();

    wlp.gravity = Gravity.CENTER;
    window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    window.setDimAmount(0.5f);
    window.setAttributes(wlp);


    ImageView btnCancel = dialog.findViewById(R.id.ivClose);
    Button btnOk = dialog.findViewById(R.id.btnOk);
    final EditText inputPlastic = dialog.findViewById(R.id.inputPlastic);
    final EditText inputMetal = dialog.findViewById(R.id.inputMetal);
    final EditText inputGlass = dialog.findViewById(R.id.inputGlass);
    final TextView txtPlastic = dialog.findViewById(R.id.txNotiplastic);
    final TextView txtMetal = dialog.findViewById(R.id.txNotiMetal);
    final TextView txtGlass = dialog.findViewById(R.id.txNotiGlass);
    final TextView tvDeviceName = dialog.findViewById(R.id.tvDeviceName);


    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("settings");
    final DatabaseReference databaseReferenceUpdate = FirebaseDatabase.getInstance().getReference();

    databaseReference.orderByChild("deviceName").equalTo(deviceConnect).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          ArrayList<HashMap<String, Object>> data = Utils.recArrayList(dataSnapshot);
          String notiPlastic = data.get(0).get("alertPlastic").toString();
          String notiMetal = data.get(0).get("alertMetal").toString();
          String notiGlass = data.get(0).get("alertGlass").toString();

          tvDeviceName.setText("Mechanical Name : " + data.get(0).get("deviceName"));
          txtPlastic.setText("Alert " + notiPlastic + " Kg.");
          txtMetal.setText("Alert " + notiMetal + " Kg.");
          txtGlass.setText("Alert " + notiGlass + " Kg.");

          inputPlastic.setText(notiPlastic);
          inputMetal.setText(notiMetal);
          inputGlass.setText(notiGlass);

        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });


    btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.dismiss();
      }
    });

    btnOk.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        final String plasticValue = inputPlastic.getText().toString().isEmpty() ? "0" : inputPlastic.getText().toString();
        final String metalValue = inputMetal.getText().toString().isEmpty() ? "0" : inputMetal.getText().toString();
        final String glassValue = inputGlass.getText().toString().isEmpty() ? "0" : inputGlass.getText().toString();

        databaseReference.orderByChild("deviceName").equalTo(deviceConnect).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
              DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
              String key = nodeDataSnapshot.getKey(); // this key is `K1NRz9l5PU_0CFDtgXz`
              String path = "/" + dataSnapshot.getKey() + "/" + key;
              HashMap<String, Object> result = new HashMap<>();
              result.put("deviceName", deviceConnect);
              result.put("alertPlastic", Double.valueOf(plasticValue));
              result.put("alertMetal", Double.valueOf(metalValue));
              result.put("alertGlass", Double.valueOf(glassValue));
              databaseReferenceUpdate.child(path).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                  Toast.makeText(MainUserActivity.this, "Save Success", Toast.LENGTH_SHORT).show();
                  dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                  dialog.dismiss();
                }
              }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
      }
    });

    dialog.show();

  }

  private void initSetNameDeviceConnect() {
    if (SharedPreferences.getDeviceConnect(getApplicationContext()).isEmpty()) {
      txmechanic.setText("Mechanical Name : 1");
    } else {
      txmechanic.setText("Mechanical Name : " + SharedPreferences.getDeviceConnect(getApplicationContext()));
    }

  }

  private void loadInfoBuyer() {
    adapterInfoBuyer = new AdapterInfoBuyer(MainUserActivity.this, infoBuyerArrayList);
    infoRv.setAdapter(adapterInfoBuyer);

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
    reference.orderByChild("AccountType").equalTo("Buyer")
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            infoBuyerArrayList.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
              DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
              reference.child(ds.getKey()).child("Information")
                  .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                      for (DataSnapshot ds : dataSnapshot2.getChildren()) {
                        ModelInfoBuyer modelInfoBuyer = ds.getValue(ModelInfoBuyer.class);
                        infoBuyerArrayList.add(modelInfoBuyer);
                      }
                      adapterInfoBuyer.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                  });
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void MakeOffline() {
    progressDialog.setMessage("Logging Out...");

    HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("online", "false");

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    databaseReference.child(firebaseAuth.getUid()).updateChildren(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {
            SharedPreferences.clearData(getApplicationContext());
            progressDialog.setMessage("Logging in");
            progressDialog.show();
            firebaseAuth.signOut();
            checkUser();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            progressDialog.dismiss();
            Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        });

  }

  private void checkUser() {
    FirebaseUser user = firebaseAuth.getCurrentUser();
    if (user == null) {
      startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
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
              String Name = "" + dataSnapshot1.child("Name").getValue();
              String AccountType = "" + dataSnapshot1.child("AccountType").getValue();
              String Email = "" + dataSnapshot1.child("Email").getValue();
              String profileImage = "" + dataSnapshot1.child("profileImage").getValue();
              String City = "" + dataSnapshot1.child("City").getValue();

              NameIv.setText(Name);
              EmailIv.setText(Email);

              try {
                Picasso.get().load(profileImage).placeholder(R.drawable.ic_account_pirple_24dp).into(profileIv);
              } catch (Exception e) {
                profileIv.setImageResource(R.drawable.ic_account_pirple_24dp);
              }

              //load only buyer in these user city
              loadBuyer(City);
              loadOrder();
              LoadMyMechanical();

            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void LoadMyMechanical() {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child("Mechanical")
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
              String pathMechanical = dataSnapshot.getValue().toString();
              initReferenceMechanical(pathMechanical);
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void initReferenceMechanical(String pathMechanical) {
    DatabaseReference storageBinReference = FirebaseDatabase.getInstance().getReference("Mechanical");
    storageBinReference.child(pathMechanical).addValueEventListener(new ValueEventListener() {
      @SuppressLint("SetTextI18n")
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        Mechanical modelMechan = dataSnapshot.getValue(Mechanical.class);

        String ulplastic = "";
        String ulmetal = "";
        String ulglass = "";

        String titleDevice = "Device name : ";
        String subTitle = "Distance : ";
        String deviceUnit = " Kg.";

        txplastic.setText(subTitle + modelMechan.getPlastic() + deviceUnit);
        txmetal.setText(subTitle + modelMechan.getMetal() + deviceUnit);
        txglass.setText(subTitle + modelMechan.getGlass() + deviceUnit);

        isCheckShowNotificationDevice(modelMechan);
        tvDeviceName.setText(titleDevice + " " + listDevice[0]);
        tvDeviceName2.setText(titleDevice + " " + listDevice[1]);
        tvDeviceName3.setText(titleDevice + " " + listDevice[2]);

        isCheckViewDeviceError(modelMechan);

        if (modelMechan.getUlglass() >= 2 && modelMechan.getUlglass() <= 38)
          ulglass += modelMechan.getUlglass();

        if (modelMechan.getUlmetal() >= 2 && modelMechan.getUlmetal() <= 38)
          ulmetal += modelMechan.getUlmetal();

        if (modelMechan.getUlplastic() >= 2 && modelMechan.getUlplastic() <= 38)
          ulplastic += modelMechan.getUlplastic();

        txVolumnP.setVisibility(View.VISIBLE);
        txVolumnP.setText(ulplastic);

        txVolumnM.setVisibility(View.VISIBLE);
        txVolumnM.setText(ulmetal);

        txVolumnG.setVisibility(View.VISIBLE);
        txVolumnG.setText(ulglass);

        String messageNotiDetail = "Device ";
        String messageNotiSubDetail = " เต็มแล้ว";

        if (ulplastic.length() > 0) {
          findViewById(R.id.tvStatusFull).setVisibility(View.VISIBLE);
          findViewById(R.id.layDevicePlastic).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bgDeviceFull_20));
          NotificationHelper.sendNotification(getApplicationContext(), getResources().getString(R.string.app_name),
              messageNotiDetail + listDevice[0] + messageNotiSubDetail);
        } else {
          findViewById(R.id.tvStatusFull).setVisibility(View.GONE);
          findViewById(R.id.layDevicePlastic).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

        if (ulmetal.length() > 0) {
          findViewById(R.id.tvStatusFull2).setVisibility(View.VISIBLE);
          findViewById(R.id.layDeviceMetal).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bgDeviceFull_20));
          NotificationHelper.sendNotification(getApplicationContext(), getResources().getString(R.string.app_name),
              messageNotiDetail + listDevice[1] + messageNotiSubDetail);
        } else {
          findViewById(R.id.tvStatusFull2).setVisibility(View.GONE);
          findViewById(R.id.layDeviceMetal).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

        if (ulglass.length() > 0) {
          findViewById(R.id.tvStatusFull3).setVisibility(View.VISIBLE);
          findViewById(R.id.layDeviceGlass).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.bgDeviceFull_20));
          NotificationHelper.sendNotification(getApplicationContext(), getResources().getString(R.string.app_name),
              messageNotiDetail + listDevice[3] + messageNotiSubDetail);
        } else {
          findViewById(R.id.tvStatusFull3).setVisibility(View.GONE);
          findViewById(R.id.layDeviceGlass).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void isCheckShowNotificationDevice(final Mechanical modelMechan) {
    DatabaseReference databaseReferenceSetting = FirebaseDatabase.getInstance().getReference("settings");
    databaseReferenceSetting.orderByChild("deviceName").equalTo(deviceConnect).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          ArrayList<HashMap<String, Object>> data = Utils.recArrayList(dataSnapshot);
          String notiPlastic = data.get(0).get("alertPlastic").toString();
          String notiMetal = data.get(0).get("alertMetal").toString();
          String notiGlass = data.get(0).get("alertGlass").toString();

          String messageNotiDetail = "Device ";
          String messageNotiSubDetail = " ถึงระยะห่างที่กำหนดไว้";

          if (Double.valueOf(notiPlastic) == modelMechan.getPlastic()) {
            NotificationHelper.sendNotification(getApplicationContext(), getResources().getString(R.string.app_name),
                messageNotiDetail + listDevice[0] + messageNotiSubDetail);

          } else if (Double.valueOf(notiMetal) == modelMechan.getMetal()) {
            NotificationHelper.sendNotification(getApplicationContext(), getResources().getString(R.string.app_name),
                messageNotiDetail + listDevice[1] + messageNotiSubDetail);

          } else if (Double.valueOf(notiGlass) == modelMechan.getGlass()) {
            NotificationHelper.sendNotification(getApplicationContext(), getResources().getString(R.string.app_name),
                messageNotiDetail + listDevice[2] + messageNotiSubDetail);
          }
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void isCheckViewDeviceError(final Mechanical modelMechan) {
    String err = modelMechan.getError();
    TextView txtMessageError = findViewById(R.id.txerror);
    ImageView viewBg = findViewById(R.id.ivStatusBg);
    if (err.length() > 1) {
      txtMessageError.setText(modelMechan.getError());
      viewBg.setImageResource(R.drawable.shape_status_not_error);
    } else {
      txtMessageError.setText(modelMechan.getError());
      viewBg.setImageResource(R.drawable.shape_status_error);
      NotificationHelper.sendNotification(getApplicationContext(), "Device Error",
          "Device error please check your machine.");
      NotificationHelper.viewAnimationShake(getApplicationContext(), findViewById(R.id.ivIconStatus));
    }
  }

  private void loadOrder() {
    OrderUsersliat = new ArrayList<>();

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        OrderUsersliat.clear();
        for (final DataSnapshot ds : dataSnapshot.getChildren()) {
          String uid = "" + ds.getRef().getKey();

          DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Order");
          reference.orderByChild("OrderBy").equalTo(firebaseAuth.getUid())
              .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                      ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                      OrderUsersliat.add(modelOrderUser);
                    }

                    adapterOrderUser = new AdapterOrderUser(MainUserActivity.this, OrderUsersliat);
                    orderRv.setAdapter(adapterOrderUser);
                  }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
              });
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void loadBuyer(final String city) {
    modelBuyerUIS = new ArrayList<>();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
    databaseReference.orderByChild("AccountType").equalTo("Buyer")
        .addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            modelBuyerUIS.clear();
            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
              ModelBuyerUI modelBuyerUI = dataSnapshot1.getValue(ModelBuyerUI.class);

              String ShopCity = "" + dataSnapshot1.child("City").getValue();

              //show only user city shop
              if (ShopCity.equals(city)) {
                modelBuyerUIS.add(modelBuyerUI);
              }
            }
            //setup adapter
            adapterBuyer = new AdapterBuyer(MainUserActivity.this, modelBuyerUIS);
            //set adapter to recycleview

            buyerRv.setAdapter(adapterBuyer);
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });
  }

  private void showPMGUI() {
    relativebuyer.setVisibility(View.GONE);
    relativeinfo.setVisibility(View.GONE);
    relativepmg.setVisibility(View.VISIBLE);
    relativeinfo.setVisibility(View.GONE);

    tabpmg.setTextColor(getResources().getColor(R.color.black));
    tabpmg.setBackgroundResource(R.drawable.sharp_recmenu1);

    tabbuyer.setTextColor(getResources().getColor(R.color.white));
    tabbuyer.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    taborder.setTextColor(getResources().getColor(R.color.white));
    taborder.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    tabinfo.setTextColor(getResources().getColor(R.color.white));
    tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));
  }

  private void showBuyerUI() {
    relativebuyer.setVisibility(View.VISIBLE);
    relativeorder.setVisibility(View.GONE);
    relativepmg.setVisibility(View.GONE);
    relativeinfo.setVisibility(View.GONE);

    tabpmg.setTextColor(getResources().getColor(R.color.white));
    tabpmg.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    tabbuyer.setTextColor(getResources().getColor(R.color.black));
    tabbuyer.setBackgroundResource(R.drawable.sharp_recmenu1);

    taborder.setTextColor(getResources().getColor(R.color.white));
    taborder.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    tabinfo.setTextColor(getResources().getColor(R.color.white));
    tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));


  }

  private void showOrderUI() {
    relativebuyer.setVisibility(View.GONE);
    relativeorder.setVisibility(View.VISIBLE);
    relativepmg.setVisibility(View.GONE);
    relativeinfo.setVisibility(View.GONE);

    tabpmg.setTextColor(getResources().getColor(R.color.white));
    tabpmg.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    tabbuyer.setTextColor(getResources().getColor(R.color.white));
    tabbuyer.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    taborder.setTextColor(getResources().getColor(R.color.black));
    taborder.setBackgroundResource(R.drawable.sharp_recmenu1);

    tabinfo.setTextColor(getResources().getColor(R.color.white));
    tabinfo.setBackgroundColor(getResources().getColor(android.R.color.transparent));
  }

  private void showInfoUI() {
    relativebuyer.setVisibility(View.GONE);
    relativeorder.setVisibility(View.GONE);
    relativepmg.setVisibility(View.GONE);
    relativeinfo.setVisibility(View.VISIBLE);

    tabpmg.setTextColor(getResources().getColor(R.color.white));
    tabpmg.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    tabbuyer.setTextColor(getResources().getColor(R.color.white));
    tabbuyer.setBackgroundColor(getResources().getColor(android.R.color.transparent));

    tabinfo.setTextColor(getResources().getColor(R.color.black));
    tabinfo.setBackgroundResource(R.drawable.sharp_recmenu1);

    taborder.setTextColor(getResources().getColor(R.color.white));
    taborder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
  }

  private void clearNotificationAll() {
    NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    nMgr.cancelAll();
  }


}
