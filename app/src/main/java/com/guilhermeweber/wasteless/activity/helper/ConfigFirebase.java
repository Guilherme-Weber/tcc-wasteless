package com.guilhermeweber.wasteless.activity.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {

    private static DatabaseReference refFirebase;
    private static FirebaseAuth refAuth;
    private static StorageReference refStorage;

    //retorna a referecia do FirebaseDatabase
    public static DatabaseReference getFirebase() {
        if (refFirebase == null) {
            refFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return refFirebase;
    }

    //retorna a instacia do Firebase Auth
    public static FirebaseAuth getFireAuth() {
        if (refAuth == null) {
            refAuth = FirebaseAuth.getInstance();
        }
        return refAuth;
    }

    //Intancia do FirebaseStorage
    public static StorageReference getRefStorage() {
        if (refStorage == null) {
            refStorage = FirebaseStorage.getInstance().getReference();
        }
        return refStorage;
    }

}
