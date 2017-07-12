package com.example.kubra.chatapp;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
public class ChatYapActivity extends AppCompatActivity {
    TextView tvBaslik;
    EditText et_mesaj;
    Button buttonGonder;
    ListView lv_chatyap;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_yap);

        tvBaslik= (TextView) findViewById(R.id.textViewBaslik);
        et_mesaj= (EditText) findViewById(R.id.editTextMesaj);
        buttonGonder= (Button) findViewById(R.id.buttonMesajGonder);
        lv_chatyap= (ListView) findViewById(R.id.listViewChatYap);

        String oda= getIntent().getStringExtra("odaKey");
        tvBaslik.setText(oda);

        final ArrayList<Mesaj> mesajList=new ArrayList<Mesaj>();

        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        database=FirebaseDatabase.getInstance();
        final DatabaseReference dbRef=database.getReference("chats/"+oda);

        buttonGonder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String gonderen=firebaseUser.getEmail();
                String mesaj=et_mesaj.getText().toString();
                SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:dd");
                String zaman=sdf.format(new Date());

                dbRef.push().setValue(new Mesaj(gonderen,mesaj,zaman));
                et_mesaj.setText("");
            }
        });

        final CustomAdapter adapter=new CustomAdapter(this,mesajList,firebaseUser);
        dbRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        mesajList.clear();
        for(DataSnapshot ds:dataSnapshot.getChildren()){
            mesajList.add(ds.getValue(Mesaj.class));
        }
        lv_chatyap.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});

    }
}
