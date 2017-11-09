package me.gavin.svg.model;

import java.io.Serializable;

public class IPath extends Drawable implements Serializable {

    public final String path;

    public IPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "path='" + path + '\'';
    }

}