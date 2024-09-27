package com.vivo.vivofanclub;


import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.util.Date;

import android.text.format.DateFormat;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Base64;
import android.Manifest;
import android.graphics.Bitmap;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.vivo.vivofanclub.Common.NetworkChangeListener;
import com.vivo.vivofanclub.Common.URLs;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    //views
    LinearLayout numberLayout, otpLayout, verifiedLayout, notVerifiedLayout;
    TextInputLayout addressInputLayout, pinInputLayout, modelInputLayout, oldPhoneInputLayout;
    TextInputEditText etNameField, etNumberField, etOtpField, etImeiNumberField, etAddressField, etPinField, etModelField, etOldPhoneField;
    ImageButton qrScanImgBtn;
    TextView tvTextSlider, tvTimer;
    Button btnSendOtp, btnVerifyOtp, addCustomerImgBtn, addInvoiceImgBtn, btnSubmit, buttonGetCurrentLocation;
    private TextView tvLatLong, tvAddress;
    private ProgressBar progressBar;


    //Required Variables
    LoadingDialog loadingDialog;
    CountDownTimer countDownTimer;
    private IntentIntegrator qrScan;
    private ResultReceiver resultReceiver;
    private LocationRequest locationRequest;

    //IMAGE VARIABLES
    private String[] cameraPermissions;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    JSONObject jsonObject;
    RequestQueue rQueue;
    private Uri imageUri = null, customer_image_uri = null, customer_invoice_image_uri = null;
    String value = "", customer_image_name = "", invoice_image_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initialization();

        //initiate qr scan function
        qrScanImgBtn.setOnClickListener(v -> qrScan.initiateScan());

        //ask location permission
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        getCurrentLocation();

        //check imei number
        etImeiNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String imeiNo = Objects.requireNonNull(etImeiNumberField.getText()).toString().trim();
                if (imeiNo.length() == 15) {
                    //check if imei is verified or not
                    loadingDialog.showDialog("Loading...");
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.checkValidImeiUrl, response -> {
                        loadingDialog.hideDialog();
                        Log.d("VERIFIED IMEI RESPONSE", response.trim());
                        try {
                            JSONObject object = new JSONObject(response);
                            String status = object.getString("status");
                            String message = object.getString("message");
                            String model = object.getString("model");

                            if (status.equalsIgnoreCase("true")) {
                                verifiedLayout.setVisibility(View.VISIBLE);
                                notVerifiedLayout.setVisibility(View.GONE);

                                //set model name
                                if (!message.isEmpty())
                                    etModelField.setText(model);

                            } else {
                                verifiedLayout.setVisibility(View.GONE);
                                notVerifiedLayout.setVisibility(View.VISIBLE);
                                etModelField.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast(e.toString());
                        }
                    }, error -> {
                        loadingDialog.hideDialog();
                        Log.e("VERIFIED IMEI ERROR", error.toString());
                        showToast(error.toString());
                    }) {
                        @NonNull
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("imeiNo", imeiNo);
                            return params;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            0,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                    requestQueue.add(stringRequest);

                } else {
                    verifiedLayout.setVisibility(View.GONE);
                    notVerifiedLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //handle send otp button
        btnSendOtp.setOnClickListener(view -> {
            String name = Objects.requireNonNull(etNameField.getText()).toString().trim();
            String number = Objects.requireNonNull(etNumberField.getText()).toString().trim();
            String imeiNo = Objects.requireNonNull(etImeiNumberField.getText()).toString().trim();

            if (name.isEmpty()) {
                etNameField.setError("Name Required");
                etNameField.requestFocus();
            } else if (imeiNo.isEmpty()) {
                etImeiNumberField.setError("IMEI No Required");
                etImeiNumberField.requestFocus();
            } else if (imeiNo.length() != 15) {
                etImeiNumberField.setError("Invalid IMEI No.");
                etImeiNumberField.requestFocus();
            } else if (notVerifiedLayout.getVisibility() == View.VISIBLE) {
                etImeiNumberField.setError("Invalid IMEI No.");
                etImeiNumberField.requestFocus();
            } else if (number.isEmpty()) {
                etNumberField.setError("Contact No Required");
                etNumberField.requestFocus();
            } else if (number.length() != 10) {
                etNumberField.setError("Invalid Contact No");
                etNumberField.requestFocus();
            } else {
                btnSendOtp.setEnabled(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Are you sure to verify this number ?");
                builder.setIcon(R.drawable.alert);
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.cancel();
                    btnSendOtp.setEnabled(true);
                    sendOtp(name, number, imeiNo);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    btnSendOtp.setEnabled(true);
                    dialog.cancel();
                });
                builder.setOnCancelListener(dialog -> btnSendOtp.setEnabled(true));
                builder.show();
            }
        });

        //handle verify otp button
        btnVerifyOtp.setOnClickListener(view -> {
            String name = Objects.requireNonNull(etNameField.getText()).toString().trim();
            String number = Objects.requireNonNull(etNumberField.getText()).toString().trim();
            String imeiNo = Objects.requireNonNull(etImeiNumberField.getText()).toString().trim();
            String otp = Objects.requireNonNull(etOtpField.getText()).toString().trim();

            if (name.isEmpty()) {
                etNameField.setError("Name Required");
                etNameField.requestFocus();
            } else if (imeiNo.isEmpty()) {
                etImeiNumberField.setError("IMEI No Required");
                etImeiNumberField.requestFocus();
            } else if (imeiNo.length() != 15) {
                etImeiNumberField.setError("Invalid IMEI No.");
                etImeiNumberField.requestFocus();
            } else if (notVerifiedLayout.getVisibility() == View.VISIBLE) {
                etImeiNumberField.setError("Invalid IMEI No.");
                etImeiNumberField.requestFocus();
            } else if (number.isEmpty()) {
                etNumberField.setError("Contact No Required");
                etNumberField.requestFocus();
            } else if (number.length() != 10) {
                etNumberField.setError("Invalid Contact No");
                etNumberField.requestFocus();
            } else if (otp.isEmpty()) {
                etOtpField.setError("OTP No Required");
                etOtpField.requestFocus();
            } else {
                verifyOtp(name, number, otp);
            }
        });

        //handle add customer image btn
        addCustomerImgBtn.setOnClickListener(v -> {
            closeKeyboard();
            value = "customer_image";
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                pickFromCamera();
            }
        });

        //handle add customer image btn
        addInvoiceImgBtn.setOnClickListener(v -> {
            closeKeyboard();
            value = "customer_invoice_image";
            if (!checkCameraPermission()) {
                requestCameraPermission();
            } else {
                pickFromCamera();
            }
        });

        //handle submit button
        btnSubmit.setOnClickListener(view -> {
            // Extract data from input fields
            String name = Objects.requireNonNull(etNameField.getText()).toString().trim();
            String number = Objects.requireNonNull(etNumberField.getText()).toString().trim();
            String imeiNo = Objects.requireNonNull(etImeiNumberField.getText()).toString().trim();
            String address = Objects.requireNonNull(etAddressField.getText()).toString().trim();
            String pin = Objects.requireNonNull(etPinField.getText()).toString().trim();
            String modelPurchase = Objects.requireNonNull(etModelField.getText()).toString().trim();
            String oldPhoneBrand = Objects.requireNonNull(etOldPhoneField.getText()).toString().trim();
            String locationPoints = tvLatLong.getText().toString().trim();
            String locationAddress = tvAddress.getText().toString().trim();

            // Validation checks
            if (name.isEmpty()) {
                etNameField.setError("Name Required");
                etNameField.requestFocus();
            } else if (imeiNo.isEmpty()) {
                etImeiNumberField.setError("IMEI No Required");
                etImeiNumberField.requestFocus();
            } else if (imeiNo.length() != 15) {
                etImeiNumberField.setError("Invalid IMEI No.");
                etImeiNumberField.requestFocus();
            } else if (notVerifiedLayout.getVisibility() == View.VISIBLE) {
                etImeiNumberField.setError("Invalid IMEI No.");
                etImeiNumberField.requestFocus();
            } else if (number.isEmpty()) {
                etNumberField.setError("Contact No Required");
                etNumberField.requestFocus();
            } else if (number.length() != 10) {
                etNumberField.setError("Invalid Contact No");
                etNumberField.requestFocus();
            }  else if (address.isEmpty()) {
                etAddressField.setError("Address Required");
                etAddressField.requestFocus();
            } else if (pin.isEmpty()) {
                etPinField.setError("PIN Required");
                etPinField.requestFocus();
            } else if (pin.length() != 6) {
                etPinField.setError("Invalid PIN Code");
                etPinField.requestFocus();
            } else if (modelPurchase.isEmpty()) {
                etModelField.setError("Model Required");
                etModelField.requestFocus();
            } else if (oldPhoneBrand.isEmpty()) {
                etOldPhoneField.setError("Old Phone Brand Required");
                etOldPhoneField.requestFocus();
            } else if (customer_image_uri == null) {
                showToast("Customer Image Required...");
            } else if (customer_invoice_image_uri == null) {
                showToast("Invoice Image Required...");
            } else if (locationPoints.isEmpty() || locationAddress.isEmpty()) {
                getCurrentLocation();
            } else {
                closeKeyboard();
                btnSubmit.setEnabled(false);

                // Show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");
                builder.setIcon(R.drawable.alert);

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dialog.cancel();
                    btnSubmit.setEnabled(true);

                    // Prepare the data to be submitted
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("number", number);
                    bundle.putString("imeiNo", imeiNo);
                    bundle.putString("address", address);
                    bundle.putString("pin", pin);
                    bundle.putString("modelPurchase", modelPurchase);
                    bundle.putString("oldPhoneBrand", oldPhoneBrand);
                    bundle.putString("locationPoints", locationPoints);
                    bundle.putString("locationAddress", locationAddress);
                    bundle.putString("customerImageUri", String.valueOf(customer_image_uri));
                    bundle.putString("customerInvoiceImageUri", String.valueOf(customer_invoice_image_uri));
                    bundle.putString("customerImageName", customer_image_name);
                    bundle.putString("customerInvoiceImageName", invoice_image_name);

                    // Execute AsyncTask to save data to the backend
                    new SaveDataToDb().execute(bundle);

                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    btnSubmit.setEnabled(true);
                    dialog.cancel();
                });

                builder.setOnCancelListener(dialogInterface -> btnSubmit.setEnabled(true));
                builder.show();
            }
        });



        //handle get current location button
        findViewById(R.id.buttonGetCurrentLocation).setOnClickListener(view -> getCurrentLocation());
    }



    private class SaveDataToDb extends AsyncTask<Bundle, Void, Boolean> {

        JSONObject jsonObject;
        RequestQueue requestQueue;
        Bitmap cImageBitmap = null, cInvoiceImageBitmap = null;

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
                String modelPurchase = bundle.getString("modelPurchase", "");
                String oldPhoneBrand = bundle.getString("oldPhoneBrand", "");
                String locationPoints = bundle.getString("locationPoints", "");
                String locationAddress = bundle.getString("locationAddress", "");
                String customerImageUri = bundle.getString("customerImageUri", "");
                String customerInvoiceImageUri = bundle.getString("customerInvoiceImageUri", "");
                String customerImageName = bundle.getString("customerImageName", "");
                String customerInvoiceImageName = bundle.getString("customerInvoiceImageName", "");

                // Convert customer image to Base64
                cImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(customerImageUri));
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                cImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                String customerImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                // Convert invoice image to Base64
                cInvoiceImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(customerInvoiceImageUri));
                ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
                cInvoiceImageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream1);
                String customerInvoiceImage = Base64.encodeToString(byteArrayOutputStream1.toByteArray(), Base64.DEFAULT);

                // Create the JSON object with the required data
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

                // Create a request to submit data to the backend
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLs.saveDataToDbUrl, jsonObject,
                        response -> {
                            Log.d("IMAGE RESPONSE", response.toString());
                            requestQueue.getCache().clear();
                            loadingDialog.hideDialog();
                            try {
                                String status = response.getString("status");
                                String message = response.getString("message");
                                if (status.equalsIgnoreCase("true")) {
                                    showToast(message);

                                    // Redirect to the next activity after successful submission
                                    startActivity(new Intent(RegisterActivity.this, GiftActivity.class)
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
                        }, error -> {
                    loadingDialog.hideDialog();
                    Log.e("IMAGE ERROR", error.toString());
                    showToast(error.toString());
                });

                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                // Initialize the request queue and add the request
                requestQueue = Volley.newRequestQueue(RegisterActivity.this);
                requestQueue.add(jsonObjectRequest);

            } catch (Exception e) {
                e.printStackTrace();
                loadingDialog.hideDialog();
            }
            return true;
        }
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
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void getCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    LocationServices.getFusedLocationProviderClient(RegisterActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(RegisterActivity.this)
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
                            resolvableApiException.startResolutionForResult(RegisterActivity.this, 2);
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

    private void fetchAddressFromLatLong(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (isGPSEnabled()) {
                    getCurrentLocation();
                } else {
                    turnOnGPS();
                }
            } else {
                showToast("Location permission is required...");
                finish();
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0) {
                try {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        showToast("Camera permissions are required");
                    }
                } catch (Exception e) {
                    Log.e("Error", e.toString());
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                showToast("Result Not Found");
            } else {
                if (TextUtils.isDigitsOnly(result.getContents()) && result.getContents().length() > 14 && result.getContents().length() < 17) {
                    showToast("QR Code " + result.getContents());
                    etImeiNumberField.setText(String.valueOf(result.getContents()));
                    etImeiNumberField.setError(null);
                    etImeiNumberField.clearFocus();
                } else {
                    showToast("Invalid IMEI Number...");
                    etImeiNumberField.setText("");
                }
                closeKeyboard();
            }
        }

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
                    if (value.equalsIgnoreCase("customer_image")) {
                        customer_image_uri = croppedImageUri;
                        customer_image_name = "customer_image_" + System.currentTimeMillis() + ".jpeg";
                        addCustomerImgBtn.setText(customer_image_name);  // Set the name of the cropped image on the button
                    }

                    if (value.equalsIgnoreCase("customer_invoice_image")) {
                        customer_invoice_image_uri = croppedImageUri;
                        invoice_image_name = "invoice_image_" + System.currentTimeMillis() + ".jpeg";
                        addInvoiceImgBtn.setText(invoice_image_name);   // Set the name of the cropped invoice image on the button
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

    private void verifyOtp(String name, String number, String otp) {
        loadingDialog.showDialog("Verifying...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.verifyOtpUrl, response -> {
            loadingDialog.hideDialog();
            Log.d("VERIFY OTP RESPONSE", response.trim());
            showToast(response);

            //show OTP dialog
            if (response.trim().equalsIgnoreCase("Verified")) {
                etImeiNumberField.setEnabled(false);
                qrScanImgBtn.setEnabled(false);

                etNumberField.setEnabled(false);

                btnSendOtp.setEnabled(false);
                btnSendOtp.setAlpha(0.6f);

                etOtpField.setEnabled(false);
                btnVerifyOtp.setEnabled(false);
                btnVerifyOtp.setAlpha(0.6f);

                countDownTimer.cancel();
                tvTimer.setVisibility(View.GONE);



                //enable submit button
                btnSubmit.setEnabled(true);
                btnSubmit.setAlpha(1f);
            }

        }, error -> {
            loadingDialog.hideDialog();
            Log.e("VERIFY OTP ERROR", error.toString());
            showToast(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
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

    private void sendOtp(String name, String number, String imeiNo) {
        loadingDialog.showDialog("Loading...");

        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.sendOtpUrl, response -> {
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
                    params.put("name", name);
                    params.put("number", number);
                    params.put("imeiNo", imeiNo);
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
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void initialization() {
        numberLayout = findViewById(R.id.numberLayout);
        otpLayout = findViewById(R.id.otpLayout);
        verifiedLayout = findViewById(R.id.verifiedLayout);
        notVerifiedLayout = findViewById(R.id.notVerifiedLayout);
        etNameField = findViewById(R.id.etNameField);
        etNumberField = findViewById(R.id.etNumberField);
        etOtpField = findViewById(R.id.etOtpField);
        etImeiNumberField = findViewById(R.id.etImeiNumberField);
        qrScanImgBtn = findViewById(R.id.qrScanImgBtn);
        addressInputLayout = findViewById(R.id.addressInputLayout);
        etAddressField = findViewById(R.id.etAddressField);
        pinInputLayout = findViewById(R.id.pinInputLayout);
        etPinField = findViewById(R.id.etPinField);
        modelInputLayout = findViewById(R.id.modelInputLayout);
        etModelField = findViewById(R.id.etModelField);
        oldPhoneInputLayout = findViewById(R.id.oldPhoneInputLayout);
        etOldPhoneField = findViewById(R.id.etOldPhoneField);
        addCustomerImgBtn = findViewById(R.id.addCustomerImgBtn);
        addInvoiceImgBtn = findViewById(R.id.addInvoiceImgBtn);

        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvLatLong = findViewById(R.id.tvLatLong);
        progressBar = findViewById(R.id.progressBar);
        tvAddress = findViewById(R.id.tvAddress);
        tvTimer = findViewById(R.id.tvTimer);

        loadingDialog = new LoadingDialog(this);

        //disable field
        btnSubmit.setEnabled(false);
        btnSubmit.setAlpha(0.6f);
        otpLayout.setVisibility(View.GONE);
        qrScan = new IntentIntegrator(RegisterActivity.this);
        resultReceiver = new AddressResultReceiver(new Handler());
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    }
}