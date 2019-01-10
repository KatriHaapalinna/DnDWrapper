package com.vaadin.addons.dndwrapper.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.vaadin.client.ui.VDragAndDropWrapper;
import com.vaadin.client.ui.dd.VHtml5File;

public class DnDWrapperWidget extends VDragAndDropWrapper {

    private boolean uploading;

    private final ReadyStateChangeHandler readyStateChangeHandler = new ReadyStateChangeHandler() {

        @Override
        public void onReadyStateChange(XMLHttpRequest xhr) {
            if (xhr.getReadyState() == XMLHttpRequest.DONE) {
                uploadHandler.uploadDone();
                uploading = false;
                startNextUpload();
                xhr.clearOnReadyStateChange();
            }
        }
    };

    @Override
    public void startNextUpload() {
        Scheduler.get().scheduleDeferred(new Command() {

            @Override
            public void execute() {
                if (!uploading) {
                    if (fileIds.size() > 0) {

                        uploading = true;
                        final Integer fileId = fileIds.remove(0);
                        VHtml5File file = files.remove(0);
                        final String receiverUrl = client.translateVaadinUri(
                                fileIdToReceiver.remove(fileId.toString()));
                        DnDExtendedXHR extendedXHR = (DnDExtendedXHR) DnDExtendedXHR
                                .create();
                        extendedXHR
                                .setOnReadyStateChange(readyStateChangeHandler);
                        extendedXHR.open("POST", receiverUrl);
                        extendedXHR.postFile(file);
                    }
                }

            }
        });

    }

    static class DnDExtendedXHR extends XMLHttpRequest {

        protected DnDExtendedXHR() {
        }

        public final native void postFile(VHtml5File file)
        /*-{
            var formData = new $wnd.FormData();
            formData.append("File", file);
            this.send(file);
        }-*/;

    }
}