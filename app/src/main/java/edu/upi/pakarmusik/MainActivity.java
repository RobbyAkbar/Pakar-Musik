package edu.upi.pakarmusik;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPRuntimeException;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPVariable;

public class MainActivity extends AppCompatActivity {

    private JIPEngine jip;
    private JIPQuery jipQuery;

    ArrayList<String> A = new ArrayList<>();
    ArrayList<String> B = new ArrayList<>();
    ArrayList<String> C = new ArrayList<>();
    ArrayList<String> D = new ArrayList<>();
    ArrayList<String> Mood = new ArrayList<>();

    private TextView personality;
    private TextView res;
    private RecyclerView rvLagu;
    private LaguAdapter laguAdapter;

    private Button btnCekLagu;
    private Spinner spPersonalityA;
    private Spinner spPersonalityB;
    private Spinner spPersonalityC;
    private Spinner spPersonalityD;
    private Spinner spMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jip = JIPrologFactory.newInstance(this);
        try {
            jip.consultStream(getApplicationContext().getAssets().open("knowledge.pl"), "knowledge.pl");
        } catch (IOException e) {
            e.printStackTrace();
        }

        initViews();

        TextView copyright = findViewById(R.id.copyright);
        res = findViewById(R.id.result);
        PrintStream copy = new PrintStream(new TextViewOutputStream(copyright));
        PrintStream result = new PrintStream(new TextViewOutputStream(res));

        jip.setUserOutputStream(result);
        copy.println(JIPEngine.getInfo());

        initObject();

        btnCekLagu.setOnClickListener(v -> {
            personality.setText("");
            String a = spPersonalityA.getSelectedItem().toString().toLowerCase();
            String b = spPersonalityB.getSelectedItem().toString().toLowerCase();
            String c = spPersonalityC.getSelectedItem().toString().toLowerCase();
            String d = spPersonalityD.getSelectedItem().toString().toLowerCase();
            String query = String.format("personality(X,%s,%s,%s,%s).",a,b,c,d);

            JIPTerm queryTerm;
            // parse query
            try {
                queryTerm = jip.getTermParser().parseTerm(query);
                // open Query
                jipQuery = jip.openSynchronousQuery(queryTerm);
            }
            catch(JIPSyntaxErrorException ex) {
                ex.printStackTrace();
                Toast.makeText(MainActivity.this, "JIPSyntaxErrorException: "+ ex, Toast.LENGTH_LONG).show();
            }

            // check if there is another solution
            if(jipQuery.hasMoreChoicePoints()) {
                JIPTerm solution;
                try {
                    solution = jipQuery.nextSolution();
                }
                catch(JIPRuntimeException ex) {
                    Toast.makeText(MainActivity.this, "JIPRuntimeException: "+ex.getMessage(), Toast.LENGTH_LONG).show();
                    jipQuery.close();
                    jipQuery = null;
                    return;
                }
                catch(Exception ex) {
                    Toast.makeText(MainActivity.this, "Exception: "+ex.getMessage(), Toast.LENGTH_LONG).show();
                    jipQuery.close();
                    jipQuery = null;
                    return;
                }

                if(solution == null) {
                    jipQuery.close();
                    jipQuery = null;
                } else {
                    JIPVariable[] vars = solution.getVariables();
                    for (JIPVariable var : vars) {
                        if (!var.isAnonymous()) {
                            personality.setText(var.toString(jip).toUpperCase());
                        }
                    }
                    if(!jipQuery.hasMoreChoicePoints()) {
                        jipQuery.close();
                        jipQuery = null;
                    }
                }
            }
            else {
                jipQuery.close();
                jipQuery = null;
            }
            cekLagu(personality.getText().toString().toLowerCase());
        });
    }

    private void initViews(){
        spPersonalityA =  findViewById(R.id.spPA);
        spPersonalityB =  findViewById(R.id.spPB);
        spPersonalityC =  findViewById(R.id.spPC);
        spPersonalityD =  findViewById(R.id.spPD);
        spMood =  findViewById(R.id.spMood);
        btnCekLagu = findViewById(R.id.cekLagu);
        personality = findViewById(R.id.personality);
        rvLagu = findViewById(R.id.recyclerView);
    }

    private void initObject(){
        A.add("Extrovert");
        A.add("Introvert");
        B.add("Sensory");
        B.add("Intuitive");
        C.add("Thinking");
        C.add("Feeling");
        D.add("Judging");
        D.add("Perceiving");
        Mood.add("Happy");
        Mood.add("Sad");
        res.setText("");

        ArrayAdapter<String> adapterA = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, A);
        ArrayAdapter<String> adapterB = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, B);
        ArrayAdapter<String> adapterC = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, C);
        ArrayAdapter<String> adapterD = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, D);
        ArrayAdapter<String> adapterMood = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, Mood);

        spPersonalityA.setAdapter(adapterA);
        spPersonalityB.setAdapter(adapterB);
        spPersonalityC.setAdapter(adapterC);
        spPersonalityD.setAdapter(adapterD);
        spMood.setAdapter(adapterMood);

        laguAdapter = new LaguAdapter(getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        rvLagu.setLayoutManager(layoutManager);
        rvLagu.setAdapter(laguAdapter);
    }

    private void cekLagu(String personal){
        res.setText("");
        String mood = spMood.getSelectedItem().toString().toLowerCase();
        String query = String.format("song(X,Y,%s,%s), write(X), write('@'), write(Y), write('~'), fail.",mood,personal);

        JIPTerm queryTerm ;
        // parse query
        try {
            queryTerm = jip.getTermParser().parseTerm(query);
            // open Query
            jipQuery = jip.openSynchronousQuery(queryTerm);
        }
        catch(JIPSyntaxErrorException ex) {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "JIPSyntaxErrorException: "+ ex, Toast.LENGTH_LONG).show();
        }

        // check if there is another solution
        if(jipQuery.hasMoreChoicePoints()) {
            JIPTerm solution;
            try {
                solution = jipQuery.nextSolution();
            }
            catch(JIPRuntimeException ex) {
                Toast.makeText(MainActivity.this, "JIPRuntimeException: "+ex.getMessage(), Toast.LENGTH_LONG).show();
                jipQuery.close();
                jipQuery = null;
                return;
            }
            catch(Exception ex) {
                Toast.makeText(MainActivity.this, "Exception: "+ex.getMessage(), Toast.LENGTH_LONG).show();
                jipQuery.close();
                jipQuery = null;
                return;
            }

            if(solution == null) {
                jipQuery.close();
                jipQuery = null;
            } else {
                JIPVariable[] vars = solution.getVariables();
                for (JIPVariable var : vars) {
                    if (!var.isAnonymous()) {
                        res.setText(var.toString(jip));
                    }
                }
                if(!jipQuery.hasMoreChoicePoints()) {
                    jipQuery.close();
                    jipQuery = null;
                }
            }
        }
        else {
            jipQuery.close();
            jipQuery = null;
        }
        String[] data = res.getText().toString().split("~", 20);
        ArrayList<Lagu> laguArrayList = new ArrayList<>();
        for (int i = 0; i < data.length-1; i++){
            String[] lagu = data[i].split("@",2);
            laguArrayList.add(new Lagu(lagu[1], lagu[0]));
        }
        laguAdapter.updateWith(laguArrayList);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) new MyDialogFragment().show(getSupportFragmentManager(), "MyDialogFragmentTag");
        return super.onOptionsItemSelected(item);
    }
}
