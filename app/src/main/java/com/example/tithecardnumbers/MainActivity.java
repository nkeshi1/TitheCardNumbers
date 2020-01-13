package com.example.tithecardnumbers;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.example.tithecardnumbers.DatabaseManager.*;

public class MainActivity extends AppCompatActivity{
    private DatabaseManager databaseManager;
    private TextInputLayout holder_name;
    private Rect displyRec;
    private int minHeight, minWeight;
    private TextInputLayout yeatText;
    private TextInputLayout year;
    private TextInputLayout holderName;
    private int cardid;
    private MaterialSearchView searchView;
    private ListView titheCardsList;
    private CardsListAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu_item, menu);

        MenuItem menuItem = menu.findItem(R.id.searchHolder);
        searchView.setMenuItem(menuItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.searchHolder:
               Toast.makeText(getApplicationContext(), "Search Holder", Toast.LENGTH_LONG).show();
               break;
           case R.id.help_line:
               Toast.makeText(getApplicationContext(), "Get Help", Toast.LENGTH_LONG).show();
               break;
       }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseManager = new DatabaseManager(this);
        displyRec = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displyRec);
        minHeight = (int) (displyRec.height() * 0.6f);
        minWeight = (int) (displyRec.width() * 0.9f);

        adapter = new CardsListAdapter(this, databaseManager.getAllCardInfo(), this);
        titheCardsList = findViewById(R.id.cardsList);
        titheCardsList.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                CardsListAdapter adapter = new CardsListAdapter(MainActivity.this, databaseManager.getAllCardInfo(), MainActivity.this);
                final ListView titheCardsList = findViewById(R.id.cardsList);
                titheCardsList.setAdapter(adapter);
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!(newText.isEmpty())){
                    ArrayList<TitheCardRegister> cardsFound = new ArrayList<>();
                    for(TitheCardRegister card: databaseManager.getAllCardInfo()){
                        if(card.getHolderName().trim().toLowerCase().contains(newText.trim().toLowerCase())){
                            cardsFound.add(card);
                        }
                    }
                    TextView noResults = findViewById(R.id.noResultsText);
                    if(cardsFound.size() == 0){
                        noResults.setVisibility(View.VISIBLE);
                        noResults.setText("No card found for "+ newText);
                    }else{
                        noResults.setVisibility(View.INVISIBLE);
                    }
                    adapter = new CardsListAdapter(MainActivity.this, cardsFound, MainActivity.this);
                    titheCardsList = findViewById(R.id.cardsList);
                    titheCardsList.setAdapter(adapter);
                }
                return true;
            }
        });

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tithing Cards");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        getSupportActionBar().setSubtitle(databaseManager.getAllCardInfo().size()+" card(s)");

        FloatingActionButton add_card = findViewById(R.id.add_new_card);
        add_card.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog cardDialog = new Dialog(MainActivity.this);
                cardDialog.setContentView(R.layout.add_card_layout);
                cardDialog.getWindow().setLayout(minWeight, minHeight);
                Button addCardBtn = cardDialog.findViewById(R.id.addCardBtn);
                ImageView closeBtn = cardDialog.findViewById(R.id.closeBtn);
                yeatText = cardDialog.findViewById(R.id.yearText);
                holder_name = cardDialog.findViewById(R.id.holderNameText);
                cardDialog.setCancelable(false);
                cardDialog.show();

                addCardBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String holderName = holder_name.getEditText().getText().toString();
                        String year = yeatText.getEditText().getText().toString();
                        if(holderName.equals("")){
                            holder_name.setError("Please enter holder name");
                            yeatText.setError(null);
                            //Toast.makeText(getApplicationContext(), "Please enter holder name", Toast.LENGTH_LONG).show();
                        }else if(year.equals("")){
                            yeatText.setError("Please enter year");
                            holder_name.setError(null);
                        }else{
                            int yearInt = Integer.parseInt(yeatText.getEditText().getText().toString());
                            TitheCardRegister titheCard = new TitheCardRegister();
                            titheCard.setHolderName(holderName);
                            titheCard.setCardYear(yearInt);
                            databaseManager.addCardNumber(titheCard);
                            Snackbar.make(findViewById(R.id.rootLayout), "New card added", Snackbar.LENGTH_LONG).show();
                            cardDialog.dismiss();
                            CardsListAdapter adapter = new CardsListAdapter(MainActivity.this, databaseManager.getAllCardInfo(), MainActivity.this);
                            ListView titheCardsList = findViewById(R.id.cardsList);
                            titheCardsList.setAdapter(adapter);
                            getSupportActionBar().setSubtitle(databaseManager.getAllCardInfo().size()+" card(s)");
                        }
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseManager.close();
    }
}
