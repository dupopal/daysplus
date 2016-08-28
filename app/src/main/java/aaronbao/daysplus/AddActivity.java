package aaronbao.daysplus;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends AppCompatActivity {
    private DBHandler mydb;
    private TextView etTitle, etDay;
    private int id_To_Update = 0;
    //private Button btDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Today's Story");

        etTitle = (TextView)findViewById(R.id.etTitle);
        etDay = (TextView)findViewById(R.id.etDay);

        mydb = new DBHandler(this);
        Log.d("Day+","onCreate");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0){
                Log.d("Day+","onCreate EXTRAS");
                Cursor rs = mydb.getDays(Value);
                id_To_Update = Value;
                rs.moveToFirst();

                String title = rs.getString(rs.getColumnIndex(DBHandler.DAY_TITLE));
                String day = rs.getString(rs.getColumnIndex(DBHandler.DAY_STORY));

                if (!rs.isClosed()) {
                    rs.close();
                }
                Button btDone = (Button)findViewById(R.id.btSave);
                btDone.setVisibility(View.INVISIBLE);

                etTitle.setText((CharSequence) title);
                etTitle.setFocusable(false);
                etTitle.setClickable(false);

                etDay.setText((CharSequence) day);
                etDay.setFocusable(false);
                etDay.setClickable(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_add, menu);
        return true;
    }

    public void run(View view){
        Log.d("Day+","clicked");
        Bundle extras = getIntent().getExtras();
        Log.d("Day+","clicked1");
        if (extras != null){
            Log.d("Day+","clicked2");
            int Value = extras.getInt("id");
            if (Value > 0){
                if(mydb.updateDay(id_To_Update,etTitle.getText().toString(), etDay.getText().toString())){
                    Toast.makeText(getApplicationContext(), "You just changed your Time Line!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to change your own Time Line", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("Day+","clicked3");
                if (mydb.addDay(1, etTitle.getText().toString(), etDay.getText().toString())){
                    Toast.makeText(getApplicationContext(), "New Story Added to Your Time Line!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Oops", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
