package todolist.arthur.com.todolist;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button b_adicionar;
    private EditText c_texto;
    private ListView l_lista;
    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            b_adicionar = findViewById(R.id.b_adicionar);
            c_texto = findViewById(R.id.c_texto);
            l_lista = findViewById(R.id.l_lista);

            //Criando banco de dados
            bancoDados = openOrCreateDatabase("apptarefas", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");
            l_lista.setLongClickable(true);

            b_adicionar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textoDigitado = c_texto.getText().toString();
                    salvarTarefa(textoDigitado);
                }
            });
            l_lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    excluirTarefa(ids.get(position));
                    return true;
                }
            });

            recuperarTarefas();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void salvarTarefa(String texto){
        try {
            if(c_texto.getText().toString().equals("")){
                Toast.makeText(MainActivity.this, "Campo de texto está vazio xD.", Toast.LENGTH_SHORT).show();
            }else{
                bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES('" + texto + "')");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso.", Toast.LENGTH_SHORT).show();
                recuperarTarefas();
                c_texto.setText("");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void recuperarTarefas(){
        try{
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");
            cursor.moveToFirst();

            //Configurando lista
            itens = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itensAdaptador = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    itens);
            l_lista.setAdapter(itensAdaptador);

            while(cursor != null){
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));
                cursor.moveToNext();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void excluirTarefa(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM tarefas WHERE id="+id);
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso.", Toast.LENGTH_SHORT).show();
            recuperarTarefas();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
