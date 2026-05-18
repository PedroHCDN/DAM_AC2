package com.example.dam_ac2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db;
    private EditText edtTitulo, edtAutor, edtPublicacao;
    private Spinner spinnerGenero, spinnerStatus_Leitura;
    private CheckBox checkBoxFavorito;
    private TextView txtTotalLivros;
    private Spinner spinnerFiltroGenero;
    private RecyclerView recyclerLivros;
    private List<Livro> listaLivros = new ArrayList<>();
    private LivroAdapter adapter;
    private Livro livroEditando = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        edtPublicacao = findViewById(R.id.edtPublicacao);
        spinnerStatus_Leitura = findViewById(R.id.spinnerStatus_Leitura);
        checkBoxFavorito = findViewById(R.id.checkBoxFavorito);
        txtTotalLivros = findViewById(R.id.txtTotalLivros);
        spinnerFiltroGenero = findViewById(R.id.spinnerFiltroGenero);
        recyclerLivros = findViewById(R.id.recyclerLivros);
        recyclerLivros.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LivroAdapter(listaLivros);
        recyclerLivros.setAdapter(adapter);

        spinnerFiltroGenero.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view,
                                               int position,
                                               long id) {

                        carregarLivros();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarLivro());

        carregarLivros();


    }

    private void salvarLivro() {
        String titulo = edtTitulo.getText().toString();
        String autor = edtAutor.getText().toString();
        String genero = spinnerGenero.getSelectedItem().toString();
        String publicacaoTexto = edtPublicacao.getText().toString().trim();
        String status_leitura = spinnerStatus_Leitura.getSelectedItem().toString();
        Boolean favorito = checkBoxFavorito.isChecked();

        if (titulo.isEmpty()) {
            edtTitulo.setError("Digite o título");
            edtTitulo.requestFocus();
            return;
        }

        if (autor.isEmpty()) {
            edtAutor.setError("Digite o autor");
            edtAutor.requestFocus();
            return;
        }

        if (publicacaoTexto.isEmpty()) {
            edtPublicacao.setError("Digite o ano de publicação");
            edtPublicacao.requestFocus();
            return;
        }

        int publicacao = Integer.parseInt(publicacaoTexto);

        if (livroEditando == null) {
            Livro livro = new Livro(null, titulo, autor, genero, publicacao, status_leitura, favorito);
            db.collection("livros")
                    .add(livro)
                    .addOnSuccessListener(doc -> {
                        livro.setId(doc.getId());
                        Toast.makeText(this, "Livro salvo!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                        carregarLivros();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                this,
                                "Erro: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        } else {
            livroEditando.setTitulo(titulo);
            livroEditando.setAutor(autor);
            livroEditando.setGenero(genero);
            livroEditando.setPublicacao(publicacao);
            livroEditando.setStatus_leitura(status_leitura);
            livroEditando.setFavorito(favorito);

            db.collection("livros").document(livroEditando.getId())
                    .set(livroEditando)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Livro atualizado!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                        carregarLivros();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                this,
                                "Erro: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        }
    }

    public void deletarLivro(String id) {
        db.collection("livros").document(id)
                .delete()
                .addOnSuccessListener(aVoid -> carregarLivros())
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erro ao deletar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    public void atualizarLivro(Livro livro) {
        db.collection("livros").document(livro.getId())
                .set(livro)
                .addOnSuccessListener(aVoid -> carregarLivros())
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Erro ao atualizar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void limparCampos() {
        edtTitulo.setText("");
        edtAutor.setText("");
        edtPublicacao.setText("");
        livroEditando = null;
        ((Button) findViewById(R.id.btnSalvar)).setText("Salvar Livro");
    }

    private void carregarLivros() {

        String filtro = spinnerFiltroGenero.getSelectedItem().toString();

        if (filtro.equals("Todos")) {

            db.collection("livros")
                    .get()
                    .addOnSuccessListener(query -> {

                        listaLivros.clear();

                        for (QueryDocumentSnapshot doc : query) {

                            Livro l = doc.toObject(Livro.class);

                            l.setId(doc.getId());

                            listaLivros.add(l);
                        }

                        adapter.notifyDataSetChanged();

                        txtTotalLivros.setText(
                                "Total de livros: "
                                        + listaLivros.size());
                    });

        } else {

            db.collection("livros")
                    .whereEqualTo("genero", filtro)
                    .get()
                    .addOnSuccessListener(query -> {

                        listaLivros.clear();

                        for (QueryDocumentSnapshot doc : query) {

                            Livro l = doc.toObject(Livro.class);

                            l.setId(doc.getId());

                            listaLivros.add(l);
                        }

                        adapter.notifyDataSetChanged();

                        txtTotalLivros.setText(
                                "Total de livros: "
                                        + listaLivros.size());
                    });
        }

        adapter.setOnItemClickListener(livro -> {

            edtTitulo.setText(livro.getTitulo());
            edtAutor.setText(livro.getAutor());

            edtPublicacao.setText(
                    String.valueOf(livro.getPublicacao()));

            checkBoxFavorito.setChecked(
                    livro.getFavorito());

            livroEditando = livro;

            ((Button) findViewById(R.id.btnSalvar))
                    .setText("Atualizar Livro");
        });
    }

    public void cadastrarUsuario(View v)
    {
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);

        mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Usuário criado com sucesso", Toast.LENGTH_LONG).show();
                        Log.d("FIREBASE", "Usuário criado com sucesso");
                    } else {
                        Toast.makeText(this, "Erro ao criar usuário: " + task.getException(), Toast.LENGTH_LONG).show();
                        Log.e("FIREBASE", "Erro ao criar usuário", task.getException());
                    }
                });
    }

    public void logarUsuario(View v)
    {
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtSenha = findViewById(R.id.edtSenha);

        mAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtSenha.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login bem-sucedido", Toast.LENGTH_LONG).show();
                        Log.d("FIREBASE", "Login bem-sucedido");
                    } else {
                        Toast.makeText(this, "Erro no login: " + task.getException(), Toast.LENGTH_LONG).show();
                        Log.e("FIREBASE", "Erro no login", task.getException());
                    }
                });
    }
}