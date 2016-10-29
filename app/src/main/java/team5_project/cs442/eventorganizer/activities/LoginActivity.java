package team5_project.cs442.eventorganizer.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;

import team5_project.cs442.eventorganizer.R;


/**
 * Minimal activity demonstrating basic Google Sign-In.
 */
public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    static final Pattern email_address = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "hawk+(\\.iit+)*(\\.edu)$");

    static final String TAG = "LoginActivity";
    public static String accoutname;

    /* RequestCode for resolutions involving sign-in */
    static final int RC_SIGN_IN = 1;

    /* RequestCode for resolutions to get GET_ACCOUNTS permission on M */
    static final int RC_PERM_GET_ACCOUNTS = 2;

    /* Keys for persisting instance variables in savedInstanceState */
    static final String KEY_IS_RESOLVING = "is_resolving";
    static final String KEY_SHOULD_RESOLVE = "should_resolve";

    /* Client for accessing Google APIs */
    public static GoogleApiClient mGoogleApiClient;

    /* View to display current status (signed-in, signed-out, disconnected, etc) */
    TextView mStatus;

    // [START resolution_variables]
    /* Is there a ConnectionResult resolution in progress? */
    boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    // [END resolution_variables]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Restore from saved instance state
        // [START restore_saved_instance_state]
        if (savedInstanceState != null) {
            mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING);
            mShouldResolve = savedInstanceState.getBoolean(KEY_SHOULD_RESOLVE);
        }
        // [END restore_saved_instance_state]

        // Set up button click listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        // findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);

        // Large sign-in
        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);

        // Start with sign-in button disabled until sign-in either succeeds or fails
        findViewById(R.id.sign_in_button).setEnabled(false);

        // Set up view instances
        mStatus = (TextView) findViewById(R.id.status);

        // [START create_google_api_client]
        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
        // [END create_google_api_client]

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sign_out_button:
                onSignOutClicked();
                return true;
            default:
                return true;
        }
    }

    public void updateUI(boolean isSignedIn) {

        if (isSignedIn) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            if (currentPerson != null) {
                // Show signed-in user's name
                String name = currentPerson.getDisplayName();
                mStatus.setText(getString(R.string.signed_in_fmt, name));

                // Show users' email address (which requires GET_ACCOUNTS permission)
                if (checkAccountsPermission()) {
                    String currentAccount = Plus.AccountApi.getAccountName(mGoogleApiClient);
                    ((TextView) findViewById(R.id.email)).setText(currentAccount);
                    accoutname=currentAccount;

                }
            } else {
                // If getCurrentPerson returns null there is generally some error with the
                // configuration of the application (invalid Client ID, Plus API not enabled, etc).
                Log.w(TAG, getString(R.string.error_null_person));
                mStatus.setText(getString(R.string.signed_in_err));
            }

            // Set button visibility
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            // Show signed-out message and clear email field
            // mStatus.setText(R.string.signed_out);
            // ((TextView) findViewById(R.id.email)).setText("");

            // Set button visibility
            findViewById(R.id.sign_in_button).setEnabled(true);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            //findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    /**
     * Check if we have the GET_ACCOUNTS permission and request it if we do not.
     *
     * @return true if we have the permission, false if we do not.
     */
    public boolean checkAccountsPermission() {
        final String perm = Manifest.permission.GET_ACCOUNTS;
        int permissionCheck = ContextCompat.checkSelfPermission(this, perm);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // We have the permission
            return true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
            // Need to show permission rationale, display a snackbar and then request
            // the permission again when the snackbar is dismissed.
            // Snackbar.make(findViewById(R.id.main_layout),
            //   R.string.contacts_permission_rationale,
            //       Snackbar.LENGTH_INDEFINITE)
            //     .setAction(android.R.string.ok, new View.OnClickListener() {
            //           @Override
            //          public void onClick(View v) {
            // Request the permission again.
            //            ActivityCompat.requestPermissions(MainActivity.this,
            //                  new String[]{perm},
            //                RC_PERM_GET_ACCOUNTS);
        }
        //}).show();
        return false;
    } //else if {
    // No explanation needed, we can request the permission.
    //ActivityCompat.requestPermissions(this,
    //      new String[]{perm},
    //    RC_PERM_GET_ACCOUNTS);
    // return false;
    //}
    //}

    void showSignedInUI() {
        updateUI(true);
    }

    void showSignedOutUI() {
        updateUI(false);
    }

    // [START on_start_on_stop]
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    //@Override
    //public void onStop() {
    //  super.onStop();
    //mGoogleApiClient.disconnect();
    //}
    // [END on_start_on_stop]

    // [START on_save_instance_state]
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
        outState.putBoolean(KEY_SHOULD_RESOLVE, mShouldResolve);
    }
    // [END on_save_instance_state]

    // [START on_activity_result]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }
    // [END on_activity_result]

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult:" + requestCode);
        if (requestCode == RC_PERM_GET_ACCOUNTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSignedInUI();
            } else {
                Log.d(TAG, "GET_ACCOUNTS Permission Denied.");
            }
        }
    }

    // [START on_connected]
    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        String currentAccount = Plus.AccountApi.getAccountName(mGoogleApiClient);
        ((TextView) findViewById(R.id.email)).setText(currentAccount);
        Matcher matcher = email_address.matcher(currentAccount);
        boolean matchFound = matcher.matches();

        // To test w/ my roomate Phone who is not IIT student

        if (!matchFound) {
            Toast.makeText(getApplicationContext(), "You ENTERED AN INVALID EMAIL(Use HAWK id)", Toast.LENGTH_LONG).show();
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            showSignedOutUI();

        } else {
            Log.d(TAG, "onConnected:" + bundle);
            mShouldResolve = false;

            // Show the signed-in UI
            //showSignedInUI();
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("email", currentAccount);
            startActivity(mapIntent);
            finish();

        }// here you should put the address of the next page to be displayed
    }
    // [END on_connected]

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost. The GoogleApiClient will automatically
        // attempt to re-connect. Any UI elements that depend on connection to Google APIs should
        // be hidden or disabled until onConnected is called again.
        Log.w(TAG, "onConnectionSuspended:" + i);
    }

    // [START on_connection_failed]
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            showSignedOutUI();
        }
    }
    // [END on_connection_failed]

    public void showErrorDialog(ConnectionResult connectionResult) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, RC_SIGN_IN,
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                mShouldResolve = false;
                                showSignedOutUI();
                            }
                        }).show();

            } else {
                Log.w(TAG, "Google Play Services Error:" + connectionResult);
                String errorString = apiAvailability.getErrorString(resultCode);
                Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();

                mShouldResolve = false;
                showSignedOutUI();
            }
        }
    }

    // [START on_click]
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                onSignInClicked();

                break;


        }
    }
    // [END on_click]

    // [START on_sign_in_clicked]
    public void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.


        mShouldResolve = true;
        mGoogleApiClient.connect();


        // Show a message to the user that we are signing in.
        mStatus.setText(R.string.signing_in);
    }
    // [END on_sign_in_clicked]

    // [START on_sign_out_clicked]
    public void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        showSignedOutUI();
    }
    // [END on_sign_out_clicked]

    // [START on_disconnect_clicked]
    private void onDisconnectClicked() {
        // Revoke all granted permissions and clear the default account.  The user will have
        // to pass the consent screen to sign in again.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        showSignedOutUI();
    }
    // [END on_disconnect_clicked]

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

}