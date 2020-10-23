package com.example.whatsappclone.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.exceptions.ValidationException;
import com.example.whatsappclone.adapters.MembroGrupoAdapter;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Usuario;
import com.example.whatsappclone.util.Base64Custom;
import com.example.whatsappclone.util.Permission;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CadastroGrupoActivity extends AppCompatActivity {

    private CircleImageView imageGrupo;
    private EditText txtNomeGrupo;
    private TextView txtParticipantes;
    private RecyclerView recyclerViewMembros;
    private FloatingActionButton fab;
    private MembroGrupoAdapter adapterMembros;
    private byte[] fotoGrupo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initControllers();
    }

    private void initControllers() {
        setContentView(R.layout.activity_cadastro_grupo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        toolbar.setSubtitle("Adicionar Nome");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtNomeGrupo = findViewById(R.id.txtNomeGrupo);
        txtParticipantes = findViewById(R.id.txtParticipantes);

        recyclerViewMembros = findViewById(R.id.recyclerViewMembros);
        recyclerViewMembros.setHasFixedSize(true);
        LinearLayoutManager layoutHorizontal = new LinearLayoutManager(getApplicationContext());
        layoutHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMembros.setLayoutManager(layoutHorizontal);
        recyclerViewMembros.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
        adapterMembros = new MembroGrupoAdapter();
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("membros")) {
            adapterMembros.getMembros().addAll((List<Usuario>) getIntent().getExtras().getSerializable("membros"));
            adapterMembros.notifyDataSetChanged();
        }
        recyclerViewMembros.setAdapter(adapterMembros);
        txtParticipantes.setText(String.format("Participantes: %s", adapterMembros.getMembros().size()));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateUI();
                    createGroup();
                } catch (ValidationException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        imageGrupo = findViewById(R.id.imageGrupo);
        imageGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Permission.checkPermission(ConfigurationActivity.NECESSARY_PERMISSIONS, CadastroGrupoActivity.this, 1);

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, ConfigurationActivity.SELECAO_GALERIA);
                }
            }
        });
    }

    private void createGroup() {
        final Grupo grupo = new Grupo();
        grupo.setNome(txtNomeGrupo.getText().toString().trim());
        for (Usuario u : adapterMembros.getMembros()) {
            grupo.getMembrosId().add(u.getId());
        }
        grupo.getMembrosId().add(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()));
        grupo.setId(FirebaseManager.getDatabaseReference().child("grupos").push().getKey());
        final DatabaseReference grupoReference = FirebaseManager.getDatabaseReference().child("grupos").child(grupo.getId());
        grupoReference.setValue(grupo);
        if (fotoGrupo != null) {
            FirebaseManager.getStorageReference().child("grupos").child(grupo.getId()).child("fotoGrupo").child("fotoGrupo.jpeg").putBytes(fotoGrupo)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "O grupo foi criado, mas a foto não pôde ser salva", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            FirebaseManager.getStorageReference().child("grupos").child(grupo.getId()).child("fotoGrupo").child("fotoGrupo.jpeg")
                                    .getDownloadUrl()
                                    .addOnSuccessListener(
                                            new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    grupoReference.child("foto").setValue(uri.toString());
                                                    finish();
                                                }
                                            }
                                    );
                        }
                    });
        } else {
            finish();
        }

    }

    private void validateUI() {
        if (txtNomeGrupo.getText().toString().trim().isEmpty()) {
            throw new ValidationException("Nome do grupo está vazio");
        }

        if (adapterMembros.getMembros().isEmpty()) {
            throw new ValidationException("A lista de membros está vazia");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int granted : grantResults) {
            if (granted == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
                alertDialog.setTitle("Atenção");
                alertDialog.setMessage("Não permitindo o acesso do aplicativo à câmera e à sua galeria, não será possível trocar a foto do grupo." +
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case ConfigurationActivity.SELECAO_GALERIA:
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                        break;
                }

                if (image == null) {
                    throw new NullPointerException("Bitmap está nulo");
                }

                imageGrupo.setImageBitmap(image);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                fotoGrupo = baos.toByteArray();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Imagem não encontrada", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Erro ao recuperar imagem", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
