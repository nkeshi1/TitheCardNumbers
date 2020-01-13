package com.example.tithecardnumbers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tithecardnumbers.DatabaseManager.*;

public class CardsListAdapter extends ArrayAdapter<TitheCardRegister> {
    private int minHeight, minWeight;
    private Activity activity;
    private Map<String, String> cardSelected;
    private TextInputLayout holder_name, year;
    private DatabaseManager databaseManager;
    private ListView titheCardsList;
    private Rect displyRec;

    public CardsListAdapter(Context context, List<TitheCardRegister> objects, Activity activity) {
        super(context, 0, objects);
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.design_layout, parent, false);
        final TitheCardRegister titheCardRegister = getItem(position);
        databaseManager = new DatabaseManager(getContext());
        titheCardsList = activity.findViewById(R.id.cardsList);

        displyRec = new Rect();

        final TextView cardNo = convertView.findViewById(R.id.cardNoText);
        TextView holderName = convertView.findViewById(R.id.cardHolderText);
        TextView fullDetails = convertView.findViewById(R.id.cardDetailsText);

        cardNo.setText(String.valueOf(titheCardRegister.getCardNo()));
        holderName.setText(titheCardRegister.getHolderName());
        fullDetails.setText("TN " + String.valueOf(titheCardRegister.getCardNo())+"/"+String.valueOf(titheCardRegister.getCardYear()));

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Window window = activity.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displyRec);
                minHeight = (int) (displyRec.height() * 0.6f);
                minWeight = (int) (displyRec.width() * 0.9f);

                Display display = activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = (int) (size.x * 0.9f);
                int height = (int) (size.y * 0.6f);

                View view = activity.getCurrentFocus();
                if(view != null){
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                cardSelected = new HashMap<>();
                cardSelected.put(CARD_HOLDER, titheCardRegister.getHolderName());
                cardSelected.put(CARD_YEAR, String.valueOf(titheCardRegister.getCardYear()));

                final Dialog cardDialog = new Dialog(getContext());
                cardDialog.setContentView(R.layout.replace_card_layout);
                cardDialog.getWindow().setLayout(width, height);
                Button replaceCardBtn = cardDialog.findViewById(R.id.replaceCardBtn);
                ImageView closeBtn = cardDialog.findViewById(R.id.closeBtn);

                holder_name = cardDialog.findViewById(R.id.replaceHolderText);
                holder_name.getEditText().setText(titheCardRegister.getHolderName());
                year = cardDialog.findViewById(R.id.replaceYearText);
                year.getEditText().setText(String.valueOf(titheCardRegister.getCardYear()));
                TextView cardDetails = cardDialog.findViewById(R.id.cardDetailsText);
                cardDetails.setText("TN " + cardSelected.get(CARD_NO) + "/" + cardSelected.get(CARD_YEAR));

//                hideKeyboard(activity);

                cardDialog.setCancelable(false);
                cardDialog.show();

                replaceCardBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String yearStr = year.getEditText().getText().toString();
                        String holdername = holder_name.getEditText().getText().toString();
                        if (holdername.equals("")) {
                            holder_name.setError("Please enter holder name");
                            year.setError(null);
                        } else if (yearStr.equals("")) {
                            year.setError("Please enter year");
                            holder_name.setError(null);
                        } else {
                            TitheCardRegister titheCard = new TitheCardRegister(Integer.parseInt(CARD_NO), holdername, Integer.parseInt(yearStr));
                            databaseManager.updateCardNumber(titheCard);
                            CardsListAdapter listAdapter = new CardsListAdapter(getContext(), databaseManager.getAllCardInfo(), activity);
                            titheCardsList.setAdapter(listAdapter);
                            cardDialog.dismiss();
                            Snackbar.make(activity.findViewById(R.id.rootLayout), "Card replaced", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cardDialog.dismiss();
                    }
                });

                return true;
            }
        });
        return convertView;
    }

    public Map<String, String> getCardSelected(){
        return cardSelected;
    }

    private static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if(view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
