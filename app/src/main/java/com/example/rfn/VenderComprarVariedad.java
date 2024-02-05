package com.example.rfn;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VenderComprarVariedad extends AppCompatActivity {
    private DatabaseReference variedadesRef, registrosRef;
    private Spinner spVariedadCoV;
    private Button btnComprarVariedad, btnVenderVariedad;
    private EditText etCantidadComprarVender;
    private TextView tvDisponibilidad, tvFincaCoV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendercomprarvariedad);

        spVariedadCoV = findViewById(R.id.spVariedadCoV);
        btnComprarVariedad = findViewById(R.id.btnComprarVariedad);
        btnVenderVariedad = findViewById(R.id.btnVenderVariedad);
        etCantidadComprarVender = findViewById(R.id.etCantidadComprarVender);
        tvDisponibilidad = findViewById(R.id.tvDisponibilidad);
        tvFincaCoV = findViewById(R.id.tvFincaCoV);

        variedadesRef = FirebaseDatabase.getInstance().getReference().child("variedadesFlor");
        registrosRef = FirebaseDatabase.getInstance().getReference().child("registros");

        cargarVariedades();

        btnComprarVariedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprarVariedad();
            }
        });

        btnVenderVariedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venderVariedad();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(VenderComprarVariedad.this,
                        android.R.layout.simple_spinner_item, listaVariedades);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spVariedadCoV.setAdapter(adapter);

                spVariedadCoV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        String variedadSeleccionada = spVariedadCoV.getSelectedItem().toString();

                        actualizarTextViews(variedadSeleccionada);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VenderComprarVariedad.this, "Error al cargar variedades de flores", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void comprarVariedad() {
        String variedadSeleccionada = spVariedadCoV.getSelectedItem().toString();
        String cantidadString = etCantidadComprarVender.getText().toString();

        if (!cantidadString.isEmpty()) {
            int cantidad = Integer.parseInt(cantidadString);

            registrosRef.orderByChild("variedad").equalTo(variedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int cantidadActual = Integer.parseInt(snapshot.child("cantidad").getValue(String.class));

                        if (cantidad >= 0) {
                            registrosRef.child(snapshot.getKey()).child("cantidad").setValue(String.valueOf(cantidadActual + cantidad));

                            actualizarTextViews(variedadSeleccionada);

                            Toast.makeText(VenderComprarVariedad.this, "Compra exitosa", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VenderComprarVariedad.this, "No hay suficiente cantidad disponible para comprar", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VenderComprarVariedad.this, "Error al comprar variedad", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarTextViews(String variedadSeleccionada) {

        registrosRef.orderByChild("variedad").equalTo(variedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cantidadTotal = 0;
                String nombreFinca = "";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    cantidadTotal += Integer.parseInt(snapshot.child("cantidad").getValue(String.class));
                    nombreFinca = snapshot.child("finca").getValue(String.class);
                }

                tvDisponibilidad.setText("Disponibilidad: " + cantidadTotal);
                tvFincaCoV.setText("Finca CoV: " + nombreFinca);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VenderComprarVariedad.this, "Error al obtener información de la variedad", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void venderVariedad() {
        String variedadSeleccionada = spVariedadCoV.getSelectedItem().toString();
        String cantidadString = etCantidadComprarVender.getText().toString();

        if (!cantidadString.isEmpty()) {
            int cantidad = Integer.parseInt(cantidadString);

            registrosRef.orderByChild("variedad").equalTo(variedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        int cantidadActual = Integer.parseInt(snapshot.child("cantidad").getValue(String.class));

                        if (cantidad <= cantidadActual) {
                            registrosRef.child(snapshot.getKey()).child("cantidad").setValue(String.valueOf(cantidadActual - cantidad));

                            actualizarTextViews(variedadSeleccionada);

                            Toast.makeText(VenderComprarVariedad.this, "Venta exitosa", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VenderComprarVariedad.this, "No hay suficiente cantidad disponible para vender", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VenderComprarVariedad.this, "Error al vender variedad", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Ingrese una cantidad válida", Toast.LENGTH_SHORT).show();
        }
    }

}
