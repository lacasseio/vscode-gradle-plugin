package io.lacasse.integtest

import static org.junit.Assert.assertTrue

class TestFile extends File {

    TestFile(File file) {
        super(file.absolutePath)
    }

    TestFile assertExists() {
        assertTrue(String.format("%s does not exists", this), exists())
        return this
    }

    TestFile assertIsFile() {
        assertTrue(String.format("%s is not a file", this), isFile())
        return this
    }

    TestFile file(String path) {
        return new TestFile(new File(this, path))
    }
}
