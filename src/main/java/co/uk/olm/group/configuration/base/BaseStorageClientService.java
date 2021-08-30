package co.uk.olm.group.configuration.base;

import co.uk.olm.group.model.FileFormData;

public interface BaseStorageClientService {

    String upload(final FileFormData file) throws Exception;

    void download(final String fileName) throws Exception;
}
