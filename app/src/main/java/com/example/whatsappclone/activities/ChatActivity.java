package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.exceptions.ValidationException;
import com.example.whatsappclone.adapters.MensagensAdapter;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Mensagem;
import com.example.whatsappclone.model.Usuario;
import com.example.whatsappclone.util.Base64Custom;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView imagePerfil;
    private RecyclerView recyclerViewChat;
    private EditText txtMensagem;
    private ImageButton btnAnexo;
    private FloatingActionButton fabSend;
    private TextView txtNome;

    private MensagensAdapter adapter;

    private Usuario contato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControllers();
    }

    private void initControllers() {
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfil = findViewById(R.id.imagePerfil);
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        txtMensagem = findViewById(R.id.txtMensagem);
        btnAnexo = findViewById(R.id.btnAnexo);
        fabSend = findViewById(R.id.fabSend);
        txtNome = findViewById(R.id.txtNomeMembro);

        if (getIntent().getExtras() != null) {
            contato = (Usuario) getIntent().getExtras().getSerializable("contato");
            txtNome.setText(contato.getNome());
            if (contato.getFoto() != null) {
                Glide.with(this).load(contato.getFoto()).into(imagePerfil);
            }
        } else {
            imagePerfil.setImageResource(R.drawable.padrao);
        }

        fabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateUI();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setId(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()));
                    mensagem.setMensagem(txtMensagem.getText().toString().trim());
                    uploadMessage(mensagem);

                    txtMensagem.getText().clear();
                } catch (ValidationException e) {
                    Toast.makeText(getApplicationContext(), "Não há mensagem", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        adapter = new MensagensAdapter(this);
        recyclerViewChat.setAdapter(adapter);

        FirebaseManager.getDatabaseReference()
                .child("mensagens")
                .child(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()))
                .child(contato.getId())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Mensagem mensagem = snapshot.getValue(Mensagem.class);
                        adapter.getMensagens().add(mensagem);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        btnAnexo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, ConfigurationActivity.SELECAO_GALERIA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode != ConfigurationActivity.SELECAO_GALERIA) {
                throw new IllegalArgumentException("requestCode não encontrado");
            }

            Bitmap imagem = null;

            try {
                imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imagemBytes = baos.toByteArray();

                final String id = UUID.randomUUID().toString();
                final StorageReference storage =
                        FirebaseManager.getStorageReference()
                                .child(FirebaseManager.STORAGE_IMAGES_FOLDER_USING_EMAIL_ENCODED)
                                .child(id + ".jpeg");

                storage.putBytes(imagemBytes)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Erro ao tentar fazer upload da foto", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                FirebaseManager.getStorageReference()
                                        .child(FirebaseManager.STORAGE_IMAGES_FOLDER_USING_EMAIL_ENCODED)
                                        .child(id + ".jpeg").getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Mensagem mensagem = new Mensagem();
                                                mensagem.setId(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()));
                                                mensagem.setImagem(uri.toString());
                                                uploadMessage(mensagem);
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

    private void uploadMessage(Mensagem mensagem) {
        FirebaseManager.getDatabaseReference()
                .child("mensagens")
                .child(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()))
                .child(contato.getId())
                .push()
                .setValue(mensagem);

        Conversa conversa = new Conversa();
        conversa.setIdRemetente(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()));
        conversa.setIdDestinatario(contato.getId());
        conversa.setUltimaMensagem(mensagem.getMensagem() == null ? "imagem" : mensagem.getMensagem());
        Usuario usuarioExibicao = new Usuario();
        usuarioExibicao.setEmail(contato.getEmail());
        usuarioExibicao.setNomeExibicao(contato.getNomeExibicao());
        usuarioExibicao.setFoto(contato.getFoto());
        conversa.setUsuarioExibicao(usuarioExibicao);
        FirebaseManager.getDatabaseReference()
                .child("conversas")
                .child(conversa.getIdRemetente())
                .child(conversa.getIdDestinatario())
                .setValue(conversa);

        FirebaseManager.getDatabaseReference()
                .child("mensagens")
                .child(contato.getId())
                .child(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()))
                .push()
                .setValue(mensagem);
    }

    private void validateUI() {
        if (txtMensagem.getText().toString().trim().isEmpty()) {
            throw new ValidationException("Mensagem vazia");
        }
    }
}
