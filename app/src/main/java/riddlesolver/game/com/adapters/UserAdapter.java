package riddlesolver.game.com.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bean.User;
import cz.msebera.android.httpclient.Header;
import riddlesolver.game.com.hajjapp.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.Holder> {


    Context context;
    ArrayList<User> data;


    public UserAdapter(Context context, ArrayList<User> data) {
        this.context = context;
        this.data = data;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new Holder(inflater.inflate(R.layout.user_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        final User user = data.get(position);
        holder.name.setText(user.getName());

        holder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
                getLocation(user.getId());
            }
        });
    }

    private void getLocation(String id) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = null;
        try {
            url = "http://hackathon.intrasols.com/api/get_location?haji_id="+id;
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
                        JSONArray array = result.getJSONArray("location");
                        JSONObject object = array.getJSONObject(0);
                        String lat = object.getString("location").split(",")[0];
                        String lang = object.getString("location").split(",")[1];

                        showDirections(lat, lang);
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
    private void showDirections(String lat, String lang){
        String uri = "http://maps.google.com/maps?daddr=" + lat + "," + lang + " (" + "Where the party is at" + ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView name;
        TextView track;

        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            track = v.findViewById(R.id.track);
        }
    }
}
