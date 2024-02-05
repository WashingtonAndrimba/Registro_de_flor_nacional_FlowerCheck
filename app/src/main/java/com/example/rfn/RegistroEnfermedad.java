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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistroEnfermedad extends AppCompatActivity {

    private EditText etNombreEnfermedad, etDescripcionEnfermedad, etCultivosAfectados, etFechaRegistro, etTipoCultivo;
    private Spinner spGravedadEnfermedad;
    private Button btnRegistrarEnfermedad;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registroenfermedad);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("enfermedades"); // Cambia "enfermedades" por el nombre de tu nodo

        etNombreEnfermedad = findViewById(R.id.etNombreFincaRegisgtro);
        etDescripcionEnfermedad = findViewById(R.id.etDueñoFincaRegistro);
        etCultivosAfectados = findViewById(R.id.etAreaFincaRegistro);
        etFechaRegistro = findViewById(R.id.dateFechaRegistroFinca);
        etTipoCultivo = findViewById(R.id.etTipoCultivoFincaRegistro);
        spGravedadEnfermedad = findViewById(R.id.spGravedadEnfermedad);
        btnRegistrarEnfermedad = findViewById(R.id.btnRegistrarFinca);

        ArrayAdapter<String> gravedadAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new String[]{"Baja", "Media", "Alta", "Grave"});
        gravedadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGravedadEnfermedad.setAdapter(gravedadAdapter);

        btnRegistrarEnfermedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarEnfermedad();
            }
        });

        calendar = Calendar.getInstance();

        etFechaRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePickerDialog();
            }
        });
    }

    private void registrarEnfermedad() {
        String nombre = etNombreEnfermedad.getText().toString().trim();
        String descripcion = etDescripcionEnfermedad.getText().toString().trim();
        String cultivosAfectados = etCultivosAfectados.getText().toString().trim();
        String fechaRegistro = etFechaRegistro.getText().toString().trim();
        String tipoCultivo = etTipoCultivo.getText().toString().trim();
        String gravedad = spGravedadEnfermedad.getSelectedItem().toString();

        if (!nombre.isEmpty() && !descripcion.isEmpty() && !cultivosAfectados.isEmpty() && !fechaRegistro.isEmpty() && !tipoCultivo.isEmpty()) {

            String idEnfermedad = databaseReference.push().getKey();

            Enfermedad enfermedad = new Enfermedad(idEnfermedad, nombre, descripcion, cultivosAfectados, fechaRegistro, tipoCultivo, gravedad);

            databaseReference.child(idEnfermedad).setValue(enfermedad);

            Toast.makeText(this, "Enfermedad registrada exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                actualizarCampoFecha();
            }
        };

        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void actualizarCampoFecha() {
        String formatoFecha = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(formatoFecha, Locale.getDefault());
        etFechaRegistro.setText(sdf.format(calendar.getTime()));
    }
}

