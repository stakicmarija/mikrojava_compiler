// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class DesignatorList_id_len extends DesignatorList {

    private String I1;
    private OptIdLen OptIdLen;

    public DesignatorList_id_len (String I1, OptIdLen OptIdLen) {
        this.I1=I1;
        this.OptIdLen=OptIdLen;
        if(OptIdLen!=null) OptIdLen.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public OptIdLen getOptIdLen() {
        return OptIdLen;
    }

    public void setOptIdLen(OptIdLen OptIdLen) {
        this.OptIdLen=OptIdLen;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptIdLen!=null) OptIdLen.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptIdLen!=null) OptIdLen.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptIdLen!=null) OptIdLen.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesignatorList_id_len(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(OptIdLen!=null)
            buffer.append(OptIdLen.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesignatorList_id_len]");
        return buffer.toString();
    }
}
