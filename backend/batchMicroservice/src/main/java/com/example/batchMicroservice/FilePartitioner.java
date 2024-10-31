package com.example.batchMicroservice;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FilePartitioner implements Partitioner {

    private final String[] directories;

    public FilePartitioner(String... directories) {
        this.directories = directories;
    }

    @Override
    public Map<String, org.springframework.batch.item.ExecutionContext> partition(int gridSize) {
        Map<String, org.springframework.batch.item.ExecutionContext> partitions = new HashMap<>();

        int partitionCount = 0;

        for (String directory : directories) {
            File dir = new File(directory);
            if (dir.exists() && dir.isDirectory()) {
                for (File file : dir.listFiles((f) -> f.getName().endsWith(".csv"))) {
                    org.springframework.batch.item.ExecutionContext context = new org.springframework.batch.item.ExecutionContext();
                    context.put("filePath", new FileSystemResource(file).getPath());

                    partitions.put("partition" + partitionCount, context);
                    partitionCount++;
                }
            }
        }
        return partitions;
    }
}
