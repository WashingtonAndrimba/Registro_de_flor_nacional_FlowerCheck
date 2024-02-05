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

public class ConsultarFincas extends AppCompatActivity {
    private Spinner spConsultaFincaNombre;
    private TextView tvInfoFinca;
    private DatabaseReference fincasRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consultafincas);

        spConsultaFincaNombre = findViewById(R.id.spConsultaEnfermedadNombre);
        tvInfoFinca = findViewById(R.id.tvInfoEnfermedad);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fincasRef = FirebaseDatabase.getInstance().getReference().child("fincas").child(userId);

        cargarFincas();


        Button btnConsultarFinca = findViewById(R.id.btnConsultarEnfermedad);
        btnConsultarFinca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultarFinca();
            }
        });

        Button btnActualizarFincas = findViewById(R.id.btnActualizarFincas);
        btnActualizarFincas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoActualizar();
            }
        });
        ImageButton btnEliminarFinca = findViewById(R.id.btnEliminarFinca);
        btnEliminarFinca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogoEliminar();
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

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ConsultarFincas.this,
                        android.R.layout.simple_spinner_item, listaFincas);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spConsultaFincaNombre.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarFincas.this, "Error al cargar fincas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarFinca() {
        String fincaSeleccionada = spConsultaFincaNombre.getSelectedItem().toString();

        fincasRef.orderByChild("nombre").equalTo(fincaSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Finca finca = snapshot.getValue(Finca.class);

                    if (finca != null) {
                        String infoFinca = "Nombre de la finca: " + finca.getNombre() + "\n"
                                + "Dueño de la finca: " + finca.getDueno() + "\n"
                                + "Área de la finca: " + finca.getArea() + "\n"
                                + "Fecha de Registro: " + finca.getFechaRegistro() + "\n"
                                + "Tipo de Cultivo: " + finca.getTipoCultivo();
                        tvInfoFinca.setText(infoFinca);
                        return;
                    }
                }

                tvInfoFinca.setText("Información de la finca no encontrada");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarFincas.this, "Error al consultar finca", Toast.LENGTH_SHORT).show();
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


    private void actualizarInformacion(String nuevoNombre, String nuevoDueno, String nuevaArea, String nuevaFechaRegistro, String nuevoTipoCultivo) {
        String fincaSeleccionada = spConsultaFincaNombre.getSelectedItem().toString();

        fincasRef.orderByChild("nombre").equalTo(fincaSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DatabaseReference fincaRef = snapshot.getRef();


                    fincaRef.child("nombre").setValue(nuevoNombre);
                    fincaRef.child("dueno").setValue(nuevoDueno);
                    fincaRef.child("area").setValue(nuevaArea);
                    fincaRef.child("fechaRegistro").setValue(nuevaFechaRegistro);
                    fincaRef.child("tipoCultivo").setValue(nuevoTipoCultivo);


                    Toast.makeText(ConsultarFincas.this, "Información actualizada correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarFincas.this, "Error al actualizar información", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(ConsultarFincas.this, "Contraseña incorrecta. No se pudo actualizar la finca.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void mostrarDialogoActualizarInformacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Actualizar Información de la Finca");


        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_actualizar, null);
        builder.setView(dialogView);

        final EditText etNuevoNombre = dialogView.findViewById(R.id.etNuevoNombre);
        final EditText etNuevoDueno = dialogView.findViewById(R.id.etNuevoDueno);
        final EditText etNuevaArea = dialogView.findViewById(R.id.etNuevaArea);
        final EditText etNuevaFechaRegistro = dialogView.findViewById(R.id.etNuevaFechaRegistro);
        final EditText etNuevoTipoCultivo = dialogView.findViewById(R.id.etNuevoTipoCultivo);

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nuevoNombre = etNuevoNombre.getText().toString().trim();
                String nuevoDueno = etNuevoDueno.getText().toString().trim();
                String nuevaArea = etNuevaArea.getText().toString().trim();
                String nuevaFechaRegistro = etNuevaFechaRegistro.getText().toString().trim();
                String nuevoTipoCultivo = etNuevoTipoCultivo.getText().toString().trim();

                if (!nuevoNombre.isEmpty() && !nuevoDueno.isEmpty() && !nuevaArea.isEmpty()
                        && !nuevaFechaRegistro.isEmpty() && !nuevoTipoCultivo.isEmpty()) {
                    actualizarInformacion(nuevoNombre, nuevoDueno, nuevaArea, nuevaFechaRegistro, nuevoTipoCultivo);
                } else {
                    Toast.makeText(ConsultarFincas.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
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

                            eliminarFinca();
                        } else {

                            Toast.makeText(ConsultarFincas.this, "Contraseña incorrecta. No se pudo eliminar la finca.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void eliminarFinca() {
        String fincaSeleccionada = spConsultaFincaNombre.getSelectedItem().toString();

        fincasRef.orderByChild("nombre").equalTo(fincaSeleccionada).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    DatabaseReference fincaRef = snapshot.getRef();


                    fincaRef.removeValue();


                    Toast.makeText(ConsultarFincas.this, "Finca eliminada correctamente", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ConsultarFincas.this, "Error al eliminar finca", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


