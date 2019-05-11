package ec.com.cooprogreso;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
/**
 * @author aquingaluisa
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        validarPermisosUbivacion();
    }

    /**
     * Metodo para  ingresar a la app
     * @param view
     */
    public void ingresar(View view){
        String txtUsuario = ((EditText) findViewById(R.id.txtUsuario)).getText().toString();
        String txtPassword = ((EditText) findViewById(R.id.txtPassword)).getText().toString();
        if(txtUsuario.equals("admin") && txtPassword.equals("admin")){
            startActivity(new Intent(MainActivity.this,Menu.class));
        }else{
            Toast.makeText(getApplicationContext(),"Usuario Incorrecto",Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Valida si existen permisos de ubicacion
     */
    private void validarPermisosUbivacion(){
        //comprobar si extien los permisos
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {



            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

        }

    }
}
