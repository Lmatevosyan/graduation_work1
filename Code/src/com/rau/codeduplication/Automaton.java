package com.rau.codeduplication;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;

public class Automaton {

    final static String[] keyWords = {"abstract", "boolean", "break", "byte", "byvalue", "case", "cast", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "extends", "false",
            "final", "finally", "float", "for", "generic", "goto", "if", "implements",
            "import", "inner", "instanceof", "int", "interface", "long", "native", "new", "null",
            "operator", "outer", "package", "private", "protected", "public", "rest", "return",
            "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient",
            "true", "try", "var", "void", "volatile", "while", "clone", "equals", "finalize", "getClass",
            "hashCode", "notify", "notifyAll", "toString", "wait", "main", "ArrayUtils", "String", "System", "err", "Override"};
    final static String[] states = {"initialState","slashWasSeen", "comment", "quoteWasSeen", "maybeTheEndOfComment", "blockComment",
            "numberWasSeen", "doubleNumberWasSeen", "idBeganWithUnderscore", "idBeganWithLetter",
            "checkerIsThrows", "checkerIsPackageOrImport", "checkerIsClass", "unicodeSymbols", "backslashWasRead",
            "symbolAfterApostropheWasSeen", "apostropheWasSeen", "stringInString"};
    final static String[] finiteStates = {"initialState", "checkerIsThrows", "checkerIsPackageOrImport", "checkerIsClass"};
    final static char[] latAlphabet = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    final static char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    final static char[] symbols = {'/', '*', '`', '~', '!', '@', '#', '¹', '$', ';', '%', '^', ':', '&', '?', '(', ')',
            '_', '-', '+', '=', '[', '{', '}', ']', '<', '>', ',', '\'', '|', '\\', '.', '[', '"'};
    final static char[] symbolsAfterId = {'\u0020', '\n', '\r', '\t', ',', ';', '+', '-', '=', ')', '.', '&', '*', '[', ']',
            '<', '>', '%', '^','!', '/'};
    final static char[] otherSymbols = {'@', '`', '~', '!', '$', ';',':', '%', '^',  '&', '?', '(', ')',
            '-', '+', '=', '*', '[', '{', '}', ']', '<', '>', ',', '\'', '|', '\\', '.', '['};
    final static char[] blankSymbols = {'\u0020','\n', '\r', '\t'};
    final static char[] symbolsAfterNumber = {'\u0020', ';', '+', '-', '=', ')', '^', '&', '*', '%', '/', ']', ','};
    final static char[] DLF = {'f', 'l', 'd', 'F', 'L', 'D'};
    final static char[] symbolsAfterFunctionsAndClasses = {':', '(', '{'};
    private String checker;
    private String nextState = "initialState";
    private  String lexeme;
    private  boolean checkerIsAKeyWord = false;
    private  boolean endOfLine = false;
    private  int countOfWords = 0;
    private  String firstWord;
    private   String oneSymbol;
    private  boolean allowed = true;
    private  String toPrint;
    private  int countDepth = 0;
    private  ArrayList<IdName> identifier = new ArrayList<IdName>();
    private  String name;
    private  int countOfIdentifiers = 0;
    private  int countOfId = 0; 
    private  boolean nameWasFound = false;
    private  ArrayList<String> number = new ArrayList<String>();
    private  int countOfNumbers = 0;
    private  boolean numberWasFound = false;
    private  String checkerForNumber;
    private String firstLexeme;
    private String secondChecker;

    public void Delta(String state, char saw) throws Exception
    {
        lexeme = null;
	//firstLexeme = "";
	//secondChecker = "";
        if(allowed) {
            findingNamesOfClasses(state, saw);
            return;
        }
        else if(isBlank(state, saw)) { // for blanks
            return;
        }
        else if(isComment(state, saw)) { // for comments
            return;
        }
        else if(isString(state, saw)) { // for strings
            return;
        }
        else if(isChar(state, saw)) {  // for chars
            return;
        }
        else if(isNumber(state, saw)) { // for numbers
            return;
        }
        else if(isId(state, saw)) { // for identifiers
            return;
        }
        else if(isPackageOrImport(state, saw)) {
            return;
        }
        else if(isThrows(state, saw)) {
            return;
        }
        else if(isClass(state, saw)) {
            return;
        }
        else if(isType(state, saw)) {
            return;
        }
        else if(isOtherSymbol(state, saw)) {
            return;
        }
        else {
            System.err.println(String.format("Bad error: state=%s, saw=%s", state, saw));
            throw new Exception("Invalid situation");
        }
    }

