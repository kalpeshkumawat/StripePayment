package com.app.stripepayment.stripe;


public class CardTypeUtil {

    public static int getCardIcon(String cardType) {
        if (cardType == null || "Unknown".equalsIgnoreCase(cardType)) {
            return com.stripe.android.R.drawable.ic_unknown;
        } else if ("Visa".equalsIgnoreCase(cardType)) {
            return com.stripe.android.R.drawable.ic_visa;
        } else if ("American Express".equalsIgnoreCase(cardType)) {
            return com.stripe.android.R.drawable.ic_amex;
        } else if ("MasterCard".equalsIgnoreCase(cardType)) {
            return com.stripe.android.R.drawable.ic_mastercard;
        } else if ("Discover".equalsIgnoreCase(cardType)) {
            return com.stripe.android.R.drawable.ic_discover;
        } else if ("JCB".equalsIgnoreCase(cardType)) {
            return com.stripe.android.R.drawable.ic_jcb;
        } else if ("Diners Club".equalsIgnoreCase(cardType)) {
            return com.stripe.android.R.drawable.ic_diners;
        } else {
            return com.stripe.android.R.drawable.ic_unknown;
        }
    }
}
