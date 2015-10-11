package com.example.navigate;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileOutputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author yashika.
 */
public class MapFragment extends Fragment {

    private static final LatLng COPPER = new LatLng(28.5865745, 77.3124283);
    final ArrayList<LatLng> coordinates = new ArrayList<LatLng>();
    public double longitude;
    public double latitude;
    GoogleMap googleMap;
    LocationManager locationManager;
    Marker now;
    String path = Environment.getExternalStorageDirectory() + "/MapScreenshot" + System.currentTimeMillis() + ".png";
    @Bind(R.id.send)
    Button sendButton;
    private boolean drawTrack = true;
    private Polyline route = null;
    private PolylineOptions routeOpts = null;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View rootView = null;
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        ButterKnife.bind(this, rootView);

        googleMap = ((com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // logging image path
        System.out.println("Image Path :: " + path);

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged (Location location) {

                if (now != null) {
                    now.remove();
                }
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                LatLng latLng = new LatLng(latitude, longitude);

                coordinates.add(latLng);

                String text = "My current location is " + "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
                Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                googleMap.setTrafficEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                now = googleMap.addMarker(new MarkerOptions().position(latLng));
                addLines();
            }

            @Override
            public void onStatusChanged (String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled (String provider) {
                Toast.makeText(getActivity().getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled (String provider) {
                Toast.makeText(getActivity().getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 5, locationListener);

        return rootView;
    }

    @OnClick(R.id.send)
    public void sendButtonListener () {
        try {
            captureScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adding lines
     */
    private void addLines () {

        googleMap.addPolyline((new PolylineOptions()).addAll(coordinates).width(5).color(Color.BLUE).geodesic(true));
        CameraPosition mCameraPosition = CameraPosition.builder()
                .target(COPPER)
                .zoom(14)
                .bearing(90)
                .build();
        // Animate the change in camera view over 2 seconds
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition), 2000, null);
    }

    /**
     * capture map screen
     */
    public void captureScreen () {

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady (Bitmap snapshot) {

                try {
                    FileOutputStream out = new FileOutputStream(
                            path);
                    snapshot.compress(Bitmap.CompressFormat.PNG, 90, out);

                    Toast.makeText(getActivity(), "Path Captured as Image, saved in" + path, Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    Log.e("Image not saved", "");
                    e.printStackTrace();
                }
            }


        };

        googleMap.snapshot(callback);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_share_text).setPositiveButton(R.string.share_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int id) {
                //user shared
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int id) {
                //user cancelled
            }
        }).show();
    }
}