package com.example.bottomsheetpickers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class TextSwitcherActivity extends AppCompatActivity {

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_switcher);

        final String[] texts = {"Text One", "Text Two",
                "Text Three", "Text Four", "Text Five", "Text Six"};

        final TextSwitcher switcher = (TextSwitcher) findViewById(R.id.text_switcher);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(TextSwitcherActivity.this);
            }
        });
        switcher.setInAnimation(this, android.R.anim.fade_in);
        switcher.setOutAnimation(this, android.R.anim.fade_out);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = texts[count % texts.length];
                switcher.setText(newText);
                count++;
            }
        });
    }
}
