package com.dallinc.masstext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dallinc.masstext.helpers.Constants;
import com.dallinc.masstext.payment.IabHelper;
import com.dallinc.masstext.payment.IabResult;
import com.dallinc.masstext.payment.Inventory;
import com.dallinc.masstext.payment.Purchase;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.List;


public class Donate extends ActionBarActivity {
    IabHelper mHelper;
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener;
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;
    String base64EncodedPublicKey;
    String donate1Price;
    String donate2Price;
    String donate5Price;
    String donate10Price;
    String donate15Price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        ButtonRectangle donateButton = (ButtonRectangle) findViewById(R.id.buttonDonate);
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
            }
        });

        mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory)
            {
                if (result.isFailure()) {
                    // handle error
//                    Log.e("InventoryListener.Error", "Failed to set up listener");
                    Toast.makeText(getBaseContext(), "Failed to find items available for purchase. Please restart the app and try again", Toast.LENGTH_LONG).show();
                } else {
                    donate1Price = inventory.getSkuDetails(Constants.DONATE_1_SKU).getPrice();
                    donate2Price = inventory.getSkuDetails(Constants.DONATE_2_SKU).getPrice();
                    donate5Price = inventory.getSkuDetails(Constants.DONATE_5_SKU).getPrice();
                    donate10Price = inventory.getSkuDetails(Constants.DONATE_10_SKU).getPrice();
                    donate15Price = inventory.getSkuDetails(Constants.DONATE_15_SKU).getPrice();
                }
            }
        };

        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase)
            {
                if (result.isFailure()) {
                    Toast.makeText(getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (purchase.getSku()) {
                    case Constants.DONATE_1_SKU:
                        Toast.makeText(getBaseContext(), "Thank you for donating " + donate1Price + "!", Toast.LENGTH_SHORT).show();
                        return;
                    case Constants.DONATE_2_SKU:
                        Toast.makeText(getBaseContext(), "Thank you for donating " + donate2Price + "!", Toast.LENGTH_SHORT).show();
                        return;
                    case Constants.DONATE_5_SKU:
                        Toast.makeText(getBaseContext(), "Thank you for donating " + donate5Price + "!", Toast.LENGTH_SHORT).show();
                        return;
                    case Constants.DONATE_10_SKU:
                        Toast.makeText(getBaseContext(), "Thank you for donating " + donate10Price + "!", Toast.LENGTH_SHORT).show();
                        return;
                    case Constants.DONATE_15_SKU:
                        Toast.makeText(getBaseContext(), "Thank you for donating " + donate15Price + "!", Toast.LENGTH_SHORT).show();
                        return;
                }
            }
        };

        // compute your public key and store it in base64EncodedPublicKey
        base64EncodedPublicKey = Constants.kagi();
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
//                    Log.d("Payment Error", "Problem setting up In-app Billing: " + result);
                    Toast.makeText(getBaseContext(), "Problem setting up In-app Billing. Please restart the app and try again", Toast.LENGTH_LONG).show();
                }
                // Hooray, IAB is fully set up!
                List additionalSkuList = new ArrayList();
                additionalSkuList.add(Constants.DONATE_1_SKU);
                additionalSkuList.add(Constants.DONATE_2_SKU);
                additionalSkuList.add(Constants.DONATE_5_SKU);
                additionalSkuList.add(Constants.DONATE_10_SKU);
                additionalSkuList.add(Constants.DONATE_15_SKU);
                mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
            }
        });
    }

    public void showList() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] prices = new String[]{donate1Price, donate2Price, donate5Price, donate10Price, donate15Price};
        builder.setTitle("Select Amount");
        builder.setItems(prices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    purchase(which);
                } catch (IllegalStateException e) {
                    Toast.makeText(getBaseContext(), "Unable to purchase. A previous transaction may still be in process. Please restart app and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void purchase(int index) {
        String sku = Constants.DONATE_1_SKU;
        switch (index) {
            case 0:
                sku = Constants.DONATE_1_SKU;
                break;
            case 1:
                sku = Constants.DONATE_2_SKU;
                break;
            case 2:
                sku = Constants.DONATE_5_SKU;
                break;
            case 3:
                sku = Constants.DONATE_10_SKU;
                break;
            case 4:
                sku = Constants.DONATE_15_SKU;
                break;
        }
        mHelper.launchPurchaseFlow(this, sku, 10001, mPurchaseFinishedListener, "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
//            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
