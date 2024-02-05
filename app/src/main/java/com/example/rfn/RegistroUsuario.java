package com.example.rfn;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegistroUsuario extends AppCompatActivity {

    private EditText etRegistroUsuario, editTextText2, editTextText;
    private Spinner spRegistroRolUsuario;
    private Button btnRegistrarNuevoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registronuevousuario);

        etRegistroUsuario = findViewById(R.id.etRegistroUsuario);
        editTextText2 = findViewById(R.id.editTextText2);
        editTextText = findViewById(R.id.editTextText);
        spRegistroRolUsuario = findViewById(R.id.spRegistroRolUsuario);
        btnRegistrarNuevoUsuario = findViewById(R.id.btnRegistrarNuevoUsuario);


        String[] roles = {"Administrador", "Visualizador"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRegistroRolUsuario.setAdapter(adapter);

        spRegistroRolUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnRegistrarNuevoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String correo = etRegistroUsuario.getText().toString().trim();
                String contraseña = editTextText2.getText().toString().trim();
                String confirmarContraseña = editTextText.getText().toString().trim();


                if (!contraseña.equals(confirmarContraseña)) {
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }


                String rolSeleccionado = spRegistroRolUsuario.getSelectedItem().toString();


                if (rolSeleccionado.equals("Administrador")) {

                    registrarUsuarioFirebaseAuth(correo, contraseña);
                } else if (rolSeleccionado.equals("Visualizador")) {

                    registrarUsuarioBaseDatos(correo, rolSeleccionado);
                }
            }
        });
    }

    private void registrarUsuarioFirebaseAuth(String correo, String contraseña) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Usuario registrado como administrador", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(getApplicationContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registrarUsuarioBaseDatos(String correo, String rol) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Usuario usuario = new Usuario(correo, rol);

        db.collection("usuarios")
                .add(usuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Usuario registrado como visualizador", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
