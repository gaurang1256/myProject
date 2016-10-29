package team5_project.cs442.eventorganizer.database;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;

import team5_project.cs442.eventorganizer.event.Event;
import team5_project.cs442.eventorganizer.event.EventTimeChecker;

/**
 * Created by sangwon on 11/3/15.
 */
public class Database {
    public static final String SERVER = "iitorganizer.16mb.com";

    public static final int KEY_ID = 0;
    public static final int KEY_NAME = 1;
    public static final int KEY_LOC = 2;
    public static final int KEY_START = 3;
    public static final int KEY_END = 4;
    public static final int KEY_DESC = 5;
    public static final int KEY_HOST = 6;
    public static final int KEY_EMAIL = 7;
    public static final int KEY_COST = 8;

    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_LOC = "location";
    public static final String TAG_START = "start";
    public static final String TAG_END = "end";
    public static final String TAG_DESC = "desc";
    public static final String TAG_HOST = "host";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_COST = "cost";

    public static final String TAG_COL = "column";
    public static final String TAG_VALUE = "value";

    public static List<Event> readList(String col, String value) {
        ArrayList<Event> list = null;
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://" + SERVER + "/readlist.php").openConnection();

            if (col != null) {
                String param = getParam(col, value);

                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Content-Length", String.valueOf(param.getBytes().length));
                con.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(param);
                wr.close();
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));

            JSONArray jsli = new JSONArray(rd.readLine());

            list = new ArrayList<Event>();

            Calendar cal = Calendar.getInstance();

            for (int iObj = 0; iObj < jsli.length(); ++iObj) {
                JSONArray jsev = jsli.getJSONArray(iObj);

                int eid = jsev.getInt(KEY_ID);
                String ename = jsev.getString(KEY_NAME);
                String eloc = jsev.getString(KEY_LOC);
                long estart = jsev.getLong(KEY_START);
                long eend = jsev.getLong(KEY_END);
                String edesc = jsev.getString(KEY_DESC);
                String ehost = jsev.getString(KEY_HOST);
                String eemail = jsev.getString(KEY_EMAIL);
                double ecost = jsev.getDouble(KEY_COST);
                Log.d("Db", ename + ":" + estart + ":" + eend );
                if (!EventTimeChecker.isEventPassed(new Date(estart), new Date(eend))) {
                    Event event = new Event(eid, ename, eloc, new Date(estart), new Date(eend), edesc, ehost, eemail, ecost);
                    list.add(event);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Event> readList() {
        return readList(null, null);
    }

    public static void insert(Event event) {

        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://" + SERVER + "/insert.php").openConnection();

            String param = getParam(event);

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", String.valueOf(param.getBytes().length));
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(param);
            wr.close();

            con.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(Event event) {
        delete(event.getmEventId());
        insert(event);
    }

    public static void delete(int id) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://" + SERVER + "/delete.php").openConnection();

            String param = getParam(id);

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Content-Length", String.valueOf(param.getBytes().length));
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(param);
            wr.close();

            con.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getParam(String col, String value) {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append(URLEncoder.encode(TAG_COL, "UTF-8") + "=");
            sb.append(URLEncoder.encode(col, "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_VALUE, "UTF-8") + "=");
            sb.append(URLEncoder.encode(value, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private static String getParam(Event event) {
        StringBuilder sb = new StringBuilder();

        try {
            sb.append(URLEncoder.encode(TAG_ID, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(event.getmEventId()), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_NAME, "UTF-8") + "=" + URLEncoder.encode(event.getmEventName(), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_LOC, "UTF-8") + "=" + URLEncoder.encode(event.getmEventLocation(), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_START, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(event.getmEventStartTime().getTime()), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_END, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(event.getmEventEndTime().getTime()), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_DESC, "UTF-8") + "=" + URLEncoder.encode(event.getmDescription(), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_HOST, "UTF-8") + "=" + URLEncoder.encode(event.getmHost(), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_EMAIL, "UTF-8") + "=" + URLEncoder.encode(event.getmEventCreator(), "UTF-8"));
            sb.append("&" + URLEncoder.encode(TAG_COST, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(event.getmCost()), "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private static String getParam(int id) {
        String param = null;
        try {
            param = URLEncoder.encode(TAG_ID, "UTF-8") + "=" + URLEncoder.encode(String.valueOf(id), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
    }
}
