package com.example.proe.utils;

import com.example.proe.Model.BodyPushToken;
import com.example.proe.Model.ModelTokenUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {

  public static BodyPushToken createObject(String title, String body, ArrayList<String> token){
    BodyPushToken modelPushToken = new BodyPushToken();
    BodyPushToken.DataBean dataBean = new BodyPushToken.DataBean();
    dataBean.setTitle(title);
    dataBean.setBody(body);

    modelPushToken.setPriority("high");
    modelPushToken.setSilent(true);
    modelPushToken.setData(dataBean);

    modelPushToken.setRegistration_ids(token);
    return modelPushToken;
  }

  public static ModelTokenUser createModelTokenUser(String email,String token,String uid){
    ModelTokenUser modelTokenUser = new ModelTokenUser();
    modelTokenUser.setEmail(email);
    modelTokenUser.setToken(token);
    modelTokenUser.setUid(uid);
    return modelTokenUser;
  }

  public static ArrayList<HashMap<String, Object>> recArrayList(DataSnapshot snapshot) {

    ArrayList<HashMap<String, Object>> list = new ArrayList<>();

    if (snapshot == null) {

      return list;
    }

    // This is awesome! You don't have to know the data structure of the database.
    Object fieldsObj = new Object();

    HashMap fldObj;

    for (DataSnapshot shot : snapshot.getChildren()) {

      try {

        fldObj = (HashMap) shot.getValue(fieldsObj.getClass());

      } catch (Exception ex) {
        continue;
      }

      // Include the primary key of this Firebase data record. Named it 'recKeyID'
      fldObj.put("recKeyID", shot.getKey());

      list.add(fldObj);
    }

    return list;
  }
}
