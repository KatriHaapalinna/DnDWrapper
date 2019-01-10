package com.vaadin.addons.dndwrapper.demo;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addons.dndwrapper.DnDWrapper;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.Page;
import com.vaadin.server.StreamVariable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.Html5File;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("DnDWrapper Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {
    @Override
    protected void init(VaadinRequest request) {
        Panel panel = new Panel("DnD upload panel");
        panel.setSizeFull();
        final DnDWrapper component = new DnDWrapper(panel);
        component.setSizeFull();

        component.setDropHandler(new DropHandler() {
            @Override
            public void drop(DragAndDropEvent event) {
                WrapperTransferable tr = (WrapperTransferable) event
                        .getTransferable();
                Html5File[] files = tr.getFiles();

                if (files != null) {
                    List<Html5File> filesToUpload = Arrays.asList(files);
                    for (Html5File file : filesToUpload) {
                        file.setStreamVariable(
                                new MyStreamVariable(file.getFileName()));
                    }
                }
            }

            @Override
            public AcceptCriterion getAcceptCriterion() {
                return AcceptAll.get();
            }

        });
        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.addComponents(component);
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        setContent(layout);
    }

    class MyStreamVariable implements StreamVariable {
        private static final long serialVersionUID = 1L;
        private String fileName;

        public MyStreamVariable(String fileName) {
            super();
            this.fileName = fileName;
        }

        @Override
        public OutputStream getOutputStream() {
            FileOutputStream fos = null; // Stream to write to
            try {
                fos = new FileOutputStream("C:/DEV/" + fileName);
            } catch (final java.io.FileNotFoundException e) {
                new Notification("Could not open file<br/>", e.getMessage(),
                        Notification.Type.ERROR_MESSAGE)
                                .show(Page.getCurrent());
                return null;
            }
            return fos;
        }

        @Override
        public boolean listenProgress() {
            return true;
        }

        long lastEvent = 0;
        long lastTime = 0;

        @Override
        public void onProgress(StreamingProgressEvent event) {
            long received = event.getBytesReceived() - lastEvent;
            long now = new Date().getTime();
            long time = now - lastTime;
            lastTime = now;
            lastEvent = event.getBytesReceived();
            if (time == 0) {
                return;
            }
        }

        @Override
        public void streamingStarted(StreamingStartEvent event) {
            lastEvent = 0;
            lastTime = new Date().getTime();
        }

        @Override
        public void streamingFinished(StreamingEndEvent event) {
            Notification.show("File(s) uploaded!");
        }

        @Override
        public void streamingFailed(StreamingErrorEvent event) {
        }

        @Override
        public boolean isInterrupted() {
            return false;
        }

    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = DemoUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
