package com.rau.codeduplication;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.util.ArrayList;

public class Duplication {
	
	private String nextState = "initialState";
	public String[] states = {"1", "2"};
	final static char[] latAlphabet = {'_','@', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
          'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	final static char[] numbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	final static char[] symbols = {'/', '*', '`', '~', '!', '#', '?', '$', ';', '%', '^', ':', '&', '?', '(', ')',
        '_', '-', '+', '=', '[', '{', '}', ']', '<', '>', ',', '\'', '|', '\\', '.', '[', '"'};
	final static char[] blankSymbols = {'\u0020','\n', '\r', '\t'};
	public boolean endOfLine = false;
	public String functionName = "";
	public ArrayList<Function> function = new ArrayList<Function>();
	public ArrayList<LineInfo> line = new ArrayList<LineInfo>();
	public ArrayList<String> toPrint = new ArrayList<String>();
	private int functionDepth = 0;
	private int countDepth = 0;
	private String lineForArray = "";
	int countOfLines = 0;	
	private int counter;
	private String checker = "";
	
	public boolean searchingFunctions(String state, char symbol, String path) {
		switch(state) {
		case "initialState":
			if(ArrayUtils.contains(latAlphabet, symbol)) {
				nextState = "1";
				functionName += String.valueOf(symbol);
			}
			else if(ArrayUtils.contains(numbers, symbol) || 
						ArrayUtils.contains(symbols, symbol) || 
						ArrayUtils.contains(blankSymbols, symbol)) {
				nextState = "initialState";
				if(symbol == '{') {
					++functionDepth;
				}
				else if(symbol == '}') {
					--functionDepth;
				}
			}
			return true;
		case "1":
			if(ArrayUtils.contains(latAlphabet, symbol) ||
					ArrayUtils.contains(numbers, symbol)) {
				nextState = "1";
				functionName += String.valueOf(symbol);				
			}
			else if(ArrayUtils.contains(blankSymbols, symbol)) {
				nextState = "1";
				functionName = "";
			}
			else if(symbol == '(') {
					nextState = "2";
			}
			else {
				nextState = "initialState";
			}
			return true;
		case "2":
			if(ArrayUtils.contains(latAlphabet, symbol) ||
					ArrayUtils.contains(numbers, symbol) ||
					ArrayUtils.contains(blankSymbols, symbol) ||
					symbol == ',' || symbol == '<' || symbol == '>') {
				nextState = "2";
			}
			else if(symbol == ')') {
				nextState = "3";
			}
			else {
				nextState = "initialState";
			}
			return true;
		case "3":
			if(ArrayUtils.contains(latAlphabet, symbol) ||
					ArrayUtils.contains(blankSymbols, symbol)) {
				nextState = "3";
			}
			else if(symbol == '{') {
				++functionDepth;
				countOfLines = 0;
				nextState = "functionLines";
				countDepth = functionDepth;
			}
			else {
				nextState = "initialState";
			}
			return true;
		case "functionLines":
			 if(ArrayUtils.contains(latAlphabet, symbol) || ArrayUtils.contains(numbers, symbol)) {
				 checker += String.valueOf(symbol);
				 lineForArray += String.valueOf(symbol);				 
				 nextState = "wordWasSeen";
			 }
			 else if(ArrayUtils.contains(blankSymbols, symbol)) {
					if(symbol == '\n') {
						++countOfLines;
					}
					nextState = "functionLines";
				}
			 else if(ArrayUtils.contains(symbols, symbol)) {
				 if(symbol == '{') {
					 LineInfo lineInfo = new LineInfo(String.valueOf(symbol), countOfLines);
					 line.add(lineInfo); 
					 ++countDepth;
				 }
				 else if(symbol == '}') {
					 if (functionDepth == countDepth) {
						 if(!lineForArray.isEmpty()) {
							 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
							 line.add(lineInfo);
						 }
						 @SuppressWarnings("unchecked")
						 Function functionForArray = new Function(functionName, (ArrayList<LineInfo>)line.clone(), path);
						 function.add(functionForArray);
						 for(int i = 0; i < line.size(); ++i) {
							 System.out.println(function.get(function.size()-1).name + " " + function.get(function.size()-1).line.get(i).line);
						 }
						 line.clear();
						 lineForArray = "";
						 functionName = "";
						 --functionDepth;
						 nextState = "initialState";
					 }
					 else {
						 --countDepth;
						 LineInfo lineInfo1 = new LineInfo(String.valueOf(symbol), countOfLines);
						 line.add(lineInfo1);
						 lineForArray = "";
					 }
				 }
				 else { 
					 lineForArray += String.valueOf(symbol);
				 }
			}
			return true;
		case "wordWasSeen":
			 if(ArrayUtils.contains(latAlphabet, symbol) || ArrayUtils.contains(numbers, symbol)) {
				 checker += String.valueOf(symbol);
				 lineForArray += String.valueOf(symbol);
			 }
			 else if(symbol == '(') {
				 if(checker.equals("if") || checker.equals("for") || checker.equals("while")) {
					 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
					 line.add(lineInfo);
					 LineInfo lineInfo1 = new LineInfo(String.valueOf(symbol), countOfLines);
					 line.add(lineInfo1);
					 nextState = "keyWordWasSeen";
					 checker = "";
					 lineForArray = "";
				 }
				 else if(checker.equals("switch")) {
				   	LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
					line.add(lineInfo);
					LineInfo lineInfo1 = new LineInfo(String.valueOf(symbol), countOfLines);
					line.add(lineInfo1);
					nextState = "switchWasSeen1";
					checker = "";
					lineForArray = "";
				}			 
				else {
					checker = "";
					lineForArray += String.valueOf(symbol);
				}
			 }
			 else if(symbol == '\u0020') {
				 if(checker.equals("if") || checker.equals("for") || checker.equals("while")) {
					 lineForArray += String.valueOf(symbol);
					 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
					 line.add(lineInfo);
					 nextState = "keyWordWasSeen2";
					 checker = "";
					 lineForArray = "";
				 }
				 else if(checker.equals("switch")) {
					 lineForArray += String.valueOf(symbol);
				   	 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
					 line.add(lineInfo);
					 nextState = "switchWasSeen2";
					 checker = "";
					 lineForArray = "";
				 }
				 else if(checker.equals("case")) {
					 lineForArray += String.valueOf(symbol);
					 nextState = "caseWasSeen";
					 checker = "";
				}	
				else {
					checker = "";
					lineForArray += String.valueOf(symbol);
				}
			 }
			 else if(symbol == ';') {
				 lineForArray += String.valueOf(symbol);
				 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
				 line.add(lineInfo);
				 lineForArray = "";
				 checker = "";
				 nextState = "functionLines";
			 }
			 else {
				 if(symbol == '\n') {
					 ++countOfLines;
					 if(lineForArray.equals("/*comment*/" + '\r') || lineForArray.equals("//comment" + '\r')) {
						 lineForArray = "";	
						 nextState = "functionLines";
						 return true;
					 }
				 }
				 lineForArray += String.valueOf(symbol);
			 }
			 return true;
		case "keyWordWasSeen":
			if(ArrayUtils.contains(latAlphabet, symbol) || ArrayUtils.contains(numbers, symbol) || ArrayUtils.contains(symbols, symbol) || symbol == '\u0020') {
				if(symbol == '&' || symbol == '|') {
					LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
					line.add(lineInfo);
					lineForArray = "";
					checker += String.valueOf(symbol);
					nextState = "andOr";
				}
				else if(symbol == '(') {
					++counter;
					lineForArray += String.valueOf(symbol);
				}
				else if(symbol == ')') {
					 if(counter == 0) {
						 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
						 line.add(lineInfo);
						 LineInfo lineInfo1 = new LineInfo(String.valueOf(symbol), countOfLines);
						 line.add(lineInfo1);
						 lineForArray = "";
						 nextState = "functionLines"; 
					 }
					 else {
						 --counter;
						 lineForArray += String.valueOf(symbol);
					 }
				}
				else if(symbol == ';') {
					 lineForArray += String.valueOf(symbol);
					 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
					 line.add(lineInfo);
					 lineForArray = "";
				 }
				else {
					lineForArray += String.valueOf(symbol);
				}
			}
			return true;
		case "andOr": 
			if(symbol == '&' || symbol == '|') {
				checker += String.valueOf(symbol);
				LineInfo lineInfo = new LineInfo(checker, countOfLines);
				line.add(lineInfo);
				checker = "";
				nextState = "keyWordWasSeen1";
			}
			return true;
		case "keyWordWasSeen2":
				if(symbol == '(') {
					lineForArray += String.valueOf(symbol);
					LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
					line.add(lineInfo);
					lineForArray = "";
					nextState = "keyWordWasSeen1";
				}
				return true;
		case "switchWasSeen1":
			if(ArrayUtils.contains(latAlphabet, symbol) || ArrayUtils.contains(numbers, symbol) || ArrayUtils.contains(symbols, symbol) || symbol == '\u0020') {
				if(symbol == '(') {
					++counter;
					lineForArray += String.valueOf(symbol);
				}
				else if(symbol == ')') {
					 if(counter == 0) {
						 LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
						 line.add(lineInfo);
						 LineInfo lineInfo1 = new LineInfo(String.valueOf(symbol), countOfLines);
						 line.add(lineInfo1);
						 lineForArray = "";
						 nextState = "functionLines"; 
					 }
					 else {
						 --counter;
						 lineForArray += String.valueOf(symbol);
					 }
				}
				else {
					lineForArray += String.valueOf(symbol);
				}
				return true;
			}
		case "switchWasSeen2":
			if(symbol == '(') {
				lineForArray += String.valueOf(symbol);
				LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
				line.add(lineInfo);
				lineForArray = "";
				nextState = "switchWasSeen1";
				return true;
			}
		case "caseWasSeen":
			if(ArrayUtils.contains(latAlphabet, symbol) || symbol =='\u0020' || 
				ArrayUtils.contains(numbers, symbol) || 
				(ArrayUtils.contains(symbols, symbol) && symbol != ':')) {
				 lineForArray += String.valueOf(symbol);
			 }
			else if(symbol == ':') {
				lineForArray += String.valueOf(symbol);
				LineInfo lineInfo = new LineInfo(lineForArray, countOfLines);
				line.add(lineInfo);
				lineForArray = "";
				nextState = "functionLines";
			}
			return true;
		}
		return false;
	}

	public void searchingDuplications(int n) {
		for(int i = 0; i < function.size(); ++i) {
            Function func1 = function.get(i);
			for(int j = i + 1; j < function.size(); ++j) {
                Function func2 = function.get(j);
                handleMatch(func1, func2, n);
			}
        }
	}

    private void handleMatch(final Function func1, final Function func2, int n) {
    	int count = 0;
    	int realCount = 0;
    	for(int i = 0; i < func1.line.size(); ++i) {
            for(int j = 0; j < func2.line.size(); ++j) {
                int firstIndex1 = 0;
                int firstIndex2 = 0;
                int lastIndex1 = 0;
                int lastIndex2 = 0;
                boolean firstMatch = true;
                int currentStatementIndex1 = 0;
                int currentStatementIndex2 = 0;
                boolean roundBracketWasSeen = false;
                for(int k = i, s = j; k < func1.line.size() && s < func2.line.size(); ++k, ++s) {
                    boolean result = checkStatementsEquality(func1.line.get(k).line, func2.line.get(s).line, currentStatementIndex1, currentStatementIndex2);
                    if(result) {
                    	if(func1.line.get(k).line.equals(")") && func1.line.get(k+1).line.equals("{")) {
                    		roundBracketWasSeen = true;
                    		++realCount;
                    		if(func1.line.get(k).number != func1.line.get(k+1).number) {
                    			++count;
                    		}
                    	}
                    	else if(func1.line.get(k).line.equals("{") && roundBracketWasSeen) {
                    		++realCount;
                    	}
                    	else if(!func1.line.get(k).line.equals("{")){
                    		roundBracketWasSeen = false;
	                    	if(!firstMatch && !roundBracketWasSeen) {
	                    		++realCount;
		                    	if(func1.line.get(k).number > firstIndex1) { 
		                    		/*schitaem kol-vo STROK, 
		                    		 * t.e. esli konstrukciya na odnoy stroke to count ne uvelichivaetsya, 
		                    		 * t.k. nam nujno, chtobi n > 2, gde n kol-vo strok.
		                    		 */
		                    		++count;
		                    	}
	                    	}
	                        //System.out.println(k + " " + s + " " + count);
	                    	if(firstMatch && !roundBracketWasSeen) {
	                    		roundBracketWasSeen = false;
	                        	++count;
	                        	++realCount;
	                            firstMatch = false;
	                            firstIndex1 = func1.line.get(k).number;
	                            firstIndex2 = func2.line.get(s).number;
	                        }
                    	}
                    }
                    if (!result || k == func1.line.size() - 1 || s == func2.line.size() - 1){
                        if(count >= n) {
                        	lastIndex1 = func1.line.get(k).number;
                        	lastIndex2 = func2.line.get(s).number;
                            reportResult(func1, func2,
                            firstIndex1, firstIndex2, lastIndex1, lastIndex2, realCount, k, s);
                        }
                        firstIndex1 = 0;
                        firstIndex2 = 0;
                        lastIndex1 = 0;
                        lastIndex2 = 0;
                        count = 0;
                        realCount = 0;
                        firstMatch = true;
                    }
                }
            }
        }
    }

    private boolean checkStatementsEquality(final String statement1, final String statement2, int currentStatementIndex1, int currentStatementIndex2) {
        final SubstituteIdResult modifiedStatement1 = substituteId(statement1, currentStatementIndex1, currentStatementIndex2);
        final SubstituteIdResult modifiedStatement2 = substituteId(statement2, currentStatementIndex1, currentStatementIndex2);
        boolean flag = modifiedStatement1.newStr.equals(modifiedStatement2.newStr);

        return flag;
    }

    
    private SubstituteIdResult substituteId(final String statement, int currentStatementIndex1, int currentStatementIndex2) {
        SubstituteIdResult result = new SubstituteIdResult();
        String newStr = "";
        String checker = "";
        String state = "initialState";
        boolean wordIsId = false;
        boolean wordIsNumber = false;
        boolean foundMatch = false;
        //newStr = statement if no id
        char[] str = statement.toCharArray();
        ArrayList<IdName> id = new ArrayList<IdName>();
        ArrayList<IdName> number = new ArrayList<IdName>();
        for(int i = 0; i < str.length; ++i) {
        	switch(state) {
        	case "initialState":
        		if(ArrayUtils.contains(blankSymbols, str[i])) {
        			newStr += String.valueOf(str[i]);
        			break;
        		}
	        	else if(ArrayUtils.contains(symbols, str[i])) {
	        		newStr += String.valueOf(str[i]);
	        		break;
	        	}
	        	else if(ArrayUtils.contains(latAlphabet, str[i])) {
	        		state = "wordWasSeen";
	        	}
        	case "wordWasSeen":
        		if(ArrayUtils.contains(latAlphabet, str[i])) {
	        		checker += String.valueOf(str[i]);
	        		newStr += String.valueOf(str[i]);
	        		break;
	        	}
        		else if (str[i] == '\u0020') {
	        		newStr += String.valueOf(str[i]);
	        		checker = "";
	        		nextState = "initialState";
	        		break;
        		}
        		else if(ArrayUtils.contains(numbers, str[i])) {
        				nextState = "numberWasSeen";
        		}        		
        	case "numberWasSeen":
        		if(ArrayUtils.contains(numbers, str[i])) {
        			if(checker.equals("id")) {
        				state = "numberWasSeen";
        				checker += String.valueOf(str[i]);
        				wordIsId = true;
        				break;
        			}
        			else if(checker.equals("number")) {
        				state = "numberWasSeen";
        				checker += String.valueOf(str[i]);
        				wordIsNumber = true;
        				break;
        			}
        		}
        		else if (ArrayUtils.contains(symbols, str[i]) || ArrayUtils.contains(blankSymbols, str[i]) || i == str.length-1){
        			if(wordIsId) {
        				for(int j = 0; j < id.size(); ++j) {
        					if(checker.equals(id.get(j).name)) {
        						newStr += String.valueOf(id.get(j).number) + String.valueOf(str[i]);
        						foundMatch = true;
        						break;
        					}
        				}
        				if(foundMatch == false) {
	        				newStr += String.valueOf(currentStatementIndex1) + String.valueOf(str[i]);
	        				IdName idForArray = new IdName(checker, currentStatementIndex1);
	        				id.add(idForArray);
	        				++currentStatementIndex1;
        				}
        				state = "initialState";
        				checker = "";
        				wordIsId = false;
        				foundMatch = false;
        				break;
        			}
        			else if(wordIsNumber) {
        				for(int j = 0; j < number.size(); ++j) {
        					if(checker.equals(id.get(j).name)) {
        						newStr += String.valueOf(id.get(j).number) + String.valueOf(str[i]);
        						foundMatch = true;
        						break;
        					}
        				}
        				if(foundMatch == false) {
	        				newStr += String.valueOf(currentStatementIndex2) + String.valueOf(str[i]);
	        				IdName numberForArray = new IdName(checker, currentStatementIndex2);
	        				number.add(numberForArray);
	        				++currentStatementIndex2;
        				}
        				state = "initialState";
        				checker = "";
        				wordIsId = false;
        				foundMatch = false;
        				break;
        			}
        		}
        	}
        
        }
        result.newStr = newStr;
        result.currentStatementIndex1 = currentStatementIndex1;
        result.currentStatementIndex2 = currentStatementIndex2;
        return result;
    }

    private void reportResult(final Function func1, final Function func2,
                              int func1StartIndex, int func2StartIndex, 
                              int func1LastIndex, int func2LastIndex, int count, int index1, int index2) {

        boolean lineIsOneSymbol = true;
        for(int i = 0; i < func1.line.size() && lineIsOneSymbol == true; ++i) {
        	if(func1.line.get(i).number == func1StartIndex) {
        		for(int j = i; j < i + count; ++j) {
        			if(!func1.line.get(j).line.equals("}")) {
        				lineIsOneSymbol = false;
        				break;
    				}
    			}
        	}
        }
        if(!lineIsOneSymbol) {
            boolean isSame = false;
            if(index1 == func1.line.size() - 1 || index2 == func2.line.size() - 1) {
	            final String same = func1.path + "  " + func1.name + " from " + String.valueOf(func1StartIndex) + " line to " +
	                    String.valueOf(func1LastIndex) + " equals to " + func2.path + "  " + func2.name + " from " +
	                    String.valueOf(func2StartIndex) + " line to " + String.valueOf(func2LastIndex) + "\r\n";
	            for(int i = 0; i< toPrint.size() && isSame == false; ++i) {
	                if(same.equals(toPrint.get(i))){
	                    isSame = true;
	                }
	            }
	            if(isSame == false) {
	                toPrint.add(same);
	            }
            }
            else { 
            	final String same = func1.path + "  " + func1.name + " from " + String.valueOf(func1StartIndex) + " line to " +
	                    String.valueOf(func1LastIndex - 1) + " equals to " + func2.path + "  " + func2.name + " from " +
	                    String.valueOf(func2StartIndex) + " line to " + String.valueOf(func2LastIndex - 1) + "\r\n";
            	 for(int i = 0; i< toPrint.size() && isSame == false; ++i) {
                     if(same.equals(toPrint.get(i))){
                         isSame = true;
                     }
                 }
                 if(isSame == false) {
                     toPrint.add(same);
                 }
            }
        }
    }
    
	public void searchDuplication(String path, int n) throws Exception {
		
		FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

        FileOutputStream out = new FileOutputStream(path + ".out");
        OutputStreamWriter writer = new OutputStreamWriter(out);
        Writer wr = new BufferedWriter(writer);
        
        try {
        	String line;
            nextState = "initialState";
            while ((line = br.readLine()) != null) {
                line = (line + "\r\n");
                char[] str = line.toCharArray();
                for (int i = 0; i < str.length; ++i) {
                	if (i == str.length - 1) {
                        endOfLine = true;
                    }
                	searchingFunctions(nextState, str[i], path);
            	}
            }
            searchingDuplications(n);
            for(int i = 0; i < toPrint.size(); ++i ) {
                wr.write(toPrint.get(i));
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