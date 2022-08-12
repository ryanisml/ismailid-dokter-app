package id.ismail.dokterapps.lib;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedDokter {
    public static final String SP_APP = "dokter_ismailid";
    public static final String SP_IDUSER = "sp_iduser";
    public static final String SP_NAMA = "sp_nama";
    public static final String SP_NO_KTP = "sp_noktp";
    public static final String SP_EMAIL = "sp_email";
    public static final String SP_STATUS = "sp_status";
    public static final String SP_IMEI = "sp_imei";
    public static final String SP_PHONE_TYPE = "sp_phone_type";
    public static final String SP_SUDAH_LOGIN = "sp_is_login";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedDokter(Context context) {
        sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value) {
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value) {
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }


    public String getSpNama() {
        return sp.getString(SP_NAMA, "");
    }

    public String getSpImei() {
        return sp.getString(SP_IMEI, "");
    }

    public String getSpPhoneType() {
        return sp.getString(SP_PHONE_TYPE, "");
    }

    public String getSpEmail() {
        return sp.getString(SP_EMAIL, "");
    }

    public String getSpIduser() {
        return sp.getString(SP_IDUSER, "");
    }

    public boolean getSpSudahLogin() {
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
