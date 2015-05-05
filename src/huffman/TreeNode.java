package huffman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by rebecca on 5/4/15.
 */
public class TreeNode {
    public String str;
    public int count;
    public ArrayList<TreeNode> children;
    public TreeNode parent;

    public TreeNode(String str, int count){
        this.str = str;
        this.count =count;
        this.children = new ArrayList<TreeNode>();
        this.parent = null;
    }
    public void setParent(TreeNode parent){
        this.parent = parent;
        parent.children.add(this);
    }
    public TreeNode getParent(){
        return this.parent;
    }
    public void addChild(TreeNode child){
        child.setParent(this);
    }

    @Override
    public String toString(){
        return this.str +", " + this.count;
    }
    private String tabs(int n) {
        String str = "";
        for(int i=0; i<n; i++)
            str += "    ";
        return str;
    }
    public void printTree(int depth){
        System.out.println(this.tabs(depth) + this.toString());
        for(int i=0; i<this.children.size(); i++)
            this.children.get(i).printTree(depth + 1);
    }


    public void getCodes(String code, TreeMap<String, String> codes){
        //if we are at a single character, add the code to the hashmap
        if (this.children.size()==0)
            codes.put(this.str, code);
        else{//else iterate through the children
            for(int i=0; i<this.children.size(); i++){
                if (i%2==0)//if it is a left child
                    this.children.get(i).getCodes(code + "0", codes);
                else//if it is a right child
                    this.children.get(i).getCodes(code + "1", codes);
            }
        }
    }


}
