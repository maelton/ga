package br.com.maelton.domain;

import java.util.Objects;

public class Classes {
    private int id;
    private int size;
    
    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Classes(int id, int size) {
        this.id = id;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || getClass() != o.getClass()) return false;
        Classes group = (Classes) o;
        return id == group.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return String.format("Group[id=%d, size=%d]", this.id, this.size);
    }
}
