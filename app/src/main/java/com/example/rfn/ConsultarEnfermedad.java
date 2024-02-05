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

public class ConsultarEnfermedad extends AppCompatActivity {
    private Spinner spConsultaEnfermedadNombre;
    private TextView tvInfoEnfermedad;
    private DatabaseReference enfermedadesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultaenfermedades);

        spConsultaEnfermedadNombre = findViewById(R.id.spConsultaEnfermedadNombre);
        tvInfoEnfermedad = findViewById(R.id.tvInfoEnfermedad);


        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


        enfermedadesRef = FirebaseDatabase.getInstance().getReference().child("enfermedades");

        cargarEnfermedades();

        Button btnConsultarEnfermedad = findViewById(R.id.btnConsultarEnfermedad);
        btnConsultarEnfermedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarEnfermedad();
            }
        });

        Button btnActualizarEnfermedad = findViewById(R.id.btnActualizarEnfermedad);
        btnActualizarEnfermedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoActualizar();
            }
        });

        ImageButton btnEliminarEnfermedad = findViewById(R.id.ibEliminarEnfermedad);
        btnEliminarEnfermedad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoEliminar();
            }
        });
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConsultarEnfermedad.this,
                        android.R.layout.simple_spinner_item, listaEnfermedades);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spConsultaEnfermedadNombre.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarEnfermedad.this, "Error al cargar enfermedades", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarEnfermedad() {
        String enfermedadSeleccionada = spConsultaEnfermedadNombre.getSelectedItem().toString();

        enfermedadesRef.orderByChild("nombre").equalTo(enfermedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Enfermedad enfermedad = snapshot.getValue(Enfermedad.class);

                    if (enfermedad != null) {
                        String info = "Nombre: " + enfermedad.getNombre() + "\n"
                                + "Descripción: " + enfermedad.getDescripcion() + "\n"
                                + "Cultivos Afectados: " + enfermedad.getCultivosAfectados() + "\n"
                                + "Fecha de Registro: " + enfermedad.getFechaRegistro() + "\n"
                                + "Tipo de Cultivo: " + enfermedad.getTipoCultivo() + "\n"
                                + "Gravedad: " + enfermedad.getGravedad();

                        tvInfoEnfermedad.setText(info);
                        return;
                    }
                }

                tvInfoEnfermedad.setText("Enfermedad no encontrada");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarEnfermedad.this, "Error al consultar enfermedad", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ConsultarEnfermedad.this, "Contraseña incorrecta. No se pudo actualizar la enfermedad.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mostrarDialogoActualizarInformacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Información de la Enfermedad");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_actualizar_enfermedad, null);
        builder.setView(dialogView);

        final EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombreEnfermedad);
        final EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionEnfermedad);
        final EditText etCultivosAfectados = dialogView.findViewById(R.id.etCultivosAfectados);
        final EditText etNuevaFechaRegistro = dialogView.findViewById(R.id.etNuevaFechaRegistroEnfermedad);
        final EditText etNuevoTipoCultivo = dialogView.findViewById(R.id.etNuevoTipoCultivoEnfermedad);
        final EditText etGravedad = dialogView.findViewById(R.id.etGravedad);

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nuevoNombre = etNuevoNombre.getText().toString().trim();
                String nuevaDescripcion = etDescripcion.getText().toString().trim();
                String nuevosCultivosAfectados = etCultivosAfectados.getText().toString().trim();
                String nuevaFechaRegistro = etNuevaFechaRegistro.getText().toString().trim();
                String nuevoTipoCultivo = etNuevoTipoCultivo.getText().toString().trim();
                String nuevaGravedad = etGravedad.getText().toString().trim();

                if (!nuevoNombre.isEmpty() && !nuevaDescripcion.isEmpty() && !nuevosCultivosAfectados.isEmpty()
                        && !nuevaFechaRegistro.isEmpty() && !nuevoTipoCultivo.isEmpty() && !nuevaGravedad.isEmpty()) {
                    actualizarInformacion(nuevoNombre, nuevaDescripcion, nuevosCultivosAfectados,
                            nuevaFechaRegistro, nuevoTipoCultivo, nuevaGravedad);
                } else {
                    Toast.makeText(ConsultarEnfermedad.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
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
                            eliminarEnfermedad();
                        } else {
                            Toast.makeText(ConsultarEnfermedad.this, "Contraseña incorrecta. No se pudo eliminar la enfermedad.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void eliminarEnfermedad() {
        String enfermedadSeleccionada = spConsultaEnfermedadNombre.getSelectedItem().toString();

        enfermedadesRef.orderByChild("nombre").equalTo(enfermedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference enfermedadRef = snapshot.getRef();
                    enfermedadRef.removeValue();
                    Toast.makeText(ConsultarEnfermedad.this, "Enfermedad eliminada correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarEnfermedad.this, "Error al eliminar enfermedad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarInformacion(String nuevoNombre, String nuevaDescripcion, String nuevosCultivosAfectados,
                                       String nuevaFechaRegistro, String nuevoTipoCultivo, String nuevaGravedad) {
        String enfermedadSeleccionada = spConsultaEnfermedadNombre.getSelectedItem().toString();

        enfermedadesRef.orderByChild("nombre").equalTo(enfermedadSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference enfermedadRef = snapshot.getRef();

                    enfermedadRef.child("nombre").setValue(nuevoNombre);
                    enfermedadRef.child("descripcion").setValue(nuevaDescripcion);
                    enfermedadRef.child("cultivosAfectados").setValue(nuevosCultivosAfectados);
                    enfermedadRef.child("fechaRegistro").setValue(nuevaFechaRegistro);
                    enfermedadRef.child("tipoCultivo").setValue(nuevoTipoCultivo);
                    enfermedadRef.child("gravedad").setValue(nuevaGravedad);

                    Toast.makeText(ConsultarEnfermedad.this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarEnfermedad.this, "Error al actualizar información", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
