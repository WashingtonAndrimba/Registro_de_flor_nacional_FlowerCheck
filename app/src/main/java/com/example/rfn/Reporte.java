package com.example.rfn;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class Reporte extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private DatosAdapter datosAdapter;
    private List<DatosItem> datosList;
    private Calendar calendar;
    private EditText btnSeleccionarFecha, btnFechaFin;
    private Button btnGeneraReporte;
    private int yearFin, monthFin, dayFin;

    private Calendar calendarInicio;
    private Calendar calendarFin;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reporte);

        btnGeneraReporte = findViewById(R.id.btnGeneraReporte);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btnFechaFin = findViewById(R.id.btnFechaFin);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        datosList = new ArrayList<>();
        datosAdapter = new DatosAdapter(datosList);
        recyclerView.setAdapter(datosAdapter);
        calendarInicio = Calendar.getInstance();
        calendarFin = Calendar.getInstance();


        btnSeleccionarFecha = findViewById(R.id.etFecha);

        calendar = Calendar.getInstance();



        btnSeleccionarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarSelectorFecha();
            }
        });

        btnFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarSelectorFechaFin();
            }
        });

        actualizarDatos();

        btnGeneraReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generarPDF();
            }
        });
    }

    public void GenerarReporte(View view) {
        generarPDF();
    }
    private void generarPDF() {
        Document document = new Document();

        try {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/reporte.pdf";

            // Añade el código para asegurarte de que el directorio existe
            File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

            if (!directory.exists()) {
                directory.mkdirs(); // Crea el directorio si no existe
            }

            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            document.add(new Paragraph("Reporte generado el: " + obtenerFechaActual()));
            document.add(new Paragraph("Fecha de Fin: " + obtenerFechaFin()));
            document.add(new Paragraph("----------------------------------------------------------"));

            for (DatosItem datosItem : datosList) {
                document.add(new Paragraph("Finca: " + datosItem.getFinca()));
                document.add(new Paragraph("Área: " + datosItem.getArea()));
                document.add(new Paragraph("Bloque: " + datosItem.getBloque()));
                document.add(new Paragraph("Variedad: " + datosItem.getVariedad()));
                document.add(new Paragraph("Enfermedad: " + datosItem.getEnfermedad()));
                document.add(new Paragraph("Cantidad: " + datosItem.getCantidad()));
                document.add(new Paragraph("----------------------------------------------------------"));
            }

            document.close();

            Toast.makeText(this, "PDF generado correctamente", Toast.LENGTH_SHORT).show();

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            Log.e("ErrorPDF", "Error al generar el PDF: " + e.getMessage());
            Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String obtenerFechaFin() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar finDia = Calendar.getInstance();
        finDia.set(Calendar.YEAR, yearFin);
        finDia.set(Calendar.MONTH, monthFin);
        finDia.set(Calendar.DAY_OF_MONTH, dayFin);
        finDia.set(Calendar.HOUR_OF_DAY, 23);
        finDia.set(Calendar.MINUTE, 59);
        finDia.set(Calendar.SECOND, 59);
        return sdf.format(finDia.getTime());
    }

    private void mostrarSelectorFecha() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarInicio.set(Calendar.YEAR, year);
                calendarInicio.set(Calendar.MONTH, monthOfYear);
                calendarInicio.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                actualizarFechaSeleccionada();
                actualizarDatos();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendarInicio.get(Calendar.YEAR),
                calendarInicio.get(Calendar.MONTH),
                calendarInicio.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void actualizarFechaSeleccionada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaSeleccionada = sdf.format(calendarInicio.getTime());
        btnSeleccionarFecha.setText(fechaSeleccionada);
    }

    private void mostrarSelectorFechaFin() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendarFin.set(Calendar.YEAR, year);
                calendarFin.set(Calendar.MONTH, monthOfYear);
                calendarFin.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                yearFin = year;
                monthFin = monthOfYear;
                dayFin = dayOfMonth;

                actualizarFechaSeleccionadaFin();
                actualizarDatos();
            }
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendarFin.get(Calendar.YEAR),
                calendarFin.get(Calendar.MONTH),
                calendarFin.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }







    private void actualizarFechaSeleccionadaFin() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaSeleccionada = sdf.format(calendarFin.getTime());
        btnFechaFin.setText(fechaSeleccionada);
    }

    private void actualizarDatos() {
        SimpleDateFormat sdfFirebase = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaInicioFirebase = sdfFirebase.format(calendarInicio.getTime());
        String fechaFinFirebase = sdfFirebase.format(calendarFin.getTime());

        Calendar inicioDia = Calendar.getInstance();
        inicioDia.setTime(calendarInicio.getTime());
        inicioDia.set(Calendar.HOUR_OF_DAY, 0);
        inicioDia.set(Calendar.MINUTE, 0);
        inicioDia.set(Calendar.SECOND, 0);

        Calendar finDia = Calendar.getInstance();
        finDia.set(Calendar.YEAR, yearFin);
        finDia.set(Calendar.MONTH, monthFin);
        finDia.set(Calendar.DAY_OF_MONTH, dayFin);
        finDia.set(Calendar.HOUR_OF_DAY, 23);
        finDia.set(Calendar.MINUTE, 59);
        finDia.set(Calendar.SECOND, 59);

        DatabaseReference registrosRef = mDatabase.child("registros");
        String fechaInicioFormatted = sdfFirebase.format(inicioDia.getTime());
        String fechaFinFormatted = sdfFirebase.format(finDia.getTime());

        registrosRef.orderByChild("fecha")
                .startAt(fechaInicioFormatted)
                .endAt(fechaFinFormatted)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        datosList.clear();

                        for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                            String finca = String.valueOf(registroSnapshot.child("finca").getValue());
                            String area = String.valueOf(registroSnapshot.child("area").getValue());
                            String bloque = String.valueOf(registroSnapshot.child("bloque").getValue());
                            String variedad = String.valueOf(registroSnapshot.child("variedad").getValue());
                            String enfermedad = String.valueOf(registroSnapshot.child("enfermedad").getValue());
                            String cantidad = String.valueOf(registroSnapshot.child("cantidad").getValue());

                            datosList.add(new DatosItem(finca, area, bloque, variedad, enfermedad, cantidad));
                        }
                        datosAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Manejar errores de base de datos si es necesario
                    }
                });
    }


    private static class DatosAdapter extends RecyclerView.Adapter<DatosAdapter.DatosViewHolder> {
        private final List<DatosItem> datosList;

        public DatosAdapter(List<DatosItem> datosList) {
            this.datosList = datosList;
        }

        @NonNull
        @Override
        public DatosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dato, parent, false);
            return new DatosViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DatosViewHolder holder, int position) {
            DatosItem datosItem = datosList.get(position);
            holder.bind(datosItem);
        }

        @Override
        public int getItemCount() {
            return datosList.size();
        }

        public static class DatosViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvFinca;
            private final TextView tvArea;
            private final TextView tvBloque;
            private final TextView tvVariedad;
            private final TextView tvEnfermedad;
            private final TextView tvCantidad;

            public DatosViewHolder(@NonNull View itemView) {
                super(itemView);
                tvFinca = itemView.findViewById(R.id.tvFinca);
                tvArea = itemView.findViewById(R.id.tvArea);
                tvBloque = itemView.findViewById(R.id.tvBloque);
                tvVariedad = itemView.findViewById(R.id.tvVariedad);
                tvEnfermedad = itemView.findViewById(R.id.tvEnfermedad);
                tvCantidad = itemView.findViewById(R.id.tvCantidad);
            }

            public void bind(DatosItem datosItem) {
                tvFinca.setText(datosItem.getFinca());
                tvArea.setText(datosItem.getArea());
                tvBloque.setText(datosItem.getBloque());
                tvVariedad.setText(datosItem.getVariedad());
                tvEnfermedad.setText(datosItem.getEnfermedad());
                tvCantidad.setText(datosItem.getCantidad());
            }
        }
    }

    private static class DatosItem {
        private final String finca;
        private final String area;
        private final String bloque;
        private final String variedad;
        private final String enfermedad;
        private final String cantidad;

        public DatosItem(String finca, String area, String bloque, String variedad, String enfermedad, String cantidad) {
            this.finca = finca;
            this.area = area;
            this.bloque = bloque;
            this.variedad = variedad;
            this.enfermedad = enfermedad;
            this.cantidad = cantidad;
        }

        public String getFinca() {
            return finca;
        }

        public String getArea() {
            return area;
        }

        public String getBloque() {
            return bloque;
        }

        public String getVariedad() {
            return variedad;
        }

        public String getEnfermedad() {
            return enfermedad;
        }

        public String getCantidad() {
            return cantidad;
        }
    }

}
