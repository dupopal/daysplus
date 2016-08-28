package aaronbao.daysplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener{

    private final static String KEY_EXTRA_CONTACT_ID = "KEY_EXTRA_CONTACT_ID";
    private SharedPreferences sharedPreferences;
    private ArrayList<News> news;
    private NewsAdapter newsAdapter;
    private ListView lvNews, lvDays;
    private ArrayList<String> title;
    private ArrayList<String> link;
    private ArrayList<String> date;
    private StringBuilder stringBuilder;
    private boolean connected = false;
    private DBHandler mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ab.addTab(ab.newTab().setText("Winnipeg News").setTabListener(this));
        ab.addTab(ab.newTab().setText("My Days").setTabListener(this));

        executeNews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        executeNews();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        executeNews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void executeNews(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
            sharedPreferences = getSharedPreferences("mainPrefs", MODE_PRIVATE);
            GetNews getNews = new GetNews();
            getNews.execute();
        } else {
            connected = false;
            Toast.makeText(MainActivity.this, "Please check your network state", Toast.LENGTH_LONG).show();
        }
    }

    public void executeDays(){
        Log.d("Day+", "Start Days");
        lvDays = (ListView)findViewById(R.id.lvDays);
        mydb = new DBHandler(this);
        ArrayList my_days = mydb.getAllDays();
        if (my_days.size() <= 0){
            Toast.makeText(getApplicationContext(), "Start adding stories to your time line now!", Toast.LENGTH_LONG).show();
        }
        ArrayAdapter daysAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, my_days);

        lvDays.setAdapter(daysAdapter);
        lvDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int id_To_Search = position + 1;
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", id_To_Search);
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting){
            Intent startSetting = new Intent(MainActivity.this, SettingActivity.class);
            startActivityForResult(startSetting, 0);
        } else if (item.getItemId() == R.id.action_addday){
            Bundle dataBundle = new Bundle();
            dataBundle.putInt("id", 0);
            Intent startAddDay = new Intent(MainActivity.this, AddActivity.class);
            startAddDay.putExtras(dataBundle);
            startActivity(startAddDay);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        int nTabSelected = tab.getPosition();
        switch (nTabSelected) {
            case 0:
                executeNews();
                setContentView(R.layout.news);

                break;
            case 1:
                setContentView(R.layout.stories);
                executeDays();
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    class GetNews extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            URL url = null;
            HttpURLConnection connection = null;
            String rssChoice;

            if (sharedPreferences.getBoolean("is_global", true)) {
                rssChoice = "http://globalnews.ca/winnipeg/feed/";
            } else if (sharedPreferences.getBoolean("is_winnipeg", true)) {
                rssChoice = "http://www.winnipeg.ca/interhom/RSS/RSSNewsTopTen.xml";
            } else {
                rssChoice = "http://www.winnipeg.ca/interhom/RSS/RSSNewsTopTen.xml";
            }

            try {
                url = new URL(rssChoice);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                connection = (HttpURLConnection)url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
                NewsHandler handler = new NewsHandler();
                saxParser.parse(connection.getInputStream(), handler);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            lvNews = (ListView)findViewById(R.id.lvNews);

            newsAdapter = new NewsAdapter(MainActivity.this, R.layout.list_item, news);
            lvNews.setAdapter(newsAdapter);

            lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News feed = (News) newsAdapter.getItem(position);
                    Uri uri;
                    uri = Uri.parse(feed.getLink());
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(launchBrowser);
                }
            });
        }
    }

    class NewsHandler extends DefaultHandler{
        private boolean inTitle, inItem;
        private Integer number;

        public NewsHandler() {
            if (sharedPreferences.getBoolean("is_3", true)){
                number = 3;
            } else if (sharedPreferences.getBoolean("is_5", true)){
                number = 5;
            } else if (sharedPreferences.getBoolean("is_10", true)) {
                number = 10;
            } else {
                number = 10;
            }

            news = new ArrayList<News>(number);
            title = new ArrayList<String>(number);
            link = new ArrayList<String>(number);
            date = new ArrayList<String>(number);
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            Log.d("Day+", "start Document");
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            Log.d("Day+", "endDocument... contents of title array list:");

            for (int i = 0; i < number; i++){
                Log.d("Day+", "Title" + i + ": " + title.get(i));
                Log.d("Day+", "Link" + i + ": " + link.get(i));
                Log.d("Day+", "Date" + i + ": " + date.get(i));

                news.add(new News(title.get(i), link.get(i), date.get(i)));
            }

            Log.d("Day+", "end of the Document.");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            Log.d("Day+", "start Element: " + qName);

            if (qName.equals(("item"))){
                inItem = true;
            } else if (inItem && qName.equals("title")){
                inTitle = true;
                stringBuilder = new StringBuilder();
            } else if (inItem && qName.equals("link")) {
                stringBuilder = new StringBuilder();
            } else if (inItem && qName.equals("pubDate")) {
                stringBuilder = new StringBuilder();
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            Log.d("Day+", "endElement: " + qName);

            if(qName.equals(("item"))){
                inItem = true;
            } else if (inItem && qName.equals("title")) {
                inTitle = true;
                title.add(stringBuilder.toString());
            } else if (inItem && qName.equals("link")) {
                link.add(stringBuilder.toString());
            } else if (inItem && qName.equals("pubDate")) {
                date.add(stringBuilder.toString());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if (inTitle) {
                stringBuilder.append(ch, start, length);
            }
        }
    }

    class News {
        private String title, link, date;

        public News(String title, String link, String date){
            this.title = title;
            this.link = link;
            this.date = date;
        }
        public String getTitle() {return title;}
        public String getLink() {return link;}
        public String getDate() {return date;}
    }

    private class NewsAdapter extends ArrayAdapter<News>{
        private ArrayList<News> items;
        private float small, medium, large, fontSize;

        public NewsAdapter(Context context, int textViewResourceId, ArrayList<News> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            small = 15;
            medium = 20;
            large = 30;

            if (sharedPreferences.getBoolean("is_large", true)){
                fontSize = large;
            } else if (sharedPreferences.getBoolean("is_medium", true)) {
                fontSize = medium;
            } else if (sharedPreferences.getBoolean("is_small", true)) {
                fontSize = small;
            } else {
                fontSize = medium;
            }

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            News o = items.get(position);
            TextView tt = (TextView)v.findViewById(R.id.toptext);
            TextView bt = (TextView)v.findViewById(R.id.bottomtext);

            tt.setText(o.getTitle());
            tt.setTextSize(fontSize);
            bt.setText(o.getDate());
            bt.setTextSize(fontSize);

            return v;
        }
    }
}
