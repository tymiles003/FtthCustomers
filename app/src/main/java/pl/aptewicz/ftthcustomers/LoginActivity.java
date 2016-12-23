package pl.aptewicz.ftthcustomers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import pl.aptewicz.ftthcustomers.model.FtthCheckerUser;
import pl.aptewicz.ftthcustomers.network.RequestQueueSingleton;
import pl.aptewicz.ftthcustomers.util.ProgressUtils;

public class LoginActivity extends AppCompatActivity {

    private RequestQueueSingleton requestQueueSingleton;
    private FtthCheckerUser ftthCheckerUser;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private View progressView;
    private View loginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    try {
                        attemptLogin();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        requestQueueSingleton = RequestQueueSingleton.getInstance(this);

        ProgressUtils.showProgress(false, this, loginFormView, progressView);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ProgressUtils.showProgress(false, this, loginFormView, progressView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.login_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_login_settings:
                Intent settingsActivity = new Intent(getBaseContext(), LoginSettingsActivity.class);
                startActivity(settingsActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void attemptLogin() throws JSONException {
        resetErrors();

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.error_invalid_password));
            focusView = passwordEditText;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            passwordEditText.setError(getString(R.string.error_field_required));
            focusView = passwordEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            View v = getCurrentFocus();
            if (v != null) {
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }

            usernameEditText.setText("");
            passwordEditText.setText("");
            showProgress(true);
            authenticateUser(username, password);
        }
    }

    private void authenticateUser(String username, final String password) {
        ftthCheckerUser = new FtthCheckerUser();
        ftthCheckerUser.setUsername(username);
        ftthCheckerUser.setPassword(password);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serverAddress = sharedPreferences.getString("server_address", "default");

        FtthCheckerRestApiRequest ftthCheckerRestApiRequest = new FtthCheckerRestApiRequest(
                Request.Method.GET, "http://" + serverAddress + "/PracaInzRest/user", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        FtthCheckerUser ftthCheckerUserFromResponse = new Gson()
                                .fromJson(response.toString(), FtthCheckerUser.class);
                        ftthCheckerUserFromResponse.setPassword(ftthCheckerUser.getPassword());

                        if (FtthCheckerUserRole.ADMIN.equals(ftthCheckerUserFromResponse.getFtthCheckerUserRole())) {
                            Intent adminMapActivity = new Intent(MainActivity.this, AdminMapActivity.class);
                            adminMapActivity.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUserFromResponse);
                            startActivity(adminMapActivity);
                        } else if (FtthCheckerUserRole.SERVICEMAN.equals(ftthCheckerUserFromResponse.getFtthCheckerUserRole())) {
                            Intent servicemanActivity = new Intent(MainActivity.this, ServicemanMapActivity.class);
                            servicemanActivity.putExtra(FtthCheckerUser.FTTH_CHECKER_USER, ftthCheckerUserFromResponse);
                            startActivity(servicemanActivity);
                        }
                        Toast.makeText(MainActivity.this, "Authorization success",
                                Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, "Authorization failed", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }, ftthCheckerUser);

        requestQueueSingleton.addToRequestQueue(ftthCheckerRestApiRequest);
    }

    private void resetErrors() {
        usernameEditText.setError(null);
        passwordEditText.setError(null);
    }
}
