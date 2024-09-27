package com.vivo.vivofanclub;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.vivo.vivofanclub.Common.NetworkChangeListener;
import com.vivo.vivofanclub.Common.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class QuestionsActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    CardView qLayout_1, qLayout_2A, qLayout_2B, qLayout_3, qLayout_4A, qLayout_4B, qLayout_5, qLayout_6;
    Spinner ansSpinner_1, ansSpinner_2A, ansSpinner_2B, ansSpinner_3, ansSpinner_4A, ansSpinner_4B, ansSpinner_5, ansSpinner_6;
    Button btnSubmit;

    //Loading Dialog
    LoadingDialog loadingDialog;

    //Required Variables
    Intent intent;
    String ansSpinner_1_value = "", ansSpinner_2A_value = "", ansSpinner_2B_value = "", ansSpinner_3_value = "", ansSpinner_4A_value = "", ansSpinner_4B_value = "", ansSpinner_5_value = "", ansSpinner_6_value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        initialization();

        setupAnswerOneSpinner();

        setupAnswerThreeSpinner();

        //setup answer two spinner
        btnSubmit.setOnClickListener(v -> validateQuestion());
    }

    private void setupAnswerThreeSpinner() {
        ansSpinner_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ansSpinner_3_value = parent.getItemAtPosition(position).toString();
                if (ansSpinner_3_value.equalsIgnoreCase(getResources().getString(R.string.select_option))) {
                    qLayout_4A.setVisibility(View.GONE);
                    ansSpinner_4A.setEnabled(false);
                    ansSpinner_4A.setSelection(0);
                    qLayout_4B.setVisibility(View.GONE);
                    ansSpinner_4B.setEnabled(false);
                    ansSpinner_4B.setSelection(0);

                } else if (ansSpinner_3_value.equalsIgnoreCase(getResources().getString(R.string.vivo))) {
                    qLayout_4A.setVisibility(View.VISIBLE);
                    ansSpinner_4A.setEnabled(true);
                    qLayout_4B.setVisibility(View.GONE);
                    ansSpinner_4B.setEnabled(false);
                    ansSpinner_4B.setSelection(0);

                } else {
                    qLayout_4A.setVisibility(View.GONE);
                    ansSpinner_4A.setEnabled(false);
                    ansSpinner_4A.setSelection(0);
                    qLayout_4B.setVisibility(View.VISIBLE);
                    ansSpinner_4B.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupAnswerOneSpinner() {
        ansSpinner_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ansSpinner_1_value = parent.getItemAtPosition(position).toString();
                if (ansSpinner_1_value.equalsIgnoreCase(getResources().getString(R.string.select_option))
                        || ansSpinner_1_value.equalsIgnoreCase(getResources().getString(R.string.neutral))) {
                    qLayout_2A.setVisibility(View.GONE);
                    ansSpinner_2A.setEnabled(false);
                    ansSpinner_2A.setSelection(0);
                    qLayout_2B.setVisibility(View.GONE);
                    ansSpinner_2B.setEnabled(false);
                    ansSpinner_2B.setSelection(0);

                } else if (ansSpinner_1_value.equalsIgnoreCase(getResources().getString(R.string.highly_satisfied))
                        || ansSpinner_1_value.equalsIgnoreCase(getResources().getString(R.string.satisfied))) {
                    qLayout_2A.setVisibility(View.VISIBLE);
                    ansSpinner_2A.setEnabled(true);
                    qLayout_2B.setVisibility(View.GONE);
                    ansSpinner_2B.setEnabled(false);
                    ansSpinner_2B.setSelection(0);

                } else if (ansSpinner_1_value.equalsIgnoreCase(getResources().getString(R.string.unsatisfied))
                        || ansSpinner_1_value.equalsIgnoreCase(getResources().getString(R.string.highly_unsatisfied))) {
                    qLayout_2A.setVisibility(View.GONE);
                    ansSpinner_2A.setEnabled(false);
                    ansSpinner_2A.setSelection(0);
                    qLayout_2B.setVisibility(View.VISIBLE);
                    ansSpinner_2B.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void validateQuestion() {
        intent = getIntent();
        if (intent.getExtras() == null) {
            showToast("Something went wrong !!!");
        } else {
            ansSpinner_1_value = ansSpinner_1.getSelectedItem().toString();
            ansSpinner_2A_value = ansSpinner_2A.getSelectedItem().toString();
            ansSpinner_2B_value = ansSpinner_2B.getSelectedItem().toString();
            ansSpinner_3_value = ansSpinner_3.getSelectedItem().toString();
            ansSpinner_4A_value = ansSpinner_4A.getSelectedItem().toString();
            ansSpinner_4B_value = ansSpinner_4B.getSelectedItem().toString();
            ansSpinner_5_value = ansSpinner_5.getSelectedItem().toString();
            ansSpinner_6_value = ansSpinner_6.getSelectedItem().toString();

            String select_option = getResources().getString(R.string.select_option);
            String highly_satisfied = getResources().getString(R.string.highly_satisfied);
            String satisfied = getResources().getString(R.string.satisfied);
            String unsatisfied = getResources().getString(R.string.unsatisfied);
            String highly_unsatisfied = getResources().getString(R.string.highly_unsatisfied);
            String vivo = getResources().getString(R.string.vivo);

            if (ansSpinner_1_value.equalsIgnoreCase("") || ansSpinner_1_value.equalsIgnoreCase(select_option)) {
                showToast("Please select question one...");
            } else if ((ansSpinner_1_value.equalsIgnoreCase(highly_satisfied) || ansSpinner_1_value.equalsIgnoreCase(satisfied))
                    && (ansSpinner_2A_value.equalsIgnoreCase("") || ansSpinner_2A_value.equalsIgnoreCase(select_option))) {
                showToast("Please select question 1.1...");
            } else if ((ansSpinner_1_value.equalsIgnoreCase(highly_unsatisfied) || ansSpinner_1_value.equalsIgnoreCase(unsatisfied))
                    && (ansSpinner_2B_value.equalsIgnoreCase("") || ansSpinner_2B_value.equalsIgnoreCase(select_option))) {
                showToast("Please select question 1.1...");
            } else if (ansSpinner_3_value.equalsIgnoreCase("") || ansSpinner_3_value.equalsIgnoreCase(select_option)) {
                showToast("Please select question 2...");
            } else if (ansSpinner_3_value.equalsIgnoreCase(vivo)
                    && (ansSpinner_4A_value.equalsIgnoreCase("") || ansSpinner_4A_value.equalsIgnoreCase(select_option))) {
                showToast("Please select question 2.1...");
            } else if ((!ansSpinner_3_value.equalsIgnoreCase(vivo))
                    && (ansSpinner_4B_value.equalsIgnoreCase("") || ansSpinner_4B_value.equalsIgnoreCase(select_option))) {
                showToast("Please select question 2.1...");
            } else if (ansSpinner_5_value.equalsIgnoreCase("") || ansSpinner_5_value.equalsIgnoreCase(select_option)) {
                showToast("Please select question 3...");
            } else if (ansSpinner_6_value.equalsIgnoreCase("") || ansSpinner_6_value.equalsIgnoreCase(select_option)) {
                showToast("Please select question 4...");
            } else {
                //save data to db
                closeKeyboard();
                btnSubmit.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(QuestionsActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure ?");
                builder.setIcon(R.drawable.alert);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.cancel();
                    btnSubmit.setEnabled(true);

                    //save data to db
                    loadingDialog.showDialog("Saving Data...");
                    new SaveDataToDb().execute();

                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    btnSubmit.setEnabled(true);
                    dialog.cancel();
                });
                builder.setOnCancelListener(dialogInterface -> btnSubmit.setEnabled(true));
                builder.show();
            }
        }

    }

    private class SaveDataToDb extends AsyncTask<String, Void, Boolean> {

        JSONObject jsonObject;
        RequestQueue requestQueue;
        Bitmap cImageBitmap = null, cInvoiceImageBitmap = null;

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                //get Bundle data
                Bundle bundle = intent.getExtras();
                String name = bundle.getString("name", "");
                String number = bundle.getString("number", "");
                String imeiNo = bundle.getString("imeiNo", "");
                String address = bundle.getString("address", "");
                String pin = bundle.getString("pin", "");
                String modelPurchase = bundle.getString("modelPurchase", "");
                String oldPhoneBrand = bundle.getString("oldPhoneBrand", "");
                String locationPoints = bundle.getString("locationPoints", "");
                String locationAddress = bundle.getString("locationAddress", "");
                String customerImageName = bundle.getString("customerImageName", "");
                String customerImageUri = bundle.getString("customerImage", "");
                String customerInvoiceImageName = bundle.getString("customerInvoiceImageName", "");
                String customerInvoiceImageUri = bundle.getString("customerInvoiceImage", "");

                //get customer image bitmap
                cImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(customerImageUri));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                cImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                String customerImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                //get invoice image bitmap
                cInvoiceImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(customerInvoiceImageUri));
                ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                cInvoiceImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream1);
                String customerInvoiceImage = Base64.encodeToString(byteArrayOutputStream1.toByteArray(), Base64.DEFAULT);

                try {
                    long millisecond = Long.parseLong(String.valueOf(Build.TIME));
                    String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();

                    jsonObject = new JSONObject();
                    jsonObject.put("name", name);
                    jsonObject.put("number", number);
                    jsonObject.put("imeiNo", imeiNo);
                    jsonObject.put("address", address);
                    jsonObject.put("pin", pin);
                    jsonObject.put("modelPurchase", modelPurchase);
                    jsonObject.put("oldPhoneBrand", oldPhoneBrand);
                    jsonObject.put("locationPoints", locationPoints);
                    jsonObject.put("locationAddress", locationAddress);
                    jsonObject.put("customerImageName", customerImageName);
                    jsonObject.put("customerImage", customerImage);
                    jsonObject.put("customerInvoiceImageName", customerInvoiceImageName);
                    jsonObject.put("customerInvoiceImage", customerInvoiceImage);

                    jsonObject.put("brandName", Build.BRAND);
                    jsonObject.put("modelName", Build.MODEL);
                    jsonObject.put("manufacturerName", Build.MANUFACTURER);
                    jsonObject.put("activationTime", dateString);

                    //save answers
                    jsonObject.put("ans_1_value", (ansSpinner_1_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_1_value));
                    jsonObject.put("ans_2A_value", (ansSpinner_2A_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_2A_value));
                    jsonObject.put("ans_2B_value", (ansSpinner_2B_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_2B_value));
                    jsonObject.put("ans_3_value", (ansSpinner_3_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_3_value));
                    jsonObject.put("ans_4A_value", (ansSpinner_4A_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_4A_value));
                    jsonObject.put("ans_4B_value", (ansSpinner_4B_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_4B_value));
                    jsonObject.put("ans_5_value", (ansSpinner_5_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_5_value));
                    jsonObject.put("ans_6_value", (ansSpinner_6_value.equalsIgnoreCase(getString(R.string.select_option)) ? "" : ansSpinner_6_value));

                } catch (JSONException e) {
                    loadingDialog.hideDialog();
                    showToast(e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.saveDataToDbUrl, jsonObject,
                        jsonObject -> {
                            Log.d("IMAGE RESPONSE", jsonObject.toString());
                            requestQueue.getCache().clear();
                            loadingDialog.hideDialog();
                            try {
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                if (status.equalsIgnoreCase("true")) {
                                    //Data Save
                                    showToast(message);
                                    startActivity(new Intent(QuestionsActivity.this, GiftActivity.class)
                                            .putExtra("name", name)
                                            .putExtra("number", number)
                                            .putExtra("imeiNo", imeiNo));
                                    finish();
                                } else {
                                    showToast(message);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                showToast(e.toString());
                            }
                        }, volleyError -> {
                    loadingDialog.hideDialog();
                    Log.e("IMAGE ERROR", volleyError.toString());
                    showToast(volleyError.toString());

                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue = Volley.newRequestQueue(QuestionsActivity.this);
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
                loadingDialog.hideDialog();
            }
            return true;
        }
    }

    private void initialization() {
        qLayout_1 = findViewById(R.id.qLayout_1);
        qLayout_2A = findViewById(R.id.qLayout_2A);
        qLayout_2B = findViewById(R.id.qLayout_2B);
        qLayout_3 = findViewById(R.id.qLayout_3);
        qLayout_4A = findViewById(R.id.qLayout_4A);
        qLayout_4B = findViewById(R.id.qLayout_4B);
        qLayout_5 = findViewById(R.id.qLayout_5);
        qLayout_6 = findViewById(R.id.qLayout_6);
        ansSpinner_1 = findViewById(R.id.ansSpinner_1);
        ansSpinner_2A = findViewById(R.id.ansSpinner_2A);
        ansSpinner_2B = findViewById(R.id.ansSpinner_2B);
        ansSpinner_3 = findViewById(R.id.ansSpinner_3);
        ansSpinner_4A = findViewById(R.id.ansSpinner_4A);
        ansSpinner_4B = findViewById(R.id.ansSpinner_4B);
        ansSpinner_5 = findViewById(R.id.ansSpinner_5);
        ansSpinner_6 = findViewById(R.id.ansSpinner_6);
        btnSubmit = findViewById(R.id.btnSubmit);

        loadingDialog = new LoadingDialog(this);
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

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showToast(String message) {
        Toast.makeText(QuestionsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}