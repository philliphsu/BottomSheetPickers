package com.example.bottomsheetpickers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class TextSwitcherActivity extends AppCompatActivity {

    private int count = 0;
    private int countGroup = 0;

    private Animation in;
    private Animation out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_switcher);

        final String[] texts = {"Text One", "Text Two",
                "Text Three", "Text Four", "Text Five", "Text Six"};

        in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        final TextSwitcher switcher = (TextSwitcher) findViewById(R.id.text_switcher);
        setupTextSwitcher(switcher);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = texts[count % texts.length];
                switcher.setText(newText);
                count++;
            }
        });

        final ViewGroup switchersGroup = (ViewGroup) findViewById(R.id.text_switchers_group);
        for (int i = 0; i < switchersGroup.getChildCount(); i++) {
            setupTextSwitcher((TextSwitcher) switchersGroup.getChildAt(i));
        }

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = countGroup % 2 == 0 ? texts[1] : texts[0];
                for (int i = 0; i < switchersGroup.getChildCount(); i++) {
                    ((TextSwitcher) switchersGroup.getChildAt(i)).setText(newText);
                }
                countGroup++;
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPadTimePickerDialogFragment dialog = new NumberPadTimePickerDialogFragment();
                dialog.show(getSupportFragmentManager(), "TAG");
            }
        });
    }

    private void setupTextSwitcher(TextSwitcher switcher) {
        switcher.setFactory(mFactory);
        switcher.setInAnimation(in);
        switcher.setOutAnimation(out);
        switcher.setCurrentText("Click the button to change the text.");
    }

    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {
        @Override
        public View makeView() {
            return new TextView(TextSwitcherActivity.this);
        }
    };
}
