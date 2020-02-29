package ru.comptech2020.support;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
    public static void main(String[] args) throws IOException, InterruptedException {
        final String folder = "/Users/hsi/study/comptech2020/openmobiledata_public";
        final String resultDataSet = "/Users/hsi/study/comptech2020/mobile_data.csv";
        final String threadsTmpFolder = "/Users/hsi/study/comptech2020/tmp";
        final List<Path> paths = Collections.unmodifiableList(
                Files.list(Paths.get(folder)).collect(Collectors.toList())
        );
        final int pathCount = paths.size();
        final int threadCount = 4;

        class MyThread extends Thread {
            private final int startOffset;
            public long recordCount = 0;

            public MyThread(int startOffset) {
                super();
                this.startOffset = startOffset;
            }

            @Override
            public void run() {
                final ObjectMapper objectMapper = new ObjectMapper();
                final Path pathOut = Paths.get(threadsTmpFolder + "/" + startOffset);
                try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(pathOut)) {
                    for (int pathNumber = startOffset; pathNumber < pathCount; pathNumber += threadCount) {
                        final Path path = paths.get(pathNumber);
                        try (final ZipFile zipFile = new ZipFile(path.toString())) {
                            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                            while (entries.hasMoreElements()) {
                                final ZipEntry zipEntry = entries.nextElement();
                                try (final InputStream bis = new BufferedInputStream(zipFile.getInputStream(zipEntry))) {
                                    final JsonNode json = objectMapper.readTree(bis);
                                    for (JsonNode event : json) {
                                        final JsonNode location = event.get("device_properties").get("location");
                                        final String lat = location.get("latitude").asText();
                                        final String lon = location.get("longitude").asText();
                                        if (lat.equals("0.0") && lon.equals("0.0")) {
                                            continue;
                                        }
                                        final String ctn = event.get("id").asText();
                                        final String timestamp = Long.toString(event.get("timestamp").asLong() / 1000);
                                        final String record = String.join(",", ctn, lat, lon, timestamp);
                                        bufferedWriter.write(record + "\n");
                                        recordCount++;
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("recordCount " + getName() + ": " + recordCount);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        final long startTime = System.currentTimeMillis();
        final List<MyThread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final MyThread thread = new MyThread(i);
            threads.add(thread);
            thread.start();
        }
        long recordCount = 0;
        for (MyThread thread : threads) {
            thread.join();
            recordCount += thread.recordCount;
        }
        System.out.println(String.format("time ms: %d", System.currentTimeMillis() - startTime));
        System.out.println("recordCount: " + recordCount);
        final Path pathOut = Paths.get(resultDataSet);
        try (final BufferedWriter bufferedWriter = Files.newBufferedWriter(pathOut)) {
            for (int i = 0; i < threadCount; i++) {
                final Path path = Paths.get(threadsTmpFolder + "/" + i);
                try (final BufferedReader bufferedReader = Files.newBufferedReader(path)) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        bufferedWriter.write(line + "\n");
                    }
                }
                Files.delete(path);
            }
        }
    }
}
