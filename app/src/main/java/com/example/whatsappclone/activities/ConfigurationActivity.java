package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.activities.exceptions.ValidationException;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Usuario;
import com.example.whatsappclone.util.Permission;
import com.example.whatsappclone.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigurationActivity extends AppCompatActivity {

    public static final String[] NECESSARY_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    private ImageView btnEdit;
    private EditText txtNomeExibicao;
    private CircleImageView imagePerfil;
    private ImageButton btnCamera;
    private ImageButton btnGaleria;
    public static final int SELECAO_CAMERA = 100;
    public static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permission.checkPermission(NECESSARY_PERMISSIONS, this, 1);
        initControllers();
    }

    private void initControllers() {
        setContentView(R.layout.activity_configuration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfil = findViewById(R.id.imagePerfil);

        FirebaseManager.getDatabaseReference()
                .child(FirebaseManager.DATABASE_USUARIO_USING_EMAIL_ENCODED).child("foto")
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    String uri = snapshot.getValue(String.class);
                                    Glide.with(ConfigurationActivity.this).load(uri).into(imagePerfil);
                                } catch (Exception e) {
                                    imagePerfil.setImageResource(R.drawable.padrao);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );

        txtNomeExibicao = findViewById(R.id.txtNomeExibicao);
        FirebaseManager.getDatabaseReference()
                .child(FirebaseManager.DATABASE_USUARIO_USING_EMAIL_ENCODED)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Usuario usuario = (Usuario) snapshot.getValue(Usuario.class);
                        txtNomeExibicao.setText(usuario.getNomeExibicao());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btnGaleria = findViewById(R.id.btnGaleria);
        btnCamera = findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });

        btnEdit = findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String nomeExibicao = txtNomeExibicao.getText().toString().trim();
                    FirebaseManager.getDatabaseReference()
                            .child(FirebaseManager.DATABASE_USUARIO_USING_EMAIL_ENCODED)
                            .child("nomeExibicao").setValue(nomeExibicao)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Nome alterado com sucesso", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "O nome não pôde ser alterado", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            });
                } catch (ValidationException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        break;
                }

                if (image == null) {
                    throw new NullPointerException("Bitmap está nulo");
                }

                imagePerfil.setImageBitmap(image);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();

                UploadTask uploadTask = FirebaseManager.getStorageReference().child(FirebaseManager.STORAGE_PERFIL_FOLDER_USING_EMAIL_ENCODED).putBytes(imageBytes);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Erro ao tentar fazer upload da foto", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        FirebaseManager.getStorageReference()
                                .child(FirebaseManager.STORAGE_PERFIL_FOLDER_USING_EMAIL_ENCODED)
                                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseManager.getDatabaseReference().child(FirebaseManager.DATABASE_USUARIO_USING_EMAIL_ENCODED).child("foto").setValue(uri.toString());
                                Toast.makeText(getApplicationContext(), "Foto alterada com sucesso", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Imagem não encontrada", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Erro ao recuperar imagem", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int granted : grantResults) {
            if (granted == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                alertDialog.setTitle("Atenção");
                alertDialog.setMessage("Não permitindo o acesso do aplicativo à câmera e à sua galeria, não será possível trocar sua foto de perfil." +
                        "\nPermita entrando novamente nesta tela para proseeguir.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.create();
                alertDialog.show();
            }
        }
    }

    private boolean validateUI() {
        if (txtNomeExibicao.getText().toString().trim().isEmpty()) {
            throw new ValidationException("Nome de exibição está vazio");
        }

        return true;
    }
}
