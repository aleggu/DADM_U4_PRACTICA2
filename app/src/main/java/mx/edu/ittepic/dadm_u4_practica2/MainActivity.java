package mx.edu.ittepic.dadm_u4_practica2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button propietarios, inmuebles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        propietarios=findViewById(R.id.boton_propietarios);
        inmuebles=findViewById(R.id.boton_inmuebles);

        propietarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrir_propietarios =new Intent(MainActivity.this, Main2Activity.class);
                startActivity(abrir_propietarios);

            }
        });
        inmuebles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrir_inmobiliario =new Intent(MainActivity.this, Main3Activity.class);
                startActivity(abrir_inmobiliario);

            }
        });
    }
}
