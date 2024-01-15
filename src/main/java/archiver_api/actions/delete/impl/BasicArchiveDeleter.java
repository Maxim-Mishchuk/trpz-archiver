package archiver_api.actions.delete.impl;

import archiver_api.output.Entity;
import archiver_api.actions.delete.ArchiveDeleter;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BasicArchiveDeleter implements ArchiveDeleter {
    @Override
    public List<Entity> delete(ArchiveInputStream ais, ArchiveOutputStream aos, List<String> entityNames) throws IOException {
        List<Entity> deletedEntities = new LinkedList<>();

        ArchiveEntry currentEntry;
        while ((currentEntry = ais.getNextEntry()) != null) {
            if (entityNames.contains(currentEntry.getName())) {
                deletedEntities.add(Entity.create(
                        currentEntry.getName(), IOUtils.toByteArray(ais)
                ));
            } else {
                aos.putArchiveEntry(currentEntry);
                IOUtils.copy(ais, aos);
                aos.closeArchiveEntry();
            }
        }

        return deletedEntities;
    }
}
