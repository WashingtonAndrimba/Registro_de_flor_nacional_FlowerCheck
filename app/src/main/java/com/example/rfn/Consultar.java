package com.example.rfn;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.widget.Spinner;

import java.util.ArrayList;


public class Consultar extends AppCompatActivity {
    private Spinner spFincaConsulta, spVariedadConsulta;
    private DatabaseReference fincasRef, variedadesRef, registrosRef;
    private Button btnConsultar1;
    private EditText editTextIDActualizar;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultar);

        btnConsultar1 = findViewById(R.id.btnConsultar1);
        spFincaConsulta = findViewById(R.id.spFincaConsulta);
        spVariedadConsulta = findViewById(R.id.spVariedadConsulta);
        fincasRef = FirebaseDatabase.getInstance().getReference().child("fincas").child("855tfHXpWsebk1OnSfUKRcKVjoa2");
        variedadesRef = FirebaseDatabase.getInstance().getReference().child("variedadesFlor");
        registrosRef = FirebaseDatabase.getInstance().getReference().child("registros");
        editTextIDActualizar = findViewById(R.id.editTextIDActualizar);

        cargarFincas();
        cargarVariedades();
        setConsultarButtonClickListener();
        setActualizarButtonClickListener();

    }

    private void cargarFincas() {
        fincasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listaFincas = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombreFinca = snapshot.child("nombre").getValue(String.class);
                    if (nombreFinca != null) {
                        listaFincas.add(nombreFinca);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Consultar.this,
                        android.R.layout.simple_spinner_item, listaFincas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spFincaConsulta.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Consultar.this, "Error al cargar fincas", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void cargarVariedades() {
        variedadesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listaVariedades = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombreVariedad = snapshot.child("nombreFlor").getValue(String.class);
                    if (nombreVariedad != null) {
                        listaVariedades.add(nombreVariedad);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Consultar.this,
                        android.R.layout.simple_spinner_item, listaVariedades);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spVariedadConsulta.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Consultar.this, "Error al cargar variedades de flores", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setConsultarButtonClickListener() {
        btnConsultar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spFincaConsulta.getSelectedItem() != null) {
                    String fincaSeleccionada = spFincaConsulta.getSelectedItem().toString();
                    String variedadSeleccionada = spVariedadConsulta.getSelectedItem().toString();


                    realizarConsultaEnFirebase(fincaSeleccionada, variedadSeleccionada);
                } else {
                    Toast.makeText(Consultar.this, "Seleccione una finca", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setActualizarButtonClickListener() {
        Button btnActualizar = findViewById(R.id.btnActualizar);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idActualizar = editTextIDActualizar.getText().toString().trim();
                if (!idActualizar.isEmpty()) {
                    // Si el ID no está vacío, muestra el cuadro de diálogo de actualización
                    mostrarVentanaActualizar(idActualizar);
                } else {
                    // Si el ID está vacío, muestra un mensaje de error
                    Toast.makeText(Consultar.this, "Ingrese un ID para actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void mostrarVentanaActualizar(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar información");


        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialogo_actualizar_variedad_nacional, null);


        final EditText etArea = viewInflated.findViewById(R.id.etArea);
        final EditText etBloque = viewInflated.findViewById(R.id.etBloque);
        final EditText etFecha = viewInflated.findViewById(R.id.etNuevaFechaRegistroFlor);
        final EditText etCantidad = viewInflated.findViewById(R.id.etCantidad);
        final EditText etEnfermedad = viewInflated.findViewById(R.id.etEnfermedad);


        etArea.setText("");
        etBloque.setText("");
        etFecha.setText("");
        etCantidad.setText("");
        etEnfermedad.setText("");

        builder.setView(viewInflated);


        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String nuevaArea = etArea.getText().toString();
                String nuevoBloque = etBloque.getText().toString();
                String nuevaFecha = etFecha.getText().toString();
                String nuevaCantidad = etCantidad.getText().toString();
                String nuevaEnfermedad = etEnfermedad.getText().toString();
                actualizarEnFirebase(id, nuevaArea, nuevoBloque, nuevaFecha, nuevaCantidad, nuevaEnfermedad);


                dialog.dismiss();
            }
        });


        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        builder.show();
    }


    private void realizarConsultaEnFirebase(String finca, String variedad) {
        Query consulta = registrosRef.orderByChild("finca").equalTo(finca);

        consulta.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinearLayout contenedorDeFilas = findViewById(R.id.contenedorDeFilas);
                contenedorDeFilas.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String area = snapshot.child("area").getValue(String.class);
                    String bloque = snapshot.child("bloque").getValue(String.class);
                    String fecha = snapshot.child("fecha").getValue(String.class);
                    String cantidad = snapshot.child("cantidad").getValue(String.class);
                    String enfermedad = snapshot.child("enfermedad").getValue(String.class);

                    View fila = getLayoutInflater().inflate(R.layout.item_dato2, null);
                    TextView tvId = fila.findViewById(R.id.tvId);
                    TextView tvArea = fila.findViewById(R.id.tvArea);
                    TextView tvBloque = fila.findViewById(R.id.tvBloque);
                    TextView tvFecha = fila.findViewById(R.id.tvFecha);
                    TextView tvCantidad = fila.findViewById(R.id.tvCantidad);
                    TextView tvEnfermedad = fila.findViewById(R.id.tvEnfermedad);

                    tvId.setText(id);
                    tvArea.setText(area);
                    tvBloque.setText(bloque);
                    tvFecha.setText(fecha);
                    tvCantidad.setText(cantidad);
                    tvEnfermedad.setText(enfermedad);

                    contenedorDeFilas.addView(fila);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Consultar.this, "Error en la consulta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarEnFirebase(String id, String nuevaArea, String nuevoBloque, String nuevaFecha, String nuevaCantidad, String nuevaEnfermedad) {
        // Aquí debes implementar la lógica para actualizar los datos en tu base de datos de Firebase
        // Utiliza la referencia correcta y realiza la actualización según tu estructura de datos.
        // Por ejemplo:
        DatabaseReference registroRef = registrosRef.child(id);
        registroRef.child("area").setValue(nuevaArea);
        registroRef.child("bloque").setValue(nuevoBloque);
        registroRef.child("fecha").setValue(nuevaFecha);
        registroRef.child("cantidad").setValue(nuevaCantidad);
        registroRef.child("enfermedad").setValue(nuevaEnfermedad);

        Toast.makeText(Consultar.this, "Registro actualizado con éxito", Toast.LENGTH_SHORT).show();
    }


    private void agregarTextViewATabla(LinearLayout fila, String texto) {
        TextView textView = new TextView(Consultar.this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(texto);
        fila.addView(textView);
    }




}

