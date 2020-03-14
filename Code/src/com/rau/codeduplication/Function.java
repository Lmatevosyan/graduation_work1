package com.rau.codeduplication;

import java.util.ArrayList;


public class Function {
	public String name;
	public ArrayList<LineInfo> line = new ArrayList<LineInfo>();
	public String path;
	
	public Function (String name, ArrayList<LineInfo> line, String path) {
        this.name = name;
        this.line = line;
        this.path = path;
    }
}