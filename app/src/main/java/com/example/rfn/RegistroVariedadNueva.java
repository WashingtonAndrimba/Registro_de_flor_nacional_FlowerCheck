package com.example.rfn;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegistroVariedadNueva extends AppCompatActivity {

    private EditText etNombreFlor, etColorFlor, etAlturaFlor, etTemporadaFlor, etCondicionesCuidadosFlor;
    private EditText dateIngresoFlor;
    private Button btnAlmacenarNuevaFlor, btnCancelarRegistroFlor;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registronuevavariedad);

        etNombreFlor = findViewById(R.id.etNombreFlor);
        etColorFlor = findViewById(R.id.etColorFlor);
        etAlturaFlor = findViewById(R.id.etAlturaFlor);
        etTemporadaFlor = findViewById(R.id.etTemporadaFlor);
        etCondicionesCuidadosFlor = findViewById(R.id.etCondicionesCuidadosFlor);
        dateIngresoFlor = findViewById(R.id.dateIngresoFlor);
        btnAlmacenarNuevaFlor = findViewById(R.id.btnAlmacenarNuevaFlor);
        btnCancelarRegistroFlor = findViewById(R.id.btnCancelarRegistroFlor);

        databaseReference = FirebaseDatabase.getInstance().getReference("variedadesFlor");

        dateIngresoFlor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        btnAlmacenarNuevaFlor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                almacenarNuevaFlor();
            }
        });

        btnCancelarRegistroFlor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implementa la lógica para cancelar el registro de flor si es necesario
                finish();
            }
        });
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // La fecha seleccionada se maneja aquí
                        String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateIngresoFlor.setText(fechaSeleccionada);
                    }
                },
                year, month, dayOfMonth
        );

        datePickerDialog.show();
    }

    private void almacenarNuevaFlor() {
        String nombreFlor = etNombreFlor.getText().toString().trim();
        String colorFlor = etColorFlor.getText().toString().trim();
        String alturaFlor = etAlturaFlor.getText().toString().trim();
        String temporadaFlor = etTemporadaFlor.getText().toString().trim();
        String condicionesCuidadosFlor = etCondicionesCuidadosFlor.getText().toString().trim();
        String fechaIngresoFlor = dateIngresoFlor.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (nombreFlor.isEmpty() || colorFlor.isEmpty() || alturaFlor.isEmpty() ||
                temporadaFlor.isEmpty() || condicionesCuidadosFlor.isEmpty() || fechaIngresoFlor.isEmpty()) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }


        Variedad nuevaFlor = new Variedad(nombreFlor, colorFlor, alturaFlor,
                temporadaFlor, condicionesCuidadosFlor, fechaIngresoFlor);

        String idFlor = databaseReference.push().getKey();
        databaseReference.child(idFlor).setValue(nuevaFlor);

        Toast.makeText(this, "Variedad de flor almacenada correctamente", Toast.LENGTH_SHORT).show();

        finish();
    }
}
