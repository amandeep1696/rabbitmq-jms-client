package com.rabbitmq.jms.parse.sql;

/**
 * An {@link SqlParseTree} has a node at each point of the tree.
 * A Node has a type ({@link SqlTreeType}) which determines its degree and a value ({@link SqlToken}) which refines the operation this denotes.
 */
public class SqlTreeNode {

    private final SqlTreeType treeType;
    private final SqlToken value;               // null value means children

    private SqlExpressionType expType = SqlExpressionType.NOT_SET;  // type of expression represented by this node (if known)
    private String code;  // compiled version of this node (including child nodes)

    SqlTreeNode(SqlTreeType treeType, SqlToken value) {
        this.treeType = treeType;
        this.value = value;
    }

    SqlTreeNode(SqlTreeType treeType) {
        this(treeType, null);
    }

    // getter and setter for expression type - used by SqlTypeSetterVisitor
    void setExpType(SqlExpressionType expType) { this.expType = expType; }
    SqlExpressionType getExpType() { return this.expType; }

    // getter and setter for code - used by SqlCompilerVisitor
    void setCode(String code) { this.code = code; }
    String getCode() { return this.code; }

    // getters for node value and type
    SqlToken    value()     { return this.value;    }
    SqlTreeType treeType()  { return this.treeType; }

    @Override
    public String toString() {
        return "SqlTreeNode [treeType=" + treeType + ", value=" + value + ", expType=" + expType + "]";
    }
}
