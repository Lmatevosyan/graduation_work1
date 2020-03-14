package com.rau.codeduplication;

public class IdName {
    public String name;
    public int depth;
    public int number;

    public IdName() {
        name = null;
        depth = 0;
        number = 0;
    }

    public IdName (String name, int depth, int number) {
        this.name = name;
        this.depth = depth;
        this.number = number;
    }
    
    public IdName (String name,  int number) {
        this.name = name;
        this.number = number;
    }
}