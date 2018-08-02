package riddlesolver.game.com.hajjapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bean.User;
import cz.msebera.android.httpclient.Header;
import riddlesolver.game.com.adapters.UserAdapter;

public class UsersList extends AppCompatActivity {


    ArrayList<User> data;
    RecyclerView list;
    UserAdapter adapter;

    ArrayList<String> temp;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);

        data = new ArrayList<>();
        temp = new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


        getData();


        list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        adapter = new UserAdapter(getBaseContext(), data);
        list.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startService("");
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 567);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 567) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService("");
            } else {
                // Permission was denied. Display an error message.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 567);
            }
        }
    }

    private void startService(String id) {
        Intent i = new Intent(getBaseContext(), UpdateLocationService.class);
        i.putExtra(Constants.USER_ID, prefs.getString(Constants.USER_ID, ""));
        startService(i);
    }

    private void getData() {
        String data = prefs.getString(Constants.USERS_DATA, " ");
        try {
            JSONObject object = new JSONObject(data);
            JSONArray users = object.getJSONArray("group_data");

            for (int i = 0; i < users.length(); i++) {
                JSONObject obj = users.getJSONObject(i);
                User user = new User();
                user.setName(obj.getString("name"));
                user.setId(obj.getString("id"));
                user.setApplicationNumber(obj.getString("app_no"));
                user.setDestination(obj.getString("destination"));
                user.setGroupId(obj.getString("group_id"));
                user.setMacAddress(obj.getString("mac_address"));
                this.data.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void trackGroup(View view) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = null;
        try {
            url = "http://hackathon.intrasols.com/api/get_location?group_id=" + getGroupId();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        url = url.replaceAll(" ", "%20");
        Log.e("Error", url);
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"


                try {
                    JSONObject result = new JSONObject(new String(response));

                    //Log.e("Error", result.toString());
                    if (result.getInt("response") == 101) {
                        JSONArray array = result.getJSONArray("grouplocations");
                        Intent intent = new Intent(UsersList.this, MapsActivity.class);
                        intent.putExtra("data", result.toString());
                        startActivity(intent);
                        Log.e("Error", result.toString());
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();

                }


            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });

    }
    private String getGroupId(){
        try{
            JSONObject userData = new JSONObject(prefs.getString(Constants.USERS_DATA, ""));
            JSONArray array = userData.getJSONArray("my_data");

            Log.e("Error", userData.toString());
            JSONObject object = array.getJSONObject(0);
            return object.getString("group_id");
        }
        catch(Exception e){

        }
        return null;
    }

}
