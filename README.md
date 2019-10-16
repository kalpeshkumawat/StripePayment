# Android - Stripe Utils

Utility Classes for easy to generate Stripe Token, Add card and retrieve cards.

## Demo

<img src="https://github.com/kalpeshkumawat/StripePayment/raw/master/screen_shot_1.png?raw=true" height="450" width="280" /> |
<img src="https://github.com/kalpeshkumawat/StripePayment/raw/master/screen_shot_2.png?raw=true" height="450" width="280" /> |
<img src="https://github.com/kalpeshkumawat/StripePayment/raw/master/screen_shot_3.png?raw=true" height="450" width="280" />

## Add the required permissions in AndroidManifest.xml

**For Networking:**

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

**Generate Token**
```
Email not mandatory to generate Token just fill card info and get Token.
```

**Save Card**
```
Here Every user must have an email, By using email Id we generate Stripe Customer Id and save Card Info on Stripe If there is already Stripe Customer Id exits then no need to generate Stripe Customer Id again.
```
**Retrieve Cards**
```
By using Stripe Customer Id we get all cards info.
```
