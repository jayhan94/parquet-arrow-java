package io.github.jayhan94.parquet.arrow;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ReadParquetTest {
    @Test
    public void readTest() throws IOException {
        String dataFile = ReadParquetTest.class.getResource("/parquet-testing/data/alltypes_plain.parquet").getPath();
        try (ParquetArrowReader reader = new ParquetArrowReader(dataFile)) {
            reader.nextBatch();
        }
    }
}
