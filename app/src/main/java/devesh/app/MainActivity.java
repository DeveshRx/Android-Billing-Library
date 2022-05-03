package devesh.app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.List;

import devesh.app.databinding.ActivityMainBinding;
import devesh.app.gplay_billing.GooglePlayBilling;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;


    String Product_ID = "001";
    String TAG = "GBilling";
    GooglePlayBilling GPB;
    PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            Log.d(TAG, "onPurchasesUpdated: ");

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                if (!purchases.isEmpty()) {
                    //MyPurchases = purchases;
                    for (Purchase purchase : purchases) {
                        Log.d(TAG, "onPurchasesUpdated: ________________________");
                        Log.d(TAG, "onPurchasesUpdated: purchase.getOrderId() : " + purchase.toString());
                        Log.d(TAG, "onPurchasesUpdated: purchase.getOrderId() : " + purchase.getOrderId());
                        Log.d(TAG, "onPurchasesUpdated: ------------------------");
                    }
                }
                //for (Purchase purchase : purchases) { }

            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Log.d(TAG, "onPurchasesUpdated: ______________________");
                Log.d(TAG, "onPurchasesUpdated: ERROR USER_CANCELED : ");
                Log.d(TAG, "onPurchasesUpdated: ----------------------");
            } else {
                // Handle any other error codes.
                Log.d(TAG, "onPurchasesUpdated: ______________________");
                Log.d(TAG, "onPurchasesUpdated: ERROR");
                Log.d(TAG, "onPurchasesUpdated: ERROR " + billingResult);
                Log.d(TAG, "onPurchasesUpdated: ----------------------");
            }
        }

    };
    SkuDetailsResponseListener skuDetailsResponseListener = new SkuDetailsResponseListener() {
        @Override
        public void onSkuDetailsResponse(BillingResult billingResult,
                                         List<SkuDetails> skuDetailsList) {
            // Process the result.

            if (skuDetailsList != null) {
                if (!skuDetailsList.isEmpty()) {

                    GPB.SKUSubscriptionsList = skuDetailsList;

                    for (SkuDetails sku : skuDetailsList) {
                        Log.d(TAG, "onSkuDetailsResponse: " + sku.getTitle());
                    }
                } else {
                    Log.d(TAG, "onSkuDetailsResponse: EMPTY SKU");
                }
            }


        }
    };
    BillingClientStateListener billingClientStateListener = new BillingClientStateListener() {
        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
            Log.d(TAG, "onBillingSetupFinished: ");
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                // The BillingClient is ready. You can query purchases here.
                Log.d(TAG, "onBillingSetupFinished: READY");
                GPB.getSubscriptionList(skuDetailsResponseListener);
                FetchPurchases();

            }
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ERROR) {
                // The BillingClient is ready. You can query purchases here.
                Log.d(TAG, "onBillingSetupFinished: ERROR");


            }

        }

        @Override
        public void onBillingServiceDisconnected() {
            // Try to restart the connection on the next request to
            // Google Play by calling the startConnection() method.
            Log.d(TAG, "onBillingServiceDisconnected: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);


    }

    @Override
    protected void onStart() {
        super.onStart();
//        BillingInit();

        GPB = new GooglePlayBilling(this);

        GPB.Connect(billingClientStateListener, purchasesUpdatedListener);

        GPB.SubscriptionsList.add("my_subs1");
        GPB.SubscriptionsList.add("001");

        if (GPB.isGooglePlayServicesAvailable()) {
            Log.d(TAG, "onStart: GOOGLE PLAY AVAILABLE");


        } else {
            Log.d(TAG, "onStart: GOOGLE PLAY NOT AVAILABLE");
        }


    }

    public void BuyNow(View v) {

        try {
            GPB.Purchase(GPB.SKUSubscriptionsList.get(0), this);
        } catch (Exception e) {
            Log.e(TAG, "BuyNow: ERROR " + e);
        }

    }

    void FetchPurchases() {

        GPB.FetchSubsPurchases(new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list) {
if(list.isEmpty()){
    Log.d(TAG, "onQueryPurchasesResponse: EMPTY PURCHASE LIST");
}
                for (Purchase purchase:list) {
                    Log.d(TAG, "onQueryPurchasesResponse: ====================");
                    Log.d(TAG, "onQueryPurchasesResponse: "+purchase.toString());
                    Log.d(TAG, "onQueryPurchasesResponse: ____________________");
                }


            }
        });


        GPB.FetchSubsPurchaseHistory((billingResult, list) -> {
            for (PurchaseHistoryRecord purchase:list) {
                Log.d(TAG, "FetchSubsPurchaseHistory: ====================");
                Log.d(TAG, "FetchSubsPurchaseHistory: "+purchase.toString());
                Log.d(TAG, "FetchSubsPurchaseHistory: ____________________");
            }
        });

    }

}