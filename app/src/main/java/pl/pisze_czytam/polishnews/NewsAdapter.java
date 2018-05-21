package pl.pisze_czytam.polishnews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<News> newsList;
    final private ListItemClickListener onClickListener;

    public interface ListItemClickListener {
        void onListItemClicked(int position);
    }

    public NewsAdapter(List<News> newsList, ListItemClickListener listener) {
        this.newsList = newsList;
        onClickListener = listener;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_item, viewGroup, false);
        NewsViewHolder viewHolder = new NewsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News currentNews = newsList.get(position);
        holder.title.setText(currentNews.getTitle());
        holder.author.setText(currentNews.getAuthor());
        holder.date.setText(currentNews.getDate());
        holder.section.setText(currentNews.getSection());
        holder.image.setImageDrawable(currentNews.getImage());
        holder.trailer.setText(currentNews.getTrailer());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView author;
        private TextView date;
        private TextView section;
        private ImageView image;
        private TextView trailer;

        public NewsViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_view);
            author = itemView.findViewById(R.id.author_view);
            date = itemView.findViewById(R.id.date_view);
            section = itemView.findViewById(R.id.section_view);
            image = itemView.findViewById(R.id.image_view);
            trailer = itemView.findViewById(R.id.trailer_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onListItemClicked(clickedPosition);
        }
    }
}


