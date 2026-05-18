package com.example.dam_ac2;

import static android.view.View.inflate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LivroAdapter extends RecyclerView.Adapter<LivroAdapter.ViewHolder>{
    private List<Livro> livros;

    public interface OnItemClickListener {
        void onItemClick(Livro livro);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public LivroAdapter(List<Livro> livros) {
        this.livros = livros;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livro, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int pos) {
        Livro l = livros.get(pos);
        holder.txtTitulo.setText(l.getTitulo());

        holder.txtAutor.setText(
                "Autor: " + l.getAutor());

        holder.txtGenero.setText(
                "Gênero: " + l.getGenero());

        holder.txtPublicacao.setText(
                "Publicação: " + l.getPublicacao());

        holder.txtStatus.setText(
                "Status: " + l.getStatus_leitura());

        holder.txtFavorito.setText(
                l.getFavorito() ? "⭐ Favorito" : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(l);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            deletarLivro(l.getId(), holder.getAdapterPosition(), v);
            return true;
        });



//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            private long lastClickTime = 0;
//
//            @Override
//            public boolean onTouch(View v, android.view.MotionEvent event) {
//                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
//                    long currentTime = System.currentTimeMillis();
//                    if (currentTime - lastClickTime < 300) {
//                        deletarLivro(l.getId(), holder.getAdapterPosition(), v);
//                    }
//                    lastClickTime = currentTime;
//                }
//                return false;
//            }
//        });
    }

    private void deletarLivro(String idDocumento, int position, View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            FirebaseFirestore.getInstance().collection("livros")
                    .document(idDocumento)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        livros.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(view.getContext(), "Livro deletado!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(view.getContext(), "Erro ao deletar", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(view.getContext(), "Você precisa estar logado para realizar essa ação", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return livros.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtAutor, txtGenero, txtPublicacao, txtStatus, txtFavorito;
        public ViewHolder(View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtAutor = itemView.findViewById(R.id.txtAutor);
            txtGenero = itemView.findViewById(R.id.txtGenero);
            txtPublicacao = itemView.findViewById(R.id.txtPublicacao);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtFavorito = itemView.findViewById(R.id.txtFavorito);
        }
    }

}
