package archiver_api.actions.read.impl;

import archiver_api.output.Entity;
import archiver_api.actions.read.ArchiveReader;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BasicArchiveReader implements ArchiveReader {
    @Override
    public List<Entity> readBasic(ArchiveInputStream ais) throws IOException {
        List<Entity> entityList = new LinkedList<>();

        ArchiveEntry currentEntry;
        while ((currentEntry = ais.getNextEntry()) != null) {
            entityList.add(Entity.create(currentEntry.getName()));
        }

        return entityList;
    }

    @Override
    public List<Entity> readFull(ArchiveInputStream ais) throws IOException {
        List<Entity> entityList = new LinkedList<>();

        ArchiveEntry currentEntry;
        while ((currentEntry = ais.getNextEntry()) != null) {
            entityList.add(Entity.create(currentEntry.getName(), currentEntry.getLastModifiedDate(), IOUtils.toByteArray(ais)));
        }

        return entityList;
    }
}
