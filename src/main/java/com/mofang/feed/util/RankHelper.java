package com.mofang.feed.util;

public class RankHelper {

	public static final String ABCDE = "ABCDE";
	public static final String FGHIJ = "FGHIJ";
	public static final String KLMNO = "KLMNO";
	public static final String PQRST = "PQRST";
	public static final String WXYZ = "WXYZ";
	public static final String OTHER = "OTHER";
	
	public static String match(String nameSp){
		char p = nameSp.charAt(0);
		int idx;
		for(idx = 0; idx < ABCDE.length(); idx ++){
			if(p == (ABCDE.charAt(idx))){
				return ABCDE;
			}
		}
		for(idx = 0; idx < FGHIJ.length(); idx ++){
			if(p == (FGHIJ.charAt(idx))){
				return FGHIJ;
			}
		}
		for(idx = 0; idx < KLMNO.length(); idx ++){
			if(p == (KLMNO.charAt(idx))){
				return KLMNO;
			}
		}
		for(idx = 0; idx < PQRST.length(); idx ++){
			if(p == (PQRST.charAt(idx))){
				return PQRST;
			}
		}
		for(idx = 0; idx < WXYZ.length(); idx ++){
			if(p == (WXYZ.charAt(idx))){
				return WXYZ;
			}
		}
		return OTHER;
	}
	
}
