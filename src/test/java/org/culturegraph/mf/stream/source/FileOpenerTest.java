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
package org.culturegraph.mf.stream.source;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.culturegraph.mf.stream.pipe.ObjectBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for class {@link FileOpener}.
 *
 * @author Christoph Böhme
 *
 */
public final class FileOpenerTest {

	private static final String DATA = "Überfacture";

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testUtf8IsDefaultEncoding() throws IOException {
		assumeThat(
				"Default encoding is UTF-8: It is not possible to test whether FileOpener sets the encoding to UTF-8 correctly.",
				Charset.defaultCharset(), not(equalTo(StandardCharsets.UTF_8)));

		final File testFile = createTestFile();

		final FileOpener opener = new FileOpener();
		final ObjectBuffer<Reader> buffer = new ObjectBuffer<>();
		opener.setReceiver(buffer);
		opener.process(testFile.getAbsolutePath());
		opener.closeStream();

		assertEquals(DATA, IOUtils.toString(buffer.pop()));
	}

	private File createTestFile() throws IOException {
		final File file = tempFolder.newFile();
		try (OutputStream stream = new FileOutputStream(file)) {
			stream.write(DATA.getBytes(StandardCharsets.UTF_8));
		}
		return file;
	}

}
