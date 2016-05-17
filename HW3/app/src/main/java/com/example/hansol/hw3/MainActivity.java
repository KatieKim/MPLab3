package com.example.hansol.hw3;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import android.webkit.JavascriptInterface;

public class MainActivity extends AppCompatActivity {
    WebView browser;
    Inputnumber locater = new Inputnumber(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //function call from html webview
        browser = (WebView) findViewById(R.id.webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(locater, "JStest");
        browser.loadUrl("file:///android_asset/my_local_webpage1.html");
        // browser.getSettings().setUseWideViewPort(true);
    }
}

//inner HTML
class Inputnumber {
    private String result = "";
    Context mContext;

    public Inputnumber(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public String returnNumber(String txt) {
        Log.i("TAG", txt);
        //save number and return to html
        result += txt;
        return result;
    }

    @JavascriptInterface
    public void printresult(String txt) {
        //make sure don't type # or * in the input number
        if(result.contains("#") || result.contains("*")){
            Toast.makeText(mContext, "Can't put # or *", Toast.LENGTH_SHORT).show();
        }
        else if (result.length() > 0 && txt.length() > 0) {
            sendSMS(result, txt);
        } else {
            Toast.makeText(mContext, "Empty", Toast.LENGTH_SHORT).show();
        }
    }
    //reset the number
    @JavascriptInterface
    public String resetMassage(){
        Log.i("TAG",result);
        result = "";
        return "(input number)";
    }
    //delete one charctor of string
    @JavascriptInterface
    public String removeoneString(){
        Log.i("TAG",result);
        result = result.substring(0,result.length()-1);

        return result;
    }
    //Function of sending message
    public void sendSMS(String smsNumber, String smsText) {
        PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent("SMS_SENT"), 0);


        //show toast about result message
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(mContext, "\n" + "Success", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:

                        Toast.makeText(mContext, "Generic failure cause", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:

                        Toast.makeText(mContext, "Failed because service is currently unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:

                        Toast.makeText(mContext, "Wireless connections OFF", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:

                        Toast.makeText(mContext, "Failed because no pdu provided", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_SENT"));
        SmsManager mSmsManager = SmsManager.getDefault();
        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, null);
    }

}

