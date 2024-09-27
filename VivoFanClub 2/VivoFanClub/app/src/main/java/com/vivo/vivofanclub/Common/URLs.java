package com.vivo.vivofanclub.Common;

public class URLs {

    public static final String APP_BASE_URL = "https://3sprotect.com/fanclub_api/";
    //Register Activity
    public static final String sendOtpUrl = APP_BASE_URL + "sendOtp.php";
    public static final String sendClaimOtpUrl = APP_BASE_URL + "claim_otp.php";
    public static final String verifyOtpUrl = APP_BASE_URL + "verifyOtp.php";
    public static final String verifyClaimOtp="https://3sprotect.com/fanclub_api/verify_claim_otp.php";
    public static final String saveDataToDbUrl = APP_BASE_URL + "saveDataToDb.php";
    public static final String checkValidImeiUrl = APP_BASE_URL + "checkValidImei.php";
    public static final String saveData = APP_BASE_URL + "testData ,so modify the API accordingly" +
            ".php";
    public static final String claim_otp = APP_BASE_URL + "get_card_details.php";

    //Main Activity
    public static final String getAllGiftsUrl = APP_BASE_URL + "getAllGifts.php";
    public static final String getAllWinnersUrl = APP_BASE_URL + "getAllWinners.php";
    public static final String checkGiftStatusUrl = APP_BASE_URL + "checkGiftStatus.php";

    //Gift Activity
    public static final String getGiftDetailUrl = APP_BASE_URL + "getGiftDetail.php";
    public static final String sendGiftMessageUrl = APP_BASE_URL + "sendGiftMessage.php";

}

