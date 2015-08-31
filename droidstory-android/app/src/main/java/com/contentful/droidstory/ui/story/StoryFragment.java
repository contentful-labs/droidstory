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
import android.support.v4.app.Fragment;
import com.contentful.droidstory.App;
import com.contentful.droidstory.data.ClientProvider;
import com.contentful.droidstory.data.vault.DroidStorySpace;
import com.contentful.droidstory.data.vault.Story;
import com.contentful.droidstory.data.vault.Story$Fields;
import com.contentful.vault.SyncConfig;
import com.contentful.vault.SyncResult;
import com.contentful.vault.Vault;
import java.util.List;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public final class StoryFragment extends Fragment {
  public static final String TAG = StoryFragment.class.getName();

  private final Vault vault = Vault.with(App.get(), DroidStorySpace.class);
  private StoryAdapter storyAdapter = new StoryAdapter();
  private CompositeSubscription subs;
  private Listener listener;

  public interface Listener {
    void stopRefresh();
    void showSyncError(Throwable throwable);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);

    subs = new CompositeSubscription();
    subscribeForSyncResults();
    requestSync();
  }

  @Override public void onDestroy() {
    subs.clear();
    subs = null;
    super.onDestroy();
  }

  public void requestSync() {
    vault.requestSync(SyncConfig.builder().setClient(ClientProvider.get()).build());
  }

  public StoryAdapter storyAdapter() {
    return storyAdapter;
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  private void subscribeForSyncResults() {
    subs.add(Vault.observeSyncResults()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onSyncResult));
  }

  private void onSyncResult(SyncResult syncResult) {
    if (syncResult.isSuccessful()) {
      reloadVersions();
    } else {
      showSyncError(syncResult.error());
    }
    stopRefresh();
  }

  private void stopRefresh() {
    if (listener != null) {
      listener.stopRefresh();
    }
  }

  private void showSyncError(Throwable error) {
    if (listener != null) {
      listener.showSyncError(error);
    }
  }

  private void reloadVersions() {
    subs.add(vault.observe(Story.class)
        .order(Story$Fields.API_LEVEL + " DESC")
        .all()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .toList()
        .subscribe(this::populateAdapter));
  }

  private void populateAdapter(List<Story> stories) {
    storyAdapter.setData(stories);
    storyAdapter.notifyDataSetChanged();
  }
}
