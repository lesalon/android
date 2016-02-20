package com.lesalon.gallery;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.lesalon.R;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        TableLayout table = new TableLayout(this);
        for (int i = 0; i < mRows; i++) {

            TableRow tr = new TableRow(mContext);

            for (int j = 0; j < mCols; j++) {

                ImageView view = new ImageView(this);
                view.setImageResource(R.drawable.star_on);
                tr.addView(view);
            }
            table.addView(tr);
        }


    }
}
