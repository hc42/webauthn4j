/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webauthn4j.extras.fido.metadata.toc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by ynojima on 2017/09/08.
 */
public class MetadataTOCPayload {
    @JsonProperty
    private OffsetDateTime nextUpdate;
    @JsonProperty
    private Integer no;
    @JsonProperty
    private List<MetadataTOCPayloadEntry> entries;

    public OffsetDateTime getNextUpdate() {
        return nextUpdate;
    }

    public Integer getNo() {
        return no;
    }

    public List<MetadataTOCPayloadEntry> getEntries() {
        return entries;
    }

}
