package com.mofang.feed.util;

public class RankHelper {

	private static final String ABCDE = "ABCDE";
	private static final String FGHIJ = "FGHIJ";
	private static final String KLMNO = "KLMNO";
	private static final String PQRST = "PQRST";
	private static final String WXYZ = "WXYZ";
	private static final String OTHER = "OTHER";
	
	public static String math(String nameSp){
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
