package mx.edu.ittepic.dadm_u4_practica2;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class  Main3Activity extends AppCompatActivity {
    EditText identificador_i, domicilio_i, venta, renta, fecha;
    ImageButton crear_i, eliminar_i, modificar_i, buscar_i;
    Spinner dueno;
    BaseDatos base;
    String [] id_datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        identificador_i=findViewById(R.id.campo_id_i);
        domicilio_i=findViewById(R.id.campo_domicilio_i);
        venta=findViewById(R.id.campo_precioven);
        renta=findViewById(R.id.campo_precioren);
        fecha=findViewById(R.id.campo_fecha);

        crear_i=findViewById(R.id.crear_inm);
        eliminar_i=findViewById(R.id.borrar_inm);
        modificar_i=findViewById(R.id.editar_inm);
        buscar_i=findViewById(R.id.ver_inm);

        dueno=findViewById(R.id.dueno);
        try{
            //Asignarle memoria (new) y configuracion
            base = new BaseDatos(this, "inmobiliaria", null, 1);


            SQLiteDatabase bd = base.getReadableDatabase();

            String SQL = "SELECT IDP  FROM PROPIETARIO ORDER BY IDP";

            Cursor id_propietarios = bd.rawQuery(SQL, null);


            if (id_propietarios.moveToFirst()==false){
                Toast.makeText(this, "Agrega propietarios", Toast.LENGTH_LONG).show();
            }
            else
            {
                if(id_propietarios.getCount()>0){
                    id_datos = new String[id_propietarios.getCount()];
                    for(int i=0; i<id_propietarios.getCount(); i++){
                        id_datos[i]=id_propietarios.getString(0);
                        id_propietarios.moveToNext();
                    }
                }
            }

            ArrayAdapter adaptador = new ArrayAdapter (this, android.R.layout.simple_list_item_1, id_datos);
            dueno.setAdapter(adaptador);

        }catch (SQLiteException e){
            Toast.makeText(this, "NO HAY CONEXION", Toast.LENGTH_LONG).show();
        }

        crear_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });
        buscar_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });

        modificar_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!crear_i.isEnabled())
                {
                    invocarConfirmacionActualizar();
                }
                else {
                    pedirID(2);
                }
            }
        });
        eliminar_i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(3);
            }
        });


    }

    private void invocarConfirmacionActualizar() {
        AlertDialog.Builder confirmar=new AlertDialog.Builder(this);
        confirmar.setTitle("IMPORTANTE!!")
                .setMessage("¿Estas seguro que deseas actualizar?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actualizarDatos();
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesyLimpiarCampos();
                dialog.cancel();
            }
        }).show();

    }

    private void actualizarDatos() {
        try
        {
            SQLiteDatabase tabla =base.getWritableDatabase();
            String SQL = "UPDATE INMUEBLE SET DOMICILIO='"+domicilio_i.getText().toString()+"', VENTA="
                    +venta.getText().toString()+", RENTA="
                    +renta.getText().toString()+"', FECHA='"
                    +fecha.getText().toString()+"' WHERE ID="+identificador_i.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "SE ACTUALIZO", Toast.LENGTH_LONG).show();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }
        habilitarBotonesyLimpiarCampos();
    }
    private void habilitarBotonesyLimpiarCampos() {
        identificador_i.setText("");
        domicilio_i.setText("");
        venta.setText("");
        renta.setText("");
        fecha.setText("");
        dueno.setEnabled(true);

        crear_i.setEnabled(true);
        buscar_i.setEnabled(true);
        eliminar_i.setEnabled(true);

        identificador_i.setEnabled(true);
    }
    private void pedirID(final int origen ) {

        final EditText pedirID=new EditText(this);

        pedirID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pedirID.setHint("VALOR ENTERO MAYOR DE 0");
        AlertDialog.Builder alerta =new AlertDialog.Builder(this);
        String mensaje="Escriba el ID a buscar";
        String mensaje2="Buscar";
        if(origen==2)
        {
            mensaje="Escriba el ID a modificar";
            mensaje2="Actualizar";
        }
        if(origen==3)
        {
            mensaje="Escriba el ID a eliminar";
            mensaje2="Eliminar";
        }
        alerta.setTitle("ATENCION!!")
                .setMessage(mensaje)
                .setView(pedirID)
                .setPositiveButton(mensaje2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pedirID.getText().toString().isEmpty())
                        {
                            Toast.makeText(Main3Activity.this, "DEBES INGRESAR UN NUMERO", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(pedirID.getText().toString(), origen);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCELAR", null).show();
    }
    private void buscarDato(String idABuscar, int origen)
    {
        try
        {
            SQLiteDatabase tabla=base.getReadableDatabase();
            String SQL ="SELECT * FROM INMUEBLE WHERE ID_INMUEBLE="+idABuscar;

            Cursor resultado=tabla.rawQuery(SQL, null);



            if (resultado.moveToFirst())
            {
                //si hay

                if(origen==3)
                {
                    String datos=resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4)+"&"+resultado.getString(5);
                    invocarConfirmacionEliminar(datos, idABuscar);
                    return;
                }
                identificador_i.setText(resultado.getString(0));
                domicilio_i.setText(resultado.getString(1));
                venta.setText(resultado.getString(2));
                renta.setText(resultado.getString(3));
                fecha.setText(resultado.getString(4));
                int posicion=Integer.parseInt(resultado.getString(5))-1;
                dueno.setSelection(posicion);

                if(origen==2)
                {
                    crear_i.setEnabled(false);
                    buscar_i.setEnabled(false);
                    eliminar_i.setEnabled(false);
                    identificador_i.setEnabled(false);
                    dueno.setEnabled(false);
                }
            }
            else
            {
                //no hay
                Toast.makeText(this, "NO HAY DATOS", Toast.LENGTH_LONG).show();
            }
            tabla.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO BUSCAR", Toast.LENGTH_LONG).show();
        }
    }
    private void invocarConfirmacionEliminar(String datos, final String idABuscar) {
        String [] partes=datos.split("&");
        String mostrardatos= partes[1]+"\n" +partes[2]+"\n" +partes[3]+"\n" +partes[4]+"\n" +partes[5]+"\n";
        AlertDialog.Builder confirmaEliminacion = new AlertDialog.Builder(this);
        confirmaEliminacion.setTitle("ATENCION !!!")
                .setMessage("Seguro que desesas eliminar los datos de "+idABuscar +"\n" +mostrardatos)
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminardato(idABuscar);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancelar", null).show();
    }
    private void eliminardato(String idABuscar) {
        try {
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "DELETE FROM INMUEBLE WHERE ID_INMUEBLE=" + idABuscar;
            tabla.execSQL(SQL);
            Toast.makeText(this, "ELIMINADO", Toast.LENGTH_LONG).show();
            tabla.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }
   private void codigoInsertar()
    {
        try
        {
            SQLiteDatabase tabla= base.getWritableDatabase();

            String SQL ="INSERT INTO INMUEBLE VALUES ("+identificador_i.getText().toString()+",'"
                    +domicilio_i.getText().toString()+"',"
                    +venta.getText().toString()+","
                    +renta.getText().toString()+",'"
                    +fecha.getText().toString()+"',"
                    +id_datos[dueno.getSelectedItemPosition()]+")";
            tabla.execSQL(SQL);
            Toast.makeText(this, "SE CREO UN NUEVO USUARIO", Toast.LENGTH_LONG).show();
            tabla.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE CREO NINGUN USUARIO", Toast.LENGTH_LONG).show();
        }
    }
}
