package memory;

import java.util.ArrayList;

public class CacheLineSet {
	private ArrayList<CacheLine> lineSet = new ArrayList<CacheLine>();
	private int associativity;
	
	public CacheLineSet(int associativity, int lineSize){
		this.associativity = associativity;
		for(int q = 0; q < associativity; q++){
			lineSet.add(new CacheLine(lineSize));
		}
	}
	
	public int searchTags(int tag){
		return searchTags(tag + "");
	}
	
	// Returns -1 if not found
	public int searchTags(String tag){
		for(int q = 0; q < associativity; q++){
			if(lineSet.get(q).getTag().equals(tag)){
				return q;
			}
		}
		
		return -1;
	}
	
	public CacheLine getCacheLine(int index){
		return lineSet.get(index);
	}
	
	public void insert(int index, CacheLine line){
		lineSet.set(index, line);
	}
	
	public int getLineIndexToReplace(){
		int notDirtyIndex = -1;
		for(int q = 0; q < associativity; q++){
			CacheLine line = lineSet.get(q);
			if(line.getTag() == null){
				return q;
			}
			if(notDirtyIndex == -1 && !line.isDirty()){
				notDirtyIndex = q;
			}
		}
		
		if(notDirtyIndex != -1){
			return notDirtyIndex;
		}
		
		return 0;
	}
}
