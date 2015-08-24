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

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import com.contentful.droidstory.R;
import com.contentful.droidstory.ui.common.VerticalSpaceDecoration;

public final class StoryActivity extends AppCompatActivity implements StoryFragment.Listener {
  private StoryFragment storyFragment;

  @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefresh;
  @Bind(R.id.recycler) RecyclerView recyclerView;

  @BindDimen(R.dimen.li_vertical_margins)
  int itemVerticalSpacing;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_story);

    ButterKnife.bind(this);
    setupMainFragment();
    setupSwipeReferesh();
    setupRecycler();
  }

  @Override protected void onStart() {
    super.onStart();
    storyFragment.setListener(this);
  }

  @Override protected void onStop() {
    storyFragment.setListener(null);
    stopRefresh();
    super.onStop();
  }

  @Override public void stopRefresh() {
    swipeRefresh.setRefreshing(false);
  }

  @Override public void showSyncError(Throwable throwable) {
    Toast.makeText(this, getString(R.string.error_sync, throwable.getMessage()),
        Toast.LENGTH_LONG).show();
  }

  private void setupSwipeReferesh() {
    swipeRefresh.setOnRefreshListener(storyFragment::requestSync);
  }

  private void setupRecycler() {
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(storyFragment.storyAdapter());
    recyclerView.addItemDecoration(new VerticalSpaceDecoration(itemVerticalSpacing));
  }

  private void setupMainFragment() {
    FragmentManager fm = getSupportFragmentManager();
    storyFragment = (StoryFragment) fm.findFragmentByTag(StoryFragment.TAG);
    if (storyFragment == null) {
      storyFragment = new StoryFragment();
      fm.beginTransaction().add(storyFragment, StoryFragment.TAG).commit();
    }
  }
}
