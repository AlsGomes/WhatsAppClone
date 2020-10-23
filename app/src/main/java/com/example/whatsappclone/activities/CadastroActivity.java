package com.example.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activities.exceptions.ValidationException;
import com.example.whatsappclone.database.FirebaseManager;
import com.example.whatsappclone.model.Usuario;
import com.example.whatsappclone.util.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText txtNome;
    private EditText txtSenha;
    private EditText txtEmail;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControllers();
    }

    private void initControllers() {
        setContentView(R.layout.activity_cadastro);

        txtEmail = findViewById(R.id.txtUltimaMensagem);
        txtSenha = findViewById(R.id.txtSenha);
        txtNome = findViewById(R.id.txtNomeMembro);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateUI();
                    cadastrar();
                } catch (ValidationException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cadastrar() {
        String nome = txtNome.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String senha = txtSenha.getText().toString();
        String id = Base64Custom.encode(email);
        final Usuario usuario = new Usuario(id, nome, email, senha, nome, null);

        FirebaseManager.getFirebaseAuth()
                .createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseManager.getDatabaseReference().child("usuarios").child(usuario.getId()).setValue(usuario)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            try {
                                                throw task.getException();
                                            } catch (FirebaseAuthWeakPasswordException e) {
                                                Toast.makeText(getApplicationContext(), "Senha com menos de 6 caracteres", Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(getApplicationContext(), "Um ou mais parâmetros de autenticação estão inválidos", Toast.LENGTH_LONG).show();
                                            } catch (FirebaseAuthUserCollisionException e) {
                                                Toast.makeText(getApplicationContext(), "Este usuário já existe", Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                Toast.makeText(getApplicationContext(), "Erro ao tentar criar usuário", Toast.LENGTH_LONG).show();
                                                e.printStackTrace();
                                            }
                                        } else {
                                            finish();
                                            Toast.makeText(getApplicationContext(), "Cadastro efetuado com sucesso!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Não foi possível efetuar seu cadastro", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void validateUI() {
        if (txtNome.getText().toString().trim().isEmpty()) {
            throw new ValidationException("Nome vazio");
        }
        if (txtEmail.getText().toString().trim().isEmpty()) {
            throw new ValidationException("E-mail vazio");
        }
        if (txtSenha.getText().toString().isEmpty()) {
            throw new ValidationException("Senha vazia");
        }
    }
}
