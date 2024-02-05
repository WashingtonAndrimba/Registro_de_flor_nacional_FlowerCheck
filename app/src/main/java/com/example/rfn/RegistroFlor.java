package com.example.rfn;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RegistroFlor {
    private String finca;
    private String area;
    private String bloque;
    private String variedad;
    private String enfermedad;
    private int cantidad;
    private String fecha;

    public RegistroFlor() {

    }

    public RegistroFlor(String finca, String area, String bloque, String variedad, String enfermedad, int cantidad, String fecha) {
        this.finca = finca;
        this.area = area;
        this.bloque = bloque;
        this.variedad = variedad;
        this.enfermedad = enfermedad;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public static void cargarDatosSpinner(final DatabaseReference ref, final Spinner spinner) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> datos = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String dato = snapshot.getValue(String.class);
                    datos.add(dato);
                }


                ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(), android.R.layout.simple_spinner_item, datos);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
