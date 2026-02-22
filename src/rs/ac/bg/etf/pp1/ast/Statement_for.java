// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class Statement_for extends Statement {

    private OptDesStatement OptDesStatement;
    private Semi Semi;
    private OptCondition OptCondition;
    private Semi Semi1;
    private OptDesStatement OptDesStatement2;
    private Statement Statement;

    public Statement_for (OptDesStatement OptDesStatement, Semi Semi, OptCondition OptCondition, Semi Semi1, OptDesStatement OptDesStatement2, Statement Statement) {
        this.OptDesStatement=OptDesStatement;
        if(OptDesStatement!=null) OptDesStatement.setParent(this);
        this.Semi=Semi;
        if(Semi!=null) Semi.setParent(this);
        this.OptCondition=OptCondition;
        if(OptCondition!=null) OptCondition.setParent(this);
        this.Semi1=Semi1;
        if(Semi1!=null) Semi1.setParent(this);
        this.OptDesStatement2=OptDesStatement2;
        if(OptDesStatement2!=null) OptDesStatement2.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public OptDesStatement getOptDesStatement() {
        return OptDesStatement;
    }

    public void setOptDesStatement(OptDesStatement OptDesStatement) {
        this.OptDesStatement=OptDesStatement;
    }

    public Semi getSemi() {
        return Semi;
    }

    public void setSemi(Semi Semi) {
        this.Semi=Semi;
    }

    public OptCondition getOptCondition() {
        return OptCondition;
    }

    public void setOptCondition(OptCondition OptCondition) {
        this.OptCondition=OptCondition;
    }

    public Semi getSemi1() {
        return Semi1;
    }

    public void setSemi1(Semi Semi1) {
        this.Semi1=Semi1;
    }

    public OptDesStatement getOptDesStatement2() {
        return OptDesStatement2;
    }

    public void setOptDesStatement2(OptDesStatement OptDesStatement2) {
        this.OptDesStatement2=OptDesStatement2;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(OptDesStatement!=null) OptDesStatement.accept(visitor);
        if(Semi!=null) Semi.accept(visitor);
        if(OptCondition!=null) OptCondition.accept(visitor);
        if(Semi1!=null) Semi1.accept(visitor);
        if(OptDesStatement2!=null) OptDesStatement2.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(OptDesStatement!=null) OptDesStatement.traverseTopDown(visitor);
        if(Semi!=null) Semi.traverseTopDown(visitor);
        if(OptCondition!=null) OptCondition.traverseTopDown(visitor);
        if(Semi1!=null) Semi1.traverseTopDown(visitor);
        if(OptDesStatement2!=null) OptDesStatement2.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(OptDesStatement!=null) OptDesStatement.traverseBottomUp(visitor);
        if(Semi!=null) Semi.traverseBottomUp(visitor);
        if(OptCondition!=null) OptCondition.traverseBottomUp(visitor);
        if(Semi1!=null) Semi1.traverseBottomUp(visitor);
        if(OptDesStatement2!=null) OptDesStatement2.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_for(\n");

        if(OptDesStatement!=null)
            buffer.append(OptDesStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Semi!=null)
            buffer.append(Semi.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptCondition!=null)
            buffer.append(OptCondition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Semi1!=null)
            buffer.append(Semi1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptDesStatement2!=null)
            buffer.append(OptDesStatement2.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_for]");
        return buffer.toString();
    }
}
