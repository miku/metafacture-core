/*
 * Copyright 2013, 2014 Deutsche Nationalbibliothek
 *
 * Licensed under the Apache License, Version 2.0 the "License";
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.culturegraph.mf.stream.sink;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for class {@link ObjectFileWriter}.
 *
 * @author Christoph Böhme
 *
 */
public final class ObjectFileWriterTest
		extends AbstractConfigurableObjectWriterTest {

	private static final String DATA = "Überfacture";

	@Rule
	public final TemporaryFolder tempFolder = new TemporaryFolder();

	private File file;
	private ObjectFileWriter<String> writer;

	@Before
	public void setup() throws IOException {
		file = tempFolder.newFile();
		writer = new ObjectFileWriter<String>(file.getAbsolutePath());
	}

	@Test
	public void shouldWriteUTF8EncodedOutput() throws IOException {
		assumeThat(
				"Default encoding is UTF-8: It is not possible to test whether ObjectFileWriter sets the encoding to UTF-8 correctly.",
				Charset.defaultCharset(),
				not(equalTo(StandardCharsets.UTF_8)));

		writer.process(DATA);
		writer.closeStream();

		final byte[] bytesWritten = Files.readAllBytes(file.toPath());
		assertArrayEquals((DATA + "\n").getBytes(StandardCharsets.UTF_8),
				bytesWritten); // FileObjectWriter appends new lines
	}

	@Override
	protected ConfigurableObjectWriter<String> getWriter() {
		return writer;
	}

	@Override
	protected String getOutput() throws IOException {
		final InputStream stream = new FileInputStream(file);
		final InputStreamReader reader;
		try { reader = new InputStreamReader(stream, writer.getEncoding()); }
		catch (final IOException e) {
			stream.close();
			throw e;
		}

		final String fileContents;
		try { fileContents = IOUtils.toString(reader); }
		finally { reader.close(); }

		return fileContents;
	}

}
