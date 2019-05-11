package ec.com.cooprogreso;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
/**
 * @author aquingaluisa
 */

public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setupActionBar();
    }

    public void navegarMapa(View view){
        startActivity(new Intent(Menu.this,Mapa.class));
    }

    public void navegarMapa2(View view){
        startActivity(new Intent(Menu.this,Mapa2.class));
    }

    public void setupActionBar(){
        ActionBar actionBar =   getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(Boolean.TRUE);
            actionBar.setTitle("MENU");
        }
    }
}
