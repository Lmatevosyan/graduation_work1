package com.rau.codeduplication;

public class SubstituteIdResult {
	 int currentStatementIndex1;
	 int currentStatementIndex2;
     String newStr;
     
     public SubstituteIdResult() {
    	 currentStatementIndex1 = 0;
    	 currentStatementIndex2 = 0;
    	 newStr = null;
     }
     public SubstituteIdResult(int currentStatementIndex1, int currentStatementIndex2, String newStr) {
    	 this.currentStatementIndex1 = currentStatementIndex1;
    	 this.currentStatementIndex2 = currentStatementIndex2;
    	 this.newStr = newStr;
     }
}