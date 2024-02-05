package com.example.rfn;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Registro extends AppCompatActivity {

    private Spinner spEnfermedad, spFinca, spVariedad, spArea, spBloque;
    private DatabaseReference enfermedadesRef, fincasRef, variedadesRef, registrosRef, contadorRef;
    private Button btnGuardar;
    private EditText etdFecha, etCantidadRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        spEnfermedad = findViewById(R.id.spEnfermedad);
        spFinca = findViewById(R.id.spFinca);
        spVariedad = findViewById(R.id.spVariedad);
        spArea = findViewById(R.id.spArea);
        spBloque = findViewById(R.id.spBloque);
        etdFecha = findViewById(R.id.etdFecha);
        etCantidadRegistro = findViewById(R.id.etCantidadRegistro);
        btnGuardar = findViewById(R.id.btnGuardar);

        registrosRef = FirebaseDatabase.getInstance().getReference().child("registros");
        contadorRef = FirebaseDatabase.getInstance().getReference().child("contadorRegistros");

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarRegistro();
            }
        });

        enfermedadesRef = FirebaseDatabase.getInstance().getReference().child("enfermedades");
        fincasRef = FirebaseDatabase.getInstance().getReference().child("fincas").child("855tfHXpWsebk1OnSfUKRcKVjoa2");
        variedadesRef = FirebaseDatabase.getInstance().getReference().child("variedadesFlor");

        cargarEnfermedades();
        cargarFincas();
        cargarVariedades();

        etdFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        etdFecha.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth
        );

        datePickerDialog.show();
    }

    private void cargarEnfermedades() {
        enfermedadesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> listaEnfermedades = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nombreEnfermedad = snapshot.child("nombre").getValue(String.class);
                    if (nombreEnfermedad != null) {
                        listaEnfermedades.add(nombreEnfermedad);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Registro.this,
                        android.R.layout.simple_spinner_item, listaEnfermedades);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spEnfermedad.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Registro.this, "Error al cargar enfermedades", Toast.LENGTH_SHORT).show();
            }
        });
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(Registro.this,
                        android.R.layout.simple_spinner_item, listaFincas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spFinca.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Registro.this, "Error al cargar fincas", Toast.LENGTH_SHORT).show();
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Registro.this,
                        android.R.layout.simple_spinner_item, listaVariedades);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spVariedad.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Registro.this, "Error al cargar variedades de flores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarRegistro() {
        String enfermedad = spEnfermedad.getSelectedItem().toString();
        String finca = spFinca.getSelectedItem().toString();
        String variedad = spVariedad.getSelectedItem().toString();
        String area = spArea.getSelectedItem().toString();
        String bloque = spBloque.getSelectedItem().toString();
        String fecha = etdFecha.getText().toString();
        String cantidad = etCantidadRegistro.getText().toString();

        if (enfermedad.isEmpty() || finca.isEmpty() || variedad.isEmpty() || fecha.isEmpty() || cantidad.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        obtenerContadorRegistros(new OnCounterValueListener() {
            @Override
            public void onValue(long contadorRegistros) {
                contadorRegistros++;

                RegistroSG registro = new RegistroSG(String.valueOf(contadorRegistros), enfermedad, finca, variedad, bloque, area, cantidad, fecha);
                registrosRef.child(String.valueOf(contadorRegistros)).setValue(registro);
                actualizarContadorRegistros(contadorRegistros);

                Toast.makeText(Registro.this, "Registro guardado exitosamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obtenerContadorRegistros(final OnCounterValueListener listener) {
        contadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long contador = dataSnapshot.exists() ? dataSnapshot.getValue(Long.class) : 0;
                listener.onValue(contador);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onValue(0);
            }
        });
    }

    private void actualizarContadorRegistros(long contador) {
        contadorRef.setValue(contador)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // La actualización fue exitosa
                        } else {
                            // Hubo un error en la actualización
                        }
                    }
                });
    }

    private interface OnCounterValueListener {
        void onValue(long contadorRegistros);
    }
}
