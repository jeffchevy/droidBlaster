package com.drillandblast.model;

import java.io.Serializable;

public abstract class Entity implements Serializable {
    boolean isDirty;

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }
}
