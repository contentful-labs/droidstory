/*
 * Copyright (C) 2015 Contentful GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.contentful.droidstory.ui.story;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.contentful.droidstory.R;
import com.contentful.droidstory.data.vault.Story;
import com.squareup.picasso.Picasso;
import java.util.List;

public final class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {
  private List<Story> data;

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_story, parent, false));
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Story av = data.get(position);

    bindInfo(holder, av);
    bindTeaser(holder, av);
    bindThumbnail(holder, av);
  }

  private void bindInfo(ViewHolder holder, Story av) {
    holder.info.setText(holder.info.getContext().getString(R.string.story_li_info,
        av.codeName(), av.version().toString(), av.apiLevel(), av.releaseDate()));
  }

  private void bindTeaser(ViewHolder holder, Story av) {
    holder.teaser.setText(av.teaser());
  }

  private void bindThumbnail(ViewHolder holder, Story av) {
    Picasso.with(holder.thumbnail.getContext())
        .load(av.images().get(0).url())
        .fit()
        .centerInside()
        .into(holder.thumbnail);
  }

  @Override public int getItemCount() {
    return data == null ? 0 : data.size();
  }

  public void setData(List<Story> data) {
    this.data = data;
  }

  static final class ViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.info) TextView info;
    @Bind(R.id.teaser) TextView teaser;
    @Bind(R.id.thumbnail) ImageView thumbnail;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
