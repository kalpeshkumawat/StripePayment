package com.app.stripepayment.stripe;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.SourceParams;
import com.stripe.android.model.Token;
import com.stripe.model.Card;
import com.stripe.model.ExternalAccount;
import com.stripe.model.ExternalAccountCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StripeUtils {



    private static final String SECRET_KEY = "sk_test_xxxx";  // Replace by your key
    private static final String PUBLISH_KEY = "pk_test_xxxx"; // Replace by your key


    private static final String URL_CUSTOMER = "https://api.stripe.com/v1/customers";
    private static StripeUtils stripeUtil;
    private static Context context;

    private AsyncTask createCustomerAsync;
    private AsyncTask generateTokenAsync;
    private AsyncTask addCardAsync;
    private GetCardsAsync getCardsAsync;

    public StripeUtils(Context context) {
        this.context = context;
        com.stripe.Stripe.apiKey = SECRET_KEY;
        new Stripe(context, PUBLISH_KEY);
    }

    public static StripeUtils init(Context context) {
        stripeUtil = new StripeUtils(context);
        return stripeUtil;
    }

    public static StripeUtils getInstance() {
        if (stripeUtil == null)
            Log.e("Stripe", "StripeUtil not initialized, call StripeUtil.init() in Application");
        return stripeUtil;
    }

    public AsyncTask generateToken(final com.stripe.android.model.Card card, final String customerId, final TokenCallback tokenCallback) {
        if (generateTokenAsync != null && !generateTokenAsync.isCancelled()) {
            try {
                generateTokenAsync.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        generateTokenAsync = new GenerateTokenAsync(card, customerId, tokenCallback);
        generateTokenAsync.execute();
        return generateTokenAsync;
    }

    private class GenerateTokenAsync extends AsyncTask {


        private TokenCallback tokenCallback;
        private Token tokenObj;
        private com.stripe.android.model.Card card;
        private String customerId;


        //final com.stripe.android.model.Card card

        private GenerateTokenAsync(final com.stripe.android.model.Card card, final String customerId, final TokenCallback tokenCallback) {


            this.card = card;
            this.customerId = customerId;


            this.tokenCallback = tokenCallback;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {

                SourceParams cardSourceParams = SourceParams.createCardParams(card);



                new Stripe(context, PUBLISH_KEY).createToken(card, new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(@NonNull Token token) {
                        if (token == null) {
                            Message message = new Message();
                            message.what = 0;
                            message.obj = new Exception("Unknown error");
                            handler.sendMessage(message);
                        } else {
                            tokenObj = token;
                            handler.sendEmptyMessage(1);
                        }
                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        Message message = new Message();
                        message.what = 0;
                        message.obj = new Exception(e);
                        handler.sendMessage(message);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = 0;
                message.obj = new Exception(e);
                handler.sendMessage(message);
            }
            return null;
        }

        private Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (tokenCallback != null)
                        tokenCallback.onTokenSuccess(tokenObj);
                } else {
                    if (tokenCallback != null)
                        tokenCallback.onTokenError((Exception) msg.obj);
                }
            }
        };
    }

    public AsyncTask createCustomer(Map<String, Object> params, CustomerCallback customerCallback) {
        if (createCustomerAsync != null && !createCustomerAsync.isCancelled()) {
            try {
                createCustomerAsync.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        createCustomerAsync = new CreateCustomerAsync(params, customerCallback);
        createCustomerAsync.execute();
        return createCustomerAsync;
    }

    private class CreateCustomerAsync extends AsyncTask {
        private Map<String, Object> params;
        private CustomerCallback customerCallback;
        private com.stripe.model.Customer customer;

        private CreateCustomerAsync(Map<String, Object> params, CustomerCallback customerCallback) {
            this.params = params;
            this.customerCallback = customerCallback;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                customer = com.stripe.model.Customer.create(params);
                if (customer == null) {
                    Message message = new Message();
                    message.what = 0;
                    message.obj = new Exception("Unknown error");
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = 0;
                message.obj = e;
                handler.sendMessage(message);
            }
            return null;
        }

        private Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (customerCallback != null)
                        customerCallback.onCustomerCreated(customer);
                } else {
                    if (customerCallback != null)
                        customerCallback.onCreateCustomerError((Exception) msg.obj);
                }
            }
        };
    }

    public AsyncTask addCard(String customerId, String tokenId, CardCallback cardCallback) {
        if (addCardAsync != null && !addCardAsync.isCancelled()) {
            try {
                addCardAsync.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        addCardAsync = new AddCardAsync(customerId, tokenId, cardCallback);
        addCardAsync.execute();
        return addCardAsync;
    }

    private class AddCardAsync extends AsyncTask {

        private String customerId;
        private String tokenId;
        private CardCallback cardCallback;
        private Card card;

        private AddCardAsync(String customerId, String tokenId, CardCallback cardCallback) {
            this.customerId = customerId;
            this.tokenId = tokenId;
            this.cardCallback = cardCallback;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                com.stripe.model.Customer customer = com.stripe.model.Customer.retrieve(customerId);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("source", tokenId);

                Log.d("check_stripe_token_id", tokenId);

                card = customer.createCard(params);
                if (card == null) {
                    Message message = new Message();
                    message.what = 0;
                    message.obj = new Exception("Unknown error");
                    handler.sendMessage(message);
                } else {
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = 0;
                message.obj = e;
                handler.sendMessage(message);
            }
            return null;
        }

        private Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (cardCallback != null)
                        cardCallback.onCardCreated(card);
                } else {
                    if (cardCallback != null)
                        cardCallback.onCreateCardError((Exception) msg.obj);
                }
            }
        };
    }

    public AsyncTask getCards(String customerId, CardsCallback cardsCallback) {
        if (getCardsAsync != null && !getCardsAsync.isCancelled()) {
            try {
                getCardsAsync.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getCardsAsync = new GetCardsAsync(customerId, cardsCallback);
        getCardsAsync.execute();
        return getCardsAsync;
    }


    private class GetCardsAsync extends AsyncTask {

        private String customerId;
        private CardsCallback cardsCallback;
        private List<Card> cardList;

        private GetCardsAsync(String customerId, CardsCallback cardsCallback) {
            this.customerId = customerId;
            this.cardsCallback = cardsCallback;

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Map<String, Object> cardParams = new HashMap<>();
                cardParams.put("limit", 3); // You can set limit of cards
                cardParams.put("object", "card");
                ExternalAccountCollection response = com.stripe.model.Customer.retrieve(customerId).getSources().list(cardParams);
                List<ExternalAccount> cardListData = response.getData();
                cardList = new ArrayList<>();
                for (ExternalAccount account : cardListData) {
                    Card card = new Gson().fromJson(account.toJson(), Card.class);
                    cardList.add(card);
                }
                handler.sendEmptyMessage(1);
            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = 0;
                message.obj = e;
                handler.sendMessage(message);
            }
            return null;
        }


        private Handler handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (cardsCallback != null)
                        cardsCallback.onCardsRetrieved(cardList);
                } else {
                    if (cardsCallback != null)
                        cardsCallback.onCardRetrievalError((Exception) msg.obj);
                }
            }
        };
    }

    public interface TokenCallback {
        void onTokenSuccess(Token token);

        void onTokenError(Exception e);
    }

    public interface CustomerCallback {
        void onCustomerCreated(com.stripe.model.Customer customer);

        void onCreateCustomerError(Exception e);
    }

    public interface CardCallback {
        void onCardCreated(Card card);

        void onCreateCardError(Exception e);
    }

    public interface CardsCallback {
        void onCardsRetrieved(List<Card> cards);

        void onCardRetrievalError(Exception e);
    }

}
