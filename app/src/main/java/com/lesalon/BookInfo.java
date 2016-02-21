package com.lesalon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lesalon.chat.ChatActivity;

/**
 * Created by User on 2/20/2016.
 */
public class BookInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_info);

        TextView t1,t2,t3,t4;

        t1= (TextView) findViewById(R.id.chat);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(i);
            }
        });

        t2= (TextView) findViewById(R.id.reviews);
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), Reviews.class);
                startActivity(i);
            }
        });

        t3= (TextView) findViewById(R.id.authors_content);
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), AuthorSpace.class);
                startActivity(i);
            }
        });

        t4= (TextView) findViewById(R.id.fan_club);
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FanFiction.class);
                startActivity(i);

            }
        });
    }

}