    private void findingNamesOfClasses(String state, char saw) throws Exception {
        if(countOfWords == 1) {
            if(ArrayUtils.contains(latAlphabet, saw)) {
                isId(state, saw);
            }
            else if(ArrayUtils.contains(symbolsAfterId, saw)) {
                if(checker != "") {
                    isId(state, saw);
                }
                else {
                    for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
                        if(name.equals(identifier.get(i).name)) {
                            toPrint = "id" + identifier.get(i).number + oneSymbol + String.valueOf(saw);
                            nameWasFound = true;
                        }
                    }
                    if(nameWasFound == false) {
                        IdName idForArray = new IdName(name, countDepth, countOfId);
                        identifier.add(countOfIdentifiers, idForArray);
                        toPrint = "id" + countOfId + oneSymbol + String.valueOf(saw);
                        ++countOfIdentifiers;
                        ++countOfId;
                    }
                    nameWasFound = false;
                    countOfWords = 0;
                    name = null;
                    firstWord = null;
                    oneSymbol = null;
                }
            }
            else if(ArrayUtils.contains(symbolsAfterFunctionsAndClasses, saw)) {
                	lexeme = name + oneSymbol + checker + String.valueOf(saw);
                	countOfWords = 0;
                    name = null;
                    firstWord = null;
                    oneSymbol = null;
                    nextState = "initialState";
            }
        }
        else {
            allowed = false;
            Delta(state, saw);
        }
    }

    public boolean isBlank(String state, char saw) 
    {
        if(ArrayUtils.contains(blankSymbols, saw)) {
            switch (state) {
                case "initialState":
                    return true;
            }
        }
        return false;
    }

    public boolean isOtherSymbol(String state, char saw)
    {
    	switch (state) {
            case "initialState":
                if(saw == '{'){
                    ++countDepth;
                    return true;
                }
                else if(saw == '}') {
                    for(int i = identifier.size()-1; i >= 0; --i) {
                      if(identifier.get(i).depth == countDepth) {
                          identifier.remove(i);
                          --countOfIdentifiers;
                      }
                    }
                    --countDepth;
                    return true;
                }
                else if(ArrayUtils.contains(otherSymbols, saw)) {
                    return true;
                }
        }
    	return false;
    }

    public boolean isComment(String state, char saw)
    {
        switch (state) {
            case "initialState":
                if(saw == '/') {
                    slashWasSeen();
                    return true;
                }
                break;
            case "slashWasSeen":
                if(saw == '/') {
                    lexeme = "//comment";
                    comment();
                    return true;
                }
                else if(saw == '*') {
                    blockComment();
                    return true;
                }
                else {
                	nextState = "initialState";
                	lexeme = "/" + String.valueOf(saw);
                	return true;
                }
            case "comment":
                if(ArrayUtils.contains(latAlphabet, saw) ||
                        ArrayUtils.contains(numbers, saw) ||
                        ArrayUtils.contains(symbols, saw) ||
                        saw == '\u0020') {
                    comment();
                    return true;
                }
                else if(endOfLine == true) {
                    commentWasRead();
                    return true;
                }
            case "blockComment":
                if(ArrayUtils.contains(latAlphabet, saw) ||
                        ArrayUtils.contains(numbers, saw) ||
                        (ArrayUtils.contains(symbols, saw) &&
                                saw != '*')) {
                    blockComment();
                    return true;
                }
                else if(saw == '*') {
                    maybeTheEndOfComment();
                    return true;
                }
            case "maybeTheEndOfComment":
                if(saw == '/') {
                    blockCommentWasRead();
                    return true;
                }
                else {
                    blockComment();
                    return true;
                }
        }
        return false;
    }

    public boolean isString(String state, char saw)
    {
        switch (state) {
            case "initialState":
                if(saw == '"') {
                    quoteWasSeen();
                    return true;
                }
                break;
            case "quoteWasSeen":
                if((ArrayUtils.contains(latAlphabet, saw) ||
                        ArrayUtils.contains(numbers, saw) ||
                        (ArrayUtils.contains(symbols, saw)) && saw != '"' && saw != '\\') ||
                        ArrayUtils.contains(blankSymbols, saw)) {
                    quoteWasSeen();
                    return true;
                }
                else if(saw == '\\') {
                    stringInString();
                    return true;
                }
                else if(saw == '"') {
                    stringWasRead();
                    return true;
                }
            case "stringInString":
                quoteWasSeen();
                return true;
        }
        return false;
    }
  
    public boolean isChar(String state, char saw)
    {
        switch (state) {
            case "initialState":
                if(saw == '\'') {
                    apostropheWasSeen();
                    return true;
                }
                break;
            case "apostropheWasSeen":
                if((ArrayUtils.contains(latAlphabet, saw) ||
                        ArrayUtils.contains(numbers, saw) ||
                        (ArrayUtils.contains(symbols, saw)) && saw != '\\')) {
                    symbolAfterApostropheWasSeen();
                    return true;
                }
                else if(saw == '\\') {
                    backslashWasRead();
                    return true;
                }
            case "symbolAfterApostropheWasSeen":
                if(saw == '\'') {
                    charWasRead();
                    return true;
                }
            case "backslashWasRead":
                if(saw == '\\' || saw == '\'' || saw == '\"' ||
                        ArrayUtils.contains(latAlphabet, saw)) {
                    unicodeSymbols();
                    return true;
                }
            case "unicodeSymbols":
                if((ArrayUtils.contains(latAlphabet, saw) ||
                        ArrayUtils.contains(numbers, saw))) {
                    unicodeSymbols();
                    return true;
                }
                else if(saw == '\'') {
                    charWasRead();
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isNumber(String state, char saw)
    {
        switch (state) {
            case "initialState":
                if(ArrayUtils.contains(numbers, saw)) {
                    numberWasSeen(saw);
                    return true;
                }
                break;
            case "numberWasSeen":
                if(saw == '.') {
                    doubleNumberWasSeen(saw);
                    return true;
                }
                else if(ArrayUtils.contains(numbers, saw)) {
                    numberWasSeen(saw);
                    return true;
                }
                else if(ArrayUtils.contains(symbolsAfterNumber, saw)) {
                    numberWasRead(saw);
                    return true;
                }
                 else if(ArrayUtils.contains(DLF, saw)) {
                    dlfNumberWasRead(saw);
                    return true;
                }
            case "doubleNumberWasSeen":
                if(ArrayUtils.contains(numbers, saw)) {
                    doubleNumberWasSeen(saw);
                    return true;
                }
                else if(ArrayUtils.contains(symbolsAfterNumber, saw)) {
                    numberWasRead(saw);
                    return true;
                }
                else if(ArrayUtils.contains(DLF, saw)) {
                    dlfNumberWasRead(saw);
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isId(String state, char saw) throws Exception
    {
        switch (state) {
            case "initialState":
                if(ArrayUtils.contains(latAlphabet, saw)) {
                    idBeganWithLetterOrUnderscore(saw);
                    return true;
                }
                break;
            case "idBeganWithLetterOrUnderscore":
                if(ArrayUtils.contains(numbers, saw)) {
                    checkerIsNotAKeyWord(saw);
                    return true;
                }
                else if(ArrayUtils.contains(latAlphabet, saw)) {
                    checker += String.valueOf(saw);
                    nextState = "idBeganWithLetterOrUnderscore";
                    return true;
                }
                else if(ArrayUtils.contains(symbolsAfterId, saw) && saw != '<' && saw != '.') {
                    for(int j = 0; j < keyWords.length && checkerIsAKeyWord == false; ++j) {
                        if(checker.equals(keyWords[j])) {
                            checkerIsAKeyWord(saw);
                        }
                    }
                    if(checkerIsAKeyWord == false) {
                        checkerIsAnId(saw);
                        nextState = "initialState";
                    }
                    else {
                        if(checker.equals("throws")) {
                            checkerIsThrows();
                        }
                        else if(checker.equals("package") || checker.equals("import")) {
                            checkerIsPackageOrImport();
                        }
                        else if(checker.equals("class")) {
                            checkerIsClass();
                        }
                        else {
                            nextState = "initialState";
                        }
                    }
                    checker = "";
                    checkerIsAKeyWord = false;
                    return true;
                }
                else if(saw == '.') {
            	    for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
                        if(checker.equals(identifier.get(i).name)) {
                            toPrint = "id" + identifier.get(i).number + String.valueOf(saw);
                            nameWasFound = true;
                        }
                    }
            	    if(nameWasFound == false ) {
                  	   toPrint = checker + String.valueOf(saw);
                     }
                     checker = "";
                     checkerIsAKeyWord = false;
                     nameWasFound = false;
                     nextState = "pointWasSeen";
                     return true;
                }
                else if(saw == '<') {
                	checkingForType();
                	return true;
                }
                else if(ArrayUtils.contains(symbolsAfterFunctionsAndClasses, saw)) {
                    if(saw == '{') {
                        ++countDepth;
                    }
                    lexeme = checker + String.valueOf(saw);
                    checker = "";
                    nextState = "initialState";
                    return true;
                }
            case "idBeganWithLetterAndCanNotBeAKeyWord":
                if(ArrayUtils.contains(numbers, saw) ||
                        ArrayUtils.contains(latAlphabet, saw)) {
                	checkerIsNotAKeyWord(saw);
                    return true;
                }
                else if(ArrayUtils.contains(symbolsAfterId, saw) && saw != '<' && saw != '.') {
                		for (int j = 0; j < keyWords.length && checkerIsAKeyWord == false; ++j) {
                			if(checker.equals(keyWords[j])) {
                				checkerIsAKeyWord(saw);
        					}
            			}
                		if(checkerIsAKeyWord == false) {
                			for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
                				if(checker.equals(identifier.get(i).name)) {
                					lexeme = "id" + identifier.get(i).number + String.valueOf(saw);
                					nameWasFound = true;
            					}
            				}
                			if(nameWasFound == false) {
                				lexeme = checker + String.valueOf(saw);
            				}
            			}
                        checker = "";
                        nameWasFound = false;
                        checkerIsAKeyWord = false;
                        nextState = "initialState";
                        return true;
                    }
                else if(saw == '.') {
            		for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
            			if(checker.equals(identifier.get(i).name)) {
            				toPrint = "id" + identifier.get(i).number + String.valueOf(saw);
            				nameWasFound = true;
        				}
        			} 
            		if(nameWasFound == false ) {
            			lexeme = checker + String.valueOf(saw);
        			}
            		checker = "";
            		nameWasFound = false;
            		nextState = "initialState";
            		return true;
        		}
            	else if (saw == '<') {
            		checkingForType();
            		return true;
        		}	
	        	else if(ArrayUtils.contains(symbolsAfterFunctionsAndClasses, saw)) {
	        		if(saw == '{') {
	        			++countDepth;
	        		}
	        		lexeme = checker + String.valueOf(saw);
	        		checker = "";
	                nextState = "initialState";
	                return true;
	        	}
            case "pointWasSeen":
            	if(ArrayUtils.contains(latAlphabet, saw) ||
            			ArrayUtils.contains(numbers, saw)) {
                    checker += String.valueOf(saw);
                    nextState = "pointWasSeen";
                    return true;
                }
            	else if(ArrayUtils.contains(symbolsAfterId, saw) && saw != '<' && saw != '.') {
            		for (int j = 0; j < keyWords.length && checkerIsAKeyWord == false; ++j) {
            			if(checker.equals(keyWords[j])) {
            				checkerIsAKeyWord(saw);
    					}
        			}
            		if(checkerIsAKeyWord == false) {
            			for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
            				if(checker.equals(identifier.get(i).name)) {
            					lexeme = "id" + identifier.get(i).number + String.valueOf(saw);
            					nameWasFound = true;
        					}
        				}
            			if(nameWasFound == false) {
            				lexeme = checker + String.valueOf(saw);
        				}
        			}
                    checker = "";
                    nameWasFound = false;
                    checkerIsAKeyWord = false;
                    nextState = "initialState";
                    return true;
                }
            	else if(saw == '.') {
            		for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
            			if(checker.equals(identifier.get(i).name)) {
            				toPrint = "id" + identifier.get(i).number + String.valueOf(saw);
            				nameWasFound = true;
        				}
        			} 
            		if(nameWasFound == false ) {
            			lexeme = checker + String.valueOf(saw);
        			}
            		checker = "";
            		nameWasFound = false;
            		nextState = "initialState";
            		return true;
        		}
	        	else if(ArrayUtils.contains(symbolsAfterFunctionsAndClasses, saw)) {
	        		if(saw == '{') {
	        			++countDepth;
	        		}
	        		lexeme = checker + String.valueOf(saw);
	        		checker = "";
	                nextState = "initialState";
	                return true;
	        	}
	            break;
        }
        return false;
    }
    
    public boolean isThrows(String state, char saw) {
        switch (state) {
            case "checkerIsThrows":
                if(ArrayUtils.contains(numbers, saw) ||
                		ArrayUtils.contains(latAlphabet, saw) ||
                		ArrayUtils.contains(blankSymbols, saw) ||
                		saw == '.'|| saw == ',') {
                    checkerIsThrows();
                    return true;
                }
                else if(saw == '{' || saw == ';') {
                    throwsWasRead();
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isPackageOrImport(String state, char saw) {
        switch (state) {
            case "checkerIsPackageOrImport":
                if(ArrayUtils.contains(numbers, saw) ||
                		ArrayUtils.contains(latAlphabet, saw) ||
                		saw == '.' || saw == '*') {
                    checkerIsPackageOrImport();
                    return true;
                }
                else if(saw == ';') {
                    packageOrImportWasRead();
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean isClass(String state, char saw) {
        switch(state) {
            case "checkerIsClass":
                if(ArrayUtils.contains(numbers, saw) ||
                		ArrayUtils.contains(latAlphabet, saw) ||
                		ArrayUtils.contains(blankSymbols, saw)) {
                    checkerIsClass();
                    return true;
                }
                else if(saw == '{') {
                    ++countDepth;
                    classWasRead();
                    return true;
                }
        }
        return false;
    }
    
    public boolean isType(String state, char saw) throws Exception {
        switch (state) {
            case "initial state":
                if(saw == '<') {
                    angleBracketsWasSeen();
                    return true;
                }
            case "angleBracketsWasSeen":
                if(ArrayUtils.contains(latAlphabet, saw)) {
                	secondChecker += String.valueOf(saw);
                    typeWasSeen();
                    return true;
                }
                else if(ArrayUtils.contains(numbers, saw) || ArrayUtils.contains(symbols, saw) 
                		&& saw != '>') {
                    state = "initialState";
                    for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
            			if(checker.equals(identifier.get(i).name)) {
            				toPrint = "id" + identifier.get(i).number + String.valueOf(saw);
            				nameWasFound = true;
        				}
        			} 
            		if(nameWasFound == false ) {
            			lexeme = checker + String.valueOf(saw);
        			}
                   // lexeme = "id" + "<";
                    Delta(state, saw);
                    return true;
                }
            case "typeWasSeen":
                if(ArrayUtils.contains(latAlphabet, saw) ||
                		ArrayUtils.contains(numbers, saw)) {
                	secondChecker += String.valueOf(saw);    
                	typeWasSeen();
                    return true;
                }
                else if(ArrayUtils.contains(symbols, saw) && saw != '>') {
                    itWasNotType(saw);
                    return true;
                }
                else if(saw == '>') {
                    typeWasRead();
                    return true;
                }
        }
        return false;
    }

    private void checkingForType() {
        nextState = "angleBracketsWasSeen";
    }

    private void typeWasRead() {
        nextState = "initialState";
        lexeme = checker + "<Type>";
    }

    private void itWasNotType(char saw) {
        nextState = "initialState";
        for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
            if(checker.equals(identifier.get(i).name)) {
            	firstLexeme = "id" + identifier.get(i).number + String.valueOf(saw);
            	nameWasFound = true;
            }
        } 
        if(nameWasFound == false ) {
           firstLexeme = checker + String.valueOf(saw);
        }
	for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
            if(secondChecker.equals(identifier.get(i).name)) {
            	toPrint = firstLexeme +"id" + identifier.get(i).number + String.valueOf(saw);
            	nameWasFound = true;
            }
        } 
        if(nameWasFound == false ) {
           toPrint = firstLexeme + secondChecker + String.valueOf(saw);
        }
       // lexeme = "id < id" + String.valueOf(saw);
    }

    private void typeWasSeen() {
        nextState = "typeWasSeen";
    }

    private void angleBracketsWasSeen() {
        nextState = "angleBracketsWasSeen";
    }

    public void  slashWasSeen() {
        nextState = "slashWasSeen";
    }

    public void comment() {
        nextState = "comment";
    }

    public void blockComment() {
        nextState = "blockComment";
    }

    public void commentWasRead() {
        nextState = "initialState";
    }

    public void maybeTheEndOfComment(){
        nextState = "maybeTheEndOfComment";
    }

    public void blockCommentWasRead() {
        nextState = "initialState";
        lexeme = "/*comment*/";
    }

    public void quoteWasSeen() {
        nextState = "quoteWasSeen";
    }

    public void stringInString() {
        nextState = "stringInString";
    }

    public void stringWasRead() {
        nextState = "initialState";
        lexeme = "\"string\"";
    }

    public void apostropheWasSeen() {
        nextState = "apostropheWasSeen";
    }

    public void symbolAfterApostropheWasSeen() {
        nextState = "symbolAfterApostropheWasSeen";
    }

    public void backslashWasRead() {
        nextState = "backslashWasRead";
    }

    public void charWasRead() {
        nextState = "initialState";
        lexeme = "'c'";
    }

    public void unicodeSymbols() {
        nextState = "unicodeSymbols";
    }

    public void numberWasSeen(char saw) {
        nextState = "numberWasSeen";
        checkerForNumber += String.valueOf(saw);
    }

    public void doubleNumberWasSeen(char saw) {
        nextState = "doubleNumberWasSeen";
        checkerForNumber += String.valueOf(saw);
    }

    public void numberWasRead(char saw) {
        nextState = "initialState";
            for(int i=0; i < number.size() && numberWasFound == false; ++i) {
                if(checkerForNumber.equals(number.get(i))) {
                    lexeme = "number" + i + String.valueOf(saw);
                    numberWasFound = true;
                }
            }
            if(numberWasFound == false) {
                number.add(countOfNumbers, checkerForNumber);
                lexeme = "number" + countOfNumbers + String.valueOf(saw);
                ++countOfNumbers;
            }
        checkerForNumber = null;
        numberWasFound = false;
    }

    public void dlfNumberWasRead(char saw){
        nextState = "initialState";
        checkerForNumber += String.valueOf(saw);
        if(countOfNumbers != 0) {
            for(int i=0; i< number.size() && numberWasFound == false; ++i) {
                if(checkerForNumber.equals(number.get(i))) {
                    lexeme = "number" + i;
                    numberWasFound = true;
                }
            }
            if(numberWasFound == false) {
                number.add(countOfNumbers, checkerForNumber);
                lexeme = "number" + countOfNumbers;
                ++countOfNumbers;
            }
        }
        else {
            number.add(countOfNumbers, checkerForNumber);
            lexeme = "number" + countOfNumbers;
            ++countOfNumbers;
        }
        checkerForNumber = null;
        numberWasFound = false;
    }
    
    public void idBeganWithUnderscore() {
        nextState = "idBeganWithUnderscore";
    }

    public void idBeganWithLetterOrUnderscore(char saw) {
        checker = String.valueOf(saw);
        nextState = "idBeganWithLetterOrUnderscore"; 
    }

    public void idWasRead(char saw) {
        nextState = "initialState";
        lexeme = "id" + String.valueOf(saw);
    }

    public void checkerIsNotAKeyWord(char c) {
        checker += String.valueOf(c);
        nextState = "idBeganWithLetterAndCanNotBeAKeyWord";
    }

    public void checkerIsAKeyWord (char saw) {
        checkerIsAKeyWord = true;
        lexeme = checker + String.valueOf(saw);
    }

    public void checkerIsThrows() {
        nextState = "checkerIsThrows";
    }

    public void throwsWasRead() {
        nextState = "initialState";
    }

    public void checkerIsPackageOrImport() {
        nextState = "checkerIsPackageOrImport";
    }

    public void packageOrImportWasRead() {
        nextState = "initialState";
    }

    public void checkerIsClass() {
        nextState = "checkerIsClass";
    }

    public void classWasRead() {
        nextState = "initialState";
    }

    public void checkerIsAnId(char saw)
    {
        if(saw == '\u0020') {
            if(countOfWords == 0) {
                firstWord = checker + String.valueOf(saw);
                name = checker;
                oneSymbol = String.valueOf(saw);
                ++countOfWords;
            }
            else if (countOfWords == 1) {
                for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
                    if(checker.equals(identifier.get(i).name)) {
                        lexeme = firstWord + "id" + identifier.get(i).number + String.valueOf(saw);
                        nameWasFound = true;
                    }
                }
                if(nameWasFound == false) {
                    IdName idForArray = new IdName(checker, countDepth, countOfId);
                    identifier.add(countOfIdentifiers, idForArray);
                    lexeme = firstWord +"id" + countOfId + String.valueOf(saw);
                    ++countOfIdentifiers;
                    ++countOfId;
                }
                nameWasFound = false;
                firstWord = null;
	            oneSymbol = null;
	            countOfWords = 0;
            }
        }
        else {
            if(countOfWords == 0) {
                for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
                    if(checker.equals(identifier.get(i).name)) {
                        lexeme = "id" + identifier.get(i).number + String.valueOf(saw);
                        nameWasFound = true;
                    }
                }
                if(nameWasFound == false) {
                	 IdName idForArray = new IdName(checker, countDepth, countOfId);
                     identifier.add(countOfIdentifiers, idForArray);
                     lexeme = "id" + countOfId + String.valueOf(saw);
                     ++countOfIdentifiers;
                     ++countOfId;
                }
                nameWasFound = false;
            }
            else if (countOfWords == 1) {
                for(int i = identifier.size()-1; i >= 0  && nameWasFound == false; --i) {
                    if(checker.equals(identifier.get(i).name)) {
                        lexeme = firstWord + "id" + identifier.get(i).number + String.valueOf(saw);
                        nameWasFound = true;
                    }
                }
                if(nameWasFound == false) {
                    IdName idForArray = new IdName(checker, countDepth, countOfId);
                    identifier.add(countOfIdentifiers, idForArray);
                    lexeme = firstWord + "id" + countOfId + String.valueOf(saw);
                    ++countOfIdentifiers;
                    ++countOfId;
                }
                nameWasFound = false;
                countOfWords = 0;
                firstWord = null;
                oneSymbol = null;
            }
        }
        checker = "";
    }
    
    public void analyze(String path) throws Exception
    {
        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

        FileOutputStream out = new FileOutputStream(path + ".out");
        OutputStreamWriter writer = new OutputStreamWriter(out);
        Writer wr = new BufferedWriter(writer);

        try {
        	String line;
            nextState = "initialState";
            while((line = br.readLine()) != null) {
            	line = (line + "\r\n");
            	char[] str = line.toCharArray();
            	for(int i = 0; i < str.length; ++i) {
            		if(i == str.length - 1) {
            			endOfLine = true;
        			}
                    allowed = true;
                    Delta(nextState, str[i]);
                    if(toPrint != null) {
                        wr.write(toPrint);
                        toPrint = null;
                    }
                    else if(lexeme != null ) {
                        wr.write(lexeme);
                    }
                    else {
                        for(int j = 0; j < finiteStates.length; ++j) {
                            if(nextState == finiteStates[j] && countOfWords == 0) {
                                wr.write(str[i]);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        finally {
            wr.close();
            br.close();
        }
    }
}