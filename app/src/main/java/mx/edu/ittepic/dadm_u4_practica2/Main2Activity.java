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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    EditText identificador, nombre, domicilio, telefono;
    ImageButton crear, modificar, borrar, buscar;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        identificador=findViewById(R.id.campo_id);
        nombre=findViewById(R.id.campo_nombre);
        domicilio=findViewById(R.id.campo_domicilio);
        telefono=findViewById(R.id.campo_telefono);

        crear=findViewById(R.id.crear_propietario);
        modificar=findViewById(R.id.editar_propietario);
        borrar=findViewById(R.id.borrar_propietario);
        buscar=findViewById(R.id.ver_propietario);

        base=new BaseDatos(this, "inmobiliaria", null, 1);//primera es el nombre de la base de datos
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codigoInsertar();
            }
        });
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });
        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!crear.isEnabled())
                {
                    invocarConfirmacionActualizar();
                }
                else {
                    pedirID(2);
                }

            }
        });
        borrar.setOnClickListener(new View.OnClickListener() {
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
    private void habilitarBotonesyLimpiarCampos() {
        identificador.setText("");
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");

        crear.setEnabled(true);
        buscar.setEnabled(true);
        borrar.setEnabled(true);

        identificador.setEnabled(true);
    }

    private void actualizarDatos() {
        try
        {
            SQLiteDatabase tabla =base.getWritableDatabase();
            String SQL = "UPDATE PROPIETARIO SET NOMBRE='"+nombre.getText().toString()+"', DOMICILIO='"
                    +domicilio.getText().toString()+"', TELEFONO='"
                    +telefono.getText().toString()+"' WHERE IDP="+identificador.getText().toString();
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

    private void pedirID(final int origen) {
        final EditText pedirID=new EditText(this);
        pedirID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pedirID.setHint("VALOR ENTERO MAYOR DE 0");
        String mensaje="Escriba el ID a buscar";
        String mensaje2="Buscar";
        AlertDialog.Builder alerta =new AlertDialog.Builder(this);

        if(origen==2)
        {
            mensaje="Escriba el ID a modificar";
            mensaje2="Modificar";
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
                            Toast.makeText(Main2Activity.this, "DEBES INGRESAR UN NUMERO", Toast.LENGTH_LONG).show();
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
            String SQL ="SELECT * FROM PROPIETARIO WHERE IDP="+idABuscar;

            Cursor resultado=tabla.rawQuery(SQL, null);

            if (resultado.moveToFirst())
            {
                //si hay

                if(origen==3)
                {
                    String datos=resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4);
                    invocarConfirmacionEliminar(datos, idABuscar);
                    return;
                }

                identificador.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));

                if(origen==2)
                {
                    crear.setEnabled(false);
                    buscar.setEnabled(false);
                    borrar.setEnabled(false);
                    identificador.setEnabled(false);
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
        String mostrardatos= partes[1]+"\n" +partes[2]+"\n" +partes[3]+"\n" +partes[1]+"\n";
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

            String SQL = "DELETE FROM PROPIETARIO WHERE IDP=" + idABuscar;
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
            String SQL ="INSERT INTO PROPIETARIO VALUES("+identificador.getText().toString()+",'"
                    +nombre.getText().toString()+"','"
                    +domicilio.getText().toString()+"','"
                    +telefono.getText().toString()+"')";

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
