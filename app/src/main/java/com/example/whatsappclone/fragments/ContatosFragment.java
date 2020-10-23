package com.example.whatsappclone.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.ChatActivity;
import com.example.whatsappclone.activities.GrupoActivity;
import com.example.whatsappclone.adapters.ContatosAdapter;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Usuario;
import com.example.whatsappclone.util.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContatosFragment extends Fragment {

    private RecyclerView recyclerViewContatos;
    private ContatosAdapter adapter;
    private ValueEventListener valueEventListener;
    private DatabaseReference usuariosRef;
    private LinearLayout btnNovoGrupo;

    public ContatosFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        btnNovoGrupo = view.findViewById(R.id.btnNovoGrupo);
        btnNovoGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), GrupoActivity.class));
            }
        });

        recyclerViewContatos = view.findViewById(R.id.recyclerViewContatos);
        recyclerViewContatos.setHasFixedSize(true);
        recyclerViewContatos.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewContatos.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayoutManager.VERTICAL));
        adapter = new ContatosAdapter(this);
        recyclerViewContatos.setAdapter(adapter);

        recyclerViewContatos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext()
                        , recyclerViewContatos
                        , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("contato", adapter.getContatos().get(position));
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        return view;
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
                adapter.getContatos().clear();
                adapter.getContatos().addAll(listContatos);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
