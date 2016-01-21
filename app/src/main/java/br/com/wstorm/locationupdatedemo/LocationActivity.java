package br.com.wstorm.locationupdatedemo;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextView txtMessage;
    private TextView txtLatitude;
    private TextView txtLongitude;
    private TextView txtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(10000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        }

        setContentView(R.layout.activity_location);

        txtMessage = (TextView)findViewById(R.id.txt_message);
        txtLatitude = (TextView)findViewById(R.id.txt_latitude);
        txtLongitude = (TextView)findViewById(R.id.txt_longitude);
        txtAddress = (TextView)findViewById(R.id.txt_address);

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, new LocationListener() {

                        @Override
                        public void onLocationChanged(Location location) {

                            try {


                                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),
                                        location.getLongitude(), 1);

                                updateValues(location, addresses.get(0));

                            } catch (IOException e) {

                                Log.e("LocationActivity", "Error getting address");

                            }

                        }

                    });

            if (mLastLocation != null) {
                updateValues(mLastLocation, null);
            }

        } catch (SecurityException exception) {

            Log.e("LocationActivity", "User doesn't allow to get location");

        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateValues(Location location, Address address) {

        txtMessage.setVisibility(View.INVISIBLE);

        txtLatitude.setText(String.format("Latitude: %f", location.getLatitude()));
        txtLongitude.setText(String.format("Longitude: %f", location.getLongitude()));

        if (address != null) {

            List<String> strAddressList = new ArrayList();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                strAddressList.add(address.getAddressLine(i).trim());
            }

            txtAddress.setText(TextUtils.join(" - ", strAddressList));

        }

    }

}
