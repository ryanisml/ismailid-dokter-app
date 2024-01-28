package id.ismail.dokterapps.lib;

import android.os.Build;
import android.text.TextUtils;
import java.util.Calendar;

public class LibHelper {
    public static String my_url = "https://klinik.ismail.id/notification-admin";

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }

    public static String getAge(String tanggal){
        String[] tgl = tanggal.split("-", 0);
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(Integer.parseInt(tgl[0]), Integer.parseInt(tgl[1]), Integer.parseInt(tgl[2]));
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        int ageInt = age;
        return Integer.toString(ageInt);
    }
}
