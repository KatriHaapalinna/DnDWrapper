package com.vaadin.addons.dndwrapper.client;

import com.vaadin.addons.dndwrapper.DnDWrapper;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.draganddropwrapper.DragAndDropWrapperConnector;
import com.vaadin.shared.ui.Connect;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(DnDWrapper.class)
public class DnDWrapperConnector extends DragAndDropWrapperConnector {

    @Override
    protected void init() {
        super.init();
        getWidget().uploadHandler = this;
    }

    @Override
    public DnDWrapperWidget getWidget() {
        return (DnDWrapperWidget) super.getWidget();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);
    }
}
