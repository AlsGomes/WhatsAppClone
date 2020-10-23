package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapters.ContatosAdapter;
import com.example.whatsappclone.adapters.MembroGrupoAdapter;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Usuario;
import com.example.whatsappclone.util.RecyclerItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GrupoActivity extends AppCompatActivity {

    private ValueEventListener valueEventListener;
    private DatabaseReference usuariosRef;
    private ContatosAdapter adapterContatos;
    private MembroGrupoAdapter adapterMembros;
    private RecyclerView recyclerViewContatos;
    private RecyclerView recyclerViewMembros;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewContatos = findViewById(R.id.recyclerViewContatos);
        recyclerViewContatos.setHasFixedSize(true);
        recyclerViewContatos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewContatos.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        adapterContatos = new ContatosAdapter(this);
        recyclerViewContatos.setAdapter(adapterContatos);

        recyclerViewContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext()
                        , recyclerViewContatos
                        , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario newMember = adapterContatos.getContatos().get(position);

                        adapterMembros.getMembros().add(newMember);
                        adapterMembros.notifyDataSetChanged();

                        adapterContatos.getContatos().remove(newMember);
                        adapterContatos.notifyDataSetChanged();

                        updateToolbar();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        recyclerViewMembros = findViewById(R.id.recyclerViewMembros);
        recyclerViewMembros.setHasFixedSize(true);

        LinearLayoutManager layoutHorizontal = new LinearLayoutManager(getApplicationContext());
        layoutHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewMembros.setLayoutManager(layoutHorizontal);

        recyclerViewMembros.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
        adapterMembros = new MembroGrupoAdapter();
        recyclerViewMembros.setAdapter(adapterMembros);

        recyclerViewMembros.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext()
                        , recyclerViewMembros
                        , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario contato = adapterMembros.getMembros().get(position);

                        adapterContatos.getContatos().add(contato);
                        adapterContatos.notifyDataSetChanged();

                        adapterMembros.getMembros().remove(contato);
                        adapterMembros.notifyDataSetChanged();

                        updateToolbar();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterMembros.getMembros().size() >= 2) {
                    Intent intent = new Intent(getApplicationContext(), CadastroGrupoActivity.class);
                    intent.putExtra("membros", (Serializable) adapterMembros.getMembros());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Só é possível criar um grupo se tiver pelo menos 3 pessoas", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateToolbar() {
        int numMembros = adapterMembros.getMembros().size();
        int numContatos = adapterContatos.getContatos().size() + numMembros;

        getSupportActionBar().setSubtitle(numMembros + " de " + numContatos + (numContatos > 1 ? " selecionados" : " selecionado"));
    }


    @Override
    public void onStart() {
        super.onStart();
        loadContatos();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuariosRef.removeEventListener(valueEventListener);
    }

    private void loadContatos() {
        usuariosRef = FirebaseManager.getDatabaseReference().child("usuarios");
        valueEventListener = usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Usuario> listContatos = new ArrayList<>();
                for (DataSnapshot ss : snapshot.getChildren()) {
                    Usuario usuario = ss.getValue(Usuario.class);
                    usuario.setId(ss.getKey());
                    if (!usuario.getEmail().equals(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail())) {
                        listContatos.add(usuario);
                    }
                }
                adapterContatos.getContatos().clear();
                adapterContatos.getContatos().addAll(listContatos);
                adapterContatos.notifyDataSetChanged();

                adapterMembros.getMembros().clear();
                adapterMembros.notifyDataSetChanged();

                updateToolbar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
