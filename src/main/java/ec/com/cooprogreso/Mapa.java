package ec.com.cooprogreso;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author aquingaluisa
 */

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    int notificationID = 1;
    private GoogleMap mMap;
    private TextView tvDireccion;
    private TextView tvLocalizacion;
    private Button guardarMarcas;
    private double longitud;
    private double latitud;
    private Map<String, Marker> markerMap;
    private LatLng localizacionActual;
    private ArrayList<LatLng> latLngLocalesCol;
    BaseDatos basedeDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        tvDireccion = findViewById(R.id.tvDireccion);
        tvLocalizacion = findViewById(R.id.tvLocalizacion);
        guardarMarcas = findViewById(R.id.btnGuardarMarcas);
        markerMap = new HashMap<>();
        latLngLocalesCol = new ArrayList<>();
        basedeDatos = new BaseDatos(this, "DEMODB", null, 1);



    }


    /**
     * Evento al cargar el Mapa
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        obtenerLocalizacion();


        LatLng latitudLongActual = new LatLng(latitud, longitud);

        //cargar de la base de datos los locales   en el mapa
        cargarMarcasDb();


        float zoomLevel = 16;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latitudLongActual, zoomLevel));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Marker marca = mMap.addMarker(new MarkerOptions()
                        .icon(bitmapDescriptorFromVector(Mapa.this, R.drawable.cooprogreso_icon))
                        .anchor(0.0f, 1.0f)
                        .position(latLng));
                latLngLocalesCol.add(latLng);

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "Has pulsado una marca", Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void obtenerLocalizacion() {
        //aqui se va a crear el que escucha la ubicacion

        //Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) Mapa.this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            //cambio la localizacion
            public void onLocationChanged(Location location) {
                //marcar la localizacion actual
                Marker markerAnterior = (Marker) markerMap.get("P");
                if (markerAnterior != null) {
                    markerAnterior.remove();

                }
                Marker markerActual = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Estas Aqui"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                markerMap.put("P", markerActual);

                localizacionActual = new LatLng(location.getLatitude(), location.getLongitude());

                tvLocalizacion.setText("" + location.getLatitude() + " " + location.getLongitude());
                tvDireccion.setText(Mapa.this.cargarDireccion(location));
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                //calcula la distancias entre los puntos
                validarLocalesCercanos();

            }

            //cambio de status
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            //proveedor habilitado
            public void onProviderEnabled(String provider) {
            }

            //proveedor dsabled
            public void onProviderDisabled(String provider) {
            }
        };

        int permissionCheck = ContextCompat.checkSelfPermission(Mapa.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    /**
     * Metodo para obtener la  direcion
     *
     * @param loc
     */
    public String cargarDireccion(Location loc) {
        String dirrecionAproximada = "";
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {//si la lista no esta vacion si tiene la localizacion
                    Address DirCalle = list.get(0);
                    dirrecionAproximada = DirCalle.getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dirrecionAproximada;
    }

    /**
     * Metodo para modificar el tamo de la imagen
     *
     * @param context
     * @param vectorDrawableResourceId
     * @return
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.cooprogreso_icon);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(40, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void guardarMarcasDb(View view) {

        basedeDatos.eliminarDatosMarcas();
        for (LatLng latLng : latLngLocalesCol) {
            basedeDatos.insertarMarca(latLng.latitude, latLng.longitude);
        }
        Toast.makeText(this, "Datos Almacenados", Toast.LENGTH_LONG).show();


    }

    public void eliminarMarcas(View view) {
        basedeDatos.eliminarDatosMarcas();
        Toast.makeText(this, "Marcas Eliminadas", Toast.LENGTH_LONG).show();
        startActivity(new Intent(Mapa.this, Mapa.class));
        finish();
    }

    /**
     * Metodo para cargar Marcas desde la base
     */
    private void cargarMarcasDb() {
        latLngLocalesCol = basedeDatos.cargarMarcas();
        for (LatLng localiza : latLngLocalesCol) {
            mMap.addMarker(new MarkerOptions()
                    .icon(bitmapDescriptorFromVector(Mapa.this, R.drawable.cooprogreso_icon))
                    .anchor(0.0f, 1.0f)
                    .position(localiza));
        }
    }

    /**
     * Metodo que calcula la distancia entre dos puntos
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double carcularDistanciaCoordenadas(double lat1, double lng1, double lat2, double lng2) {
        //double radioTierra = 3958.75;//en millas
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        //kilometros
        double distancia = radioTierra * va2;

        distancia=(distancia*1000)/1;
        return distancia;
    }

    /**
     * Valida los locales cercanos a 300 metros
     */
    public void validarLocalesCercanos() {
        String mensaje="";
        for (LatLng latLng : latLngLocalesCol) {
            double distancia = carcularDistanciaCoordenadas(localizacionActual.latitude, localizacionActual.longitude, latLng.latitude, latLng.longitude);
            Log.d("distancia:", distancia + "");
            if(distancia>300){
                mostrarNotificacion("Un local Cooprogreso esta a "+distancia+" metros.");
                mensaje="Un local Cooprogreso esta a "+distancia+" metros.\n" +mensaje;
            }else{
                mensaje="Marcas y localizacion actualizada";
            }


        }
        Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
        toast.show();
    }


    /**
     * Metodo no funcional
     * @param mensaje
     */
    protected void mostrarNotificacion(String mensaje){
        Intent i = new Intent(this, Mapa.class);
        i.putExtra("notificationID", notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        CharSequence ticker ="Nueva entrada en SekthDroid";
        CharSequence contentTitle = "SekthDroid";
        CharSequence contentText = "Visita ahora SekthDroid!";
        Notification noti = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setTicker(ticker)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_focused, ticker, pendingIntent)
                .setVibrate(new long[] {100, 250, 100, 500})
                .build();
        nm.notify(notificationID, noti);
    }


}
