/*
 * Copyright 2018 the original author or authors.
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

package io.lacasse.vscode.gradle.internal.generator;

import com.google.gson.GsonBuilder;
import org.gradle.plugins.ide.internal.generator.AbstractPersistableConfigurationObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;

public abstract class JsonPersistableConfigurationObject<T> extends AbstractPersistableConfigurationObject {
    private final Type type;
    private T rootObject;

    protected JsonPersistableConfigurationObject(Class<T> clazz) {
        this.type = clazz;

    }

    protected JsonPersistableConfigurationObject(Type type) {
        this.type = type;
    }

    protected abstract T newRootObject();

    @Override
    public void load(InputStream inputStream) throws Exception {
        rootObject = new GsonBuilder().create().fromJson(new InputStreamReader(inputStream), type);
        if (rootObject == null) {
            rootObject = newRootObject();
        }
        load(rootObject);
    }

    @Override
    public void store(OutputStream outputStream) {
        store(rootObject);
        try (Writer writer = new OutputStreamWriter(outputStream)) {
            new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(rootObject, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected abstract void store(T rootObject);

    protected abstract void load(T rootObject);
}
