package ru.comptech2020.support;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * gsutil -m cp -R gs://openmobiledata_public .
 * files: 16795 of 16796, lines: 5733852
 */
public class DataSet {
    public static void main(String[] args) throws IOException {
        final String folder = "/Users/hsi/study/comptech2020/openmobiledata_public";
        final String resultDataSet = "/Users/hsi/study/comptech2020/mobile_data.csv";
        final FileOutputStream fileOutputStream = new FileOutputStream(resultDataSet);
        final ObjectMapper objectMapper = new ObjectMapper();
        final List<Path> paths = Files.list(Paths.get(folder)).collect(Collectors.toList());
        final int size = paths.size();
        int fileCount = 1;
        final long[] recordCount = {0};
        for (Path path : paths) {
            final ZipFile zipFile = new ZipFile(path.toString());
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry zipEntry = entries.nextElement();
                final InputStream inputStream = zipFile.getInputStream(zipEntry);
                final JsonNode json = objectMapper.readTree(inputStream);
                json.elements().forEachRemaining(event -> {
                    final String ctn = event.get("id").asText();
                    final long timestamp = event.get("timestamp").asLong() / 1000;
                    final JsonNode location = event.get("device_properties").get("location");
                    final String lat = location.get("latitude").asText();
                    final String lon = location.get("longitude").asText();
                    if (!lat.equals("0.0") || !lon.equals("0.0")) {
                        final String record = String.join(",", ctn, lat, lon, Long.toString(timestamp)) + "\n";
                        try {
                            fileOutputStream.write(record.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        recordCount[0]++;
                    }
                });
            }
            System.out.println(String.format("files: %d of %d, lines: %d", fileCount++, size, recordCount[0]));
        }
        fileOutputStream.close();
    }
}
