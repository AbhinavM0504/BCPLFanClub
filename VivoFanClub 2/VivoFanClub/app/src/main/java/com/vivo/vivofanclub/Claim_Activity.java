package com.vivo.vivofanclub;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import com.vivo.vivofanclub.adapter.GiftAdapter;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateFormat;
import android.util.Base64;
import android.widget.ProgressBar;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.vivo.vivofanclub.model.GiftModel;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.LocationRequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.yalantis.ucrop.UCrop;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.vivo.vivofanclub.Common.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Claim_Activity extends AppCompatActivity {
    LinearLayout numberLayout, otpLayout;
    TextInputEditText etNumberField, etOtpField, etAadharField,etPanField;
    Button btnSendOtp, btnVerifyOtp, btnSubmit, addAadharFrontImgBtn,addAadharBacktImgBtn,addPanImgBtn;
    LoadingDialog loadingDialog;
    CountDownTimer countDownTimer;
    TextView tvTextSlider, tvTimer,tvFullName,tvImeiNumber,tvAddress,tvPinCode;

    String value = "", aadhar_front_image_name = "", aadhar_back_image_name = "",pan_image_name="";
    TableLayout details_card;
    private ResultReceiver resultReceiver;
    private TextView tvLatLong;
    private ProgressBar progressBar;
    private ArrayList<GiftModel> giftList;
    private GiftAdapter giftAdapter;


    private LocationRequest locationRequest;

    private Uri imageUri = null;
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int CAMERA_REQUEST_CODE = 100;
    private String[] cameraPermissions;

    private Uri aadhar_front_image_uri = null;
    private Uri aadhar_back_image_uri = null;
    private Uri pan_image_uri=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim);


        initialization();


        btnSendOtp.setOnClickListener(view -> {
            String number = etNumberField.getText() != null ? etNumberField.getText().toString().trim() : "";
            if (number.isEmpty()) {
                etNumberField.setError("Contact No Required");
                etNumberField.requestFocus();
            } else if (number.length() != 10) {
                etNumberField.setError("Invalid Contact No");
                etNumberField.requestFocus();
            } else {
                btnSendOtp.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(Claim_Activity.this);
                builder.setTitle("Alert");
                builder.setMessage("Are you sure to verify this number?");
                builder.setIcon(R.drawable.alert);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.cancel();
                    btnSendOtp.setEnabled(true);
                    sendOtp(number);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    btnSendOtp.setEnabled(true);
                    dialog.cancel();
                });
                builder.setOnCancelListener(dialog -> btnSendOtp.setEnabled(true));
                builder.show();
            }
        });


        btnVerifyOtp.setOnClickListener(view -> {
            String number = etNumberField.getText() != null ? etNumberField.getText().toString().trim() : "";
            String otp = etOtpField.getText() != null ? etOtpField.getText().toString().trim() : "";

            if (number.isEmpty()) {
                etNumberField.setError("Contact No Required");
                etNumberField.requestFocus();
            } else if (number.length() != 10) {
                etNumberField.setError("Invalid Contact No");
                etNumberField.requestFocus();
            } else if (otp.isEmpty()) {
                etOtpField.setError("OTP No. Required");
                etOtpField.requestFocus();
            } else {
                verifyOtp(number, otp);
            }
        });


        addAadharFrontImgBtn.setOnClickListener(view -> {
            closeKeyboard();
            value = "aadhar_front_image";
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                pickFromCamera();
            }
        });


        addAadharBacktImgBtn.setOnClickListener(view -> {
            closeKeyboard();
            value = "aadhar_back_image";
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                pickFromCamera();
            }
        });

        addPanImgBtn.setOnClickListener(view -> {
            closeKeyboard();
            value = "pan_image";
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                pickFromCamera();
            }
        });
        btnSubmit.setOnClickListener(view -> {
            // Extract data from input fields
            String name = Objects.requireNonNull(tvFullName.getText()).toString().trim();
            String number = Objects.requireNonNull(etNumberField.getText()).toString().trim();
            String imeiNo = Objects.requireNonNull(tvImeiNumber.getText()).toString().trim();
            String address = Objects.requireNonNull(tvAddress.getText()).toString().trim();
            String pin = Objects.requireNonNull(tvPinCode.getText()).toString().trim();
            String aadharNumber = Objects.requireNonNull(etAadharField.getText()).toString().trim();
            String panNumber = Objects.requireNonNull(etPanField.getText()).toString().trim();

            // Validation checks
            if (name.isEmpty()) {
                tvFullName.setError("Full Name Required");
                tvFullName.requestFocus();
            } else if (imeiNo.isEmpty()) {
                tvImeiNumber.setError("IMEI No Required");
                tvImeiNumber.requestFocus();
            } else if (imeiNo.length() != 15) {
                tvImeiNumber.setError("Invalid IMEI No.");
                tvImeiNumber.requestFocus();
            } else if (number.isEmpty()) {
                etNumberField.setError("Contact No Required");
                etNumberField.requestFocus();
            } else if (number.length() != 10) {
                etNumberField.setError("Invalid Contact No");
                etNumberField.requestFocus();
            } else if (address.isEmpty()) {
                tvAddress.setError("Address Required");
                tvAddress.requestFocus();
            } else if (pin.isEmpty()) {
                tvPinCode.setError("PIN Required");
                tvPinCode.requestFocus();
            } else if (pin.length() != 6) {
                tvPinCode.setError("Invalid PIN Code");
                tvPinCode.requestFocus();
            } else if (aadharNumber.isEmpty()) {
                etAadharField.setError("Aadhar Number Required");
                etAadharField.requestFocus();
            } else if (panNumber.isEmpty()) {
                etPanField.setError("Pan Number Required");
                etPanField.requestFocus();
            } else if (aadhar_front_image_uri == null) {
                showToast("Aadhar Front Image Required...");
            } else if (aadhar_back_image_uri == null) {
                showToast("Aadhar Back Image Required...");
            } else if (pan_image_uri == null) {
                showToast("Pan Image Required...");
            } else {
                closeKeyboard();
                btnSubmit.setEnabled(false);

                // Show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Claim_Activity.this);
                builder.setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dialog.cancel();
                            btnSubmit.setEnabled(true);

                            // Prepare the data to be submitted
                            Bundle bundle = new Bundle();
                            bundle.putString("name", name);
                            bundle.putString("number", number);
                            bundle.putString("imeiNo", imeiNo);
                            bundle.putString("address", address);
                            bundle.putString("pin", pin);
                            bundle.putString("aadhar_number", aadharNumber);
                            bundle.putString("pan_number", panNumber);
                            bundle.putString("aadharFrontImageUri", String.valueOf(aadhar_front_image_uri));
                            bundle.putString("aadharBackImageUri", String.valueOf(aadhar_back_image_uri));
                            bundle.putString("panImageUri", String.valueOf(pan_image_uri));
                            bundle.putString("aadharFrontImageName", aadhar_front_image_name);
                            bundle.putString("aadharBackImageName", aadhar_back_image_name);
                            bundle.putString("panImageName", pan_image_name);

                            // Execute AsyncTask to save data to the backend
                            new SaveDataToDb().execute(bundle);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            btnSubmit.setEnabled(true);
                            dialog.cancel();
                        })
                        .setOnCancelListener(dialogInterface -> btnSubmit.setEnabled(true))
                        .show();
            }
        });




    }



    private class SaveDataToDb extends AsyncTask<Bundle, Void, Boolean> {
        JSONObject jsonObject;
        RequestQueue requestQueue;
        Bitmap cAadharFrontImageBitmap = null, cAadharBackImageBitmap = null, cPanImageBitmap = null;

        @Override
        protected Boolean doInBackground(Bundle... bundles) {
            try {
                // Retrieve data from the bundle
                Bundle bundle = bundles[0];
                String name = bundle.getString("name", "");
                String number = bundle.getString("number", "");
                String imeiNo = bundle.getString("imeiNo", "");
                String address = bundle.getString("address", "");
                String pin = bundle.getString("pin", "");
                String aadharNumber = bundle.getString("aadhar_number", "");
                String panNumber = bundle.getString("pan_number", "");
                String aadharFrontImageUri = bundle.getString("aadharFrontImageUri", "");
                String aadharBackImageUri = bundle.getString("aadharBackImageUri", "");
                String panImageUri = bundle.getString("panImageUri", "");
                String aadharFrontImageName = bundle.getString("aadharFrontImageName", "");
                String aadharBackImageName = bundle.getString("aadharBackImageName", "");
                String panImageName = bundle.getString("panImageName", "");

                // Convert images to Base64
                cAadharFrontImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(aadharFrontImageUri));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                cAadharFrontImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                String aadharFrontImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                cAadharBackImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(aadharBackImageUri));
                ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                cAadharBackImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream1);
                String aadharBackImage = Base64.encodeToString(byteArrayOutputStream1.toByteArray(), Base64.DEFAULT);

                cPanImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(panImageUri));
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                cPanImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream2);
                String panImage = Base64.encodeToString(byteArrayOutputStream2.toByteArray(), Base64.DEFAULT);

                // Create the JSON object with the required data
                long millisecond = Long.parseLong(String.valueOf(Build.TIME));
                String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();

                jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("number", number);
                jsonObject.put("imeiNo", imeiNo);
                jsonObject.put("address", address);
                jsonObject.put("pin", pin);
                jsonObject.put("aadhar_number", aadharNumber);
                jsonObject.put("pan_number", panNumber);
                jsonObject.put("aadharFrontImageName", aadharFrontImageName);
                jsonObject.put("aadharBackImageName", aadharBackImageName);
                jsonObject.put("panImageName", panImageName);
                jsonObject.put("aadharFrontImage", aadharFrontImage);
                jsonObject.put("aadharBackImage", aadharBackImage);
                jsonObject.put("panImage", panImage);
                jsonObject.put("brandName", Build.BRAND);
                jsonObject.put("modelName", Build.MODEL);
                jsonObject.put("manufacturerName", Build.MANUFACTURER);
                jsonObject.put("activationTime", dateString);

                // Create a request to submit data to the backend
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.saveData, jsonObject,
                        response -> {
                            Log.d("IMAGE RESPONSE", response.toString());
                            requestQueue.getCache().clear();
                            loadingDialog.hideDialog();
                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equalsIgnoreCase("true")) {
                                    showToast(message);
                                    startActivity(new Intent(Claim_Activity.this, MainActivity.class));
                                    finish();
                                } else {
                                    showToast(message);
                                }
                            } catch (JSONException e) {
                                Log.e("JSON EXCEPTION", e.toString());
                                showToast("JSON parsing error: " + e.getMessage());
                            }
                        }, error -> {
                    loadingDialog.hideDialog();
                    Log.e("IMAGE ERROR", error.toString());
                    showToast("Network error: " + error.getMessage());
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Initialize the request queue and add the request
                requestQueue = Volley.newRequestQueue(Claim_Activity.this);
                requestQueue.add(jsonObjectRequest);

            } catch (IOException e) {
                Log.e("IO EXCEPTION", e.toString());
                loadingDialog.hideDialog();
                showToast("Image processing error: " + e.getMessage());
                return false;
            } catch (JSONException e) {
                Log.e("JSON EXCEPTION", e.toString());
                loadingDialog.hideDialog();
                showToast("JSON error: " + e.getMessage());
                return false;
            } catch (Exception e) {
                Log.e("EXCEPTION", e.toString());
                loadingDialog.hideDialog();
                showToast("Unexpected error: " + e.getMessage());
                return false;
            }
            return true;
        }
    }









        private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(Claim_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(Claim_Activity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(Claim_Activity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() > 0) {

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        Log.d("COORDINATES: ", "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                                        tvLatLong.setText("Latitude: " + latitude + "\n" + "Longitude: " + longitude);

                                        Location location = new Location("providerNA");
                                        location.setLatitude(latitude);
                                        location.setLongitude(longitude);
                                        fetchAddressFromLatLong(location);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }





    private void fetchAddressFromLatLong(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }





    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(task -> {

            try {
                LocationSettingsResponse response = task.getResult(ApiException.class);
                //Toast.makeText(RegisterActivity.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

            } catch (ApiException e) {
                switch (e.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(Claim_Activity.this, 2);
                        } catch (IntentSender.SendIntentException ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //Device does not have location
                        break;
                }
            }
        });

    }






    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }







    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image description");
        //put image uri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to open camera for image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }






    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }








    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }





    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }







    private void sendOtp(String number) {
        loadingDialog.showDialog("Loading...");

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.sendClaimOtpUrl, response -> {
                loadingDialog.hideDialog();
                Log.d("SEND OTP RESPONSE", response.trim());

                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equalsIgnoreCase("true")) {
                        showToast(message);
                        startTimer();
                    } else {
                        showToast(message);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                loadingDialog.hideDialog();
                Log.e("SEND OTP ERROR", error.toString());
                showToast(error.toString());
            }) {
                @NonNull
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("number", number);
                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            showToast(e.toString());
        }
    }










    private void verifyOtp(String number, String otp) {
        loadingDialog.showDialog("Verifying...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.verifyOtpUrl, response -> {
            loadingDialog.hideDialog();
            Log.d("VERIFY OTP RESPONSE", response);  // Log the raw response
            showToast(response);
            if (response.trim().equalsIgnoreCase("Verified")) {

                etNumberField.setEnabled(false);
                btnSendOtp.setEnabled(false);
                btnSendOtp.setAlpha(0.6f);
                etOtpField.setEnabled(false);
                btnVerifyOtp.setEnabled(false);
                btnVerifyOtp.setAlpha(0.6f);
                setCard();
                TableLayout card_view=findViewById(R.id.details_card);
                details_card.setVisibility(View.VISIBLE);




                countDownTimer.cancel();
                tvTimer.setVisibility(View.GONE);





                //enable submit button
                btnSubmit.setEnabled(true);
                btnSubmit.setAlpha(1f);
            }

        },error -> {
            loadingDialog.hideDialog();
            Log.e("VERIFY OTP ERROR", error.toString());
            showToast(error.toString());
        })
        {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", "claim otp");
                params.put("number", number);
                params.put("otp", otp);
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


    private void setCard() {
        String number = etNumberField.getText().toString().trim();
        Log.d("SENDING NUMBER", number);



        if (number.isEmpty()) {
            showToast("Please enter a valid number.");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.verifyClaimOtp, response -> {
            Log.d("TEXT RESPONSE", response.trim());
            try {
                JSONObject mainObject = new JSONObject(response);
                boolean status = mainObject.getBoolean("status"); // Changed to boolean
                String message = mainObject.getString("message");

                if (status) {
                    JSONArray jsonArray = new JSONArray(message);
                    if (jsonArray.length() > 0) { // Check if the array has items
                        JSONObject object = jsonArray.getJSONObject(0); // Get the first object
                        tvFullName.setText(object.getString("name"));
                        tvAddress.setText(object.getString("address"));
                        tvImeiNumber.setText(object.getString("IMEI"));// Only displaying the name from the first object
                    } else {
                        showToast("No data found.");
                    }
                } else {
                    showToast(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showToast("JSON Parsing error: " + e.getMessage()); // Improved error message
            }
        }, error -> {
            Log.e("TEXT ERROR", error.toString());
            showToast("Network Error: " + error.getMessage()); // More informative error message
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("number", number);
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









    private void startTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setVisibility(View.VISIBLE);
                tvTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
                btnSendOtp.setEnabled(false);
                btnSendOtp.setAlpha(0.6f);
                otpLayout.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                tvTimer.setVisibility(View.GONE);
                btnSendOtp.setEnabled(true);
                btnSendOtp.setAlpha(1f);

                btnSendOtp.setText("Resend");
                otpLayout.setVisibility(View.GONE);
            }
        }.start();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);


        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            } else {
                showToast("Location permission is required...");
                finish();
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // When the image is picked from the camera, start the UCrop activity
                Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));

                UCrop.of(imageUri, destinationUri)  // Start uCrop with source and destination URIs
                        .withAspectRatio(1, 1)           // Optional: Set aspect ratio (e.g., square cropping)
                        .withMaxResultSize(1080, 1080)   // Optional: Set max width and height
                        .start(this);                    // Start UCrop

            } else if (requestCode == UCrop.REQUEST_CROP) {
                // Handling the UCrop result
                final Uri croppedImageUri = UCrop.getOutput(data); // Get the cropped image URI

                if (croppedImageUri != null) {
                    if (value.equalsIgnoreCase("aadhar_front_image")) {
                        aadhar_front_image_uri = croppedImageUri;
                        aadhar_front_image_name = "aadhar_front_image_" + System.currentTimeMillis() + ".jpeg";
                        addAadharFrontImgBtn.setText(aadhar_front_image_name);  // Set the name of the cropped image on the button
                    }

                    if (value.equalsIgnoreCase("aadhar_back_image")) {
                        aadhar_back_image_uri = croppedImageUri;
                        aadhar_back_image_name = "aadhar_back_image" + System.currentTimeMillis() + ".jpeg";
                        addAadharBacktImgBtn.setText(aadhar_back_image_name);   // Set the name of the cropped invoice image on the button
                    }
                    if (value.equalsIgnoreCase("pan_image")) {
                        pan_image_uri = croppedImageUri;
                        pan_image_name = "pan_image" + System.currentTimeMillis() + ".jpeg";
                        addPanImgBtn.setText(pan_image_name);   // Set the name of the cropped invoice image on the button
                    }
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Handle the cropping error
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, "Cropping failed: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }




    private void showToast(String message) {
        Toast.makeText(Claim_Activity.this, message, Toast.LENGTH_SHORT).show();
    }


    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == Constants.SUCCESS_RESULT) {
                tvAddress.setText(resultData.getString(Constants.RESULT_DATA_KEY));
                Log.d("LOCATION ADDRESS", Constants.RESULT_DATA_KEY);
            } else {
                showToast(resultData.getString(Constants.RESULT_DATA_KEY));
            }
            progressBar.setVisibility(View.GONE);
        }
    }





    private void initialization() {
        numberLayout = findViewById(R.id.numberLayout);
        etNumberField = findViewById(R.id.etNumberField);
        etAadharField = findViewById(R.id.etAadharField);
        etPanField=findViewById(R.id.etPanField);

        btnSendOtp = findViewById(R.id.btnSendOtp);
        loadingDialog = new LoadingDialog(this);
        tvTimer = findViewById(R.id.tvTimer);
        otpLayout = findViewById(R.id.otpLayout);
        etOtpField = findViewById(R.id.etOtpField);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnSubmit = findViewById(R.id.btnSubmit);
        addAadharFrontImgBtn = findViewById(R.id.addAadharFrontImgBtn);
        addAadharBacktImgBtn = findViewById(R.id.addAadharBackImgBtn);
        addPanImgBtn=findViewById(R.id.addPanImgBtn);
        progressBar = findViewById(R.id.progressBar);
        details_card=findViewById(R.id.details_card);
        btnSubmit=findViewById(R.id.btnSubmit);
        tvFullName = findViewById(R.id.tvFullName);
        tvImeiNumber = findViewById(R.id.tvImeiNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvPinCode = findViewById(R.id.tvPinCode);

        btnSubmit.setEnabled(false);
        btnSubmit.setAlpha(0.6f);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        resultReceiver = new AddressResultReceiver(new Handler());
        tvLatLong = findViewById(R.id.tvLatLong);



    }
}
