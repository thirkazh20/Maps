package com.thirafikaz.mapsintermediate;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.thirafikaz.mapsintermediate.Network.ConfigRetrofit;
import com.thirafikaz.mapsintermediate.helper.DirectionMapsV2;
import com.thirafikaz.mapsintermediate.helper.GPStrack;
import com.thirafikaz.mapsintermediate.model.ResponseRoute;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final int REQAWAL = 10;
    private final int REQAKHIR = 11;

    @BindView(R.id.edtawal)
    EditText edtawal;
    @BindView(R.id.edtakhir)
    EditText edtakhir;
    @BindView(R.id.textjarak)
    TextView textjarak;
    @BindView(R.id.textwaktu)
    TextView textwaktu;
    @BindView(R.id.textharga)
    TextView textharga;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.spinmode)
    Spinner spinmode;
    @BindView(R.id.relativemap)
    RelativeLayout relativemap;
    @BindView(R.id.frame1)
    FrameLayout frame1;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private static final int REQUEST_LOCATION = 1;
    private String nama_lokasi;
    private LatLng lokasisaya;
    List<Place.Field> fieldsAwal = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    List<Place.Field> fieldsAkhir = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    private double latawal, lonawal, latakhir, lonakhir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        cekStatusGPS();
    }

    private void cekStatusGPS() {
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS Already Enabled", Toast.LENGTH_SHORT).show();
        }
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS Already Unenabled", Toast.LENGTH_SHORT).show();
        }
    }

    private void enabledLocation() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    }) .build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 100);
            locationRequest.setFastestInterval(5 * 100);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();
                    switch (status.getStatusCode()){
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(MapsActivity.this, REQUEST_LOCATION);
                            }catch (IntentSender.SendIntentException sender){

                            }
                    }
                }
            });
         }
        }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        }, 100
                );
            }
            return;
        } else {
            mMap = googleMap;
            akseslokasiku();
        }
    }

    private void akseslokasiku() {
        GPStrack gpStrack = new GPStrack(this);
        if (gpStrack.canGetLocation() &&  mMap != null) {
            double lat = gpStrack.getLatitude();
            double lon = gpStrack.getLatitude();

            nama_lokasi = convertlocation(lat, lon);
            latawal = lat;
            lonawal = lon;
            lokasisaya = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(lokasisaya).title(nama_lokasi));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasisaya,16));
            mMap.getUiSettings().isCompassEnabled();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().isMyLocationButtonEnabled();
            edtawal.setText(nama_lokasi);
        }
    }

    private String convertlocation(double lat, double lon) {
        nama_lokasi = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(lat, lon, 1);
            if (list != null && list.size() > 0) {
                nama_lokasi = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            } else {
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nama_lokasi;
    }

    @OnClick({R.id.edtawal, R.id.edtakhir})
    public void onViewClicked(View view) {
            Intent mapIntent;
        switch (view.getId()) {
            case R.id.edtawal:
                mapIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fieldsAwal).build(this);
                startActivityForResult(mapIntent, REQAWAL);
                break;
            case R.id.edtakhir:
                mapIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldsAkhir).build(this);
                startActivityForResult(mapIntent, REQAKHIR);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQAWAL) {
            if (resultCode == RESULT_OK) {
                mMap.clear();
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng placeAwal = place.getLatLng();
                latawal = placeAwal.latitude;
                lonawal = placeAwal.longitude;
                mMap.addMarker(new MarkerOptions().position(placeAwal).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeAwal, 14));
                edtawal.setText(place.getAddress());
            }
        } else if (requestCode == REQAKHIR) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                LatLng placeAkhir = place.getLatLng();
                latakhir = placeAkhir.latitude;
                lonakhir = placeAkhir.longitude;
                mMap.addMarker(new MarkerOptions().position(placeAkhir).title(place.getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeAkhir, 14));
                edtakhir.setText(place.getAddress());

                actionRoute();
            }
        }
    }

    private void actionRoute() {
        String origin = latawal+","+lonawal;
        String destination = latakhir+","+lonakhir;
        String mode = "driving";

        ConfigRetrofit.service.requestRoute(origin, destination, mode, getString(R.string.google_maps_key)).enqueue(new Callback<ResponseRoute>() {
            @Override
            public void onResponse(Call<ResponseRoute> call, Response<ResponseRoute> response) {
                if (response.isSuccessful()) {
                    ResponseRoute json = response.body();
                    ArrayList<ResponseRoute.Object0> routeArray = json.getRoutes();
                    ResponseRoute.Object0 object0 = routeArray.get(0);

                    ResponseRoute.Object0.OverView overView = object0.getOverview_polyline();

                    String points = overView.getPoints();

                    String distance = object0.getLegs().get(0).getDistance().getText();
                    String duration = object0.getLegs().get(0).getDuration().getText();

                    new DirectionMapsV2(MapsActivity.this).gambarRoute(mMap, points);
                    textjarak.setText(distance);
                    textwaktu.setText(duration);

                } else {
                    Toast.makeText(MapsActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseRoute> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
