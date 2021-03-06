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

package com.webauthn4j.converter;

import com.webauthn4j.converter.util.JsonConverter;
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientInputs;
import com.webauthn4j.data.extension.client.ExtensionClientInput;
import com.webauthn4j.util.AssertUtil;

/**
 * Converter for {@link AuthenticationExtensionsClientInputs}
 */
public class AuthenticationExtensionsClientInputsConverter {

    // ~ Instance fields
    // ================================================================================================
    private JsonConverter jsonConverter;

    // ~ Constructors
    // ================================================================================================

    public AuthenticationExtensionsClientInputsConverter(JsonConverter jsonConverter) {
        AssertUtil.notNull(jsonConverter, "jsonConverter must not be null");
        this.jsonConverter = jsonConverter;
    }

    // ~ Methods
    // ================================================================================================

    public AuthenticationExtensionsClientInputs<ExtensionClientInput> convert(String value) {
        if (value == null) {
            return null;
        }
        return jsonConverter.readValue(value, AuthenticationExtensionsClientInputs.class);
    }

    public String convertToString(AuthenticationExtensionsClientInputs value) {
        if (value == null) {
            return null;
        }
        return jsonConverter.writeValueAsString(value);
    }

}
