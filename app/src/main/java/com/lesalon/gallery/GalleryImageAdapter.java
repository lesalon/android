package com.lesalon.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesalon.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kanishk on 2/21/16.
 */
public class GalleryImageAdapter extends BaseAdapter {

    private List<GridCell> gridCell;

    private Context context;

    public GalleryImageAdapter(Context context, List<GridCell> gridCells) {
        this.gridCell = gridCells;
        this.context = context;
    }

    @Override
    public int getCount() {
        return gridCell.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            convertView = inflater.inflate(R.layout.genre_grid, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        GridCell currentListData = gridCell.get(position);

        mViewHolder.text.setText(currentListData.text);
        mViewHolder.img.setImageResource(currentListData.imgSrc);
        return convertView;
    }

    private class MyViewHolder {
        TextView text;
        ImageView img;

        public MyViewHolder(View item) {
            text =  (TextView) item.findViewById(R.id.text);
            img =  (ImageView) item.findViewById(R.id.img);
        }
    }

    public static class GridCell {
        int imgSrc;
        String text;

        public GridCell(int imageResource, String imgText) {
            text = imgText;
            imgSrc = imageResource;
        }
    }
}

    /*public void onClick(View v) {

        switch(v.getId()) {

            case R.id.chat_user:

                String chatUser = mEnterChat.getText().toString();

                if(chatUser.length() > 0) {

                    GoogleSignInAccount acct = result.getSignInAccount();

                    Intent chatIntent = ChatActivity.getIntent(this,

                            acct.getEmail(), chatUser, acct.getDisplayName());

                    startActivity(chatIntent);

                }

                break;

            case R.id.gen_scifi:

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(GalleryActivity.this);

                builderSingle.setTitle("Select Book");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(

                        GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                arrayAdapter.add("Dune - Frank Herert");

                arrayAdapter.add("The Book of the New Sun - Gene Wolfe");

                arrayAdapter.add("The Moon is a Harsh Mistress - Robert A. Heinlein");

                arrayAdapter.add("The Dispossessed - Ursula K. Le Guin");

                arrayAdapter.add("Hyperion - Dan Simmons");

                builderSingle .setAdapter(

                        arrayAdapter,

                        new DialogInterface.OnClickListener(){

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                                String strName = arrayAdapter.getItem(which);

                                Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                i.putExtra(strName, 10);

                                startActivity(i);

                            }

                        }

                );

                break;

            case R.id.gen_comics:

                AlertDialog.Builder builderSingle1 = new AlertDialog.Builder(GalleryActivity.this);

                builderSingle1.setTitle("Select Book");

                final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(

                        GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                arrayAdapter1.add("Maus - Art Spiegelman");

                arrayAdapter1.add("The Dark Night Returns - Frank Miller");

                arrayAdapter1.add("V for Vendetta - Alan Moore");

                arrayAdapter1.add("Sin City - Frank Miller");

                arrayAdapter1.add("Ghost World - Dan Clowes");

                builderSingle1 .setAdapter(

                        arrayAdapter1,

                        new DialogInterface.OnClickListener(){

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                                String strName = arrayAdapter1.getItem(which);

                                Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                i.putExtra(strName, 10);

                                startActivity(i);

                            }

                        }

                );

                break;

            case R.id.gen_drama:

                AlertDialog.Builder builderSingle2 = new AlertDialog.Builder(GalleryActivity.this);

                builderSingle2.setTitle("Select Book");

                final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(

                        GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                arrayAdapter2.add("Romeo and Juliet - William Shakespeare");

                arrayAdapter2.add("Hamlet -William Shakespeare");

                arrayAdapter2.add("The Crucible - Arthur Miller");

                arrayAdapter2.add("The Importance of Being Earnest - Oscar Wilde");

                arrayAdapter2.add("A Streetcar named desire - Tennessee Williams");

                builderSingle2 .setAdapter(

                        arrayAdapter2,

                        new DialogInterface.OnClickListener(){

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                                String strName = arrayAdapter2.getItem(which);

                                Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                i.putExtra(strName, 10);

                                startActivity(i);

                            }

                        }

                );

                break;

            case R.id.gen_fantasy:

                AlertDialog.Builder builderSingle3 = new AlertDialog.Builder(GalleryActivity.this);

                builderSingle3.setTitle("Select Book");

                final ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(

                        GalleryActivity.this,android.R.layout.select_dialog_singlechoice);

                arrayAdapter3.add("The Lord of the Rings - J. R. R. Tolkien");

                arrayAdapter3.add("A Game of Thrones - George R. R. Martin");

                arrayAdapter3.add("The Hobbit - J. R. R. Tolkien");

                arrayAdapter3.add("Harry Porter and the Sorcerer's Stone - J. K. Rowling");

                arrayAdapter3.add("Harry Porter and the Sorcerer's Stone - J. K. Rowling");

                builderSingle3 .setAdapter(

                        arrayAdapter3,

                        new DialogInterface.OnClickListener(){

                            @Override

                            public void onClick(DialogInterface dialog, int which) {

                                String strName = arrayAdapter3.getItem(which);

                                Toast.makeText(getApplicationContext(), strName,Toast.LENGTH_LONG).show();

                                Intent i = new Intent(getApplicationContext(), BookInfo.class);

                                i.putExtra(strName, 10);

                                startActivity(i);

                            }

                        }

                );

                break;

        }

    }*/