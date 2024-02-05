package com.example.rfn;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConsultarVariedad extends AppCompatActivity {
    private Spinner spConsultarNombreVariedad;
    private TextView tvInfoVariedad;
    private DatabaseReference variedadesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultarvariedades);

        spConsultarNombreVariedad = findViewById(R.id.spConsultarNombreVariedad);
        tvInfoVariedad = findViewById(R.id.tvInfoVariedad);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        variedadesRef = FirebaseDatabase.getInstance().getReference().child("variedadesFlor");

        cargarVariedades();

        Button btnConsultarVariedad = findViewById(R.id.btnConsultarVariedad);
        btnConsultarVariedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarVariedades();
            }
        });
        Button btnActualizarVariedades = findViewById(R.id.btnActualizarVariedades);
        btnActualizarVariedades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoActualizar();
            }
        });
        ImageButton btnEliminarVariedad = findViewById(R.id.btnEliminarVariedad);
        btnEliminarVariedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoEliminar();
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConsultarVariedad.this,
                        android.R.layout.simple_spinner_item, listaVariedades);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spConsultarNombreVariedad.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarVariedad.this, "Error al cargar variedades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarVariedades() {
        String variedadSeleccionada = spConsultarNombreVariedad.getSelectedItem().toString();

        variedadesRef.orderByChild("nombreFlor").equalTo(variedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Variedad variedad = snapshot.getValue(Variedad.class);

                    if (variedad != null) {

                        String info = "Nombre Flor: " + variedad.getNombreFlor() + "\n"
                                + "Color: " + variedad.getColorFlor() + "\n"
                                + "Altura: " + variedad.getAlturaFlor() + "\n"
                                + "Fecha de Ingreso: " + variedad.getFechaIngresoFlor() + "\n"
                                + "Condiciones de cuidado: " + variedad.getCondicionesCuidadosFlor() + "\n"
                                + "Temporada: " + variedad.getTemporadaFlor();

                        tvInfoVariedad.setText(info);
                        return;
                    }
                }

                tvInfoVariedad.setText("Variedad no encontrada");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarVariedad.this, "Error al consultar variedad", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void mostrarDialogoActualizar() {

        final EditText inputPassword = new EditText(this);
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(this);
        passwordDialogBuilder.setTitle("Ingrese su contraseña");
        passwordDialogBuilder.setView(inputPassword);

        passwordDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String enteredPassword = inputPassword.getText().toString().trim();
                autenticarYActualizar(enteredPassword);
            }
        });

        passwordDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        passwordDialogBuilder.create().show();
    }


    private void actualizarInformacion(String nuevoNombre, String nuevoColor, String nuevaAltura, String nuevaTemporada, String nuevaCondicion, String nuevaFechaRegistroFlor) {
        String variedadSeleccionada = spConsultarNombreVariedad.getSelectedItem().toString();

        variedadesRef.orderByChild("nombreFlor").equalTo(variedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference variedadesRef = snapshot.getRef();


                    variedadesRef.child("nombreFlor").setValue(nuevoNombre);
                    variedadesRef.child("colorFlor").setValue(nuevoColor);
                    variedadesRef.child("alturaFlor").setValue(nuevaAltura);
                    variedadesRef.child("temporadaFlor").setValue(nuevaTemporada);
                    variedadesRef.child("condicionesCuidadosFlor").setValue(nuevaCondicion);
                    variedadesRef.child("fechaIngresoFlor").setValue(nuevaFechaRegistroFlor);



                    Toast.makeText(ConsultarVariedad.this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarVariedad.this, "Error al actualizar información", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void autenticarYActualizar(final String enteredPassword) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), enteredPassword);


        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            mostrarDialogoActualizarInformacion();
                        } else {

                            Toast.makeText(ConsultarVariedad.this, "Contraseña incorrecta. No se pudo actualizar la variedad.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mostrarDialogoActualizarInformacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Información de la Variedad");


        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_actualizar_flor, null);
        builder.setView(dialogView);

        final EditText etNuevoNombreFlor = dialogView.findViewById(R.id.etNuevoNombreFlor);
        final EditText etNuevoColorFlor = dialogView.findViewById(R.id.etNuevoColorFlor);
        final EditText etNuevaAlturaFlor = dialogView.findViewById(R.id.etNuevaAlturaFlor);
        final EditText etNuevaTemporadaFlor = dialogView.findViewById(R.id.etNuevaTemporadaFlor);
        final EditText etNuevaCondicionesCuidadoFlor = dialogView.findViewById(R.id.etNuevaCondicionesCuidadoFlor);
        final EditText etNuevaFechaRegistroFlor = dialogView.findViewById(R.id.etNuevaFechaRegistroFlor);

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nuevoNombreFlor = etNuevoNombreFlor.getText().toString().trim();
                String nuevoColorFlor = etNuevoColorFlor.getText().toString().trim();
                String nuevaAlturaFlor = etNuevaAlturaFlor.getText().toString().trim();
                String nuevaTemporadaFlor = etNuevaTemporadaFlor.getText().toString().trim();
                String nuevaCondicionesCuidadoFlor = etNuevaCondicionesCuidadoFlor.getText().toString().trim();
                String nuevaFechaRegistroFlor = etNuevaFechaRegistroFlor.getText().toString().trim();

                if (!nuevoNombreFlor.isEmpty() && !nuevoColorFlor.isEmpty() && !nuevaAlturaFlor.isEmpty()
                        && !nuevaTemporadaFlor.isEmpty() && !nuevaCondicionesCuidadoFlor.isEmpty() && !nuevaFechaRegistroFlor.isEmpty()) {
                    actualizarInformacion(nuevoNombreFlor, nuevoColorFlor, nuevaAlturaFlor, nuevaTemporadaFlor, nuevaCondicionesCuidadoFlor, nuevaFechaRegistroFlor);
                } else {
                    Toast.makeText(ConsultarVariedad.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.create().show();
    }

    private void mostrarDialogoEliminar() {

        final EditText inputPassword = new EditText(this);
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(this);
        passwordDialogBuilder.setTitle("Ingrese su contraseña");
        passwordDialogBuilder.setView(inputPassword);

        passwordDialogBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String enteredPassword = inputPassword.getText().toString().trim();
                autenticarYEliminar(enteredPassword);
            }
        });

        passwordDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        passwordDialogBuilder.create().show();
    }

    private void autenticarYEliminar(final String enteredPassword) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), enteredPassword);


        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            eliminarVariedad();
                        } else {

                            Toast.makeText(ConsultarVariedad.this, "Contraseña incorrecta. No se pudo eliminar la variedad.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void eliminarVariedad() {
        String variedadSeleccionada = spConsultarNombreVariedad.getSelectedItem().toString();

        variedadesRef.orderByChild("nombreFlor").equalTo(variedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    DatabaseReference variedadesRef = snapshot.getRef();


                    variedadesRef.removeValue();


                    Toast.makeText(ConsultarVariedad.this, "Variedad eliminada correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarVariedad.this, "Error al eliminar variedad", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


