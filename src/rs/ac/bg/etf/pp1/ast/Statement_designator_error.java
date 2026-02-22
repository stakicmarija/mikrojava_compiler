// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class Statement_designator_error extends Statement {

    public Statement_designator_error () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_designator_error(\n");

        buffer.append(tab);
        buffer.append(") [Statement_designator_error]");
        return buffer.toString();
    }
}
