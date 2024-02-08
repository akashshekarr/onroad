package com.example.login_register;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class HomePage extends AppCompatActivity {
    Button user, business ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        user = (Button) findViewById(R.id.user);
        business = (Button) findViewById(R.id.business);
        user.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(HomePage.this, LoginUser.class);
                startActivity(i);
            }
        });
        business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, LoginBusi.class);
                startActivity(i);
            }
        });
    }
}