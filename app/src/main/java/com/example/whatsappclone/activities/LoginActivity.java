package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.exceptions.ValidationException;
import com.example.whatsappclone.database.FirebaseManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText txtEmail;
    private EditText txtSenha;
    private Button btnLogar;
    private TextView txtCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseManager.getFirebaseAuth().signOut();
        initControllers();
    }

    private void initControllers() {
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.txtUltimaMensagem);
        txtSenha = findViewById(R.id.txtSenha);
        btnLogar = findViewById(R.id.btnCadastrar);
        txtCadastro = findViewById(R.id.txtCadastro);

        txtCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CadastroActivity.class));
            }
        });

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        try {
            validateUI();
            String email = txtEmail.getText().toString().trim();
            String senha = txtSenha.getText().toString();

            FirebaseManager.getFirebaseAuth()
                    .signInWithEmailAndPassword(email, senha)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startHomeActivity();
                            Toast.makeText(getApplicationContext(), "Bem vindo!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            try {
                                throw e;
                            } catch (FirebaseAuthInvalidCredentialsException ex) {
                                Toast.makeText(getApplicationContext(), "Um ou mais parâmetros de autenticação estão inválidos", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidUserException ex) {
                                Toast.makeText(getApplicationContext(), "Este usuário não existe", Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), "Erro ao tentar se autenticar", Toast.LENGTH_LONG).show();
                                ex.printStackTrace();
                            }
                        }
                    });
        } catch (ValidationException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void validateUI() {
        if (txtEmail.getText().toString().trim().isEmpty()) {
            throw new ValidationException("E-mail vazio");
        }
        if (txtSenha.getText().toString().trim().isEmpty()) {
            throw new ValidationException("Senha vazia");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseManager.getFirebaseAuth().getCurrentUser() != null) {
            startHomeActivity();
        }
    }

    private void startHomeActivity() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}
