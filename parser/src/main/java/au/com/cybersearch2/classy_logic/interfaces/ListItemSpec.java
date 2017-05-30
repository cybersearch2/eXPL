package au.com.cybersearch2.classy_logic.interfaces;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;

public interface ListItemSpec
{

    /**
     * Returns name of axiom list
     * @return String
     */
    String getListName();

    /**
     * Returns name of axiom list
     * @return String
     */
    QualifiedName getQualifiedListName();

    /**
     * Returns unique name for variable to reference axiom list
     * @return String
     */
    QualifiedName getVariableName();

    /**
     * Returns term selection index
     * @return Valid index or -1 if not used
     */
    int getItemIndex();

    /**
     * Returns Compiler operand for term selection
     * @return Operand object
     */
    Operand getItemExpression();

    /**
     * Returns Text to append to name of variable
     * @return String
     */
    String getSuffix();

    void assemble(ItemList<?> itemList);
    boolean evaluate(ItemList<?> itemList, int id);
}