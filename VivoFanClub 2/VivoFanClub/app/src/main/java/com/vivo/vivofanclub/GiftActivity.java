package com.vivo.vivofanclub;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.anupkumarpanwar.scratchview.ScratchView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vivo.vivofanclub.Common.NetworkChangeListener;
import com.vivo.vivofanclub.Common.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GiftActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    //views
    ImageView imgScratch;
    ProgressBar progressBar;
    LottieAnimationView giftAnimation;
    TextView tvScratch;
    ScratchView scratchView;
    Button btnHome, btnExplore;
    //Progress Dialog
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);

        //initialization
        initialization();

        //get Intent values
        String name = getIntent().getStringExtra("name");
        String number = getIntent().getStringExtra("number");
        String imeiNo = getIntent().getStringExtra("imeiNo");

        //get gift details
        if (name != null || number != null || imeiNo != null) {
            getGiftDetail(name, number, imeiNo);
        }


        //scratch view reveled
        scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                showToast("Reveled");
                scratchView.clear();

                //set to scratched
                tvScratch.setText("Scratched");

                //enable button
                btnHome.setVisibility(View.VISIBLE);
                btnExplore.setVisibility(View.VISIBLE);

                //display and play animation
                giftAnimation.setVisibility(View.VISIBLE);
                giftAnimation.playAnimation();
                giftAnimation.setRepeatMode(LottieDrawable.RESTART);
                giftAnimation.setRepeatCount(LottieDrawable.INFINITE);

                //send Message
                sendMessage(name, number, imeiNo);
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                Log.d("REVELED", String.valueOf(percent));
            }
        });

        //handle btn home click
        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(GiftActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        //handle btn explore click
        btnExplore.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://vivorajasthan.com/"));
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        //showToast("Disabled Back Press");
    }

    private void sendMessage(String name, String number, String imeiNo) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.sendGiftMessageUrl, response -> {
            Log.d("MESSAGE RESPONSE", response.trim());
            //showToast(response);

        }, error -> {
            loadingDialog.hideDialog();
            Log.e("MESSAGE ERROR", error.toString());
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("number", number);
                params.put("imeiNo", imeiNo);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getGiftDetail(String name, String number, String imeiNo) {
        loadingDialog.showDialog("Loading...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.getGiftDetailUrl, response -> {
            loadingDialog.hideDialog();
            Log.d("GIFT RESPONSE", response.trim());
            //showToast(response);
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    //tvScratch.setText(object.getString("gift_detail"));
                    String giftImage = object.getString("gift_image");
                    Picasso.get().load(giftImage).placeholder(R.drawable.giftbox).into(imgScratch, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            showToast(e.toString());
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            loadingDialog.hideDialog();
            Log.e("GIFT ERROR", error.toString());
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("number", number);
                params.put("imeiNo", imeiNo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showToast(String message) {
        Toast.makeText(GiftActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void initialization() {
        imgScratch = findViewById(R.id.imgScratch);
        progressBar = findViewById(R.id.progressBar);
        giftAnimation = findViewById(R.id.giftAnimation);
        tvScratch = findViewById(R.id.tvScratch);
        scratchView = findViewById(R.id.scratchView);
        btnHome = findViewById(R.id.btnHome);
        btnHome.setVisibility(View.GONE);
        btnExplore = findViewById(R.id.btnExplore);
        btnExplore.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(this);
    }
}