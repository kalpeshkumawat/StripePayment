package com.app.stripepayment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.stripepayment.adapter.CardListAdapter;
import com.app.stripepayment.stripe.StripeUtils;
import com.app.stripepayment.stripe.Utils;
import com.app.stripepayment.utils.SharedPref;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText ed_card_number, ed_card_holder_name, ed_card_exp_month, ed_card_exp_year, ed_card_cvv, ed_email_id;
    private CheckBox check_box;
    private Button btn_submit, btn_get_cards;

    private RecyclerView recycler_view;

    private AsyncTask createCustomerTask;
    private AsyncTask addCardTask;
    private AsyncTask generateTokenTask;
    private AsyncTask getCardsTask;




    private static final String ERROR_MSG_CARD_NUMBER = "Enter valid card number";
    private static final String ERROR_MSG_EXP_MONTH = "Enter valid exp month";
    private static final String ERROR_MSG_EXP_YEAR = "Enter valid exp year";
    private static final String ERROR_MSG_CVV = "Enter valid cvv";
    private static final String ERROR_MSG_CARD_VALIDATION = "Please check card details";

    private List<String>  mCardList = new ArrayList<>();
    private CardListAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StripeUtils.init(this);


        initUtils();
        initRecyclerView();


    }

    private void initRecyclerView() {

        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mCardAdapter = new CardListAdapter(this,mCardList);
        recycler_view.setAdapter(mCardAdapter);
        recycler_view.setNestedScrollingEnabled(false);
    }

    private void initUtils() {


        ed_email_id = findViewById(R.id.ed_email_id);
        ed_card_number = findViewById(R.id.ed_card_number);
        ed_card_holder_name = findViewById(R.id.ed_card_holder_name);
        ed_card_exp_month = findViewById(R.id.ed_card_exp_month);
        ed_card_cvv = findViewById(R.id.ed_card_cvv);
        ed_card_exp_year = findViewById(R.id.ed_card_exp_year);

        check_box = findViewById(R.id.check_box);
        btn_submit = findViewById(R.id.btn_submit);
        btn_get_cards = findViewById(R.id.btn_get_cards);

        btn_submit.setOnClickListener(this);
        btn_get_cards.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {


        if (view == btn_submit) {

            final String ed_email_id_star = getString(ed_email_id);

            String ed_card_number_str = getString(ed_card_number);
            String ed_card_exp_month_str = getString(ed_card_exp_month);
            String ed_card_exp_year_str = getString(ed_card_exp_year);
            String ed_card_cvv_str = getString(ed_card_cvv);


            final com.stripe.android.model.Card card = com.stripe.android.model.Card.create(
                    ed_card_number_str,
                    Integer.parseInt(ed_card_exp_month_str),
                    Integer.parseInt(ed_card_exp_year_str),
                    ed_card_cvv_str
            );


            if (TextUtils.isEmpty(ed_email_id_star)) {


                showToast("Please enter valid email ID");
                return;
            }


            if (!card.validateNumber()) {


                showToast(ERROR_MSG_CARD_NUMBER);
                return;

            }

            if (!card.validateExpMonth()) {

                showToast(ERROR_MSG_EXP_MONTH);
                return;
            }

            if (!card.validateExpiryDate()) {

                showToast(ERROR_MSG_EXP_YEAR);
                return;
            }

            if (!card.validateCVC()) {

                showToast(ERROR_MSG_CVV);
                return;
            }


            if (!card.validateCard()) {

                showToast(ERROR_MSG_CARD_VALIDATION);
                return;
            }


            if (check_box.isChecked()) {




                // Save card on stripe


                //Todo => Step 1. Firstly we check particular email Id have a Stripe customer Id



                String stripeCustomerId = SharedPref.getInstance().readString(ed_email_id_star);


                if(TextUtils.isEmpty(stripeCustomerId)){


                    //Todo => Step 2. No, Not have Customer ID, Then we create a Stripe customer Id using user Email Id and send to server, In next time we check this email contain Stripe Customer Id


                    Map<String, Object> params = new HashMap<>();
                    params.put("email", ed_email_id_star);



                    Utils.showProgress(this);

                    createCustomerTask = StripeUtils.getInstance().createCustomer(params, new StripeUtils.CustomerCallback() {
                        @Override
                        public void onCustomerCreated(final com.stripe.model.Customer customer) {


                            Utils.hideProgress();

                            SharedPref.getInstance().writeString(ed_email_id_star,customer.getId());

                            addCard(customer.getId(), card);


                        }

                        @Override
                        public void onCreateCustomerError(Exception e) {
                            Utils.hideProgress();
                            e.printStackTrace();

                            Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                        }
                    });
                }
                else {

                    //Todo => Step 3. Yes, We have Customer ID, Then we can save card directly


                    addCard(stripeCustomerId, card);
                }









            } else {


                Utils.showProgress(this);


                String customerId = SharedPref.getInstance().readString(ed_email_id_star);


                generateTokenTask = StripeUtils.getInstance().generateToken(card,
                        customerId,
                        new StripeUtils.TokenCallback() {
                            @Override
                            public void onTokenSuccess(Token token) {

                                check_box.setChecked(false);

                                ed_card_number.setText("");
                                ed_card_exp_month.setText("");
                                ed_card_exp_year.setText("");
                                ed_card_cvv.setText("");
                                ed_email_id.setText("");
                                ed_card_holder_name.setText("");


                                Utils.hideProgress();


                                showToast("Token - " + token.getId());
                            }

                            @Override
                            public void onTokenError(Exception e) {

                                Utils.hideProgress();

                                showToast(e.getMessage());


                            }
                        });


            }


        } else if (view == btn_get_cards) {


            String ed_email_id_star = getString(ed_email_id);




            if (TextUtils.isEmpty(ed_email_id_star)) {


                showToast("Please enter valid email ID");
                return;
            }


            Utils.showProgress(this);

            String customerId = SharedPref.getInstance().readString(ed_email_id_star);

            getCardsTask = StripeUtils.getInstance().getCards(customerId, new StripeUtils.CardsCallback() {
                @Override
                public void onCardsRetrieved(List<com.stripe.model.Card> cards) {

                    Utils.hideProgress();

                    if (cards != null && cards.size()>0) {

                        handleCardList(cards);
                    }
                    else {

                        showToast("No saved card found!");
                    }




                }

                @Override
                public void onCardRetrievalError(Exception e) {

                    e.printStackTrace();

                    Utils.hideProgress();

                    showToast(e.getMessage());
                }
            });
        }


    }

    private void handleCardList(List<com.stripe.model.Card> cards) {


        mCardList.clear();

        for (com.stripe.model.Card card : cards) {


            mCardList.add(card.getLast4());

        }


        mCardAdapter.notifyDataSetChanged();

    }

    private void addCard(final String customerId, Card card) {



        Utils.showProgress(this);

        generateTokenTask = StripeUtils.getInstance().generateToken(card, customerId, new StripeUtils.TokenCallback() {
            @Override
            public void onTokenSuccess(Token token) {

                addCardTask = StripeUtils.getInstance().addCard(customerId, token.getId(), new StripeUtils.CardCallback() {
                    @Override
                    public void onCardCreated(com.stripe.model.Card card) {

                        Utils.hideProgress();
                        check_box.setChecked(false);
                        ed_card_number.setText("");
                        ed_card_exp_month.setText("");
                        ed_card_exp_year.setText("");
                        ed_card_cvv.setText("");
                        ed_email_id.setText("");
                        ed_card_holder_name.setText("");

                        showToast("Card Added Successfully!");

                    }

                    @Override
                    public void onCreateCardError(Exception e) {
                        Utils.hideProgress();
                        e.printStackTrace();
                        showToast(e.getMessage());

                    }
                });
            }


            @Override
            public void onTokenError(Exception e) {
                Utils.hideProgress();
                e.printStackTrace();

                showToast(e.getMessage());

            }
        });
    }


    private void showToast(String msg) {

        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
    }

    private String getString(EditText editText) {


        return editText.getText().toString().trim();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (createCustomerTask != null && !createCustomerTask.isCancelled()) {
            try {
                createCustomerTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (addCardTask != null && !addCardTask.isCancelled()) {
            try {
                addCardTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (generateTokenTask != null && !generateTokenTask.isCancelled()) {
            try {
                generateTokenTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getCardsTask != null && !getCardsTask.isCancelled()) {
            try {
                getCardsTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
