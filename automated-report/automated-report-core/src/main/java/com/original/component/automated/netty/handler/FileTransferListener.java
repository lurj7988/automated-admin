package com.original.component.automated.netty.handler;

import java.nio.file.Path;

public interface FileTransferListener {

    void onSuccess(Path path);

    void onFailure(Path path);

    void onFinished(Path path);
}
