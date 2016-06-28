package com.androidzeitgeist.webcards.overlay.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidzeitgeist.webcards.R;
import com.androidzeitgeist.webcards.model.ProductWebCard;
import com.androidzeitgeist.webcards.model.WebCard;
import com.squareup.picasso.Picasso;

public class ProductViewHolder extends WebCardViewHolder {
    private final TextView titleView;
    private final ImageView imageView;
    private final TextView priceView;

    public ProductViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.title);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        priceView = (TextView) itemView.findViewById(R.id.price);
    }

    @Override
    public void bind(WebCard webCard) {
        ProductWebCard card = (ProductWebCard) webCard;

        titleView.setText(card.getTitle());
        priceView.setText(card.getPrice());

        Picasso.with(itemView.getContext())
                .load(card.getImageUrl())
                .into(imageView);
    }
}
