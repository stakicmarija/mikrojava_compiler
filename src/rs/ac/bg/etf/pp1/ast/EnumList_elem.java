// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class EnumList_elem extends EnumList {

    private EnumElem EnumElem;
    private EnumList EnumList;

    public EnumList_elem (EnumElem EnumElem, EnumList EnumList) {
        this.EnumElem=EnumElem;
        if(EnumElem!=null) EnumElem.setParent(this);
        this.EnumList=EnumList;
        if(EnumList!=null) EnumList.setParent(this);
    }

    public EnumElem getEnumElem() {
        return EnumElem;
    }

    public void setEnumElem(EnumElem EnumElem) {
        this.EnumElem=EnumElem;
    }

    public EnumList getEnumList() {
        return EnumList;
    }

    public void setEnumList(EnumList EnumList) {
        this.EnumList=EnumList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumElem!=null) EnumElem.accept(visitor);
        if(EnumList!=null) EnumList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumElem!=null) EnumElem.traverseTopDown(visitor);
        if(EnumList!=null) EnumList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumElem!=null) EnumElem.traverseBottomUp(visitor);
        if(EnumList!=null) EnumList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumList_elem(\n");

        if(EnumElem!=null)
            buffer.append(EnumElem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumList!=null)
            buffer.append(EnumList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumList_elem]");
        return buffer.toString();
    }
}
