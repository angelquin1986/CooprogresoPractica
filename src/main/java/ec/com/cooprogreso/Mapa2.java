package ec.com.cooprogreso;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class Mapa2 extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mmap;
    GoogleMap gmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa2);
        mmap = (MapView) findViewById(R.id.mapView);

        mmap.getMapAsync(this);

        //map.getUiSettings().setMyLocationButtonEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }

    /**
     * Lifecycle method for mapview
     */
    @Override
    public final void onDestroy() {
        mmap.onDestroy();
        super.onDestroy();
    }

    /**
     * Lifecycle method for mapview
     */
    @Override
    public final void onLowMemory() {
        mmap.onLowMemory();
        super.onLowMemory();
    }

    /**
     * Lifecycle method for mapview
     */
    @Override
    public final void onPause() {
        mmap.onPause();
        super.onPause();
    }

    /**
     * Lifecycle method for mapview
     */
    @Override
    protected void onResume() {
        super.onResume();
        mmap.onResume();
    }

}
