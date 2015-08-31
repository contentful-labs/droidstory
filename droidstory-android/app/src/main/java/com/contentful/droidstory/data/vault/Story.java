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

package com.contentful.droidstory.data.vault;

import com.contentful.droidstory.data.Config;
import com.contentful.vault.Asset;
import com.contentful.vault.ContentType;
import com.contentful.vault.Field;
import com.contentful.vault.Resource;
import java.util.List;

@ContentType(Config.STORY_CONTENT_TYPE)
public final class Story extends Resource {
  @Field String codeName;
  @Field Double version;
  @Field String releaseDate;
  @Field Integer apiLevel;
  @Field List<Asset> images;
  @Field String teaser;

  public String codeName() {
    return codeName;
  }

  public Double version() {
    return version;
  }

  public String releaseDate() {
    return releaseDate;
  }

  public Integer apiLevel() {
    return apiLevel;
  }

  public List<Asset> images() {
    return images;
  }

  public String teaser() {
    return teaser;
  }
}
