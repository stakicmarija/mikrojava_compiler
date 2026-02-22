// generated with ast extension for cup
// version 0.8
// 16/1/2026 12:21:7


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListMeth_elem extends VarDeclListMeth {

    private VarDeclListMeth VarDeclListMeth;
    private VarDecl VarDecl;

    public VarDeclListMeth_elem (VarDeclListMeth VarDeclListMeth, VarDecl VarDecl) {
        this.VarDeclListMeth=VarDeclListMeth;
        if(VarDeclListMeth!=null) VarDeclListMeth.setParent(this);
        this.VarDecl=VarDecl;
        if(VarDecl!=null) VarDecl.setParent(this);
    }

    public VarDeclListMeth getVarDeclListMeth() {
        return VarDeclListMeth;
    }

    public void setVarDeclListMeth(VarDeclListMeth VarDeclListMeth) {
        this.VarDeclListMeth=VarDeclListMeth;
    }

    public VarDecl getVarDecl() {
        return VarDecl;
    }

    public void setVarDecl(VarDecl VarDecl) {
        this.VarDecl=VarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclListMeth!=null) VarDeclListMeth.accept(visitor);
        if(VarDecl!=null) VarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclListMeth!=null) VarDeclListMeth.traverseTopDown(visitor);
        if(VarDecl!=null) VarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclListMeth!=null) VarDeclListMeth.traverseBottomUp(visitor);
        if(VarDecl!=null) VarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclListMeth_elem(\n");

        if(VarDeclListMeth!=null)
            buffer.append(VarDeclListMeth.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDecl!=null)
            buffer.append(VarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListMeth_elem]");
        return buffer.toString();
    }
}
