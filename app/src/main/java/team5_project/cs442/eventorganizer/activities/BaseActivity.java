package team5_project.cs442.eventorganizer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.plus.Plus;

import java.io.Serializable;

import team5_project.cs442.eventorganizer.R;
import team5_project.cs442.eventorganizer.database.Database;
import team5_project.cs442.eventorganizer.event.Tuple;

public class BaseActivity extends FragmentActivity {
    LoginActivity lg = new LoginActivity();
    public static Activity listInstance;

    protected static String email;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCreate:
                Intent createActivity = new Intent(this, CreateActivity.class);
                startActivity(createActivity);
                return true;
            case R.id.menuMap:
                returnToMap();
                return true;

            case R.id.menuEvents:
                if (listInstance != null) {
                    listInstance.finish();
                }
                showAllListView(Database.TAG_EMAIL, email);
                return true;
            case R.id.sign_out:
                Plus.AccountApi.clearDefaultAccount(LoginActivity.mGoogleApiClient);
                Plus.AccountApi.revokeAccessAndDisconnect(LoginActivity.mGoogleApiClient);
                LoginActivity.mGoogleApiClient.disconnect();

                Intent backtoLogin = new Intent(this, LoginActivity.class);
                backtoLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                backtoLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                backtoLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(backtoLogin);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed()
    {
     finish();
    }

    public void returnToMap() {
        Intent backtoMap = new Intent(this, MapsActivity.class);
        backtoMap.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        backtoMap.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        backtoMap.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        backtoMap.putExtra("email", email);
        startActivity(backtoMap);
        finish();
    }

    public void showFlagListView(String tag, String value) {
        Intent i = new Intent(getApplicationContext(), EventListViewActivity.class);
        Bundle bundle = new Bundle();
        Tuple tuple = new Tuple(tag, value);
        bundle.putSerializable("Tuple", (Serializable) tuple);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void showAllListView(String tag, String value) {
        Intent i = new Intent(getApplicationContext(), TabActivity.class);
        Bundle bundle = new Bundle();
        Tuple tuple = new Tuple(tag, value);
        bundle.putSerializable("Tuple", (Serializable) tuple);
        i.putExtras(bundle);
        startActivity(i);
    }
}
