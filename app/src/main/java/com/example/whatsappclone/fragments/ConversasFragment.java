package com.example.whatsappclone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.whatsappclone.adapters.ConversasAdapter;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Usuario;
import com.example.whatsappclone.util.Base64Custom;
import com.example.whatsappclone.util.RecyclerItemClickListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversasFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private ConversasAdapter adapter;
    private ChildEventListener childEventListener;
    private DatabaseReference conversasReference;

    public ConversasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);
        initControllers(view);
        return view;
    }

    private void initControllers(View view) {
        recyclerViewConversas = view.findViewById(R.id.recyclerViewConversas);
        recyclerViewConversas.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerViewConversas.addItemDecoration(new DividerItemDecoration(view.getContext(), LinearLayout.VERTICAL));
        recyclerViewConversas.setHasFixedSize(true);

        recyclerViewConversas.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext()
                , recyclerViewConversas
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String idContato = adapter.getConversas().get(position).getIdDestinatario();
                FirebaseManager.getDatabaseReference()
                        .child("usuarios")
                        .child(idContato)
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Usuario contato = snapshot.getValue(Usuario.class);
                                        contato.setId(snapshot.getKey());
                                        Intent intent = new Intent(getContext(), ChatActivity.class);
                                        intent.putExtra("contato", contato);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                }
                        );

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();

        adapter = new ConversasAdapter(this);
        recyclerViewConversas.setAdapter(adapter);

        conversasReference = FirebaseManager.getDatabaseReference()
                .child("conversas")
                .child(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()));

        childEventListener = conversasReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                conversa.setIdDestinatario(snapshot.getKey());
                conversa.setIdRemetente(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()));
                adapter.getConversas().add(conversa);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                conversa.setIdDestinatario(snapshot.getKey());
                conversa.setIdRemetente(Base64Custom.encode(FirebaseManager.getFirebaseAuth().getCurrentUser().getEmail()));
                for (Conversa c : adapter.getConversas()) {
                    if (c.getIdDestinatario().equals(conversa.getIdDestinatario())) {
                        adapter.getConversas().set(adapter.getConversas().indexOf(c), conversa);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
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

    }

    @Override
    public void onStop() {
        super.onStop();
        conversasReference.removeEventListener(childEventListener);
    }

    public void filterConversas(String newText) {
        List<Conversa> conversasFiltradas = new ArrayList<>();

        for (Conversa x : adapter.getConversas()) {
            if (x.getUsuarioExibicao().getNomeExibicao().toLowerCase().contains(newText.toLowerCase())) {
                conversasFiltradas.add(x);
            }
        }

        ConversasAdapter newAdapter = new ConversasAdapter(this);
        recyclerViewConversas.setAdapter(newAdapter);
        newAdapter.getConversas().addAll(conversasFiltradas);
    }
}
