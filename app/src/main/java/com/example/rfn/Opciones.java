package com.example.rfn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Opciones extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opciones);
        drawerLayout = findViewById(R.id.drawerLayout);
        firebaseAuth = FirebaseAuth.getInstance();

        Button btnRegistro = findViewById(R.id.btnRegistro);
        Button btnConsultarActualizar = findViewById(R.id.btnConsultarActualizar);
        Button btnReporte = findViewById(R.id.btnReporte);
        Button btnConsultarFincasPorNombre = findViewById(R.id.btnConsultarFincasPorNombre);
        Button btnConsultarVariedadPorNombre = findViewById(R.id.btnConsultarVariedadPorNombre);
        Button btnConsultarEnfermedadPorNombre = findViewById(R.id.btnConsultarEnfermedadPorNombre);
        Button btnVenderComprar = findViewById(R.id.btnVenderComprar);

        btnConsultarFincasPorNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, ConsultarFincas.class);
                startActivity(intent);
            }
        });

        btnConsultarEnfermedadPorNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, ConsultarEnfermedad.class);
                startActivity(intent);
            }
        });
        btnConsultarVariedadPorNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, ConsultarVariedad.class);
                startActivity(intent);
            }
        });


        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, Registro.class);
                startActivity(intent);
            }
        });

        btnConsultarActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, Consultar.class);
                startActivity(intent);
            }
        });

        btnReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, Reporte.class);
                startActivity(intent);
            }
        });

        btnVenderComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Opciones.this, VenderComprarVariedad.class);
                startActivity(intent);
            }
        });
    }

    public void mostrarMenu(View view) {
        realizarAccionEspecifica("Menú");
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void accionOpcion1(View view) {
        mostrarDialogoContraseña("Registro Finca", "btnOpcion1");
    }

    public void accionOpcion2(View view) {
        mostrarDialogoContraseña("Registro Variedad", "btnOpcion2");
    }

    public void accionOpcion3(View view) {
        mostrarDialogoContraseña("Registro Enfermedad", "btnOpcion3");
    }
    public void accionOpcion4(View view) {
        mostrarDialogoContraseña("Registro Usuario", "btnOpcion4");
    }

    private void realizarAccionEspecifica(String opcion) {
        if (verificarUsuarioAutenticado()) {
            abrirActividadEspecifica(opcion);
        }
    }

    private boolean verificarUsuarioAutenticado() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            return true;
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void verificarContraseña(String contraseñaIngresada, final String opcion, final String opcionBtn) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();

            firebaseAuth.signInWithEmailAndPassword(email, contraseñaIngresada)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                abrirActividadEspecifica(opcionBtn);
                            } else {
                                Toast.makeText(Opciones.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarOpciones() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void mostrarDialogoContraseña(final String opcion, final String opcionBtn) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialogo_password, null);
        builder.setView(dialogView);

        final EditText editTextContraseña = dialogView.findViewById(R.id.editTextContraseña);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = obtenerEmailDelUsuario();
                String contraseñaIngresada = editTextContraseña.getText().toString();
                verificarContraseña(contraseñaIngresada, opcion, opcionBtn);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private String obtenerEmailDelUsuario() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return user.getEmail();
        }
        return "";
    }

    private void abrirActividadEspecifica(String opcion) {

        Intent intent = null;

        switch (opcion) {
            case "btnOpcion1":
                intent = new Intent(Opciones.this, RegistroFinca.class);
                break;
            case "btnOpcion2":
                intent = new Intent(Opciones.this, RegistroVariedadNueva.class);
                break;
            case "btnOpcion3":
                intent = new Intent(Opciones.this, RegistroEnfermedad.class);
                break;
            case "btnOpcion4":
                intent = new Intent(Opciones.this, RegistroUsuario.class);
                break;

        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
