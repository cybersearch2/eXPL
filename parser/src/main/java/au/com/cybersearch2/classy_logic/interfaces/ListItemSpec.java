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
     * Set item index 
     * @param appendIndex Index to set
     */
    void setItemIndex(int appendIndex);
    
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

    /**
     * Sets text to append to name of variable
     * @param suffix
     */
    void setSuffix(String suffix);

    /**
     * Complete binding to item list
     * @param itemList The item list
     */
    void assemble(ItemList<?> itemList);
    
    /**
     * Evaluate index used to select value
     * @param itemList The item list
     * @param id Modification id
     * @return flag set true if evaluation successful
     */
    boolean evaluate(ItemList<?> itemList, int id);
}