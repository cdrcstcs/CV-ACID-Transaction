package Db.bufferManager;
import Db.Utils;
import java.util.HashMap;
class Entry {
    int bfPoolId;
    Entry left;
    Entry right;
}
public class Lru extends Replacer {
    HashMap<Integer, Entry> hashmap;
    Entry start, end;
    int LRU_SIZE = Utils.bfPoolsize;
    Manager buffMan;
    public Lru (Manager buffMan) {
        super(buffMan);
        hashmap = new HashMap<Integer, Entry>();
        this.buffMan = buffMan;
    }
    public int updateEntry(int bfPoolId) {

        if (hashmap.containsKey(bfPoolId)) {
            Entry entry = hashmap.get(bfPoolId);
            removeNode(entry);
            addAtTop(entry);
            return entry.bfPoolId;
        }else if (hashmap.size() < Utils.bfPoolsize){
            return insertEntry(bfPoolId);
        }else {
            return -1;
        }
    }
    private int insertEntry(int bfPoolId){
        boolean found = false;
        int index = -1;
        for(int i=0; i< Utils.bfPoolsize; i++){
            if(!hashmap.containsKey(i)){
                index = i;
                found = true;
                break;
            }
        }
        if(index!=-1){
            Entry entry = new Entry();
            entry.bfPoolId = bfPoolId;
            addAtTop(entry);
            hashmap.put(entry.bfPoolId, entry);
            return index;
        }else {
            return -1;
        }
    }
    @Override
    public int pickVictim() {
        if (hashmap.size() < LRU_SIZE) {
            int bfPoolId = -1;
            for(int i=0; i< Utils.bfPoolsize; i++){
                if(!hashmap.containsKey(i)){
                    bfPoolId = i;
                    break;
                }
            }
            if(bfPoolId != -1){
                return bfPoolId;
            }
        }
        Entry iter = end;
        boolean found = false;
        while (iter != start){
            if(!buffMan.isPagePinned(iter.bfPoolId)){
                found = true;
                break;
            }
            iter = iter.left;
        }
        if(found){
            hashmap.remove(iter.bfPoolId);
            removeNode(end);

            return iter.bfPoolId;
        }else {
            throw new RuntimeException("all pages pinned in buffer pool");
        }
    }
    private void addAtTop(Entry node) {
        node.right = start;
        node.left = null;
        if (start != null)
            start.left = node;
        start = node;
        if (end == null)
            end = start;
    }
    private void removeNode(Entry node) {
        if (node.left != null) {
            node.left.right = node.right;
        } else {
            start = node.right;
        }
        if (node.right != null) {
            node.right.left = node.left;
        } else {
            end = node.left;
        }
    }
}