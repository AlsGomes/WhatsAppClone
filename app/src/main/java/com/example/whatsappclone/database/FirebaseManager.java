package com.example.whatsappclone.database;

import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseManager {

    public static final String STORAGE_PERFIL_FOLDER_USING_EMAIL_ENCODED = "usuarios/" + encode(getFirebaseAuth().getCurrentUser().getEmail()) + "/perfil/perfil.jpeg";
    public static final String STORAGE_IMAGES_FOLDER_USING_EMAIL_ENCODED = "usuarios/" + encode(getFirebaseAuth().getCurrentUser().getEmail()) + "/imagens/";
    public static final String STORAGE_PERFIL_FOLDER_USING_UID = "usuarios/" + getFirebaseAuth().getCurrentUser().getUid() + "/perfil/perfil.jpeg";

    public static final String DATABASE_USUARIO_USING_EMAIL_ENCODED = "usuarios/" + encode(getFirebaseAuth().getCurrentUser().getEmail());

    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference databaseReference;
    private static StorageReference storageReference;

    public static FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }

        return firebaseAuth;
    }

    public static DatabaseReference getDatabaseReference() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        }

        return databaseReference;
    }

    public static StorageReference getStorageReference() {
        if (storageReference == null) {
            storageReference = FirebaseStorage.getInstance().getReference();
        }

        return storageReference;
    }

    private static String encode(String toEncode) {
        String encoded = Base64.encodeToString(toEncode.getBytes(), Base64.DEFAULT).replaceAll("\\n|\\r", "");
        return encoded;
    }
}
