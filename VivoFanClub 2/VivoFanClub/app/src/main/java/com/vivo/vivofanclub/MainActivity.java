package com.vivo.vivofanclub;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vivo.vivofanclub.Common.URLs;
import com.vivo.vivofanclub.adapter.GiftAdapter;
import com.vivo.vivofanclub.adapter.WinnersAdapter;
import com.vivo.vivofanclub.model.GiftModel;
import com.vivo.vivofanclub.model.WinnersModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    //views
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView giftRv, winnersRv;
    CardView scootyGiftCv, ledTvGiftCv, wmGiftCv;
    ImageView imageGiftIv, ledImageGiftIv, wmImageGiftIv;
    TextView tvGiftTs, scootyGiftTv, ledGiftTv, wmGiftTv;
    Button btnRegister;
    LinearLayout giftStatusLayout;

    //Dialog views
    EditText contactNoEt;
    Button checkGiftBtn, participateBtn;
    CircleImageView closeImgBtn;
    CardView giftCv;
    LinearLayout giftLayout, giftAvailableLayout, giftNotAvailableLayout;
    TextView giftMsgTv, giftMsgTv1;
    ImageView checkGiftIv;
    LottieAnimationView giftAnimation;
    ProgressBar progressBar;

    //ArrayList
    private ArrayList<GiftModel> giftList;
    private ArrayList<WinnersModel> winnersList;

    //Adapters
    private GiftAdapter giftAdapter;
    private WinnersAdapter winnersAdapter;

    //Required Variables
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //init
        initialization();

        //handle register button click
        btnRegister.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        //get Gift List
        getGiftList();

        //get Winners List
        getWinnersList();

        //handle gift terms and conditions
        tvGiftTs.setOnClickListener(view -> goToTermsConditionDialog());

        //handle giftStatusLayout click
        giftStatusLayout.setOnClickListener(v -> showGiftStatusLayout());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            getGiftList();
            getWinnersList();
            swipeRefreshLayout.setRefreshing(false);
        });
        CardView cardClaimGift = findViewById(R.id.cardClaimGift);

        cardClaimGift.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, Claim_Activity.class);
            startActivity(intent);
        });
    }

    private void showGiftStatusLayout() {
        giftStatusLayout.setEnabled(false);

        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.check_gift_status_layout);

        //getting views
        closeImgBtn = dialog.findViewById(R.id.closeImgBtn);
        contactNoEt = dialog.findViewById(R.id.contactNoEt);
        checkGiftBtn = dialog.findViewById(R.id.checkGiftBtn);
        giftCv = dialog.findViewById(R.id.giftCv);
        giftLayout = dialog.findViewById(R.id.giftLayout);
        giftAvailableLayout = dialog.findViewById(R.id.giftAvailableLayout);
        giftNotAvailableLayout = dialog.findViewById(R.id.giftNotAvailableLayout);
        giftMsgTv = dialog.findViewById(R.id.giftMsgTv);
        giftMsgTv1 = dialog.findViewById(R.id.giftMsgTv1);
        checkGiftIv = dialog.findViewById(R.id.checkGiftIv);
        giftAnimation = dialog.findViewById(R.id.giftAnimation);
        progressBar = dialog.findViewById(R.id.progressBar);
        participateBtn = dialog.findViewById(R.id.participateBtn);

        //handle checkGiftBtn Click
        checkGiftBtn.setOnClickListener(v -> {
            String contactNo = contactNoEt.getText().toString().trim();
            if (contactNo.isEmpty() || contactNo.length() < 10) {
                contactNoEt.setError("Invalid contact no...");
                contactNoEt.requestFocus();
            } else {
                getGiftDetail(contactNo);
            }
        });

        //handle participateBtn Click
        participateBtn.setOnClickListener(v -> {
            giftCv.setVisibility(View.GONE);
            giftLayout.setVisibility(View.GONE);
            giftAvailableLayout.setVisibility(View.GONE);
            giftNotAvailableLayout.setVisibility(View.GONE);
            giftStatusLayout.setEnabled(true);
            dialog.dismiss();
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });

        //handle closeBtn click
        closeImgBtn.setOnClickListener(v -> {
            giftStatusLayout.setEnabled(true);
            dialog.dismiss();
        });

        dialog.setOnCancelListener(dialog1 -> {
            giftCv.setVisibility(View.GONE);
            giftLayout.setVisibility(View.GONE);
            giftAvailableLayout.setVisibility(View.GONE);
            giftNotAvailableLayout.setVisibility(View.GONE);
            giftStatusLayout.setEnabled(true);
        });
        dialog.show();
    }

    private void getGiftDetail(String contactNo) {
        loadingDialog.showDialog("Checking...");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.checkGiftStatusUrl, response -> {
            Log.d("RESPONSE", response);
            loadingDialog.hideDialog();
            try {
                JSONObject mainObject = new JSONObject(response);
                String status = mainObject.getString("status");
                String message = mainObject.getString("message");
                String giftImage = mainObject.getString("image");

                if (status.equalsIgnoreCase("true")) {
                    //gift available

                    //show gift Layouts
                    giftCv.setVisibility(View.VISIBLE);
                    giftLayout.setVisibility(View.VISIBLE);
                    giftAvailableLayout.setVisibility(View.VISIBLE);
                    giftNotAvailableLayout.setVisibility(View.GONE);
                    giftMsgTv.setText(message);

                    if (!giftImage.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        Picasso.get().load(giftImage).fit().into(checkGiftIv, new Callback() {
                            @Override
                            public void onSuccess() {
                                giftAnimation.playAnimation();//play animation
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                showToast(e.toString());
                                checkGiftIv.setVisibility(View.GONE);
                            }
                        });
                    }
                } else {
                    //gift not available
                    giftLayout.setVisibility(View.VISIBLE);
                    giftCv.setVisibility(View.VISIBLE);
                    giftAvailableLayout.setVisibility(View.GONE);
                    giftNotAvailableLayout.setVisibility(View.VISIBLE);
                    giftMsgTv1.setText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast(e.toString());
            }
        }, error -> {
            loadingDialog.hideDialog();
            Log.e("ERROR", error.toString());
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("contactNo", contactNo);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void goToTermsConditionDialog() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://vivorajasthan.com/v29-series-luckydraw"));
        startActivity(intent);
    }

    public void autoScroll() {
        final int speedScroll = 3000;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;

            @Override
            public void run() {
                if (count == winnersAdapter.getItemCount())
                    count = 0;
                if (count < winnersAdapter.getItemCount()) {
                    winnersRv.smoothScrollToPosition(count++);
                    handler.postDelayed(this, speedScroll);
                }
            }
        };
        handler.postDelayed(runnable, speedScroll);
    }

    private void getWinnersList() {
        winnersList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.getAllWinnersUrl, response -> {
            Log.d("WINNERS LIST RESPONSE", response.trim());
            try {
                JSONObject mainObject = new JSONObject(response);
                String status = mainObject.getString("status");
                String message = mainObject.getString("message");

                if (status.equalsIgnoreCase("true")) {
                    JSONArray jsonArray = new JSONArray(message);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        WinnersModel winnersModel = new WinnersModel();

                        winnersModel.setWinnerName(object.getString("winners_name"));

                        winnersList.add(winnersModel);
                    }
                    if (winnersList.size() > 0) {
                        winnersAdapter = new WinnersAdapter(MainActivity.this, winnersList);
                        autoScroll();
                        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this) {
                            @Override
                            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(MainActivity.this) {
                                    private static final float SPEED = 2000f;// Change this value (default=25f)

                                    @Override
                                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                        return SPEED / displayMetrics.heightPixels;
                                    }
                                };
                                smoothScroller.setTargetPosition(position);
                                startSmoothScroll(smoothScroller);
                            }
                        };
                        winnersRv.setLayoutManager(layoutManager);
                        winnersRv.setAdapter(winnersAdapter);
                        winnersAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToast(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("WINNERS LIST ERROR", error.toString());
            showToast(error.toString());
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getGiftList() {
        giftList = new ArrayList<>();
        scootyGiftCv.setVisibility(View.GONE);
        ledTvGiftCv.setVisibility(View.GONE);
        wmGiftCv.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.getAllGiftsUrl, response -> {
            Log.d("TEXT RESPONSE", response.trim());
            try {
                giftList.clear();

                JSONObject mainObject = new JSONObject(response);
                String status = mainObject.getString("status");
                String message = mainObject.getString("message");

                if (status.equalsIgnoreCase("true")) {

                    JSONArray jsonArray = new JSONArray(message);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        GiftModel giftModel = new GiftModel();

                        giftModel.setGiftName(object.getString("gift_name"));
                        giftModel.setGiftImage(object.getString("gift_image"));
                        giftModel.setGift_category(object.getString("gift_category"));
                        if (giftModel.getGift_category().equalsIgnoreCase("Mega")) {
                            scootyGiftCv.setVisibility(View.VISIBLE);
                            Picasso.get().load(giftModel.getGiftImage()).placeholder(R.drawable.giftbox).into(imageGiftIv);
                            scootyGiftTv.setText(giftModel.getGiftName());
                        } else if (giftModel.getGift_category().equalsIgnoreCase("Mega2")) {
                            ledTvGiftCv.setVisibility(View.VISIBLE);
                            Picasso.get().load(giftModel.getGiftImage()).placeholder(R.drawable.giftbox).into(ledImageGiftIv);
                            ledGiftTv.setText(giftModel.getGiftName());
                        } else if (giftModel.getGift_category().equalsIgnoreCase("Mega3")) {
                            wmGiftCv.setVisibility(View.VISIBLE);
                            Picasso.get().load(giftModel.getGiftImage()).placeholder(R.drawable.giftbox).into(wmImageGiftIv);
                            wmGiftTv.setText(giftModel.getGiftName());
                        } else {
                            giftList.add(giftModel);
                        }
                    }
                    if (giftList.size() > 0) {
                        giftAdapter = new GiftAdapter(MainActivity.this, giftList);
                        giftRv.setAdapter(giftAdapter);
                        giftAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToast(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showToast(e.toString());
            }
        }, error -> {
            Log.e("TEXT ERROR", error.toString());
            showToast(error.toString());
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void initialization() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        scootyGiftCv = findViewById(R.id.scootyGiftCv);
        ledTvGiftCv = findViewById(R.id.ledTvGiftCv);
        wmGiftCv = findViewById(R.id.wmGiftCv);
        imageGiftIv = findViewById(R.id.imageGiftIv);
        ledImageGiftIv = findViewById(R.id.ledImageGiftIv);
        wmImageGiftIv = findViewById(R.id.wmImageGiftIv);
        giftRv = findViewById(R.id.giftRv);
        winnersRv = findViewById(R.id.winnersRv);
        tvGiftTs = findViewById(R.id.tvGiftTs);
        scootyGiftTv = findViewById(R.id.scootyGiftTv);
        ledGiftTv = findViewById(R.id.ledGiftTv);
        wmGiftTv = findViewById(R.id.wmGiftTv);
        btnRegister = findViewById(R.id.btnRegister);
        giftStatusLayout = findViewById(R.id.giftStatusLayout);

        loadingDialog = new LoadingDialog(this);
    }
}