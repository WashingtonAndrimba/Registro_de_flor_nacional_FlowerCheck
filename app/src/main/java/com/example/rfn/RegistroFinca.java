package com.example.rfn;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegistroFinca extends AppCompatActivity {

    private EditText etNombreFinca, etDuenoFinca, etAreaFinca, etFechaRegistroFinca, etTipoCultivoFinca;
    private Button btnRegistrarFinca, btnCancel;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrofinca);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("fincas");

        etNombreFinca = findViewById(R.id.etNombreFincaRegisgtro);
        etDuenoFinca = findViewById(R.id.etDueñoFincaRegistro);
        etAreaFinca = findViewById(R.id.etAreaFincaRegistro);
        etFechaRegistroFinca = findViewById(R.id.dateFechaRegistroFinca);
        etTipoCultivoFinca = findViewById(R.id.etTipoCultivoFincaRegistro);
        btnRegistrarFinca = findViewById(R.id.btnRegistrarFinca);
        btnCancel = findViewById(R.id.button);

        btnRegistrarFinca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarFinca();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    private void registrarFinca() {

        String nombre = etNombreFinca.getText().toString().trim();
        String dueno = etDuenoFinca.getText().toString().trim();
        String area = etAreaFinca.getText().toString().trim();
        String fechaRegistro = etFechaRegistroFinca.getText().toString().trim();
        String tipoCultivo = etTipoCultivoFinca.getText().toString().trim();

        if (nombre.isEmpty() || dueno.isEmpty() || area.isEmpty() || fechaRegistro.isEmpty() || tipoCultivo.isEmpty()) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();

        String fincaId = databaseReference.push().getKey();

        Finca finca = new Finca(fincaId, nombre, dueno, area, fechaRegistro, tipoCultivo);

        databaseReference.child(userId).child(fincaId).setValue(finca);

        Toast.makeText(this, "Finca registrada exitosamente", Toast.LENGTH_SHORT).show();


        etNombreFinca.setText("");
        etDuenoFinca.setText("");
        etAreaFinca.setText("");
        etFechaRegistroFinca.setText("");
        etTipoCultivoFinca.setText("");


        finish();
    }
    public void mostrarDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                        etFechaRegistroFinca.setText(fechaSeleccionada);
                    }
                },
                year, month, dayOfMonth
        );

        datePickerDialog.show();
    }
}
